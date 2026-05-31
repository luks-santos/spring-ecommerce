# Shopping Cart Service

Manages one cart per user. The cart owner is always the authenticated user.

> Part of the [Scalable E-Commerce Platform](../README.md) study project.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Security OAuth2 Resource Server (JWT)
- Spring Data JPA, PostgreSQL 16, Flyway
- Spring Cloud Netflix Eureka Client
- springdoc-openapi

## Port

`8083` — database: `cart_db`

## What to study here

This service is the clearest lesson in the whole project on **deriving identity from the
token instead of trusting the client**. It used to expose routes like
`/api/carts/user/{userId}/items` — which meant any authenticated user could pass someone
else's `userId` and operate on their cart. That is a textbook broken-access-control bug.

The fix is the thing to study: the `{userId}` path parameter is **gone**. Every endpoint
now reads `userId` from the JWT claims, so a request can only ever touch the caller's own
cart. Compare the controller before/after in your head — same features, but identity is no
longer an input. This pairs directly with `user-service` (which puts `userId` into the
token) to show the complete "issue identity / consume identity" loop.

There is also an honest trade-off to learn from here. Because the cart can no longer be
addressed by id, **internal callers lost a door**: `order-service` still tries to read the
cart by `userId` when building an order from cart, which now fails. That regression is
tracked in issue #14, and it is a good illustration of how a security change can ripple
into service-to-service contracts (see also issue #9 on propagating identity internally).

Business invariant worth noting: **one cart per user** (unique `user_id`), and adding the
same product twice updates the quantity instead of duplicating the line.

## Swagger

- UI: http://localhost:8083/swagger-ui.html
- OpenAPI JSON: http://localhost:8083/v3/api-docs

## Build, run & test

See the [root README](../README.md#running-with-docker) — `docker compose up shopping-cart-service`, or `cd shopping-cart-service && ./mvnw test`.
