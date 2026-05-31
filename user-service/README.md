# User Service

Handles registration, authentication, and profiles. It is the system's **identity provider**: it issues the JWT that every other service trusts.

> Part of the [Scalable E-Commerce Platform](../README.md) study project.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Security + JWT/RSA (Nimbus)
- Spring Data JPA, PostgreSQL 16, Flyway
- Spring Cloud Netflix Eureka Client
- Spring AMQP (RabbitMQ)
- springdoc-openapi

## Port

`8081` — database: `user_db`

## What to study here

This service is the root of trust, so it is the best place to study **stateless
authentication across services**. It signs tokens with an **RSA private key**; every other
service verifies them with the matching **public key**. The "why" matters: with asymmetric
RSA, the issuer is the only party that can mint tokens, but anyone holding the public key
can validate them — no shared secret has to be distributed to all services (which would be
the weakness of a symmetric HMAC approach).

Look at what goes **into** the token. Besides `sub`, the access token carries `userId`,
`email`, and `roles` (`ROLE_ADMIN` / `ROLE_CLIENT`). This is the heart of the security
model: downstream services read identity from these claims and never trust a `userId` sent
in a request body or path. Studying this alongside the cart/order/payment services shows
the full loop — issue identity here, consume it safely there.

Two more design points to notice. First, **three separate `SecurityFilterChain` beans**
split responsibilities cleanly: HTTP Basic for `/sign-in`, Bearer-access-token validation
for `/api/**`, and a dedicated chain for `/refresh-token`. Second, **refresh tokens** are
persisted and revocable (short-lived access token + longer-lived refresh token), which is
the standard pattern for keeping access tokens short without forcing users to log in
constantly.

It also publishes a `user.registration` event (with `eventId`/`correlationId`/`producer`/
`occurredAt`) consumed by `notification-service` — a small first taste of the event-driven
side of the system.

## Swagger

- UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/v3/api-docs

## Build, run & test

See the [root README](../README.md#running-with-docker) — `docker compose up user-service`, or `cd user-service && ./mvnw test`.
