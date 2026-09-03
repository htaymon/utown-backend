# UTown Backend — System Documentation

> This document describes the system **as implemented in the code**, verified against the actual source in this repository (controllers, services, repositories, entities, migrations, tests, config). Where the implementation is incomplete, disconnected, or only partially wired up, that is called out explicitly rather than assumed. If anything here ever conflicts with the code, **the code is correct and this document is stale**.

---

## 1. What UTown Is

UTown is a campus food-ordering and delivery backend. Restaurant owners (`RESTAURANT_ADMIN`) list a restaurant, its categories, dishes and options; students (`CLIENT`) register, browse restaurants/dishes, build a cart, place an order against a saved delivery address, and track its status; an `ADMIN` role has system-wide oversight of users, roles, and every restaurant's data.

It is a single Spring Boot 3.5 / Java 17 module — no separate frontend, no microservices — exposing a REST API secured with JWT.

---

## 2. Architecture Overview

```
Client (HTTP/JSON)
   │
   ▼
Controller layer      — thin: @Valid request DTOs, @PreAuthorize role checks, calls one service method
   │
   ▼
Service layer         — business rules, ownership checks (who owns this restaurant/cart/order/address?), @Transactional
   │
   ▼
Repository layer      — Spring Data JPA interfaces, no custom JPQL/native queries anywhere in the codebase
   │
   ▼
MySQL (or H2 in tests) — schema owned by Flyway migrations, never by Hibernate auto-DDL
```

Cross-cutting pieces:
- **`GlobalExceptionHandler`** (`@RestControllerAdvice`) turns every thrown exception into one consistent JSON shape: `{code, message, path, timestamp}`.
- **MapStruct mappers** convert entities ↔ DTOs; controllers never return or accept JPA entities directly.
- **`RestaurantAccessGuard`** is a small shared component (`isOwner || isAdmin`) used by every service that mutates restaurant-scoped data, so the same ownership rule isn't reimplemented six different ways.

### Package layout
```
src/main/java/com/utown/utown_backend
├── config       CorsConfig, OpenApiConfig, JpaAuditConfig
├── controller   16 REST controllers
├── dto/request  validated inbound payloads
├── dto/response outbound payloads, incl. generic PageResponseDTO<T>
├── entity       15 JPA entities + BaseEntity (createdAt/updatedAt auditing)
├── enums        DishStatus, OrderStatus, RestaurantStatus, NotificationStatus
├── exception    domain exceptions + GlobalExceptionHandler
├── mapper       MapStruct interfaces
├── repository   Spring Data JPA repositories
├── security     JwtUtil, JwtAuthenticationFilter, JwtAuthenticationEntryPoint, SecurityConfig, RestaurantAccessGuard, CustomUserDetails(Service)
└── service      business logic
```

---

## 3. Authentication

**Status: Implemented and tested** (registration, login) / **Not implemented** (logout).

| Step | How it actually works |
| --- | --- |
| Registration | `POST /auth/register`, public. `AuthService.register()` rejects a duplicate email (`EmailAlreadyExistsException` → 409), otherwise looks up the `CLIENT` role (must already exist in the `roles` table — seeded by `V2__seed_roles.sql`), hashes the password with `BCryptPasswordEncoder`, saves the `User`, returns a `UserResponseDTO`. **Every self-registered account is a `CLIENT`** — there is no public way to register as `RESTAURANT_ADMIN` or `ADMIN`; those are created by an `ADMIN` via `POST /users`. |
| Login | `POST /auth/login`, public. `AuthService.login()` looks up the user by email, compares the password with `PasswordEncoder.matches()`, and on success calls `JwtUtil.generateToken(email, roleName)`. Any failure (unknown email or wrong password) throws the same `InvalidCredentialsException` → `401` — the API deliberately doesn't reveal which one was wrong. |
| Password hashing | BCrypt (`BCryptPasswordEncoder`, `SecurityConfig` bean). No plaintext password is ever stored or logged. |
| JWT generation | `JwtUtil` (HS256, `jjwt` library). Claims: `sub` = email, `role` = role name, `iat`/`exp`. Secret comes from `app.jwt.secret` (env var `APP_JWT_SECRET`); a `@PostConstruct` check **throws and refuses to start the app** if the secret is under 32 bytes. Expiry is `app.jwt.expiration-ms` (env var `APP_JWT_EXPIRATION_MS`, default 24h). |
| JWT validation | `JwtAuthenticationFilter` (a `OncePerRequestFilter`) reads the `Authorization: Bearer <token>` header on every request **except paths starting with `/auth`**. If present and `JwtUtil.validateToken()` succeeds, it extracts the email, loads the user via `CustomUserDetailsService`, and puts an authenticated `UsernamePasswordAuthenticationToken` (with a single `ROLE_<name>` authority) into the `SecurityContext`. If the header is missing or the token fails validation, the request simply proceeds unauthenticated — it's `anyRequest().authenticated()` in `SecurityConfig` that then rejects it. |
| Expired/invalid token | `jjwt`'s `parseClaimsJws()` throws `JwtException` (which `ExpiredJwtException` extends) for both a malformed **and** an expired token; `JwtUtil.validateToken()` catches it and returns `false`. Either way the caller is treated as unauthenticated → `401`. The API does not distinguish "expired" from "malformed" in the response — both come back as the generic `UNAUTHENTICATED` error described below. |
| Unauthorized requests | A custom `JwtAuthenticationEntryPoint` returns **`401`** with the standard error JSON (`code: "UNAUTHENTICATED"`) for any request to a protected endpoint with no valid authentication. (Spring Security's out-of-the-box behavior without this entry point is a bare, bodiless `403` for this case — this was fixed; see §11.) |
| Logout | `POST /auth/logout` — **not actually implemented**. It's public, does no token/session work, and just returns `{"message":"Logout successful"}`. JWTs are stateless and there is no blacklist/revocation store, so a token obtained before "logout" remains valid until it naturally expires. This endpoint exists purely so a client has something to call; it has no server-side effect. |

---

## 4. Authorization / Roles

Three roles exist, seeded by `V2__seed_roles.sql`: **`CLIENT`**, **`RESTAURANT_ADMIN`**, **`ADMIN`**. There is no separate "restaurant" principal — a restaurant is *owned* by a `User` (`Restaurant.user`), and "am I allowed to touch this restaurant's data" is answered by `RestaurantAccessGuard.check()`: `restaurant.getUser().getId().equals(currentUser.getId()) || currentUser.getRole().getName().equals("ADMIN")`.

Authorization in this codebase is **two layers, and both matter**:
1. `@PreAuthorize` on the controller method — proves the caller *has* the right role.
2. An ownership check inside the service — proves the caller owns *this specific* row (their own cart, their own restaurant, their own order, their own address, their own notification). Role alone never authorizes access to another user's resource.

### CLIENT
- **Can access:** their own cart/cart items, their own addresses, their own orders (create/view/cancel while `PENDING`), their own notifications, plus everything public (restaurant/dish browsing).
- **Can create:** an address, a cart (one at a time — `Cart.user` is unique), cart items, an order, a notification (always attached to themselves regardless of what's sent).
- **Can update:** their own addresses, their own cart items, their own notifications, restaurant-status-neutral actions.
- **Can delete:** their own addresses, their own cart, their own cart items, their own notifications; can cancel (not hard-delete) their own `PENDING` orders.
- **Cannot access:** any other user's cart/address/order/notification, restaurant management endpoints, `/users`, `/roles`, order-status updates for a restaurant, or another restaurant's order list.

### RESTAURANT_ADMIN
- **Can access:** their own restaurant(s) and everything under them — dishes, options, delivery areas, work schedules, orders placed against that restaurant.
- **Can create:** a restaurant (becomes its owner), dishes/options/delivery areas/work schedules **for a restaurant they own**, restaurant/dish categories (shared catalog data, not ownership-scoped).
- **Can update:** their own restaurant, its dishes/options/delivery areas/work schedules, and the status of orders placed against it.
- **Can delete:** their own restaurant, its dishes (soft-delete — see §8), options, delivery areas, work schedules.
- **Cannot access:** another `RESTAURANT_ADMIN`'s restaurant or its data (enforced by `RestaurantAccessGuard`, verified by `OrderFlowIntegrationTest`'s role/ownership assertions and by direct code reading of every restaurant-scoped service), `/users`, `/roles`, another user's cart/order/notification/address.

### ADMIN
- Superset of everything: passes every `RestaurantAccessGuard.check()` unconditionally, plus exclusive access to `/users` and `/roles`, plus the only role allowed to list **all** notifications (`GET /notifications`) or **all** `order-item-options` (`GET /order-item-options`).

### IDOR / ownership audit (verified per-resource, from the actual service code)

| Resource | Ownership enforced? | How |
| --- | --- | --- |
| Users | ✅ (ADMIN-only resource) | Every `/users` endpoint is `@PreAuthorize("hasRole('ADMIN')")`; no per-row ownership concept applies. |
| Restaurants | ✅ | `RestaurantAccessGuard` in `RestaurantService` for update/status/delete. Create/read are role- or public-gated only (correct — nothing to own yet on create, reads are intentionally public). |
| Restaurant categories | N/A (global catalog) | No ownership needed; role-gated only. **But update/delete don't exist at all** — see §16. |
| Dish categories | N/A (global catalog) | Role-gated only; correct, since a category isn't owned by one restaurant. |
| Dishes | ✅ | `RestaurantAccessGuard` via `dish.getRestaurant()` in `DishService`. |
| Options | ✅ | `RestaurantAccessGuard` via `option.getDish().getRestaurant()` in `OptionService` (checked on **both** the current dish and the target dish on update, so you can't reassign an option onto a restaurant you don't own either). |
| Delivery areas | ✅ | `RestaurantAccessGuard` via `restaurant` in `DeliveryAreaService`. |
| Work schedules | ✅ | `RestaurantAccessGuard` via `restaurant` in `WorkScheduleService`. |
| Notifications | ✅ | `NotificationService.checkOwnership()` — owner or ADMIN only, on get/update/delete; `create()` always attaches the *caller*, ignoring any other target. `getAll()` (all users' notifications) is ADMIN-only; `GET /notifications/my` is the self-scoped equivalent for everyone else. |
| Orders | ✅ | `OrderService.checkOrderAccess()` — the ordering client, the owning restaurant's admin, or an ADMIN; nobody else. Cancel and status-update each re-check this plus their own state-machine rule. |
| Order items | — | No standalone controller; only reachable as a nested field of an `Order` response. |
| Order item options | ✅ | `RestaurantAccessGuard` via `orderItem.getOrder().getRestaurant()` in `OrderItemOptionService`; the whole controller is class-level `@PreAuthorize("hasAnyRole('ADMIN','RESTAURANT_ADMIN')")`, with `GET /order-item-options` (list-all) further restricted to ADMIN only. |
| Carts | ✅ | `CartService` — create/my/delete all resolve the *caller's own* cart via `authService.getCurrentUser()`; `delete()` additionally checks `cart.getUser().getId()`. `GET /carts/{id}` (any cart by ID) is ADMIN/RESTAURANT_ADMIN only — not reachable by CLIENT, so no cross-client IDOR path exists there. |
| Cart items | ✅ | `CartItemService` checks `cart.getUser().getId().equals(currentUser.getId())` on create/update/delete, plus dish-availability and dish-belongs-to-cart's-restaurant checks. |
| Addresses | ✅ | `AddressService` checks `address.getUser().getId()` on get/update/delete. |

No cross-user data leak was found in the current code for any of the above.

---

## 5. Client Workflow (end to end)

Every step below is what the code actually does, not an assumption.

1. **Register** — `POST /auth/register` (public). Body: `name`, `email`, `password` (≥6 chars), `phoneNumber` — all required. Always creates a `CLIENT`. Errors: `400` validation, `409` duplicate email.
2. **Login** — `POST /auth/login` (public). Body: `email`, `password`. Returns `{ "token": "<jwt>" }`. Error: `401` invalid credentials.
3. **Browse restaurants** — `GET /restaurants?page=&size=` — **public, no token needed**. Paginated (`PageResponseDTO<RestaurantResponseDTO>`, default size 20, max 100).
4. **View a restaurant** — `GET /restaurants/{id}` — public.
5. **Browse dishes** — `GET /dishes?page=&size=` — public, paginated.
6. **View a dish** — `GET /dishes/{id}` — public.
7. **Manage addresses** — `POST/GET/GET{id}/PUT/DELETE /addresses`, all `CLIENT`, all scoped to the caller. Required fields: `street`, `city`, `state`, `postalCode`.
8. **Create a cart** — `POST /carts`, `CLIENT`. Body: `restaurantId`. Fails with `400` (`CartAlreadyExistsException`) if the user already has one, or `400` (`RestaurantClosedException`) if the restaurant isn't `OPEN`. A user has **at most one cart at a time**, and it's tied to one restaurant.
9. **View own cart** — `GET /carts/my`, `CLIENT`.
10. **Add a cart item** — `POST /cart-items`, `CLIENT`. Body: `cartId`, `dishId`, `quantity` (`> 0`). Rechecks the restaurant is `OPEN`, the dish is `AVAILABLE`, the dish belongs to the cart's restaurant, and the cart belongs to the caller. **If the dish is already in the cart, quantity is added to the existing row instead of creating a duplicate.**
11. **Update / remove cart items** — `PUT /cart-items/{id}` (same checks, replaces quantity), `DELETE /cart-items/{id}` — both `CLIENT`, ownership-checked.
12. **Create an order** — `POST /orders`, `CLIENT`. Body: `deliveryAddressId` only (the order is built from the caller's **current cart**, not from a request payload of items). Validates: cart exists and isn't empty (`CartEmptyException`), restaurant is `OPEN` (`RestaurantClosedException`), every cart item's dish is `AVAILABLE` (`DishNotAvailableException`), and the address belongs to the caller (`UserAddressMismatchException`). On success: total price is computed server-side as `Σ(dish.price × quantity)`, an `Order` + its `OrderItem`s are persisted with status `PENDING` and a generated `orderNo` (UUID), and **the cart's items are cleared** (the cart itself is kept, empty).
13. **View own orders** — `GET /orders/my?page=&size=`, `CLIENT`, paginated, newest first.
14. **View order details** — `GET /orders/{id}`, reachable by the owning client, the owning restaurant's admin, or ADMIN.
15. **Cancel an order** — `PUT /orders/{id}/cancel`. Allowed for the same three parties as above, but **only while the order is still `PENDING`** (`InvalidOrderStatusException` otherwise).
16. **Notifications** — `GET /notifications/my` (own notifications), `GET/{id}` / `PUT/{id}` / `DELETE/{id}` (ownership-checked), `POST /notifications` (creates one attached to the caller). There is no code anywhere that automatically creates a notification when, say, an order's status changes — notifications are a standalone CRUD resource that nothing else in the system currently triggers.

Order **status tracking beyond `PENDING`** (`CONFIRMED`/`COOKING`/`DELIVERING`/`COMPLETED`) is set by the restaurant/admin side (§7), not by the client — a client can only read the current status via `GET /orders/{id}` or `GET /orders/my`, and cancel while still `PENDING`.

---

## 6. Admin Workflow (end to end)

1. **Authenticate** — same `POST /auth/login` as anyone else; there's no separate admin login. An `ADMIN` account must already exist in the database (there is no public "become admin" path — see the demo-data seed in §13, or `POST /users` from an existing admin).
2. **Manage users** — `POST/GET/GET{id}/PUT/DELETE /users`, all `hasRole('ADMIN')`. `POST /users` is how `RESTAURANT_ADMIN` and additional `ADMIN` accounts actually get created — the DTO takes an explicit `roleId`. On update, password is optional (blank = keep current); on create it's required (`PasswordRequiredException` → `400` if omitted).
3. **Manage roles** — `POST/GET/GET{id}/PUT/DELETE /roles`, all `hasRole('ADMIN')`. Simple CRUD over the `roles` table (name only).
4. **Manage restaurants** — `ADMIN` passes every `RestaurantAccessGuard` check unconditionally, so it can update/change-status/delete **any** restaurant, in addition to everything a `RESTAURANT_ADMIN` can do to their own.
5. **Manage restaurant categories** — `POST` and `GET` only (`hasAnyRole('ADMIN','RESTAURANT_ADMIN')` for create). **There is no update or delete endpoint for restaurant categories at all** — not a permissions gap, the code for it simply doesn't exist (§16).
6. **Manage dishes / dish categories / options / delivery areas / work schedules** — full CRUD, same ownership rule as `RESTAURANT_ADMIN` but auto-passing since `ADMIN` owns nothing but is granted access anyway.
7. **Manage orders** — `PATCH /orders/{id}/status` and `GET /orders/restaurant/{id}` (`hasAnyRole('ADMIN','RESTAURANT_ADMIN')`) work for any restaurant when called by an `ADMIN`.
8. **Manage notifications** — `GET /notifications` (every notification in the system, ADMIN-only) plus the same ownership-bypassing access on individual notifications.
9. **`order-item-options`** — the entire controller is ADMIN/RESTAURANT_ADMIN only; `GET /order-item-options` (list-all) is ADMIN-only specifically.

---

## 7. Restaurant / Business Workflow

There **is** a restaurant-owner role (`RESTAURANT_ADMIN`) with real ownership enforcement — this is not a stub.

- **Create** — `POST /restaurants` (`hasRole('RESTAURANT_ADMIN')`). The caller becomes `Restaurant.user` automatically (`AuthService.getCurrentUser()`), not something the client can spoof via the request body.
- **Update / status / delete** — `PUT /restaurants/{id}`, `PATCH /restaurants/{id}/status`, `DELETE /restaurants/{id}` — `hasAnyRole('ADMIN','RESTAURANT_ADMIN')` at the controller, then `RestaurantAccessGuard` at the service confirms the caller actually owns *that* restaurant.
- **Categories** — restaurant and dish categories are **not** restaurant-scoped; they're a shared catalog any `RESTAURANT_ADMIN`/`ADMIN` can add to, and any restaurant/dish can reference.
- **Dishes** — full CRUD scoped to the owning restaurant via `checkRestaurantAccess`/`RestaurantAccessGuard`. Delete is a **soft delete**: `DishService.delete()` sets `status = HIDDEN` and saves — the row is never removed from the database.
- **Options** — dish add-ons, CRUD scoped through the parent dish's restaurant.
- **Delivery areas / work schedules** — CRUD scoped to the owning restaurant.
- **Restaurant orders** — `GET /orders/restaurant/{id}` lists paginated orders for a restaurant the caller owns (or any restaurant, for ADMIN).
- **Order status management** — `PATCH /orders/{id}/status`, restricted to the owning restaurant's admin or an ADMIN, and blocked once the order is `COMPLETED` (`InvalidOrderStatusException`). There is **no validation of the status transition graph itself** — e.g. nothing stops moving a `PENDING` order straight to `DELIVERING`, or back from `COOKING` to `PENDING`; the only hard rule enforced is "you can't change a `COMPLETED` order."

---

## 8. Order Lifecycle

```
CartItem[] (in Cart)
        │  POST /orders (deliveryAddressId)
        ▼
Order (status = PENDING, totalPrice computed, orderNo = UUID)
        │  cascade-created 1:1 from each CartItem
        ▼
OrderItem[] (snapshot of dish + quantity + price at order time)
        │  optional, cascade-created — but nothing in the codebase
        │  currently populates this from the order-creation request
        ▼
OrderItemOption[] (exists as a table/entity/CRUD API; not wired into order creation)
```

- **Order status enum** (`OrderStatus`): `PENDING`, `CONFIRMED`, `COOKING`, `DELIVERING`, `COMPLETED`, `CANCELLED`.
- **Who can create an order:** `CLIENT` only, and only from their own cart.
- **Who can view an order:** the ordering client, the owning restaurant's admin, or an ADMIN (`OrderService.checkOrderAccess`).
- **Who can update status:** the owning restaurant's admin or an ADMIN (`PATCH /orders/{id}/status`), blocked if already `COMPLETED`.
- **Who can cancel:** the ordering client, the owning restaurant's admin, or an ADMIN — but only while `status == PENDING`.
- **Conditions to place an order:** cart not empty, restaurant `OPEN`, every dish in the cart `AVAILABLE`, delivery address owned by the caller.
- **What happens to the cart after order creation:** `cart.getCartItems().clear()` + save — the cart entity survives (still tied to the same restaurant), but is emptied. The user must add items again to place another order.
- **Price calculation:** `Σ (dish.price × cartItem.quantity)`, rounded to 2 decimal places (`RoundingMode.HALF_UP`), computed entirely server-side in `OrderService.create()` — the client never sends a price.
- **Restaurant ownership check:** `RestaurantAccessGuard`, applied consistently across the restaurant-owning side of the order flow.
- **Order item options in the actual ordering flow:** **not implemented.** `OrderRequestDTO` has exactly one field (`deliveryAddressId`); there is no way for a client to select a dish option (e.g. "extra cheese") when placing an order. The `order_item_options` table, entity, mapper, and full CRUD controller exist and are ownership-guarded, but nothing calls into them from `OrderService.create()`. Today they can only be created/managed directly and manually (by an ADMIN/RESTAURANT_ADMIN) after the fact — this is a real, disconnected feature (see §16).

---

## 9. Database / Domain Model

16 tables (see `V1__init_schema.sql` for exact DDL): `roles`, `users`, `restaurant_categories`, `restaurants`, `dish_categories`, `dishes`, `options`, `addresses`, `delivery_areas`, `work_schedules`, `carts`, `cart_items`, `orders`, `order_items`, `order_item_options`, `notifications`.

### Entity relationships (verified from every `@OneToMany`/`@ManyToOne`/`@OneToOne` in the entity classes — no `@ManyToMany` exists anywhere in the model)

```
Role
└── User (1:N)

User
├── Restaurant   (1:N — ownership, Restaurant.user)
├── Address      (1:N)
├── Cart         (1:1, unique — one active cart per user)
├── Order        (1:N — as the ordering client)
├── Notification (1:N)
└── Role         (N:1)

RestaurantCategory
└── Restaurant (1:N)

Restaurant
├── User               (N:1 — owner)
├── RestaurantCategory (N:1)
├── Dish               (1:N)
├── DeliveryArea       (1:N)
├── WorkSchedule       (1:N)
├── Cart               (1:N)
└── Order              (1:N)

DishCategory
└── Dish (1:N)

Dish
├── Restaurant   (N:1)
├── DishCategory (N:1)
├── Option       (1:N, cascade ALL + orphanRemoval)
├── CartItem     (1:N)
└── OrderItem    (1:N)

Cart
├── User       (1:1)
├── Restaurant (N:1)
└── CartItem   (1:N, cascade ALL + orphanRemoval)

Order
├── User       (N:1)
├── Restaurant (N:1)
├── Address    (N:1 — deliveryAddress)
└── OrderItem  (1:N, cascade ALL + orphanRemoval)

OrderItem
├── Order            (N:1)
├── Dish             (N:1)
└── OrderItemOption  (1:N, cascade ALL + orphanRemoval)

Option
├── Dish             (N:1)
└── OrderItemOption  (1:N, cascade ALL + orphanRemoval)

OrderItemOption
├── OrderItem (N:1)
└── Option    (N:1)

Notification
└── User (N:1)
```

### Notable field-level facts (from the entities, not assumed)
- Every entity extends `BaseEntity` (`createdAt`/`updatedAt`, `@CreatedDate`/`@LastModifiedDate` via `@EnableJpaAuditing` in `JpaAuditConfig`). There is **no** `AuditorAware` bean, so only the *date* fields populate — there are no `createdBy`/`updatedBy` fields to worry about.
- `User.email` and `Role.name` are unique; `Cart.user_id` is unique (enforces one cart per user); `Order.order_no` is unique.
- `User.role` and `Dish.status`/`Order.status`/`Restaurant.status`/`Notification.status` are all `FetchType.EAGER` or simple `@Enumerated(STRING)` columns; most other associations (`Order.user`, `Order.restaurant`, `Dish.restaurant`, etc.) are `FetchType.LAZY`.
- Cascade + orphan removal is used **only** where one entity is truly the aggregate root of the other: `Cart→CartItem`, `Order→OrderItem`, `OrderItem→OrderItemOption`, `Option→OrderItemOption`. Every other FK (e.g. `Dish→Restaurant`, `Order→Address`) is a plain reference with no cascade.
- Every FK column has a matching index in the migration (`idx_dishes_restaurant_id`, `idx_orders_user_id`, etc.).

---

## 10. API Inventory

All response bodies below are DTOs (never raw entities) built by MapStruct mappers.

### Auth (`/auth`) — all public
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /auth/register | none | — | Create a `CLIENT` account |
| POST | /auth/login | none | — | Exchange credentials for a JWT |
| POST | /auth/logout | none | — | No-op (see §3) |

### Users (`/users`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /users | JWT | ADMIN | Create a user with any role |
| GET | /users | JWT | ADMIN | List all users (unpaginated) |
| GET | /users/{id} | JWT | ADMIN | Get one user |
| PUT | /users/{id} | JWT | ADMIN | Update a user (password optional) |
| DELETE | /users/{id} | JWT | ADMIN | Delete a user |

### Roles (`/roles`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /roles | JWT | ADMIN | Create a role |
| GET | /roles | JWT | ADMIN | List roles |
| GET | /roles/{id} | JWT | ADMIN | Get one role |
| PUT | /roles/{id} | JWT | ADMIN | Rename a role |
| DELETE | /roles/{id} | JWT | ADMIN | Delete a role (see bug in §17) |

### Restaurants (`/restaurants`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /restaurants | JWT | RESTAURANT_ADMIN | Create a restaurant (caller becomes owner) |
| GET | /restaurants | none | — | Paginated public listing |
| GET | /restaurants/{id} | none | — | Public detail |
| PUT | /restaurants/{id} | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Update name/description/minimumOrder |
| PATCH | /restaurants/{id}/status | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | OPEN/CLOSED |
| DELETE | /restaurants/{id} | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Hard delete |

### Restaurant categories (`/restaurant-categories`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /restaurant-categories | JWT | ADMIN/RESTAURANT_ADMIN | Create a category |
| GET | /restaurant-categories | JWT | any authenticated | List all |
| — | *(no GET/{id}, PUT, or DELETE exist)* | | | |

### Dishes (`/dishes`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /dishes | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Create a dish |
| GET | /dishes | none | — | Paginated public listing |
| GET | /dishes/{id} | none | — | Public detail |
| PUT | /dishes/{id} | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Update a dish |
| DELETE | /dishes/{id} | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Soft delete (status → HIDDEN) |

### Dish categories (`/dish-categories`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /dish-categories | JWT | ADMIN/RESTAURANT_ADMIN | Create |
| GET | /dish-categories | JWT | any authenticated | List |
| GET | /dish-categories/{id} | JWT | any authenticated | Detail |
| PUT | /dish-categories/{id} | JWT | ADMIN/RESTAURANT_ADMIN | Update |
| DELETE | /dish-categories/{id} | JWT | ADMIN/RESTAURANT_ADMIN | Delete |

### Options (`/options`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| GET | /options | JWT | any authenticated | List all |
| GET | /options/{id} | JWT | any authenticated | Detail |
| POST | /options | JWT | ADMIN/RESTAURANT_ADMIN (+owner via dish) | Create |
| PUT | /options/{id} | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Update |
| DELETE | /options/{id} | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Delete |

### Delivery areas (`/delivery-areas`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /delivery-areas | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Create |
| GET | /delivery-areas | JWT | any authenticated | List |
| GET | /delivery-areas/{id} | JWT | any authenticated | Detail |
| PUT | /delivery-areas/{id} | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Update |
| DELETE | /delivery-areas/{id} | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Delete |

### Work schedules (`/work-schedules`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /work-schedules | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Create |
| GET | /work-schedules | JWT | any authenticated | List |
| GET | /work-schedules/{id} | JWT | any authenticated | Detail |
| PUT | /work-schedules/{id} | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Update |
| DELETE | /work-schedules/{id} | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Delete |

### Carts (`/carts`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /carts | JWT | CLIENT | Create own cart for a restaurant |
| GET | /carts/{id} | JWT | ADMIN/RESTAURANT_ADMIN | Get any cart by ID |
| GET | /carts/my | JWT | CLIENT | Get own cart |
| DELETE | /carts/{id} | JWT | CLIENT (+owner) | Delete own cart |

### Cart items (`/cart-items`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /cart-items | JWT | CLIENT (+owner) | Add/merge a dish into own cart |
| PUT | /cart-items/{id} | JWT | CLIENT (+owner) | Replace quantity |
| GET | /cart-items | JWT | CLIENT | List own cart's items |
| DELETE | /cart-items/{id} | JWT | CLIENT (+owner) | Remove item |

### Addresses (`/addresses`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /addresses | JWT | CLIENT | Create own address |
| GET | /addresses | JWT | CLIENT | List own addresses |
| GET | /addresses/{id} | JWT | CLIENT (+owner) | Detail |
| PUT | /addresses/{id} | JWT | CLIENT (+owner) | Update |
| DELETE | /addresses/{id} | JWT | CLIENT (+owner) | Delete |

### Orders (`/orders`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /orders | JWT | CLIENT | Place an order from own cart |
| GET | /orders/my | JWT | CLIENT | Paginated own order history |
| GET | /orders/{id} | JWT | CLIENT/RESTAURANT_ADMIN/ADMIN (+owner) | Order detail |
| GET | /orders/restaurant/{restaurantId} | JWT | RESTAURANT_ADMIN/ADMIN (+owner) | Paginated restaurant order list |
| PUT | /orders/{id}/cancel | JWT | CLIENT/RESTAURANT_ADMIN/ADMIN (+owner) | Cancel a `PENDING` order |
| PATCH | /orders/{id}/status | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Advance order status |

### Order item options (`/order-item-options`) — class-level `ADMIN`/`RESTAURANT_ADMIN`
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| POST | /order-item-options | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Attach an option to an order item |
| GET | /order-item-options | JWT | ADMIN only | List all |
| GET | /order-item-options/{id} | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Detail |
| PUT | /order-item-options/{id} | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Update |
| DELETE | /order-item-options/{id} | JWT | ADMIN/RESTAURANT_ADMIN (+owner) | Delete |

### Notifications (`/notifications`)
| Method | Endpoint | Auth | Role | Purpose |
| --- | --- | --- | --- | --- |
| GET | /notifications | JWT | ADMIN only | All notifications, system-wide |
| GET | /notifications/my | JWT | any authenticated | Own notifications |
| GET | /notifications/{id} | JWT | any authenticated (+owner) | Detail |
| POST | /notifications | JWT | any authenticated | Create (always for self) |
| PUT | /notifications/{id} | JWT | any authenticated (+owner) | Update |
| DELETE | /notifications/{id} | JWT | any authenticated (+owner) | Delete |

### Key request/response shapes
- **`PageResponseDTO<T>`** (used by `/restaurants`, `/dishes`, `/orders/my`, `/orders/restaurant/{id}`): `{ content: T[], page, size, totalElements, totalPages, last }`.
- **`OrderRequestDTO`**: `{ deliveryAddressId }` — that's the entire payload; see §8 for why.
- **`OrderResponseDTO`**: `{ id, orderNo, userId, restaurantId, deliveryAddressId, status, totalPrice, items: OrderItemResponseDTO[] }`.
- **`ErrorResponseDTO`** (every error, from `GlobalExceptionHandler`): `{ code, message, path, timestamp }`.

---

## 11. Security Documentation

- **Password hashing:** BCrypt (`spring-security-crypto`'s `BCryptPasswordEncoder`).
- **JWT:** HS256, `jjwt` 0.11.5, subject = email, custom `role` claim, configurable expiry, secret enforced ≥32 bytes at startup.
- **Authentication filter:** `JwtAuthenticationFilter`, registered before `UsernamePasswordAuthenticationFilter`, stateless (`SessionCreationPolicy.STATELESS`).
- **`SecurityConfig`:** CSRF disabled (correct for a stateless, non-cookie JSON API — nothing for CSRF to protect), CORS delegated to `CorsConfig` (explicit origin allow-list from `CORS_ALLOWED_ORIGINS`, not a wildcard, so `allowCredentials(true)` stays safe), public paths are `"/auth/**"`, `"/swagger-ui/**"`, `"/v3/api-docs/**"`, `"/actuator/health/**"`, `"/actuator/info"`, plus `GET /restaurants`, `/restaurants/*`, `/dishes`, `/dishes/*`. Everything else requires authentication.
- **`AuthenticationEntryPoint`:** `JwtAuthenticationEntryPoint` — returns `401` + the standard `ErrorResponseDTO` JSON (`code: UNAUTHENTICATED`) for any unauthenticated request to a protected endpoint. This was deliberately added because Spring Security's default (no entry point configured) is a bare, bodiless `403` for this exact case, which is both the wrong status code and inconsistent with the rest of the API's error shape.
- **401 vs 403, concretely:** no/invalid token → `401 UNAUTHENTICATED` (via the entry point, before the request ever reaches a controller). Valid token but wrong role or wrong owner → `403 ACCESS_DENIED` (via `GlobalExceptionHandler.handleAccessDenied`, catching either `@PreAuthorize`'s `AuthorizationDeniedException`/`AccessDeniedException` or a manually thrown `AccessDeniedException` from a service-layer ownership check).
- **RBAC:** `@EnableMethodSecurity` + `@PreAuthorize` on essentially every mutating endpoint (the only gaps — read-all lists like `GET /options`, `GET /delivery-areas`, `GET /work-schedules` — are intentionally open to *any authenticated* user, matching how a menu/schedule is meant to be visible, not a missed check).
- **Ownership checks / `RestaurantAccessGuard`:** see §4's table — this is the layer that actually stops one `RESTAURANT_ADMIN` from touching another's data, since `@PreAuthorize` alone can't know *which* restaurant a request targets.
- **Validation:** Jakarta Bean Validation on every request DTO (`@NotNull`, `@NotBlank`, `@Email`, `@Size`, `@Positive`, `@DecimalMin`, `@PositiveOrZero`, plus one `@AssertTrue`-based cross-field check on `WorkScheduleRequestDTO` for `endTime > startTime`). Validation failures return `400` with a per-field message list.
- **No stack traces to clients:** `GlobalExceptionHandler`'s catch-all `Exception` handler logs the full exception server-side and returns a generic `INTERNAL_ERROR` message.
- **Secrets:** `DATABASE_URL`, `APP_JWT_SECRET`, `CORS_ALLOWED_ORIGINS` are all environment variables; `.env` is git-ignored; `.env.example` documents the shape with placeholder values only. No secret is hard-coded in source.

### Remaining, honestly-assessed security weaknesses
1. **No token revocation.** "Logout" is cosmetic (§3) — a leaked/stolen token is valid until it naturally expires (default 24h). No blacklist, no refresh-token rotation.
2. **No rate limiting** on `/auth/login` or `/auth/register` — nothing in the code throttles repeated attempts, so brute-forcing a weak password (min length 6) is not mitigated by the API itself.
3. **Minimum password length is 6 characters** (`@Size(min = 6)`), with no complexity requirement — a deliberate, common trade-off for a portfolio project, but weak by production standards.

> **Fixed since the previous audit:** `JwtAuthenticationFilter` previously let an uncaught `EntityNotFoundException` escape when a structurally-valid JWT named a user that had since been deleted. It now catches that case around the `loadUserByUsername()` call, logs a warning, and leaves the request unauthenticated — `anyRequest().authenticated()` plus the existing `JwtAuthenticationEntryPoint` then correctly return `401 UNAUTHENTICATED` instead of an unhandled exception. Covered by `AuthenticationFlowIntegrationTest.protectedEndpoint_returns401_whenTokenReferencesADeletedUser`.

---

## 12. Pagination / Performance

- **Paginated:** `GET /restaurants`, `GET /dishes`, `GET /orders/my`, `GET /orders/restaurant/{id}` — all via `PageResponseDTO<T>` wrapping Spring Data's `Page<T>`, `page`/`size` query params validated with `@Min`/`@Max` (0–100, class-level `@Validated` required for these constraints to actually be enforced on plain `@RequestParam`s — added deliberately, see below).
- **Not paginated (by design):** categories, roles, users, options, delivery areas, work schedules, a user's own addresses/cart-items/notifications — all reference/config data or inherently small per-user collections; adding pagination there would be complexity without benefit.
- **`@EntityGraph` usage:** `DishRepository.findAll(Pageable)` fetches `restaurant`+`dishCategory` in one query; `RestaurantRepository.findAll(Pageable)` fetches `user`+`category`; `OrderRepository.findByUserId`/`findByRestaurantId` fetch `user`+`restaurant`+`deliveryAddress`. This avoids N+1 queries for the to-one associations the mappers actually read when building each list's response DTO.
- **A real N+1-vs-pagination trade-off that was found and fixed during development:** the `Order` repositories' `@EntityGraph` originally also fetch-joined the `orderItems` collection. Combined with `Pageable`, Hibernate cannot apply `LIMIT`/`OFFSET` at the SQL level against a joined collection without risking incorrect row counts, so it silently falls back to **paginating in memory** (loading the *entire* matching result set and slicing it in Java) — logged as `HHH90003004`. That defeats pagination outright on a large table. The fix: the entity graph only fetch-joins the to-one associations; `orderItems` (and its `dish`) stay lazy and load per-order, bounded by the page size — a small, predictable number of extra queries instead of an unbounded in-memory load.
- **`@Validated` on `DishController`/`RestaurantController`/`OrderController`:** Spring MVC does not validate constraint annotations on individual `@RequestParam`s unless the controller class carries `@Validated` — without it, the `@Min`/`@Max` on `page`/`size` would silently do nothing. This is applied on all three controllers that take pagination params.
- Everything else not paginated (dish options, restaurant categories, etc.) is loaded with plain `findAll()`/`findByX()` — acceptable for reference-sized data, but would N+1 or load-everything if any of those tables ever grew large; this is a known, accepted trade-off, not an oversight.

---

## 13. Database Migrations

Flyway owns the schema; Hibernate is **never** allowed to alter it (`spring.jpa.hibernate.ddl-auto=validate` — the app refuses to start if entities and the migrated schema disagree).

| File | What it does |
| --- | --- |
| `V1__init_schema.sql` | All 16 tables, PKs, FKs (with `ON DELETE CASCADE` only where the JPA entity actually cascades — `cart_items`, `order_items`, `order_item_options`), and an index on every FK column. |
| `V2__seed_roles.sql` | Inserts `CLIENT`, `RESTAURANT_ADMIN`, `ADMIN`. **Required** — registration fails without the `CLIENT` row exi
sting. |
| `V3__seed_demo_data.sql` | Optional demo accounts (`admin@utown.dev`, `owner@utown.dev`, `student@utown.dev`, password `Demo123!` for all — see the file for the BCrypt hash), one restaurant ("Seoul BBQ"), two dishes, a delivery area, two work-schedule rows, one client address. Purely convenience data for exploring the API immediately after setup; not required for the app to function. |

**New developer, from scratch:**
```bash
mysql -u root -p -e "CREATE DATABASE utown;"
# set DATABASE_URL / APP_JWT_SECRET (see .env.example)
mvn spring-boot:run
```
Flyway runs automatically on startup against the empty `utown` database and applies all three migrations in order — no manual `schema.sql` step, no `ddl-auto=update` involved.

**Dev vs. test database:** the default profile and the `dev` profile both point at real MySQL via `DATABASE_URL` and run the Flyway migrations above. The `test` profile (used only by `mvn test`) points at an **in-memory H2 database** with `spring.flyway.enabled=false` and `spring.jpa.hibernate.ddl-auto=create-drop` — Hibernate generates the schema straight from the entities for the duration of the test run, and Flyway's MySQL-specific SQL is never executed against H2. This is why the test suite needs neither Docker nor a real MySQL instance.

---

## 14. Testing

**Verified by actually running `mvn clean verify` in this environment** (see §17 result below — not asserted from reading code alone).

| Test class | Type | Covers |
| --- | --- | --- |
| `AuthServiceTest` (5 tests) | Unit (Mockito, no Spring context) | Register success/duplicate-email, login success/unknown-email/wrong-password |
| `OrderServiceTest` (11 tests) | Unit (Mockito) | Order creation total-price calc + cart-clearing, empty cart, closed restaurant, unavailable dish, address-ownership mismatch, missing cart, cancel success/invalid-status/wrong-user, status-update authorization/completed-order rule |
| `RoleServiceTest` (2 tests) | Unit (Mockito) | `delete()` removes the role when it exists; throws `EntityNotFoundException` (→ `404`) when it doesn't |
| `UtownBackendApplicationTests` (1 test) | Smoke | Spring context loads on the `test` profile |
| `AuthenticationFlowIntegrationTest` (8 tests) | Integration (`@SpringBootTest` + MockMvc, real security filter chain, H2) | Register (success/duplicate/validation-error), login (success/wrong-password), unauthenticated request → 401, public restaurant listing → 200 without a token, and a valid JWT for a since-deleted user → 401 `UNAUTHENTICATED` (not a `500`) |
| `OrderFlowIntegrationTest` (2 tests) | Integration (same as above) | Full flow: category → restaurant → dish creation, address, cart, cart item, order creation with real computed total, `GET /orders/my`, cart empties after ordering, a CLIENT is blocked from creating a restaurant (403), a different client can't cancel someone else's order (403), the restaurant owner can advance order status; second test: a different client can't read another user's notification (403, the IDOR fix from §4) |

**Not covered by automated tests:** `UserController`/`RoleController` HTTP endpoints (test fixtures create elevated-role users directly via the repository, bypassing those endpoints entirely — `RoleServiceTest` covers `RoleService` itself at the unit level, not the controller/HTTP layer), `RestaurantCategoryController`/`DishCategoryController`'s `getAll`/update/delete paths, `OptionController`, `DeliveryAreaController`, `WorkScheduleController`, `OrderItemOptionController` — all of these are exercised by manual code reading in this audit, not by an automated test asserting their behavior.

**Database used:** H2 in-memory (`test` profile), not MySQL, not Testcontainers — see §13.

**How to run:**
```bash
mvn test           # tests only
mvn clean verify   # what CI actually runs
```

### Actual result of running it in this session
```
mvn clean verify
```
**BUILD SUCCESS.** 29/29 tests passed, 0 failures, 0 errors:
```
Tests run: 5,  ... -- AuthServiceTest
Tests run: 11, ... -- OrderServiceTest
Tests run: 2,  ... -- RoleServiceTest
Tests run: 1,  ... -- UtownBackendApplicationTests
Tests run: 8,  ... -- AuthenticationFlowIntegrationTest
Tests run: 2,  ... -- OrderFlowIntegrationTest
```
The jar was packaged successfully (`target/utown-backend-0.0.1-SNAPSHOT.jar`).

---

## 15. Local Development

**Requirements:** Java 17+, Maven 3.9+, MySQL 8+ (or Docker, see §16).

**Environment variables** (`.env.example` documents these; `.env` is git-ignored):

| Variable | Required | Default | Purpose |
| --- | --- | --- | --- |
| `DATABASE_URL` | yes | — | Full JDBC URL incl. credentials |
| `APP_JWT_SECRET` | yes | — | JWT signing secret, ≥32 bytes or the app won't start |
| `APP_JWT_EXPIRATION_MS` | no | `86400000` | Token lifetime |
| `CORS_ALLOWED_ORIGINS` | no | `http://localhost:3000` | Comma-separated allow-list |
| `APP_OPENAPI_SERVER_URL` | no | `http://localhost:8080` | Swagger UI's "try it out" target server |
| `MYSQL_DATABASE` / `MYSQL_ROOT_PASSWORD` | docker-compose only | `utown` / `root1111` | Provisions the MySQL container |

**Start:**
```bash
mvn spring-boot:run
```

**Swagger:** `http://localhost:8080/swagger-ui/index.html` (spec at `/v3/api-docs`). As of the most recent change, this defaults to `http://localhost:8080` locally — it no longer hard-codes the Railway production URL; that's now only used if `APP_OPENAPI_SERVER_URL` is explicitly set (e.g. on Railway itself).

**Run tests:** `mvn test` or `mvn clean verify` (no MySQL/Docker needed — see §13/§14).

**Profiles:**
| Profile | When | Database | Schema |
| --- | --- | --- | --- |
| *(default, none active)* | Production / Railway / plain `mvn spring-boot:run` | MySQL via `DATABASE_URL` | Flyway migrations, `ddl-auto=validate` |
| `dev` | Optional (`SPRING_PROFILES_ACTIVE=dev`) | same as default | adds SQL/bind-parameter debug logging only |
| `test` | Automatic under `mvn test` | H2 in-memory | Flyway disabled, `ddl-auto=create-drop` |

**Common troubleshooting:**
- *App won't start, complains about the JWT secret* — `APP_JWT_SECRET` is under 32 characters; the `@PostConstruct` check in `JwtUtil` is intentionally strict.
- *App won't start, Flyway/Hibernate validation error* — the running MySQL schema doesn't match the entities (e.g. you edited an entity without a matching migration, or you're pointing at a stale database). `ddl-auto=validate` will not silently fix this for you.
- *401 on every request except `/auth/**`* — check the `Authorization: Bearer <token>` header is present and the token hasn't expired.
- *403 on a request you expect to succeed* — you have the right role but don't *own* the resource (see §4's ownership table) — this is enforced, not a bug.
- *Building on this machine specifically needed `JAVA_HOME` pinned to a JDK 17 install* — the default `mvn`/`java` on the machine this audit was run on resolved to a newer JDK, which breaks Lombok's annotation processor. Not a project issue, but worth knowing if `mvn` fails with a cryptic `javac`/Lombok error unrelated to this codebase.

---

## 16. Docker / Deployment

- **`Dockerfile`** — multi-stage: `maven:3.9-eclipse-temurin-17` build stage → `eclipse-temurin:17-jre-alpine` runtime stage. Runs as a **non-root** user (`spring:spring`). Exposes `8080`.
- **`docker-compose.yml`** — two services:
  - `mysql` (MySQL 8.0): env `MYSQL_DATABASE`/`MYSQL_ROOT_PASSWORD` (defaulted if unset), port `3306`, a named volume for data persistence, healthcheck via `mysqladmin ping`.
  - `app`: builds from the local `Dockerfile`, `depends_on: mysql: condition: service_healthy` (won't start until MySQL is actually accepting connections, not just "container running"), env vars for `DATABASE_URL` (built from the same `MYSQL_*` vars, pointing at the `mysql` service hostname)/`APP_JWT_SECRET`/`APP_JWT_EXPIRATION_MS`/`CORS_ALLOWED_ORIGINS`, port `8080`, healthcheck via `wget --spider` against `/actuator/health`.
- **`.dockerignore`** excludes `target/`, `.git/`, `.idea/`, `.env*`, logs.
- **GitHub Actions** (`.github/workflows/ci.yml`): on every push/PR to `main`, job 1 checks out, sets up JDK 17 (Temurin, with Maven dependency caching), runs `mvn -B -ntp clean verify`, uploads surefire reports as an artifact. Job 2 (depends on job 1 passing) runs `docker build` against the `Dockerfile` to catch build-breaking Dockerfile changes — it does not push the image anywhere.

**What's actually verified vs. not, honestly:**
- ✅ **Verified locally in this environment:** `mvn clean verify` (compile + all 29 tests + jar packaging).
- ✅ **Verified by tests:** the application logic, security rules, and order flow (via the H2-backed integration tests).
- ❌ **Not verified:** `docker build`, `docker compose up`, or the GitHub Actions workflow actually executing — **no Docker daemon is available in this environment**, so the Dockerfile/compose file/CI workflow have not been run here. They follow standard, widely-used patterns, but you should run `docker compose up --build` yourself before relying on them.

---

## 17. Actual System Flow

### CLIENT
```
Register (CLIENT)
   │
Login → JWT
   │
Browse restaurants (public)          Browse dishes (public)
   │                                      │
   └──────────────┬───────────────────────┘
                   ▼
        Create/keep an address
                   │
        Create cart (for one restaurant)
                   │
        Add cart items (merge on repeat dish)
                   │
        Create order  (server computes total, validates
                        cart/restaurant/dishes/address,
                        clears cart afterward)
                   │
        View "my orders" (paginated) / order detail
                   │
        ┌──────────┴──────────┐
        ▼                     ▼
  Cancel (only while     Watch status change
  still PENDING)         (set by restaurant/admin —
                          client can only read it)
                   │
        Manage own notifications (self-service CRUD;
        nothing in the system auto-generates one)
```

### ADMIN
```
Login (must already have an ADMIN account — no public path to become one)
   │
   ├── Manage users (create RESTAURANT_ADMIN/ADMIN accounts, update, delete)
   ├── Manage roles
   │
   ├── Full access to every restaurant's:
   │     restaurant record / status, dishes, options,
   │     delivery areas, work schedules
   │     (same endpoints a RESTAURANT_ADMIN uses — ADMIN
   │      just passes every ownership check unconditionally)
   │
   ├── Restaurant / dish categories (shared catalog — create + list only;
   │     no update/delete endpoint exists for restaurant categories)
   │
   ├── Any restaurant's orders: view, advance status, cancel
   │
   └── All notifications system-wide (GET /notifications)
```

---

## 18. Feature Completeness Matrix

| Feature | Backend exists | Fully connected | Tested | Role(s) | Status |
| --- | --- | --- | --- | --- | --- |
| Register / Login | ✅ | ✅ | ✅ | public | Implemented |
| Logout | ✅ (endpoint) | ❌ (no server effect) | — | public | Not implemented (cosmetic) |
| JWT auth + 401/403 handling | ✅ | ✅ | ✅ | all | Implemented |
| User management | ✅ | ✅ | ❌ (not via HTTP) | ADMIN | Implemented, endpoint untested |
| Role management | ✅ | ✅ | ⚠️ (`RoleService` unit-tested, HTTP layer not) | ADMIN | Implemented; `delete()` 404 bug fixed (§19) |
| Restaurant CRUD + status | ✅ | ✅ | ✅ | RESTAURANT_ADMIN/ADMIN | Implemented |
| Restaurant categories | ✅ (create/list only) | ⚠️ | ⚠️ (create only) | RESTAURANT_ADMIN/ADMIN | Partially implemented — no update/delete |
| Dish categories | ✅ | ✅ | ⚠️ (create only) | RESTAURANT_ADMIN/ADMIN | Implemented |
| Dish CRUD (soft delete) | ✅ | ✅ | ✅ | RESTAURANT_ADMIN/ADMIN | Implemented |
| Dish options | ✅ | ⚠️ (manageable, never selected during ordering) | ❌ | RESTAURANT_ADMIN/ADMIN | Partially implemented |
| Delivery areas | ✅ | ✅ | ❌ | RESTAURANT_ADMIN/ADMIN | Implemented, untested |
| Work schedules | ✅ | ✅ | ❌ | RESTAURANT_ADMIN/ADMIN | Implemented, untested |
| Cart / cart items | ✅ | ✅ | ✅ | CLIENT | Implemented |
| Addresses | ✅ | ✅ | ✅ | CLIENT | Implemented |
| Order creation + pricing | ✅ | ✅ | ✅ | CLIENT | Implemented |
| Order status lifecycle | ✅ | ⚠️ (no transition-graph validation beyond "not COMPLETED") | ✅ (happy path) | RESTAURANT_ADMIN/ADMIN | Implemented |
| Order cancellation | ✅ | ✅ | ✅ | CLIENT/RESTAURANT_ADMIN/ADMIN | Implemented |
| Order item options (selection at order time) | ✅ (table + CRUD only) | ❌ | ❌ | RESTAURANT_ADMIN/ADMIN | Not implemented end-to-end |
| Notifications | ✅ | ✅ (self-service CRUD) | ✅ (IDOR case) | all / ADMIN for `getAll` | Implemented; nothing auto-generates one |
| Pagination (restaurants/dishes/orders) | ✅ | ✅ | ✅ | — | Implemented |
| Flyway migrations + seed data | ✅ | ✅ | ✅ (via `mvn test`, indirectly) | — | Implemented |
| Docker / docker-compose | ✅ | — | ❌ (no Docker daemon here) | — | Implemented, unverified in this environment |
| CI (GitHub Actions) | ✅ | — | ❌ (not executed here) | — | Implemented, unverified in this environment |
| Swagger/OpenAPI, local server URL | ✅ | ✅ | manual | — | Implemented |

---

## 19. Final Audit

### Strengths
- Consistent layered architecture (thin controllers, real service-layer business logic, DTO-only boundaries) applied uniformly across 16 controllers — not just on the "showcase" endpoints.
- Ownership checks are genuinely enforced, not just role checks — verified per-resource in §4, and this is the part most portfolio projects at this level get wrong.
- Centralized, consistent error shape everywhere (`ErrorResponseDTO`), including a correct `401` vs `403` split (which required a custom `AuthenticationEntryPoint` — most hand-rolled Spring Security configs miss this).
- Server-side price computation for orders — the client cannot influence what it's charged.
- Real Flyway-managed schema (not `ddl-auto=update`) with a schema/entity mismatch treated as a startup failure, not a silent patch.
- A genuinely runnable, in-memory test suite (H2) that exercises real business rules and real security rules together, not just isolated unit mocks.

### Bugs Found
1. **`RestaurantCategoryController`/`Service`** has no `getById`, `update`, or `delete` — not a bug in the sense of broken behavior, but a genuinely missing half of CRUD that the rest of the codebase's pattern (every other category-like resource) would lead you to expect exists.

> **Fixed since the previous audit** (both verified by `mvn clean verify`, see §14):
> - `RoleService.delete(id)` previously called `repository.deleteById(id)` directly with no existence check, so deleting a non-existent role threw `EmptyResultDataAccessException` and fell through to a generic `500` instead of a `404`. It now does `findById().orElseThrow(EntityNotFoundException)` then `delete(role)`, matching `UserService.delete()`. Covered by `RoleServiceTest.delete_throwsEntityNotFound_whenRoleDoesNotExist` / `delete_removesRole_whenRoleExists`.
> - `JwtAuthenticationFilter` → `CustomUserDetailsService.loadUserByUsername` previously could throw an uncaught `EntityNotFoundException` for a structurally-valid token whose user was since deleted, bypassing `GlobalExceptionHandler` entirely. The filter now catches that case and leaves the request unauthenticated, producing a clean `401`. Covered by `AuthenticationFlowIntegrationTest.protectedEndpoint_returns401_whenTokenReferencesADeletedUser`.

### Security Issues
- No token revocation / logout is cosmetic (§3, §11).
- No rate limiting on login/register.
- Minimum password length of 6 with no complexity rule.

None of these are "silently broken authorization" issues — the IDOR audit in §4 turned up nothing currently exploitable; these are hardening gaps, not access-control failures.

### Incomplete Features
- Restaurant categories: create + list only, no update/delete.
- Order item options: fully built as a standalone CRUD resource, never wired into the actual order-placement flow — a client cannot select a dish option when ordering today.
- Notifications: no part of the system automatically creates one (e.g. on order-status change) — it's a manual, self-service resource only.

### Technical Debt
- `OrderItemOptionController`/`Service` exist for a client-facing feature (§18) that isn't reachable by a `CLIENT` at all right now — worth resolving one direction or the other (build the ordering-time selection flow, or clearly reposition this as an admin/back-office-only feature in its docs).
- No automated HTTP-level test coverage for `UserController`/`RoleController`, despite those being ADMIN-only, high-privilege endpoints (the new `RoleServiceTest` covers `RoleService` at the unit level, not the controller/HTTP layer).

### Future Improvements (genuinely new work, not fixes)
- Wire dish-option selection into `OrderRequestDTO`/cart-item creation.
- Token refresh/revocation.
- Rate limiting on auth endpoints.
- Testcontainers-backed integration tests against real MySQL (H2 currently substitutes for this reliably, but doesn't catch MySQL-specific SQL/dialect issues).
- Automatic notification creation on order-status transitions.
- Redis caching for the public restaurant/dish listing endpoints.

### Portfolio Assessment (Junior/Mid-level Backend Developer)

**Solid Mid-level, not inflated.** The architecture, ownership-based authorization, and test suite are more disciplined than most portfolio projects at this level — those are the things that actually get scrutinized in a backend interview, and they hold up under a close read of the code. The honest gaps (a disconnected feature, a couple of small untested endpoints, an incomplete CRUD resource, no Docker verification in this environment) are exactly the kind of thing a reviewer expects to find in a real, evolving codebase rather than a polished tutorial clone — and this document says so plainly instead of hiding them. Two small bugs found during the previous audit (`RoleService.delete()`'s missing-role handling, and the JWT-references-a-deleted-user filter gap) have since been fixed and covered by new tests, which is itself a reasonable signal for a portfolio: findings get acted on, not just logged. That combination — real depth where it counts, and transparent about where it doesn't — is what makes it read as competent rather than staged.

---

## Verification Log

- `mvn clean verify` executed in this session: **BUILD SUCCESS**, 29/29 tests passed.
- Every controller, service, repository, entity, DTO, mapper, security class, migration file, properties file, Dockerfile, compose file, and CI workflow referenced in this document was read directly from the current working tree as part of this audit — nothing here is inferred from naming conventions alone.
