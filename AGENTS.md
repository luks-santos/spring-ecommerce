# AGENTS.md

Operational guide for agents working in this repository.

## Project context

Scalable e-commerce platform based on the [roadmap.sh challenge](https://roadmap.sh/projects/scalable-ecommerce-platform). Backend in Java/Spring Boot with independent microservices for users, catalog, cart, orders, payments, and notifications.

## Current structure

```
.
├── README.md
├── AGENTS.md
├── docs/
├── .github/workflows/
└── backend/
    ├── docker-compose.yml
    ├── init-postgres/
    ├── eureka-service/
    ├── gateway-service/
    ├── user-service/
    ├── product-catalog-service/
    ├── shopping-cart-service/
    ├── order-service/
    ├── payment-service/
    └── notification-service/
```

## Implemented services

| Service | Port | Database | Description |
|---------|------|----------|-------------|
| eureka-service | 8761 | - | Service registry (Netflix Eureka) |
| gateway-service | 8080 | - | API Gateway (Spring Cloud Gateway) |
| user-service | 8081 | user_db | Registration, login, JWT/RSA, refresh token |
| product-catalog-service | 8082 | product_db | Categories, products, inventory |
| shopping-cart-service | 8083 | cart_db | Cart management per user |
| notification-service | 8084 | H2 in-memory | Email/console notifications via RabbitMQ |
| order-service | 8085 | order_db | Order creation from cart, status lifecycle |
| payment-service | 8086 | payment_db | Simulated payments with RabbitMQ events |

## RabbitMQ events

| Event | Producer | Consumer | Trigger |
|-------|----------|----------|---------|
| user.registration | user-service | notification-service | User registered |
| order.confirmation | order-service | notification-service | Payment confirmed |
| payment.success | payment-service | order-service | Payment confirmed |
| payment.failed | payment-service | order-service | Payment failed |
| payment.refunded | payment-service | order-service | Payment refunded |

## Commands

Run the full stack:

```powershell
cd backend
docker compose up --build
```

Run tests per service:

```powershell
cd backend/<service-name>
.\mvnw.cmd test
```

No Maven parent aggregator at the root. Run commands per service.

## Working standards

- Keep each service isolated with its own database. Never share databases between services.
- Use synchronous REST for simple queries between services. Use RabbitMQ events for async side effects.
- Each service owns its Flyway migrations, README, Dockerfile, and tests.
- When adding a service: include Dockerfile, README, pom.xml, application.yml, Flyway migrations (if relational), tests, docker-compose entry, gateway route, and CI workflow.
- Use environment variables for credentials, URLs, and keys. No real secrets in the repository.
- When changing an endpoint, update the README, tests, and gateway route to stay consistent.
- Document inter-service contracts in `docs/` or in the service README.

## Pending work

- Gateway JWT validation on private routes (see `docs/14-debito-autenticacao.md`).
- Services deriving user identity from JWT token instead of request body.
- Idempotent RabbitMQ consumers with `eventId` and `correlationId`.
- Stock compensation on payment or order failure.
- Integration tests with Testcontainers.
- Observability: Actuator, Prometheus, Grafana, structured logs.
- Production deploy (Kubernetes or Docker Swarm).
