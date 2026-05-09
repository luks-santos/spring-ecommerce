# Swagger / OpenAPI

## Services with Swagger configured

| Service | Swagger UI | OpenAPI JSON |
|---------|-----------|--------------|
| user-service | http://localhost:8081/swagger-ui.html | http://localhost:8081/v3/api-docs |
| product-catalog-service | http://localhost:8082/swagger-ui.html | http://localhost:8082/v3/api-docs |
| shopping-cart-service | http://localhost:8083/swagger-ui.html | http://localhost:8083/v3/api-docs |
| order-service | http://localhost:8085/swagger-ui.html | http://localhost:8085/v3/api-docs |
| payment-service | http://localhost:8086/swagger-ui.html | http://localhost:8086/v3/api-docs |

`notification-service`, `eureka-service`, and `gateway-service` do not expose Swagger.

## Via Gateway

| Service | URL |
|---------|-----|
| user-service | http://localhost:8080/api/user/v3/api-docs |
| product-catalog-service | http://localhost:8080/api/product-catalog/v3/api-docs |

For Swagger UI, access the service directly by port. The Springdoc UI resolves assets relative to the host, which may not work correctly behind the gateway's `StripPrefix=2` filter.

## Security schemes (user-service)

The user-service Swagger registers two security schemes:
- `bearer-key`: JWT Bearer token for `/api/**` routes.
- `basic-auth`: HTTP Basic for `/sign-in`.

## Validation checklist

With the stack running (`docker compose up --build`):

1. http://localhost:8081/swagger-ui.html — user-service UI.
2. http://localhost:8082/swagger-ui.html — product-catalog UI.
3. http://localhost:8083/swagger-ui.html — shopping-cart UI.
4. http://localhost:8085/swagger-ui.html — order-service UI.
5. http://localhost:8086/swagger-ui.html — payment-service UI.
6. http://localhost:8080/api/user/v3/api-docs — user-service OpenAPI JSON via gateway.
7. http://localhost:8080/api/product-catalog/v3/api-docs — product-catalog OpenAPI JSON via gateway.
