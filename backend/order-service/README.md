# Order Service

Microservice responsible for managing order operations in the e-commerce platform.

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

- ✅ Create orders from cart items
- ✅ Manage order lifecycle and status
- ✅ Track order history
- ✅ Cancel orders
- ✅ Update order status with validation
- ✅ Query orders by user, status, date
- ✅ Automatic order status history tracking
- ✅ Integration with Payment Service
- ✅ Event publishing to RabbitMQ
- ✅ Service registration with Eureka

## Architecture

### Package Structure

```
com.ecommerce.order_service/
├── config/              # RabbitMQ configuration
├── controllers/         # REST API endpoints
├── dto/                 # Data Transfer Objects
├── entities/            # JPA entities
├── enums/               # OrderStatus enum
├── exceptions/          # Custom exceptions
├── repositories/        # JPA repositories
└── services/            # Business logic
```

### Database Schema

**orders**
- `id` (UUID, PK)
- `user_id` (UUID)
- `status` (VARCHAR(50))
- `total_amount` (DECIMAL)
- `shipping_address` (TEXT)
- `payment_id` (UUID)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

**order_items**
- `id` (UUID, PK)
- `order_id` (UUID, FK → orders)
- `product_id` (UUID)
- `quantity` (INTEGER)
- `unit_price` (DECIMAL)
- `subtotal` (DECIMAL)

**order_status_history**
- `id` (UUID, PK)
- `order_id` (UUID, FK → orders)
- `status` (VARCHAR(50))
- `notes` (TEXT)
- `created_at` (TIMESTAMP)

## Order Status Flow

```
CREATED → PAYMENT_PENDING → PAYMENT_CONFIRMED → PROCESSING →
SHIPPED → DELIVERED → COMPLETED

Exception States:
PAYMENT_FAILED → CANCELLED
PROCESSING → REFUNDED
```

### Available Statuses

- `CREATED` - Order created
- `PAYMENT_PENDING` - Waiting for payment
- `PAYMENT_CONFIRMED` - Payment successful
- `PROCESSING` - Order being processed
- `SHIPPED` - Order shipped
- `DELIVERED` - Order delivered
- `COMPLETED` - Order completed
- `PAYMENT_FAILED` - Payment failed
- `CANCELLED` - Order cancelled
- `REFUNDED` - Order refunded

## API Endpoints

### Order Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/orders` | Create order |
| GET | `/api/orders/{orderId}` | Get order by ID |
| GET | `/api/orders/user/{userId}` | Get orders by user |
| GET | `/api/orders/status/{status}` | Get orders by status |
| PATCH | `/api/orders/{orderId}/status` | Update order status |
| POST | `/api/orders/{orderId}/cancel` | Cancel order |
| GET | `/api/orders/{orderId}/history` | Get status history |
| PATCH | `/api/orders/{orderId}/payment/{paymentId}` | Update payment ID |

### Request/Response Examples

**Create Order**
```json
POST /api/orders

{
  "userId": "770e8400-e29b-41d4-a716-446655440002",
  "shippingAddress": "123 Main St, City, State, ZIP",
  "items": [
    {
      "productId": "550e8400-e29b-41d4-a716-446655440000",
      "quantity": 2,
      "unitPrice": 99.90
    },
    {
      "productId": "660e8400-e29b-41d4-a716-446655440001",
      "quantity": 1,
      "unitPrice": 149.90
    }
  ]
}
```

**Update Order Status**
```json
PATCH /api/orders/{orderId}/status

{
  "status": "SHIPPED",
  "notes": "Shipped via FedEx - Tracking: 1234567890"
}
```

**Order Response**
```json
{
  "id": "880e8400-e29b-41d4-a716-446655440003",
  "userId": "770e8400-e29b-41d4-a716-446655440002",
  "status": "PROCESSING",
  "totalAmount": 349.70,
  "shippingAddress": "123 Main St, City, State, ZIP",
  "paymentId": "990e8400-e29b-41d4-a716-446655440004",
  "items": [
    {
      "id": "aa0e8400-e29b-41d4-a716-446655440005",
      "productId": "550e8400-e29b-41d4-a716-446655440000",
      "quantity": 2,
      "unitPrice": 99.90,
      "subtotal": 199.80
    },
    {
      "id": "bb0e8400-e29b-41d4-a716-446655440006",
      "productId": "660e8400-e29b-41d4-a716-446655440001",
      "quantity": 1,
      "unitPrice": 149.90,
      "subtotal": 149.90
    }
  ],
  "totalItems": 3,
  "createdAt": "2025-11-16T10:00:00",
  "updatedAt": "2025-11-16T10:30:00"
}
```

## Configuration

### Database Setup

Create PostgreSQL database:
```sql
CREATE DATABASE order_db;
```

### Environment Variables

Configure in `application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/order_db
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
  port: 8085
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

- **Application**: http://localhost:8085
- **Swagger UI**: http://localhost:8085/swagger-ui.html
- **API Docs**: http://localhost:8085/api-docs
- **Health Check**: http://localhost:8085/actuator/health

## RabbitMQ Events

### Published Events

**Order Created**
- Exchange: `order.exchange`
- Routing Key: `order.created`
- Queue: `order.created.queue`

**Order Payment Confirmed**
- Exchange: `order.exchange`
- Routing Key: `order.confirmed`
- Queue: `order.confirmed.queue`

**Order Shipped**
- Exchange: `order.exchange`
- Routing Key: `order.shipped`
- Queue: `order.shipped.queue`

**Order Delivered**
- Exchange: `order.exchange`
- Routing Key: `order.delivered`
- Queue: `order.delivered.queue`

**Order Cancelled**
- Exchange: `order.exchange`
- Routing Key: `order.cancelled`
- Queue: `order.cancelled.queue`

## Business Rules

1. **Order Creation**: Requires at least one item with valid product ID and price
2. **Status Transitions**: Validated to prevent invalid state changes
3. **Cancellation**: Cannot cancel shipped, delivered, or completed orders
4. **Status History**: All status changes are automatically tracked
5. **Total Calculation**: Automatically calculated from order items
6. **Immutable Completed Orders**: Completed, cancelled, and refunded orders cannot be modified

## Status Transition Rules

- `CREATED` → Can transition to any status
- `PAYMENT_PENDING` → `PAYMENT_CONFIRMED` or `PAYMENT_FAILED`
- `PAYMENT_FAILED` → `CANCELLED`
- `PAYMENT_CONFIRMED` → `PROCESSING`
- `PROCESSING` → `SHIPPED`, `CANCELLED`, or `REFUNDED`
- `SHIPPED` → `DELIVERED` (cannot be cancelled)
- `DELIVERED` → `COMPLETED`
- `CANCELLED` → Terminal state (no transitions)
- `COMPLETED` → Terminal state (no transitions)
- `REFUNDED` → Terminal state (no transitions)

## Integration with Other Services

- **Shopping Cart Service**: Orders are created from cart data
- **Payment Service**: Payment confirmation updates order status
- **Notification Service**: Events trigger email notifications
- **Product Catalog**: Product IDs reference the catalog

## Error Handling

All errors return a standard format:

```json
{
  "message": "Error description",
  "path": "/api/orders/{orderId}",
  "status": 404,
  "error": "Not Found",
  "timestamp": "2025-11-16 10:30:00"
}
```

### Common Error Codes

- `400 Bad Request` - Invalid input or status transition
- `404 Not Found` - Order not found
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

- `V20251116000001__create_table_orders.sql`
- `V20251116000002__create_table_order_items.sql`
- `V20251116000003__create_table_order_status_history.sql`

## Development

### Code Style

- Use Lombok annotations to reduce boilerplate
- Follow REST best practices
- Implement proper validation on DTOs
- Use meaningful exception messages
- Log important operations
- Validate status transitions

### Adding New Features

1. Create entities in `entities/`
2. Create repositories in `repositories/`
3. Implement business logic in `services/`
4. Create DTOs in `dto/`
5. Create controllers in `controllers/`
6. Add Flyway migrations if schema changes
7. Update RabbitMQ config for new events

## Production Considerations

- [ ] Implement distributed transaction handling
- [ ] Add comprehensive integration tests
- [ ] Implement order timeout mechanism
- [ ] Add monitoring and metrics (Prometheus)
- [ ] Implement rate limiting
- [ ] Add order search and filtering
- [ ] Implement pagination for order lists
- [ ] Set up CI/CD pipeline
- [ ] Add inventory reservation logic
- [ ] Implement compensation patterns for failures

## License

Part of the Spring E-commerce Platform project.

## Author

Lucas Santos
