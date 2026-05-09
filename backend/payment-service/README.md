# Payment Service

Manages the payment lifecycle with simulated processing. Publishes events to RabbitMQ on payment confirmation, failure, or refund.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Data JPA, PostgreSQL 16, Flyway
- Spring AMQP (RabbitMQ)
- Spring Cloud Netflix Eureka Client
- Springdoc-OpenAPI

## Port

`8086` — database: `payment_db`

## API

### Via Gateway (prefix: `/api/payments`)

| Method | Route | Description |
|--------|-------|-------------|
| POST | `/api/payments` | Create payment |
| POST | `/api/payments/{paymentId}/process` | Process payment (simulated) |
| POST | `/api/payments/{paymentId}/confirm` | Confirm payment |
| POST | `/api/payments/{paymentId}/fail` | Fail payment |
| POST | `/api/payments/{paymentId}/refund` | Refund payment |
| GET | `/api/payments/{paymentId}` | Get payment by ID |
| GET | `/api/payments/order/{orderId}` | Get payment by order |
| GET | `/api/payments/user/{userId}` | List payments by user |
| GET | `/api/payments/{paymentId}/transactions` | List transactions |

**Create payment:**
```json
POST /api/payments
{
  "orderId": "<order-uuid>",
  "userId": "<user-uuid>",
  "amount": 199.90,
  "currency": "BRL",
  "paymentMethod": "PIX",
  "provider": "INTERNAL"
}
```

## Payment methods

`CREDIT_CARD`, `DEBIT_CARD`, `PIX`, `BANK_TRANSFER`

## Providers

`INTERNAL` (simulated), `STRIPE`, `PAYPAL`

## Payment status flow

```
PENDING → PROCESSING → SUCCESS
                     → FAILED
SUCCESS → REFUNDED
```

## RabbitMQ events

| Event | Exchange | Routing key | Trigger |
|-------|----------|------------|---------|
| payment.success | payment.exchange | payment.success | Payment confirmed |
| payment.failed | payment.exchange | payment.failed | Payment failed |
| payment.refunded | payment.exchange | payment.refunded | Payment refunded |

Consumed by `order-service`.

## Database schema

| Table | Description |
|-------|-------------|
| payments | id (UUID), order_id, user_id, amount, currency, payment_method, provider, provider_transaction_id, status, timestamps |
| payment_transactions | id (UUID), payment_id (FK), type, amount, status, provider_response, timestamps |

Transaction types: `AUTHORIZATION`, `CHARGE`, `REFUND`.

Migrations in `src/main/resources/db/migration/`.

## Running

```powershell
# Via Docker Compose (recommended)
cd backend
docker compose up payment-service

# Locally (requires PostgreSQL, RabbitMQ, and Eureka)
cd backend/payment-service
.\mvnw.cmd spring-boot:run
```

## Tests

```powershell
cd backend/payment-service
.\mvnw.cmd test
```

## Swagger

- UI: http://localhost:8086/swagger-ui.html
- OpenAPI JSON: http://localhost:8086/v3/api-docs
