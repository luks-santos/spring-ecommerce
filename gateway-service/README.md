# Gateway Service

Single entry point for all client requests. Routes to downstream services via Eureka discovery and validates the JWT on private routes.

> Part of the [Scalable E-Commerce Platform](../README.md) study project.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Cloud Gateway (reactive / WebFlux)
- Spring Security OAuth2 Resource Server (reactive)
- Spring Cloud Netflix Eureka Client

## Port

`8080` — no database.

## What to study here

The gateway is where two big microservices ideas meet: **a single entry point** and
**centralized, edge-level authentication**.

Routing is path-based: `/api/orders/**` goes to `order-service`, and so on. Notice the
`StripPrefix` difference — user and product-catalog use `StripPrefix=2` (so
`/api/user/sign-up` reaches the service as `/sign-up`), while cart/order/payment use **no**
strip because their controllers are already mapped under `/api/carts`, `/api/orders`,
`/api/payments`. Reading the route table next to each controller's `@RequestMapping` is a
good way to understand how gateway rewriting works.

For security, the gateway is an **OAuth2 resource server**: it verifies the RSA-signed JWT
issued by `user-service` (using the public key) *before* forwarding, so unauthenticated
calls never even reach the business services. It also maps the token's `roles` and `scope`
claims to authorities, which is what makes `hasRole("ADMIN")` work for catalog writes.

The key design decision to reflect on: validation happens **both** at the gateway and
inside each service. That looks redundant, but it is intentional **defense in depth** — in
the internal Docker network a service can be reached directly (bypassing the gateway), so
each one re-validates the token. The gateway is the convenient front door, not the only
lock.

Public routes (no token): `sign-in`, `sign-up`, `refresh-token`, catalog `GET`s, and each
service's OpenAPI JSON at `/api/<service>/v3/api-docs`. Everything else requires a valid
token; catalog writes additionally require `ROLE_ADMIN`.

## OpenAPI through the gateway

Each business service's OpenAPI JSON is reachable at `/api/<service>/v3/api-docs`
(e.g. http://localhost:8080/api/orders/v3/api-docs). The interactive Swagger **UI** is
served per service on its own port.

## Build, run & test

See the [root README](../README.md#running-with-docker) — `docker compose up gateway-service` from the repo root (Eureka must be up first).
