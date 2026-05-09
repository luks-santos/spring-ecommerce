# Implementation phases

## Summary

| Phase | Goal | Status |
|-------|------|--------|
| 0 | Base fixes and technical alignment | Done |
| 1 | Documentation and contracts | Done |
| 2 | `user-service` → `notification-service` via RabbitMQ | Done |
| 3 | `shopping-cart-service` | Done |
| 4 | `order-service` | Done |
| 5 | `payment-service` | Done |
| 6 | Full order → payment → stock → notification flow | Done |
| 7 | Gateway, Docker Compose, and CI | Done |
| 8 | Observability | Pending |
| 9 | Hardening, integration tests, final review | Pending |

## Phase 0: Base fixes

- CI workflows aligned with `main` branch.
- All services standardized on Java 25, Spring Boot 3.5.14, Spring Cloud 2025.0.2.
- `docker-compose.yml` updated with correct service names, databases, and environment variables.

## Phase 1: Documentation and contracts

- `docs/` structure created.
- Architecture overview, service docs, API routes, and RabbitMQ events documented.
- Technical decisions recorded (REST for queries, events for async side effects).

## Phase 2: User + Notification integration

- `user-service` publishes `user.registration` to `user.exchange` on registration.
- `notification-service` consumes the event and sends welcome email or logs to console.

## Phase 3: Shopping cart service

- `shopping-cart-service` with `shopping_carts` and `cart_items` tables.
- Flyway migrations, `cart_db` database.
- Endpoints: get/create cart, add item, update quantity, remove item, clear cart, delete cart.
- Dockerfile, Compose entry, gateway route, CI workflow, unit tests.

## Phase 4: Order service

- `order-service` with `orders`, `order_items`, and `order_status_history` tables.
- Flyway migrations, `order_db` database.
- Manual order creation and creation from cart.
- Calls `shopping-cart-service` (REST) to fetch cart items.
- Calls `product-catalog-service` (REST) to deduct inventory per item.
- Status lifecycle: `CREATED → WAITING_PAYMENT → PAYMENT_CONFIRMED → PAID / FAILED / CANCELLED / SHIPPED`.
- Consumes `payment.success`, `payment.failed`, `payment.refunded` from RabbitMQ.
- Publishes `order.confirmation` after payment confirmation.
- Dockerfile, Compose entry, gateway route, CI workflow, unit tests.

## Phase 5: Payment service

- `payment-service` with `payments` and `payment_transactions` tables.
- Flyway migrations, `payment_db` database.
- Simulated payment processing (no real gateway).
- Status lifecycle: `PENDING → PROCESSING → SUCCESS / FAILED`, `SUCCESS → REFUNDED`.
- Publishes `payment.success`, `payment.failed`, `payment.refunded` to `payment.exchange`.
- Dockerfile, Compose entry, gateway route, CI workflow, unit tests.

## Phase 6: Full flow

Connected flow:
1. User creates or updates cart.
2. User creates order from cart (`POST /api/orders/from-cart`).
3. Order deducts inventory and sets status to `WAITING_PAYMENT`.
4. Payment is created and confirmed (`POST /api/payments/{id}/confirm`).
5. `payment-service` publishes `payment.success`.
6. `order-service` consumes the event, sets status to `PAYMENT_CONFIRMED`, then `PAID`, publishes `order.confirmation`.
7. `notification-service` consumes `order.confirmation` and notifies the user.

## Phase 7: Gateway, Docker Compose, and CI

- Gateway routes for `/api/carts/**`, `/api/orders/**`, `/api/payments/**`.
- `docker-compose.yml` includes all eight services and their databases.
- CI workflows for all services.

## Phase 8: Observability (pending)

- Spring Boot Actuator on all services.
- Health checks in Docker Compose.
- Structured logs (JSON).
- Prometheus and Grafana via Docker Compose with basic dashboards per service.

Estimate: 16–32h.

## Phase 9: Hardening, integration tests, final review (pending)

- Integration tests with Testcontainers for the full flow.
- Gateway JWT validation on private routes.
- Services derive user identity from token, not request body.
- Idempotent RabbitMQ consumers with `eventId` and `correlationId`.
- Stock compensation on payment failure.
- HTTP collection (Bruno or Postman) for the full flow.
- Seed data for clean-environment testing.
- Final documentation review.

Estimate: 24–48h.
