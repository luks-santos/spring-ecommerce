# Shopping Cart Service

Microservice responsible for managing shopping cart operations in the e-commerce platform.

## Technologies

- **Java 25**
- **Spring Boot 3.5.14**
- **Spring Cloud 2025.0.2**
- **PostgreSQL** - Database
- **Flyway** - Database migration
- **RabbitMQ** - Event messaging
- **Eureka Client** - Service discovery
- **OpenAPI/Swagger** - API documentation
- **Lombok** - Code generation

## Features

- ✅ Create and manage shopping carts per user
- ✅ Add items to cart
- ✅ Update item quantities
- ✅ Remove items from cart
- ✅ Clear entire cart
- ✅ Calculate cart totals (subtotal, total items)
- ✅ Automatic cart creation for new users
- ✅ Validation of cart operations
- ✅ Integration with Product Catalog via product IDs
- ✅ Event publishing to RabbitMQ (checkout, abandoned carts)
- ✅ Service registration with Eureka

## Architecture

### Package Structure

```
com.ecommerce.shopping_cart_service/
├── config/              # RabbitMQ configuration
├── controllers/         # REST API endpoints
├── dto/                 # Data Transfer Objects
├── entities/            # JPA entities
├── exceptions/          # Custom exceptions
├── repositories/        # JPA repositories
└── services/            # Business logic
```

### Database Schema

**shopping_carts**
- `id` (UUID, PK)
- `user_id` (UUID, UNIQUE)
- `created_at` (TIMESTAMP)
- `updated_at` (TIMESTAMP)

**cart_items**
- `id` (UUID, PK)
- `cart_id` (UUID, FK → shopping_carts)
- `product_id` (UUID)
- `quantity` (INTEGER)
- `price` (DECIMAL)
- `created_at` (TIMESTAMP)

## API Endpoints

### Shopping Cart Operations

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/carts/user/{userId}` | Get cart by user ID |
| POST | `/api/carts/user/{userId}/items` | Add item to cart |
| PUT | `/api/carts/user/{userId}/items/{itemId}` | Update item quantity |
| DELETE | `/api/carts/user/{userId}/items/{itemId}` | Remove item from cart |
| DELETE | `/api/carts/user/{userId}/clear` | Clear all items from cart |
| DELETE | `/api/carts/user/{userId}` | Delete cart |

### Request/Response Examples

**Add Item to Cart**
```json
POST /api/carts/user/{userId}/items

{
  "productId": "550e8400-e29b-41d4-a716-446655440000",
  "quantity": 2,
  "price": 99.90
}
```

**Update Item Quantity**
```json
PUT /api/carts/user/{userId}/items/{itemId}

{
  "quantity": 5
}
```

**Cart Response**
```json
{
  "id": "660e8400-e29b-41d4-a716-446655440001",
  "userId": "770e8400-e29b-41d4-a716-446655440002",
  "items": [
    {
      "id": "880e8400-e29b-41d4-a716-446655440003",
      "productId": "550e8400-e29b-41d4-a716-446655440000",
      "quantity": 2,
      "price": 99.90,
      "subtotal": 199.80,
      "createdAt": "2025-11-16T10:30:00"
    }
  ],
  "totalAmount": 199.80,
  "totalItems": 2,
  "createdAt": "2025-11-16T10:00:00",
  "updatedAt": "2025-11-16T10:30:00"
}
```

## Configuration

### Database Setup

Create PostgreSQL database:
```sql
CREATE DATABASE cart_db;
```

### Environment Variables

Configure in `application-dev.yml`:

```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/cart_db
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
  port: 8083
```

## Running the Service

### Prerequisites

- Java 25
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

- **Application**: http://localhost:8083
- **Swagger UI**: http://localhost:8083/swagger-ui.html
- **API Docs**: http://localhost:8083/api-docs
- **Health Check**: http://localhost:8083/actuator/health

## RabbitMQ Events

### Published Events

**Cart Checkout Event**
- Exchange: `cart.exchange`
- Routing Key: `cart.checkout`
- Queue: `cart.checkout.queue`

**Cart Abandoned Event**
- Exchange: `cart.exchange`
- Routing Key: `cart.abandoned`
- Queue: `cart.abandoned.queue`

## Business Rules

1. **One Cart Per User**: Each user can have only one active cart
2. **Unique Items**: A product can appear only once in a cart (quantity is updated if added again)
3. **Positive Quantities**: Quantity and price must be greater than zero
4. **Automatic Creation**: Cart is created automatically when first item is added
5. **Cascade Delete**: Deleting a cart removes all its items

## Integration with Other Services

- **Product Catalog Service**: Product IDs reference the catalog for price validation
- **Order Service**: Cart data is used to create orders during checkout
- **Notification Service**: Events published for abandoned cart reminders

## Error Handling

All errors return a standard format:

```json
{
  "message": "Error description",
  "path": "/api/carts/user/{userId}",
  "status": 404,
  "error": "Not Found",
  "timestamp": "2025-11-16 10:30:00"
}
```

### Common Error Codes

- `400 Bad Request` - Invalid input (negative quantity, invalid price)
- `404 Not Found` - Cart or item not found
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

- `V20251116000001__create_table_shopping_carts.sql`
- `V20251116000002__create_table_cart_items.sql`

## Development

### Code Style

- Use Lombok annotations to reduce boilerplate
- Follow REST best practices
- Implement proper validation on DTOs
- Use meaningful exception messages
- Log important operations

### Adding New Features

1. Create entities in `entities/`
2. Create repositories in `repositories/`
3. Implement business logic in `services/`
4. Create DTOs in `dto/`
5. Create controllers in `controllers/`
6. Add Flyway migrations if schema changes

## Production Considerations

- [ ] Implement Redis caching for frequently accessed carts
- [ ] Add scheduled task to clean abandoned carts
- [ ] Implement cart expiration policy
- [ ] Add monitoring and metrics (Prometheus)
- [ ] Implement rate limiting
- [ ] Add comprehensive integration tests
- [ ] Set up CI/CD pipeline

## License

Part of the Spring E-commerce Platform project.

## Author

Lucas Santos
