# Eureka Service

Service registry and discovery server. All other services register here on startup and use Eureka to resolve service addresses dynamically.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Cloud Netflix Eureka Server

## Port

`8761`

## Registered services

| Service | Port |
|---------|------|
| gateway-service | 8080 |
| user-service | 8081 |
| product-catalog-service | 8082 |
| shopping-cart-service | 8083 |
| notification-service | 8084 |
| order-service | 8085 |
| payment-service | 8086 |

## Access points

- Dashboard: http://localhost:8761

## Running

```powershell
# Via Docker Compose (recommended)
cd backend
docker compose up eureka-service

# Locally
cd backend/eureka-service
.\mvnw.cmd spring-boot:run
```

## Configuration

```yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: false
```
