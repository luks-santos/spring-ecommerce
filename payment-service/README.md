# Payment Service

Simulates the payment lifecycle and publishes the result as events. The payer is the authenticated user, and a payment can only be created for an order that user owns.

> Part of the [Scalable E-Commerce Platform](../README.md) study project.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Security OAuth2 Resource Server (JWT)
- Spring Data JPA, PostgreSQL 16, Flyway
- Spring AMQP (RabbitMQ)
- Spring Cloud Netflix Eureka Client
- springdoc-openapi

## Port

`8086` — database: `payment_db`

## What to study here

Payment-service is where to study **cross-service authorization** and **publishing domain
events**.

The interesting authorization case: unlike cart or order, the resource being acted on (an
order) lives in **another** service. The `userId` is still taken from the token, but the
`orderId` arrives in the request body — so before creating a payment, this service calls
`order-service` (**propagating the caller's JWT**) to confirm the order belongs to the
caller, returning `403` if not. That is a small but important pattern: when you cannot
check ownership locally, ask the service that owns the data, and carry the user's identity
with you. (It is also the model the other internal calls should follow — see issue #9.)

On the messaging side, study the **typed event**. When a payment reaches a terminal state
it publishes a `PaymentProcessedEvent` carrying shared metadata
(`eventId`/`correlationId`/`producer`/`occurredAt`) plus `paymentId`, `orderId`, `status`.
The `eventId` is exactly what lets `order-service` consume it idempotently. Contrast this
with publishing a raw DTO or a `Map`: a typed contract with an explicit id is what makes
reliable, evolvable event-driven communication possible.

The lifecycle itself (`PENDING → PROCESSING → SUCCESS/FAILED`, and `SUCCESS → REFUNDED`) is
simulated — there is no real provider — which keeps the focus on the integration patterns
rather than payment-gateway specifics.

## Swagger

- UI: http://localhost:8086/swagger-ui.html
- OpenAPI JSON: http://localhost:8086/v3/api-docs

## Build, run & test

See the [root README](../README.md#running-with-docker) — `docker compose up payment-service`, or `cd payment-service && ./mvnw test`.
