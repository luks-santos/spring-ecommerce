# Product Catalog Service

Manages categories, products, and inventory. Called synchronously by `order-service` to deduct stock when an order is created.

> Part of the [Scalable E-Commerce Platform](../README.md) study project.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Security OAuth2 Resource Server (JWT)
- Spring Data JPA, PostgreSQL 16, Flyway
- Spring Cloud Netflix Eureka Client
- springdoc-openapi

## Port

`8082` — database: `product_db`

## What to study here

This is the place to study **role-based authorization** in a resource server. The rule is
simple but realistic: **reads are public, writes require `ROLE_ADMIN`**. Anyone can browse
the catalog, but only an admin can create or change products. Worth noticing: that rule is
enforced **both** at the gateway and here in the service's `SecurityConfig` — the same
defense-in-depth idea as the gateway, because a service on the internal network can be
called directly.

The other thing to study is the **inventory model**. Stock lives in its own `inventories`
entity (one per product) with a `quantity` and a `reserved_quantity` column. Today the
order flow simply decrements `quantity` when an order is created. The presence of
`reserved_quantity` hints at the better design that is not yet implemented: **reserving**
stock at order time and only committing it after payment, so a failed payment does not
leave inventory wrong. That gap — compensation/reservation — is tracked as a deliberate
next step (issue #8) and is a great exercise in distributed-transaction (saga) thinking.

## Swagger

- UI: http://localhost:8082/swagger-ui.html
- OpenAPI JSON: http://localhost:8082/v3/api-docs

## Build, run & test

See the [root README](../README.md#running-with-docker) — `docker compose up product-catalog-service`, or `cd product-catalog-service && ./mvnw test`.
