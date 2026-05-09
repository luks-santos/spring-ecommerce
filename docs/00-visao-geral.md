# Overview

E-commerce platform built as microservices, based on the [Scalable E-Commerce Platform](https://roadmap.sh/projects/scalable-ecommerce-platform) challenge from roadmap.sh.

## Architecture

Eight independent services communicating via HTTP (synchronous queries) and RabbitMQ (async events):

```
Client
  │
  ▼
Gateway (8080)
  ├── /api/user/**             → user-service (8081)
  ├── /api/product-catalog/**  → product-catalog-service (8082)
  ├── /api/carts/**            → shopping-cart-service (8083)
  ├── /api/orders/**           → order-service (8085)
  └── /api/payments/**         → payment-service (8086)

Eureka (8761) ← all services register here

RabbitMQ
  ├── user.registration        → notification-service
  ├── order.confirmation       → notification-service
  ├── payment.success          → order-service
  ├── payment.failed           → order-service
  └── payment.refunded         → order-service
```

## Services

| Service | Port | Database | Role |
|---------|------|----------|------|
| eureka-service | 8761 | - | Service registry |
| gateway-service | 8080 | - | Single entry point for clients |
| user-service | 8081 | user_db | Auth, registration, JWT/RSA |
| product-catalog-service | 8082 | product_db | Products, categories, inventory |
| shopping-cart-service | 8083 | cart_db | Cart per user |
| notification-service | 8084 | H2 in-memory | Email/console via RabbitMQ |
| order-service | 8085 | order_db | Orders, status lifecycle |
| payment-service | 8086 | payment_db | Simulated payments |

## Stack

| Component | Technology |
|-----------|-----------|
| Language | Java 25 |
| Framework | Spring Boot 3.5.14 |
| Cloud | Spring Cloud 2025.0.2 |
| Database | PostgreSQL 16 |
| Messaging | RabbitMQ |
| Service discovery | Netflix Eureka |
| API Gateway | Spring Cloud Gateway |
| Auth | Spring Security + JWT/RSA (NimbusDS) |
| ORM | Hibernate/JPA + Flyway |
| Tests | JUnit 5 + Mockito |
| Container | Docker + Docker Compose |
| CI | GitHub Actions |
| API docs | Springdoc-OpenAPI |

## Inter-service communication

**REST (synchronous):**
- `order-service` calls `shopping-cart-service` to fetch cart items when creating an order from cart.
- `order-service` calls `product-catalog-service` to deduct inventory per item.

**RabbitMQ (asynchronous):**
- `user-service` → `notification-service`: welcome notification on registration.
- `payment-service` → `order-service`: payment result updates order status.
- `order-service` → `notification-service`: order confirmation email after payment.

## Known limitations

- Gateway does not validate JWT on private routes.
- Cart, order, and payment accept `userId` from the request body instead of deriving it from the token.
- RabbitMQ events lack `eventId` and `correlationId` for idempotency.
- No stock compensation on payment failure after inventory was already deducted.
- No observability (Actuator, Prometheus, Grafana).
