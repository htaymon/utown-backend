# UTown Backend

A production-style Spring Boot REST API for a campus food ordering and delivery platform. Restaurant owners manage menus and incoming orders, students browse restaurants, build a cart, place orders, and track delivery status — all behind JWT-authenticated, role-based access control.

![Java](https://img.shields.io/badge/Java-17-orange?logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.5-brightgreen?logo=springboot)
![MySQL](https://img.shields.io/badge/MySQL-8-blue?logo=mysql)
![Flyway](https://img.shields.io/badge/Flyway-migrations-CC0200?logo=flyway)
![Docker](https://img.shields.io/badge/Docker-Compose-2496ED?logo=docker)
![Maven](https://img.shields.io/badge/Build-Maven-red?logo=apachemaven)

---

## Overview

UTown models the core of a food-delivery product end to end:

- **Users & auth** — registration, login, BCrypt password hashing, JWT issuance/validation, role-based authorization (`CLIENT`, `RESTAURANT_ADMIN`, `ADMIN`).
- **Restaurants & menus** — restaurant/dish CRUD, categories, dish options, delivery areas, work schedules, open/closed status.
- **Ordering** — cart → cart items → order, server-computed totals, order status lifecycle, ownership-scoped access, cancellation rules.
- **Notifications & staff scheduling** — per-user notifications, restaurant work schedules.

The project is intentionally scoped to what a real food-ordering backend needs — no speculative features bolted on to look bigger.

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
├── controller       # REST endpoints (thin)
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
| Testing | JUnit 5, Mockito, MockMvc, AssertJ, H2 (in-memory) |
| Build | Maven |
| Containerization | Docker, Docker Compose |
| CI | GitHub Actions |

---

## Database design

16 tables modeling the domain: `roles`, `users`, `restaurant_categories`, `restaurants`, `dish_categories`, `dishes`, `options`, `addresses`, `delivery_areas`, `work_schedules`, `carts`, `cart_items`, `orders`, `order_items`, `order_item_options`, `notifications`.

Notable design decisions:

- **Schema is owned by Flyway**, not Hibernate. `spring.jpa.hibernate.ddl-auto=validate` — the app refuses to start if the entities and the migrated schema disagree, instead of silently patching the schema (`ddl-auto=update` in production is exactly the kind of thing that causes 2am incidents).
- Every foreign key has a matching index (`idx_dishes_restaurant_id`, `idx_orders_user_id`, etc.) since every one of them is used in a `WHERE`/join in the service layer.
- Cascades follow the JPA entity model: `order_items`/`order_item_options`/`cart_items` are `ON DELETE CASCADE` from their aggregate root (matching `cascade = ALL, orphanRemoval = true` on `Order`/`OrderItem`/`Cart`); everything else defaults to `RESTRICT` since no entity cascades deletes in that direction.
- `orders.order_no` and `carts.user_id` (one active cart per user) are unique-constrained.
- List endpoints that can grow without bound (`/dishes`, `/restaurants`, `/orders/my`, `/orders/restaurant/{id}`) are paginated and use `@EntityGraph` to fetch their associations in one query instead of N+1-ing through lazy relations. Small reference/config data (categories, roles, delivery areas, work schedules) intentionally isn't paginated — it's not going to have thousands of rows.

Migrations live in [`src/main/resources/db/migration`](src/main/resources/db/migration):

| File | Purpose |
| --- | --- |
| `V1__init_schema.sql` | Full schema: tables, FKs, indexes |
| `V2__seed_roles.sql` | Seeds `CLIENT`, `RESTAURANT_ADMIN`, `ADMIN` (registration depends on `CLIENT` existing) |
| `V3__seed_demo_data.sql` | Optional demo accounts/restaurant/menu so the API is explorable immediately after setup |

---

## Security

- **JWT auth**: stateless (`SessionCreationPolicy.STATELESS`), `Authorization: Bearer <token>`. Token carries the user's email (subject) and role; expiry is configurable.
- **Password hashing**: BCrypt via Spring Security's `PasswordEncoder`.
- **Role-based access control**: `@EnableMethodSecurity` + `@PreAuthorize` on every mutating endpoint.
- **Ownership checks**: role alone isn't enough to authorize most actions — e.g. `@PreAuthorize("hasRole('RESTAURANT_ADMIN')")` only proves *a* restaurant admin is calling; the service layer (`RestaurantAccessGuard`) then verifies *this* admin owns *this* restaurant before allowing a dish/schedule/delivery-area mutation. The same pattern protects a user's own cart, addresses, orders and notifications from being read or modified by anyone else.
- **401 vs 403 are distinct**: a custom `AuthenticationEntryPoint` returns `401 UNAUTHENTICATED` with the standard JSON error body for missing/invalid credentials; `@PreAuthorize`/ownership failures return `403 ACCESS_DENIED`. (Spring Security's default behavior here is a bare, bodiless `403` for both cases unless you configure this explicitly — a common gap in hand-rolled Spring Security setups.)
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

- API: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui/index.html`
- MySQL is exposed on `localhost:3306` (root / `root1111` by default — override via `.env`, see below)

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

29 tests, all runnable without Docker or a real MySQL instance (the `test` profile uses in-memory H2):

- **Unit tests** (`AuthServiceTest`, `OrderServiceTest`) — Mockito-based, no Spring context; cover registration/login edge cases and order business rules (empty cart, closed restaurant, unavailable dish, address ownership mismatch, status-transition rules, cancel authorization).
- **Integration tests** (`AuthenticationFlowIntegrationTest`, `OrderFlowIntegrationTest`) — full Spring context + MockMvc against H2, covering:
  - registration/login, validation errors, duplicate email, invalid credentials
  - unauthenticated requests get `401`, public endpoints work without a token
  - the full menu-setup → cart → order flow with real total-price calculation
  - role boundaries (a `CLIENT` can't create a restaurant) and ownership boundaries (one client can't cancel another's order, or read another user's notification)

CI (`.github/workflows/ci.yml`) runs `mvn clean verify` on every push/PR, then builds the Docker image as a second job to catch Dockerfile breakage.

---

## Docker / deployment

- **`Dockerfile`** — multi-stage build (Maven build stage → `eclipse-temurin:17-jre-alpine` runtime), runs as a non-root user, ships only the built jar.
- **`docker-compose.yml`** — MySQL 8 + the app, with healthchecks so the app waits for MySQL to actually be ready before starting.
- **Actuator health check**: `GET /actuator/health` is public (no auth) and used as the container healthcheck — only `health`/`info` are exposed, nothing sensitive.

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

## Future improvements

- Testcontainers-backed integration tests against real MySQL (future improvement)
- Redis caching for hot read paths (restaurant/dish listings)
- Refresh tokens / token revocation (current JWTs are stateless and simply expire)
- Wiring dish options into the cart/order request payloads end-to-end (the `options`/`order_item_options` tables and admin CRUD exist; client-side selection during ordering doesn't yet)
- Rate limiting on `/auth/login`

---

## License

Provided for portfolio and educational purposes.

## Author

**Htay Htay Mon** — Backend Developer specializing in Java, Spring Boot, and REST API development.
GitHub: https://github.com/htaymon
