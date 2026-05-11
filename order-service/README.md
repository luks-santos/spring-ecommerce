# Order Service

Manages the order lifecycle. Creates orders manually or from a cart, deducts inventory via REST, and reacts to payment events from RabbitMQ.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Data JPA, PostgreSQL 16, Flyway
- Spring AMQP (RabbitMQ)
- Spring Cloud Netflix Eureka Client
- Springdoc-OpenAPI

## Port

`8085` — database: `order_db`

## API

### Via Gateway (prefix: `/api/orders`)

| Method | Route | Description |
|--------|-------|-------------|
| POST | `/api/orders` | Create order manually |
| POST | `/api/orders/from-cart` | Create order from cart |
| GET | `/api/orders/{orderId}` | Get order by ID |
| GET | `/api/orders/user/{userId}` | List orders by user |
| GET | `/api/orders/status/{status}` | List orders by status |
| PATCH | `/api/orders/{orderId}/status` | Update order status |
| POST | `/api/orders/{orderId}/cancel` | Cancel order |
| GET | `/api/orders/{orderId}/history` | Get status history |
| PATCH | `/api/orders/{orderId}/payment/{paymentId}` | Associate payment to order |

**Create order from cart:**
```json
POST /api/orders/from-cart
{
  "userId": "<user-uuid>",
  "userEmail": "user@example.com",
  "shippingAddress": "123 Main St"
}
```

**Create order manually:**
```json
POST /api/orders
{
  "userId": "<user-uuid>",
  "userEmail": "user@example.com",
  "shippingAddress": "123 Main St",
  "items": [
    { "productId": "<product-uuid>", "quantity": 1, "unitPrice": 99.90 }
  ]
}
```

**Update status:**
```json
PATCH /api/orders/{orderId}/status
{ "status": "SHIPPED", "notes": "Shipped via Correios" }
```

## Order status flow

```
CREATED → WAITING_PAYMENT → PAYMENT_CONFIRMED → PAID
                                              → FAILED
                         → CANCELLED
                         → SHIPPED
```

Available statuses: `CREATED`, `WAITING_PAYMENT`, `PAYMENT_CONFIRMED`, `PAID`, `FAILED`, `CANCELLED`, `SHIPPED`.

## Inter-service communication

**On `POST /api/orders/from-cart`:**
1. Calls `shopping-cart-service` (REST) to fetch cart items.
2. For each item, calls `product-catalog-service` (REST) to deduct inventory.
3. Creates order with status `CREATED`.

**RabbitMQ consumed events:**

| Event | Routing key | Action |
|-------|------------|--------|
| payment.success | payment.success | Sets order to `PAYMENT_CONFIRMED`, publishes `order.confirmation` |
| payment.failed | payment.failed | Sets order to `FAILED` |
| payment.refunded | payment.refunded | Updates order accordingly |

**RabbitMQ published events:**

| Event | Exchange | Routing key | Trigger |
|-------|----------|------------|---------|
| order.confirmation | order.exchange | order.confirmation | Payment confirmed |

## Database schema

| Table | Description |
|-------|-------------|
| orders | id (UUID), user_id, user_email, status, total_amount, shipping_address, payment_id, timestamps |
| order_items | id (UUID), order_id (FK), product_id, quantity, unit_price, subtotal |
| order_status_history | id (UUID), order_id (FK), status, notes, timestamps |

Migrations in `src/main/resources/db/migration/`.

## Running

```powershell
# Via Docker Compose (recommended)
cd backend
docker compose up order-service

# Locally (requires PostgreSQL, RabbitMQ, Eureka, shopping-cart-service, and product-catalog-service)
cd backend/order-service
.\mvnw.cmd spring-boot:run
```

## Tests

```powershell
cd backend/order-service
.\mvnw.cmd test
```

## Swagger

- UI: http://localhost:8085/swagger-ui.html
- OpenAPI JSON: http://localhost:8085/v3/api-docs
