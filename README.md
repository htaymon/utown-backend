# UTown Backend

[![CI](https://github.com/htaymon/utown-backend/actions/workflows/ci.yml/badge.svg)](https://github.com/htaymon/utown-backend/actions/workflows/ci.yml)
![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8-blue?logo=mysql)
![Flyway](https://img.shields.io/badge/Flyway-migrations-CC0200?logo=flyway)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![Maven](https://img.shields.io/badge/Build-Maven-red?logo=apachemaven)

A production-style Spring Boot REST API for a campus food ordering and delivery platform. Restaurant owners manage menus and incoming orders, students browse restaurants, build a cart, place orders, and track delivery status — all behind JWT-authenticated, role-based **and ownership-based** access control.

---

## Overview

UTown models the core of a food-delivery product end to end:

- **Users & auth** — registration, login, BCrypt password hashing, JWT issuance/validation, role-based authorization (`CLIENT`, `RESTAURANT_ADMIN`, `ADMIN`).
- **Restaurants & menus** — restaurant/dish CRUD, categories, dish options, delivery areas, work schedules, open/closed status.
- **Ordering** — cart → cart items → order, server-computed totals, order status lifecycle, ownership-scoped access, cancellation rules, and concurrency-safe order placement (see below).
- **Notifications & staff scheduling** — per-user notifications, restaurant work schedules.

The project is intentionally scoped to what a real food-ordering backend needs — no speculative features bolted on to look bigger. It's a single Spring Boot module (modular monolith), not microservices.

---

## Architecture

```
Client
  │
  ▼
Controller  (thin — request/response DTOs, validation, @PreAuthorize)
  │
  ▼
Service     (business rules, ownership checks, transactions)
  │
  ▼
Repository  (Spring Data JPA)
  │
  ▼
MySQL  (schema owned by Flyway migrations)
```

- **DTOs everywhere** — entities never cross the controller boundary; MapStruct handles entity ↔ DTO mapping.
- **Centralized error handling** — a single `@RestControllerAdvice` turns every exception into a consistent `{code, message, path, timestamp}` JSON body.
- **Ownership + role checks in the service layer**, not just `@PreAuthorize` — e.g. a `RESTAURANT_ADMIN` can only manage *their own* restaurant's dishes, delivery areas, schedules and orders; a `CLIENT` can only see their own cart, addresses, orders and notifications.

### Package layout

```
src/main/java/com/utown/utown_backend
├── config          # CORS, OpenAPI, JPA auditing
├── controller      # REST endpoints (thin)
├── dto/request     # validated inbound payloads
├── dto/response    # outbound payloads (incl. generic PageResponseDTO<T>)
├── entity          # JPA entities
├── enums           # DishStatus, OrderStatus, RestaurantStatus, NotificationStatus
├── exception       # domain exceptions + GlobalExceptionHandler
├── mapper          # MapStruct entity↔DTO mappers
├── repository      # Spring Data JPA repositories
├── security        # JWT filter/util, SecurityConfig, ownership guard
└── service         # business logic, transactions
```

---

## Technology stack

| Category | Technology |
| --- | --- |
| Language / runtime | Java 17 |
| Framework | Spring Boot 3.5 |
| Security | Spring Security 6, JWT (jjwt), BCrypt |
| Persistence | Spring Data JPA / Hibernate, MySQL 8 |
| Migrations | Flyway |
| Mapping | MapStruct |
| Validation | Jakarta Bean Validation |
| API docs | springdoc-openapi (Swagger UI) |
| Monitoring | Spring Boot Actuator (`/actuator/health`) |
| Testing | JUnit 5, Mockito, MockMvc, AssertJ, H2 (in-memory), JaCoCo |
| Build | Maven |
| Containerization | Docker, Docker Compose |
| CI | GitHub Actions (Continuous Integration) |

---

## Database design

16 tables modeling the domain: `roles`, `users`, `restaurant_categories`, `restaurants`, `dish_categories`, `dishes`, `options`, `addresses`, `delivery_areas`, `work_schedules`, `carts`, `cart_items`, `orders`, `order_items`, `order_item_options`, `notifications`.

Notable design decisions:

- **Schema is owned by Flyway**, not Hibernate. `spring.jpa.hibernate.ddl-auto=validate` — the app refuses to start if the entities and the migrated schema disagree, instead of silently patching the schema (`ddl-auto=update` in production is exactly the kind of thing that causes 2am incidents).
- Every foreign key has a matching index (`idx_dishes_restaurant_id`, `idx_orders_user_id`, etc.) since every one of them is used in a `WHERE`/join in the service layer.
- Cascades follow the JPA entity model: `order_items`/`order_item_options`/`cart_items` are `ON DELETE CASCADE` from their aggregate root (matching `cascade = ALL, orphanRemoval = true` on `Order`/`OrderItem`/`Cart`); everything else defaults to `RESTRICT` since no entity cascades deletes in that direction.
- `orders.order_no`, `carts.user_id` (one active cart per user), and `cart_items (cart_id, dish_id)` (one row per dish per cart) are unique-constrained — see **Concurrency & data integrity** below for why the last one matters.
- List endpoints that can grow without bound (`/dishes`, `/restaurants`, `/orders/my`, `/orders/restaurant/{id}`) are paginated and use `@EntityGraph` to fetch their to-one associations in one query instead of N+1-ing through lazy relations. Small reference/config data (categories, roles, delivery areas, work schedules) intentionally isn't paginated — it's not going to have thousands of rows.

Migrations live in [`src/main/resources/db/migration`](src/main/resources/db/migration):

| File | Purpose |
| --- | --- |
| `V1__init_schema.sql` | Full schema: tables, FKs, indexes |
| `V2__seed_roles.sql` | Seeds `CLIENT`, `RESTAURANT_ADMIN`, `ADMIN` (registration depends on `CLIENT` existing) |
| `V3__seed_demo_data.sql` | Optional demo accounts/restaurant/menu so the API is explorable immediately after setup |
| `V4__add_cart_items_unique_constraint.sql` | `UNIQUE (cart_id, dish_id)` on `cart_items` — database-level backstop for the cart-item race fix below |

### Concurrency & data integrity

Two race conditions were identified by design review and fixed with real, deterministic multithreaded tests (not mocked call-count tests) proving the fix:

1. **Order double-submission.** `OrderService.create()` reads the current cart, builds the order, then clears the cart. Without protection, two concurrent "place order" requests (a double-click, or a client retry after a slow response) could both read the same non-empty cart before either cleared it, producing two orders from one cart. **Fix:** the cart row is read with a pessimistic write lock (`SELECT ... FOR UPDATE`, via `CartRepository.findByUserIdForUpdate`) for the duration of the transaction. The second concurrent request simply blocks until the first commits, then correctly sees an empty cart and gets the same `400 Cart is empty` response a real second click would produce — never a duplicate order. Chosen over `@Version` optimistic locking because the desired behavior isn't "reject the second request for the client to retry," it's "serialize the two attempts so only one order is ever created" — a lock does that for free.
2. **Duplicate cart-item rows.** `CartItemService.create()` merges quantity into an existing row if one is found for that `(cart, dish)` pair, otherwise inserts a new one — a read-then-write with no database guarantee. **Fix:** a unique constraint on `(cart_id, dish_id)` (migration `V4`, mirrored in the `CartItem` entity so the test schema enforces it too). The rare loser of a genuine race gets a clean `409 CONFLICT` (via the pre-existing generic `DataIntegrityViolationException` handler — no new exception-handling code needed) instead of a duplicate row.

Both are covered by `ConcurrencyIntegrationTest`, which fires two real threads at the same endpoint against a real committed database state and asserts the actual row counts afterward.

---

## Security

- **JWT auth**: stateless (`SessionCreationPolicy.STATELESS`), `Authorization: Bearer <token>`. Token carries the user's email (subject) and role; expiry is configurable.
- **Password hashing**: BCrypt via Spring Security's `PasswordEncoder`.
- **Role-based access control**: `@EnableMethodSecurity` + `@PreAuthorize` on every mutating endpoint.
- **Ownership checks**: role alone isn't enough to authorize most actions — e.g. `@PreAuthorize("hasRole('RESTAURANT_ADMIN')")` only proves *a* restaurant admin is calling; the service layer (`RestaurantAccessGuard`) then verifies *this* admin owns *this* restaurant before allowing a dish/schedule/delivery-area mutation. The same pattern protects a user's own cart, addresses, orders and notifications from being read or modified by anyone else.
- **401 vs 403 are distinct**: a custom `AuthenticationEntryPoint` returns `401 UNAUTHENTICATED` with the standard JSON error body for missing/invalid credentials (including a JWT that's structurally valid but names a user since deleted); `@PreAuthorize`/ownership failures return `403 ACCESS_DENIED`. (Spring Security's default behavior here is a bare, bodiless `403` for both cases unless you configure this explicitly — a common gap in hand-rolled Spring Security setups.)
- **No secrets in source control**: `DATABASE_URL`, `APP_JWT_SECRET`, `CORS_ALLOWED_ORIGINS` are all environment variables; `.env` is gitignored; `.env.example` documents the shape without real values.
- **CORS** is explicit-origin allow-listing (from `CORS_ALLOWED_ORIGINS`), not a wildcard, so `allowCredentials(true)` stays safe.
- **CSRF** is disabled deliberately — this is a stateless, token-authenticated JSON API with no cookie-based session, so CSRF (which protects cookie-authenticated browser sessions) doesn't apply.
- **No stack traces leak to clients**: the global exception handler logs full exceptions server-side and returns a generic `INTERNAL_ERROR` message to the caller.

---

## API documentation

Once running, browse the interactive API docs:

- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- OpenAPI spec: `http://localhost:8080/v3/api-docs`

Every endpoint documents its required role, request/response shape, and possible status codes (200/201/204, 400, 401, 403, 404, 409).

### Example: register → login → browse → order

```bash
# Register (always created as CLIENT)
curl -X POST http://localhost:8080/auth/register \
  -H "Content-Type: application/json" \
  -d '{"name":"Jane Student","email":"jane@utown.dev","password":"Password123!","phoneNumber":"0900000000"}'

# Login
TOKEN=$(curl -s -X POST http://localhost:8080/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"jane@utown.dev","password":"Password123!"}' | jq -r .token)

# Browse restaurants (public, paginated)
curl http://localhost:8080/restaurants?page=0&size=20

# Everything else requires the token
curl http://localhost:8080/orders/my -H "Authorization: Bearer $TOKEN"
```

Demo accounts seeded by `V3__seed_demo_data.sql` (password for all: `Demo123!`):

| Email | Role |
| --- | --- |
| `admin@utown.dev` | `ADMIN` |
| `owner@utown.dev` | `RESTAURANT_ADMIN` (owns "Seoul BBQ") |
| `student@utown.dev` | `CLIENT` |

---

## Running locally

### Option A — Docker Compose (recommended)

Brings up MySQL + the app together; no local Java/Maven install needed.

```bash
docker compose up --build
```

This has been manually verified end-to-end: both the `app` and `mysql` containers reach a healthy state, Swagger UI loads, `POST /auth/register` + `/auth/login` work, a JWT obtained that way successfully authorizes a protected endpoint, and Flyway-backed database operations (create a restaurant, place an order, etc.) work against the containerized MySQL.

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- MySQL is available to the app on the Docker Compose network at `mysql:3306` (root / `root1111` by default — override via `.env`, see below)

Check container health at any time:
```bash
docker compose ps
```

Stop with `docker compose down` (add `-v` to also drop the MySQL volume and start from a clean database next time).

### Option B — Local MySQL + Maven

**Prerequisites:** Java 17+, Maven 3.9+, MySQL 8+.

```bash
mysql -u root -p -e "CREATE DATABASE utown;"

cp .env.example .env   # then edit .env with real values
export $(grep -v '^#' .env | xargs)   # or set these in your IDE run config

mvn spring-boot:run
```

Flyway runs automatically on startup and creates the schema (plus seed roles/demo data) on a fresh database.

### Environment variables

| Variable | Required | Description |
| --- | --- | --- |
| `DATABASE_URL` | yes | Full JDBC URL, including credentials (see `.env.example`) |
| `APP_JWT_SECRET` | yes | JWT signing secret, **minimum 32 bytes** — the app fails fast on startup otherwise |
| `APP_JWT_EXPIRATION_MS` | no (default `86400000`) | Token lifetime in ms |
| `CORS_ALLOWED_ORIGINS` | no (default `http://localhost:3000`) | Comma-separated allow-list |
| `MYSQL_DATABASE` / `MYSQL_ROOT_PASSWORD` | docker-compose only | Provisions the MySQL container |

### Profiles

| Profile | Used for | Database | Schema management |
| --- | --- | --- | --- |
| *(default)* | Production / Docker / Railway | MySQL via `DATABASE_URL` | Flyway migrations, `ddl-auto=validate` |
| `dev` | Optional local overrides | same as default | adds SQL/bind-parameter debug logging |
| `test` | `mvn test` | H2 in-memory | Flyway disabled, `ddl-auto=create-drop` |

---

## Testing

```bash
mvn test
```

36 tests, all runnable without Docker or a real MySQL instance (the `test` profile uses in-memory H2):

- **Unit tests** (`AuthServiceTest`, `OrderServiceTest`, `RoleServiceTest`) — Mockito-based, no Spring context; cover registration/login edge cases, order business rules (empty cart, closed restaurant, unavailable dish, address ownership mismatch, status-transition rules, cancel authorization), and the `RoleService.delete()` 404 fix.
- **Integration tests** (`AuthenticationFlowIntegrationTest`, `OrderFlowIntegrationTest`, `CartItemFlowIntegrationTest`, `RestaurantCategoryFlowIntegrationTest`) — full Spring context + MockMvc against H2, covering:
  - registration/login, validation errors, duplicate email, invalid credentials
  - unauthenticated requests get `401` (including a JWT for a since-deleted user), public endpoints work without a token
  - the full menu-setup → cart → order flow with real total-price calculation
  - role boundaries (a `CLIENT` can't create a restaurant) and ownership boundaries (one client can't cancel another's order, or read another user's notification)
  - cart-item quantity merge vs. new-row creation
  - full `RestaurantCategory` CRUD (create/get/update/delete, 404, 403)
- **`ConcurrencyIntegrationTest`** — real multithreaded regression tests for the two race-condition fixes above. Deliberately not built on the shared transactional test base (see the class Javadoc): it fires actual concurrent threads against a real, committed database state and asserts the resulting row counts, not just that a method was called.

CI (`.github/workflows/ci.yml`) runs `mvn clean verify` on every push/PR, then builds the Docker image as a second job to catch Dockerfile breakage. This is **Continuous Integration** — automated build, test, and Docker-build verification on every push/PR. There is no deployment step; see Known Limitations.

### Coverage

JaCoCo is configured (`mvn test` or `mvn verify` generates `target/site/jacoco/index.html`) with no enforced threshold — the goal here is an honest, visible number, not a gate tuned to pass. Current measured coverage: **~50% instructions, ~53% lines, ~34% branches.** Coverage is uneven by design: business-logic-heavy services (`OrderService`, `AuthService`, `RoleService`) and the security/authorization paths are well covered by the tests above; simple pass-through CRUD services and MapStruct-generated mapper implementations pull the aggregate number down without being meaningful gaps.

---

## Docker / deployment

- **`Dockerfile`** — multi-stage build (Maven build stage → `eclipse-temurin:17-jre` runtime), runs as a non-root user, ships only the built jar.
- **`docker-compose.yml`** — MySQL 8 + the app, each with a healthcheck, and the app's `depends_on` waits for MySQL to be actually healthy (not just started) before starting.
- **Actuator health check**: `GET /actuator/health` is public (no auth) and used as the container healthcheck — only `health`/`info` are exposed, nothing sensitive.
- Manually verified end-to-end: `docker compose up --build` → both containers reach `healthy` → Swagger UI, registration/login, a protected endpoint with a real JWT, and database writes all work against the containerized stack.

---

## Project structure

```
.
├── src/main/java/com/utown/utown_backend
├── src/main/resources
│   ├── db/migration            # Flyway SQL migrations
│   ├── application.properties         # default (prod-equivalent) config
│   ├── application-dev.properties        # optional local dev overrides
│   └── application-test.properties          # H2, used by the test suite
├── src/test/java/com/utown/utown_backend
├── Dockerfile
├── docker-compose.yml
├── .github/workflows/ci.yml
└── .env.example
```

---

## Known limitations

Scoped out deliberately, not overlooked — listed here rather than left for someone else to discover:

- **API versioning** — endpoints are unversioned (`/orders`, not `/api/v1/orders`). Fine for a single-consumer portfolio API; would need a strategy (URI or header-based) before a real external client depended on it.
- **Rate limiting** — no throttling on `/auth/login` or `/auth/register`.
- **Refresh tokens / token revocation** — JWTs are stateless and simply expire (default 24h); "logout" has no server-side effect. No blacklist.
- **Caching** — no Redis or in-process caching on the public restaurant/dish listing endpoints.
- **Production monitoring** — Actuator exposes `health`/`info` only; no Micrometer/Prometheus metrics, no structured/correlated logging, no dashboards.
- **CD** — CI verifies every push/PR (build, test, Docker image build); there's no deployment pipeline, registry push, or environment promotion.
- **Dish options in the order flow** — the `options`/`order_item_options` tables, entity model, and admin CRUD exist and are ownership-guarded, but a client can't select a dish option (e.g. "extra cheese") when placing an order yet — `OrderRequestDTO` only takes a delivery address.
- **H2 instead of Testcontainers/real MySQL in tests** — the `test` profile uses in-memory H2 so the suite runs anywhere without Docker; this is reliable for the application logic being tested but wouldn't catch a MySQL-specific dialect issue.

---

## License

Provided for portfolio and educational purposes.

## Author

**Htay Htay Mon** — Backend Developer specializing in Java, Spring Boot, and REST API development.
GitHub: https://github.com/htaymon
