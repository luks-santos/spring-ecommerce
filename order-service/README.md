# Order Service

Owns the order lifecycle. Builds orders (manually or from a cart), coordinates with cart and catalog over REST, and reacts to payment events from RabbitMQ.

> Part of the [Scalable E-Commerce Platform](../README.md) study project.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Security OAuth2 Resource Server (JWT)
- Spring Data JPA, PostgreSQL 16, Flyway
- Spring AMQP (RabbitMQ)
- Spring Cloud Netflix Eureka Client
- springdoc-openapi

## Port

`8085` — database: `order_db`

## What to study here

Order-service is the busiest node in the system and the best place to study how a request
that spans several services is **orchestrated** — and where the hard problems live.

**Synchronous orchestration.** Creating an order from a cart fans out over REST: it reads
the cart, then deducts inventory in the catalog per item, then persists the order. This
shows the upside (simple, immediate) and the downside (it couples availability — if a
downstream call fails midway, you can be left in a partial state) of synchronous
choreography.

**Event-driven status updates + idempotency.** The order's status is driven by payment
events (`payment.success` / `failed` / `refunded`) consumed from RabbitMQ. The lesson here
is **idempotency**: RabbitMQ delivers *at least once*, so the same event can arrive twice.
Each handled `eventId` is recorded in a `processed_events` table, and the id is saved
**only after** the business logic succeeds — so a redelivery is skipped, but a mid-failure
can still be retried. Read `PaymentEventConsumer` and its test to see this concretely.

**Authorization by ownership.** Like cart, identity comes from the token: a user sees only
their own orders (`403` otherwise), while admin-only routes (list-by-status, update-status)
require `ROLE_ADMIN`.

**Where it is intentionally incomplete.** Two gaps are worth studying as exercises, not
bugs to hide: stock is **not** compensated if a payment fails after inventory was already
deducted (issue #8, classic saga territory), and the from-cart REST call still targets a
cart route that no longer exists (issue #14). They make the trade-offs of distributed
transactions tangible.

It publishes typed events (`order.created`, `order.confirmation`) carrying shared metadata
(`eventId`/`correlationId`/`producer`/`occurredAt`).

## Swagger

- UI: http://localhost:8085/swagger-ui.html
- OpenAPI JSON: http://localhost:8085/v3/api-docs

## Build, run & test

See the [root README](../README.md#running-with-docker) — `docker compose up order-service`, or `cd order-service && ./mvnw test`.
