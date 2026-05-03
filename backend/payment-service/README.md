# Payment Service

Microservice responsible for managing payment operations in the e-commerce platform.

## Technologies

- **Java 23**
- **Spring Boot 3.5.14**
- **Spring Cloud 2025.0.2**
- **PostgreSQL** - Database
- **Flyway** - Database migration
- **RabbitMQ** - Event messaging
- **Eureka Client** - Service discovery
- **OpenAPI/Swagger** - API documentation
- **Lombok** - Code generation

## Features

- ✅ Create payments for orders
- ✅ Process payments (simulated)
- ✅ Confirm successful payments
- ✅ Handle payment failures
- ✅ Refund payments (full or partial)
- ✅ Track payment transaction history
- ✅ Query payments by user, order, status
- ✅ Integration with multiple payment providers (simulated)
- ✅ Event publishing to RabbitMQ
- ✅ Service registration with Eureka

## Architecture

### Package Structure

```
com.ecommerce.payment_service/
├── config/              # RabbitMQ configuration
├── controllers/         # REST API endpoints
├── dto/                 # Data Transfer Objects
├── entities/            # JPA entities
├── enums/               # Payment enums
├── exceptions/          # Custom exceptions
├── repositories/        # JPA repositories
└── services/            # Business logic
```

### Database Schema

**payments**
- `id` (UUID, PK)
- `order_id` (UUID)
- `user_id` (UUID)
- `amount` (DECIMAL)
- `currency` (VARCHAR(3))
- `payment_method` (VARCHAR(50))
- `provider` (VARCHAR(50))
- `provider_transaction_id` (VARCHAR(255))
- `status` (VARCHAR(50))
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

**payment_transactions**
- `id` (UUID, PK)
- `payment_id` (UUID, FK → payments)
- `type` (VARCHAR(50))
- `amount` (DECIMAL)
- `status` (VARCHAR(50))
- `provider_response` (TEXT)
- `created_at` (TIMESTAMP)

## Payment Status Flow

```
PENDING → PROCESSING → SUCCESS
PENDING → PROCESSING → FAILED
SUCCESS → REFUND_REQUESTED → REFUNDED
Any → CANCELLED
```

### Available Statuses

- `PENDING` - Payment created, awaiting processing
- `PROCESSING` - Payment being processed
- `SUCCESS` - Payment successful
- `FAILED` - Payment failed
- `REFUND_REQUESTED` - Refund requested
- `REFUNDED` - Payment refunded
- `CANCELLED` - Payment cancelled

### Payment Methods

- `CREDIT_CARD` - Credit card
- `DEBIT_CARD` - Debit card
- `PIX` - PIX (Brazil)
- `PAYPAL` - PayPal
- `BANK_TRANSFER` - Bank transfer
- `BOLETO` - Boleto (Brazil)

### Payment Providers

- `STRIPE` - Stripe
- `PAYPAL` - PayPal
- `PAGSEGURO` - PagSeguro (Brazil)
- `MERCADO_PAGO` - Mercado Pago (Brazil)
- `INTERNAL` - Internal processing

### Transaction Types

- `CHARGE` - Payment charge
- `REFUND` - Payment refund
- `CAPTURE` - Payment capture
- `AUTHORIZATION` - Payment authorization
- `CANCELLATION` - Payment cancellation

## API Endpoints

### Payment Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/payments` | Create payment |
| POST | `/api/payments/{paymentId}/process` | Process payment |
| POST | `/api/payments/{paymentId}/confirm` | Confirm payment |
| POST | `/api/payments/{paymentId}/fail` | Fail payment |
| POST | `/api/payments/{paymentId}/refund` | Refund payment |
| GET | `/api/payments/{paymentId}` | Get payment by ID |
| GET | `/api/payments/order/{orderId}` | Get payment by order |
| GET | `/api/payments/user/{userId}` | Get payments by user |
| GET | `/api/payments/{paymentId}/transactions` | Get transaction history |

### Request/Response Examples

**Create Payment**
```json
POST /api/payments

{
  "orderId": "880e8400-e29b-41d4-a716-446655440003",
  "userId": "770e8400-e29b-41d4-a716-446655440002",
  "amount": 349.70,
  "currency": "BRL",
  "paymentMethod": "CREDIT_CARD",
  "provider": "STRIPE"
}
```

**Refund Payment**
```json
POST /api/payments/{paymentId}/refund?amount=349.70&reason=Customer%20request
```

**Payment Response**
```json
{
  "id": "990e8400-e29b-41d4-a716-446655440004",
  "orderId": "880e8400-e29b-41d4-a716-446655440003",
  "userId": "770e8400-e29b-41d4-a716-446655440002",
  "amount": 349.70,
  "currency": "BRL",
  "paymentMethod": "CREDIT_CARD",
  "provider": "STRIPE",
  "providerTransactionId": "TXN-abc123-def456",
  "status": "SUCCESS",
  "createdAt": "2025-11-16T10:00:00",
  "updatedAt": "2025-11-16T10:01:00"
}
```

## Configuration

### Database Setup

Create PostgreSQL database:
```sql
CREATE DATABASE payment_db;
```

### Environment Variables

Configure in `application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/payment_db
    username: postgres
    password: postgres

  rabbitmq:
    host: localhost
    port: 5672
    username: guest
    password: guest

eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/

server:
  port: 8086
```

## Running the Service

### Prerequisites

- Java 23
- PostgreSQL running on port 5432
- RabbitMQ running on port 5672
- Eureka Server running on port 8761

### Build and Run

```bash
# Build
mvn clean install

# Run with dev profile (default)
mvn spring-boot:run

# Run with specific profile
mvn spring-boot:run -Pdev
mvn spring-boot:run -Phomolog
mvn spring-boot:run -Pprod
```

### Access Points

- **Application**: http://localhost:8086
- **Swagger UI**: http://localhost:8086/swagger-ui.html
- **API Docs**: http://localhost:8086/api-docs
- **Health Check**: http://localhost:8086/actuator/health

## RabbitMQ Events

### Published Events

**Payment Success**
- Exchange: `payment.exchange`
- Routing Key: `payment.success`
- Queue: `payment.success.queue`

**Payment Failed**
- Exchange: `payment.exchange`
- Routing Key: `payment.failed`
- Queue: `payment.failed.queue`

**Payment Refunded**
- Exchange: `payment.exchange`
- Routing Key: `payment.refunded`
- Queue: `payment.refunded.queue`

## Business Rules

1. **One Payment Per Order**: Each order can have only one payment
2. **Amount Validation**: Payment amount must be greater than zero
3. **Refund Validation**: Only successful payments can be refunded
4. **Refund Amount**: Refund amount cannot exceed original payment amount
5. **Status Transitions**: Validated to prevent invalid state changes
6. **Transaction History**: All payment actions are tracked in transactions table
7. **Idempotency**: Payment creation checks for existing payment for order

## Payment Processing

The payment processing is **simulated** in this implementation. In a production environment, you would integrate with real payment gateways:

- **Stripe**: Use Stripe Java SDK
- **PayPal**: Use PayPal Java SDK
- **PagSeguro/Mercado Pago**: Use their REST APIs

### Simulation Details

- 90% success rate for payment processing
- Automatic transaction ID generation
- Transaction history tracking

## Integration with Other Services

- **Order Service**: Receives order_id and updates order with payment_id
- **Notification Service**: Events trigger payment confirmation emails
- **User Service**: Tracks payment history per user

## Error Handling

All errors return a standard format:

```json
{
  "message": "Error description",
  "path": "/api/payments/{paymentId}",
  "status": 404,
  "error": "Not Found",
  "timestamp": "2025-11-16 10:30:00"
}
```

### Common Error Codes

- `400 Bad Request` - Invalid input or business rule violation
- `404 Not Found` - Payment not found
- `500 Internal Server Error` - Unexpected errors

## Testing

```bash
# Run tests
mvn test

# Run with coverage
mvn test jacoco:report
```

## Database Migrations

Flyway migrations are located in `src/main/resources/db/migration/`:

- `V20251116000001__create_table_payments.sql`
- `V20251116000002__create_table_payment_transactions.sql`

## Development

### Code Style

- Use Lombok annotations to reduce boilerplate
- Follow REST best practices
- Implement proper validation on DTOs
- Use meaningful exception messages
- Log important operations
- Track all payment actions in transactions

### Adding Payment Providers

To add a new payment provider:

1. Add enum value to `PaymentProvider`
2. Implement provider-specific logic in `PaymentService`
3. Add provider configuration in `application.yml`
4. Update documentation

## Production Considerations

- [ ] Implement real payment gateway integrations (Stripe, PayPal, etc.)
- [ ] Add webhook endpoints for payment confirmations
- [ ] Implement payment encryption for sensitive data
- [ ] Add PCI DSS compliance measures
- [ ] Implement idempotency keys for duplicate prevention
- [ ] Add comprehensive integration tests
- [ ] Implement retry logic for failed payment attempts
- [ ] Add monitoring and alerting for payment failures
- [ ] Implement rate limiting
- [ ] Set up CI/CD pipeline
- [ ] Add payment reconciliation mechanism
- [ ] Implement fraud detection

## Security

- [ ] Encrypt payment card data
- [ ] Use HTTPS for all payment endpoints
- [ ] Implement tokenization for card details
- [ ] Add 3D Secure support
- [ ] Validate webhook signatures
- [ ] Implement request signing
- [ ] Add audit logging for all payment operations

## License

Part of the Spring E-commerce Platform project.

## Author

Lucas Santos
