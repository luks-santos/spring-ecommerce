# Gateway Service

Single entry point for all client requests. Routes to downstream services via Eureka service discovery.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Cloud Gateway (reactive)
- Spring Cloud Netflix Eureka Client

## Port

`8080`

## Routes

| Prefix | Target service | Strip prefix |
|--------|---------------|--------------|
| `/api/user/**` | user-service (8081) | 2 |
| `/api/product-catalog/**` | product-catalog-service (8082) | 2 |
| `/api/carts/**` | shopping-cart-service (8083) | 1 |
| `/api/orders/**` | order-service (8085) | 1 |
| `/api/payments/**` | payment-service (8086) | 1 |

Note: `StripPrefix=2` on `/api/user/**` means the gateway strips `/api/user` before forwarding, so `/api/user/sign-up` reaches `user-service` as `/sign-up`, and `/api/user/api/account/logged-user` reaches it as `/api/account/logged-user`.

## Running

```powershell
# Via Docker Compose (recommended)
cd backend
docker compose up gateway-service

# Locally (requires Eureka running first)
cd backend/gateway-service
.\mvnw.cmd spring-boot:run
```

## Troubleshooting

- `503 Service Unavailable`: target service not registered with Eureka. Check http://localhost:8761.
- `404 Not Found`: no route matches the request path.
