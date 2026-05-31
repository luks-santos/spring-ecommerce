# Scalable E-Commerce Platform (Microservices Study Project)

A hands-on **study project** that implements an e-commerce backend as a set of
independent Spring Boot microservices. It follows the
[Scalable E-Commerce Platform](https://roadmap.sh/projects/scalable-ecommerce-platform)
challenge from roadmap.sh and is meant for **learning** the patterns behind a real
microservices system — not for production.

If you want to understand how service discovery, an API gateway, JWT security across
services, per-service databases, and event-driven communication fit together, this
repo is a small but complete playground for exactly that.

---

## Table of contents

- [What you get](#what-you-get)
- [Architecture](#architecture)
- [Tech stack](#tech-stack)
- [Running with Docker](#running-with-docker)
- [Accessing Swagger / OpenAPI](#accessing-swagger--openapi)
- [Testing the full flow](#testing-the-full-flow)
- [Security model](#security-model)
- [Asynchronous events](#asynchronous-events)
- [What this project teaches](#what-this-project-teaches)
- [Suggested study path](#suggested-study-path)

---

## What you get

Eight services that together form a working "register → browse → cart → order → pay →
get notified" flow:

| Service | Port | Database | Responsibility |
|---------|------|----------|----------------|
| `eureka-service` | 8761 | – | Service registry (Netflix Eureka) |
| `gateway-service` | 8080 | – | Single entry point, routing + JWT validation |
| `user-service` | 8081 | `user_db` | Registration, login, JWT/RSA issuing, refresh token |
| `product-catalog-service` | 8082 | `product_db` | Categories, products, inventory |
| `shopping-cart-service` | 8083 | `cart_db` | Cart per authenticated user |
| `notification-service` | 8084 | H2 (in-memory) | Email/console notifications via RabbitMQ |
| `order-service` | 8085 | `order_db` | Orders, status lifecycle, idempotent consumers |
| `payment-service` | 8086 | `payment_db` | Simulated payments + events |

Supporting infrastructure: **PostgreSQL** (one logical DB per service) and **RabbitMQ**.

---

## Architecture

```
                Client (Swagger UI / Postman / curl)
                                │  Bearer JWT
                                ▼
                       Gateway (8080)  ── validates JWT, routes by path
        ┌───────────────┬───────────────┬───────────────┬───────────────┐
        ▼               ▼               ▼               ▼               ▼
  /api/user/**   /api/product-     /api/carts/**   /api/orders/**  /api/payments/**
   user (8081)   catalog (8082)    cart (8083)     order (8085)    payment (8086)
                                                       │  REST            │ REST (JWT)
                                                       ▼                  ▼
                                          cart + catalog (sync)      order (ownership check)

  Eureka (8761) ◄── every service registers and is discovered by name

  RabbitMQ
    user.registration   user    ─► notification   (welcome message)
    order.created       order   ─► (audit / future consumers)
    order.confirmation  order   ─► notification   (order confirmed email)
    payment.success     payment ─► order          (confirm order, idempotent)
    payment.failed      payment ─► order           (fail order, idempotent)
    payment.refunded    payment ─► order           (refund order, idempotent)
```

Each service owns its data and its Flyway migrations. Services never share a database;
they talk over **synchronous REST** for queries and **RabbitMQ events** for async side
effects.

---

## Tech stack

| Concern | Technology |
|---------|-----------|
| Language | Java 25 |
| Framework | Spring Boot 3.5.14 |
| Cloud | Spring Cloud 2025.0.2 (Gateway, Eureka, LoadBalancer) |
| Security | Spring Security OAuth2 Resource Server + JWT signed with RSA (Nimbus) |
| Persistence | PostgreSQL 16, Hibernate/JPA, Flyway migrations |
| Messaging | RabbitMQ (topic exchanges) |
| API docs | springdoc-openapi (Swagger UI) |
| Tests | JUnit 5, Mockito, Spring Security Test, `@WebMvcTest` slices |
| Build | Maven Wrapper per service (`mvnw`) |
| Runtime | Docker + Docker Compose |
| CI | GitHub Actions |

> **Build note:** the code targets **JDK 25**. Inside Docker this is handled by each
> service's Dockerfile. To build locally, make sure `JAVA_HOME` points to a JDK 25.

---

## Running with Docker

Prerequisites: **Docker** and **Docker Compose** (Docker Desktop on Windows/Mac).

From the repository root:

```bash
docker compose up --build
```

This builds and starts everything: Postgres, RabbitMQ, Eureka, the gateway and all six
business services on the same Docker network. First boot takes a few minutes (Maven
builds inside each image).

Useful checks while it comes up:

| What | URL |
|------|-----|
| Eureka dashboard (see all services registered) | http://localhost:8761 |
| RabbitMQ management (guest / guest) | http://localhost:15672 |
| Gateway (entry point for all API calls) | http://localhost:8080 |

Stop everything:

```bash
docker compose down          # keep data
docker compose down -v       # also drop the Postgres volume
```

### Run or test a single service

Each service has its own Maven wrapper (there is no root aggregator). Building locally
requires a **JDK 25**.

```bash
docker compose up <service-name>     # run one service (with its dependencies)

cd <service-name>
./mvnw test                          # run that service's tests
./mvnw spring-boot:run               # run it locally (its dependencies must be up)
```

---

## Accessing Swagger / OpenAPI

Every business service ships an interactive **Swagger UI**. Open it directly on the
service port — this is the easiest way to explore and call the API:

| Service | Swagger UI |
|---------|-----------|
| user-service | http://localhost:8081/swagger-ui.html |
| product-catalog-service | http://localhost:8082/swagger-ui.html |
| shopping-cart-service | http://localhost:8083/swagger-ui.html |
| order-service | http://localhost:8085/swagger-ui.html |
| payment-service | http://localhost:8086/swagger-ui.html |

**Using a token in Swagger UI:** call `POST /sign-up` (user-service UI) to get a JWT,
then click **Authorize** and paste the access token. Authenticated endpoints will then
send the `Bearer` header for you.

**OpenAPI JSON through the gateway** (handy to confirm routing/security is working):

| Service | OpenAPI JSON via gateway |
|---------|--------------------------|
| user-service | http://localhost:8080/api/user/v3/api-docs |
| product-catalog-service | http://localhost:8080/api/product-catalog/v3/api-docs |
| shopping-cart-service | http://localhost:8080/api/carts/v3/api-docs |
| order-service | http://localhost:8080/api/orders/v3/api-docs |
| payment-service | http://localhost:8080/api/payments/v3/api-docs |

> The **UI** is served per service; the gateway exposes the **JSON** contracts.
> `eureka-service`, `gateway-service` and `notification-service` have no business API.

---

## Testing the full flow

All requests go through the gateway at `http://localhost:8080`. The user identity is
**always derived from the JWT** — you never pass `userId` in the body or path.

1. **Register and get a token** (returns access + refresh tokens):

   ```bash
   curl -X POST http://localhost:8080/api/user/sign-up \
     -H "Content-Type: application/json" \
     -d '{
       "firstName": "Lucas", "lastName": "Silva",
       "email": "lucas@example.com", "phone": "11999999999",
       "address": "Rua Exemplo, 123", "password": "123456"
     }'
   ```

   Save the access token and send it as `Authorization: Bearer <token>` on every call
   below.

2. **Create catalog data** — `POST /api/product-catalog/categories`, `/products`,
   `/inventories`. These are **admin-only** writes (require a user with the `ADMIN`
   role); reads are public. A normal sign-up is a `CLIENT`.

3. **Fill the cart** — `POST /api/carts/items` `{ "productId": "...", "quantity": 2, "price": 199.90 }`
   (the cart belongs to the token's user).

4. **Create the order from the cart** — `POST /api/orders/from-cart`
   `{ "shippingAddress": "Rua Exemplo, 123" }` (deducts stock, waits for payment).

5. **Create and confirm the payment** — `POST /api/payments`
   `{ "orderId": "...", "amount": 199.90, "paymentMethod": "PIX", "provider": "INTERNAL" }`,
   then `POST /api/payments/{paymentId}/confirm`.

6. **Watch the events fire:**
   - `payment-service` publishes `payment.success`.
   - `order-service` consumes it (idempotently), sets the order to `PAYMENT_CONFIRMED`
     and publishes `order.confirmation`.
   - `notification-service` consumes `order.confirmation` and "sends" the email
     (printed to its console by default).

The exact request/response schema for every endpoint lives in each service's Swagger UI.

---

## Security model

- **`user-service` issues JWTs** signed with an **RSA private key**; every other service
  validates them with the matching **public key** (`certs/publicKey.pem`).
- The JWT carries `userId`, `email`, `roles` (`ROLE_ADMIN` / `ROLE_CLIENT`) and `scope`.
- The **gateway** and each business service are configured as **OAuth2 resource
  servers** — a request without a valid token on a private route gets `401`.
- **Identity comes from the token, not the request.** Controllers read `userId` from the
  JWT claims, so a user cannot act on another user's resources (`403` on cross-user
  access; admins bypass the ownership check).
- **Role-based rules:** product-catalog writes require `ROLE_ADMIN`; order/payment admin
  routes (e.g. list by status) require `ROLE_ADMIN`.
- **Service-to-service:** when payment-service needs to confirm an order belongs to the
  caller, it **propagates the user's JWT** to order-service.

---

## Asynchronous events

RabbitMQ decouples side effects from the request/response path. Events carry shared
metadata (`eventId`, `correlationId`, `producer`, `occurredAt`) and **consumers are
idempotent**: each handled `eventId` is recorded (order-service `processed_events`
table), so a redelivered message is processed at most once.

| Event | Producer | Consumer | Trigger |
|-------|----------|----------|---------|
| `user.registration` | user-service | notification-service | User registered |
| `order.created` | order-service | – | Order created |
| `order.confirmation` | order-service | notification-service | Payment confirmed |
| `payment.success` | payment-service | order-service | Payment confirmed |
| `payment.failed` | payment-service | order-service | Payment failed |
| `payment.refunded` | payment-service | order-service | Payment refunded |

---

## What this project teaches

Concepts you can study end-to-end in real, runnable code:

- **Microservice decomposition** — one bounded context per service, one database each,
  no shared tables.
- **Service discovery** — services register with **Eureka** and call each other by name
  (`lb://order-service`) instead of hardcoded hosts.
- **API gateway** — a single entry point (Spring Cloud Gateway) doing routing,
  path stripping and centralized JWT validation.
- **Stateless auth across services** — RSA-signed JWT issued by one service and verified
  by many as **OAuth2 resource servers**; roles/scopes for authorization.
- **Deriving identity from the token** — why trusting a client-supplied `userId` is a
  vulnerability, and how to close it.
- **Event-driven architecture** — topic exchanges, producers/consumers, and **idempotency**
  with a processed-events table to survive at-least-once delivery.
- **Database-per-service + migrations** — Flyway versioned schemas isolated per service.
- **Testing strategy** — fast unit tests, `@WebMvcTest` security slices with mocked
  `JwtDecoder`, and cross-user/admin authorization matrices.
- **Containerization** — multi-service local environment with Docker Compose.

---

## Suggested study path

1. Bring the stack up (`docker compose up --build`) and open the Eureka dashboard to see
   discovery in action.
2. Read `user-service` first: how the JWT is built and signed (`JwtTokenGenerator`,
   `certs/`).
3. Follow a request through `gateway-service/.../config/SecurityConfig` to a downstream
   service's `SecurityConfig` to see resource-server validation.
4. Open a controller (e.g. `OrderController`) and notice how `userId` comes from the
   token and how ownership/admin checks work.
5. Trace one event: `PaymentService` publishing `payment.success` →
   `order-service` `PaymentEventConsumer` (idempotency) → `order.confirmation` →
   `notification-service`.
6. Run a service's tests (`./mvnw test`) and read the `*SecurityTest` classes.

Each service also has its own `README.md` with service-specific notes.

> **Roadmap:** this is a study project and intentionally **not** production-grade.
> Planned improvements (saga/stock compensation, internal service auth, Testcontainers,
> etc.) are tracked in the repository's [open issues](../../issues). Observability and
> production deployment are out of scope for now.
