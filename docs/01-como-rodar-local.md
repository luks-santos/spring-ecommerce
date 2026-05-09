# Running locally

## Prerequisites

- Docker and Docker Compose
- Java 25 (only needed to run services outside Docker)
- Maven or the `mvnw`/`mvnw.cmd` wrappers inside each service directory

## Start the full stack

```powershell
cd backend
docker compose up --build
```

## Access points

| Service | URL |
|---------|-----|
| Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| RabbitMQ Management | http://localhost:15672 (guest/guest) |
| User Service | http://localhost:8081 |
| Product Catalog Service | http://localhost:8082 |
| Shopping Cart Service | http://localhost:8083 |
| Notification Service | http://localhost:8084 |
| Order Service | http://localhost:8085 |
| Payment Service | http://localhost:8086 |
| PostgreSQL | localhost:5433 |

## Environment variables

The `docker-compose.yml` accepts these variables with development defaults:

```text
POSTGRES_USER=ecommerce
POSTGRES_PASSWORD=ecommerce
```

Databases created automatically via `init-postgres/`: `user_db`, `product_db`, `cart_db`, `order_db`, `payment_db`.

## Run tests

Tests use H2 in-memory and do not require PostgreSQL or RabbitMQ.

```powershell
cd backend/<service-name>
.\mvnw.cmd test
```

## Swagger UI

Available per service after the stack is running:

| Service | URL |
|---------|-----|
| User Service | http://localhost:8081/swagger-ui.html |
| Product Catalog | http://localhost:8082/swagger-ui.html |
| Shopping Cart | http://localhost:8083/swagger-ui.html |
| Order Service | http://localhost:8085/swagger-ui.html |
| Payment Service | http://localhost:8086/swagger-ui.html |

OpenAPI JSON via Gateway:

| Service | URL |
|---------|-----|
| User Service | http://localhost:8080/api/user/v3/api-docs |
| Product Catalog | http://localhost:8080/api/product-catalog/v3/api-docs |
