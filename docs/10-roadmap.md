# Roadmap

## Implemented (MVP complete)

- Service discovery with `eureka-service`.
- API Gateway routing users, catalog, cart, orders, and payments.
- `user-service`: registration, login, JWT/RSA, refresh token, publishes `user.registration` event.
- `product-catalog-service`: categories, products, and inventory CRUD.
- `shopping-cart-service`: cart persistence per user, item management.
- `order-service`: manual order creation and creation from cart, inventory deduction via REST, consumes payment events, publishes `order.confirmation`.
- `payment-service`: simulated payment flow, publishes `payment.success`, `payment.failed`, `payment.refunded`.
- `notification-service`: consumes `user.registration` and `order.confirmation`, sends email or logs to console.
- Docker Compose for local environment.
- PostgreSQL with one database per service.
- CI per service via GitHub Actions.

## Pending

**Security hardening** (see `14-debito-autenticacao.md`):
- Gateway validates JWT on all private routes.
- Services derive user identity from the token, not from the request body.
- Role-based authorization (admin vs. regular user).

**Reliability:**
- Idempotent RabbitMQ consumers with `eventId` and `correlationId`.
- Stock compensation when payment or order fails after inventory was already deducted.
- Idempotency on payment creation endpoints.

**Integration tests:**
- Testcontainers-based tests covering the full cart → order → payment → stock → notification flow.
- Security scenario tests (missing token, invalid token, cross-user access).

**Observability:**
- Spring Boot Actuator on all services.
- Health checks in Docker Compose.
- Structured logs (JSON).
- Prometheus and Grafana via Docker Compose.

**Production deploy:**
- Kubernetes manifests or Docker Swarm.
- Secrets management.

## Recommended order

1. Security hardening.
2. Reliability: idempotency and compensation.
3. Integration tests with Testcontainers.
4. Observability: Actuator, Prometheus, Grafana.
5. Production deploy.
