# Shopping Cart Service

Manages shopping carts per user. Each user has at most one active cart. Called by `order-service` via REST when creating an order from cart.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Data JPA, PostgreSQL 16, Flyway
- Spring Cloud Netflix Eureka Client
- Springdoc-OpenAPI

## Port

`8083` — database: `cart_db`

## API

### Via Gateway (prefix: `/api/carts`)

| Method | Route | Description |
|--------|-------|-------------|
| GET | `/api/carts/user/{userId}` | Get cart (creates if not exists) |
| POST | `/api/carts/user/{userId}/items` | Add item to cart |
| PUT | `/api/carts/user/{userId}/items/{itemId}` | Update item quantity |
| DELETE | `/api/carts/user/{userId}/items/{itemId}` | Remove item |
| DELETE | `/api/carts/user/{userId}/clear` | Clear all items |
| DELETE | `/api/carts/user/{userId}` | Delete cart |

**Add item:**
```json
POST /api/carts/user/{userId}/items
{ "productId": "<uuid>", "quantity": 2, "price": 99.90 }
```

**Update quantity:**
```json
PUT /api/carts/user/{userId}/items/{itemId}
{ "quantity": 3 }
```

**Cart response:**
```json
{
  "id": "<uuid>",
  "userId": "<uuid>",
  "items": [
    { "id": "<uuid>", "productId": "<uuid>", "quantity": 2, "price": 99.90, "subtotal": 199.80 }
  ],
  "totalAmount": 199.80,
  "totalItems": 2
}
```

## Business rules

- One cart per user (unique `user_id`).
- Adding the same product again updates quantity instead of creating a duplicate item.
- Quantity and price must be greater than zero.

## Database schema

| Table | Description |
|-------|-------------|
| shopping_carts | id (UUID), user_id (UUID, unique), timestamps |
| cart_items | id (UUID), cart_id (FK), product_id (UUID), quantity, price, timestamps |

Migrations in `src/main/resources/db/migration/`.

## Running

```powershell
# Via Docker Compose (recommended)
cd backend
docker compose up shopping-cart-service

# Locally (requires PostgreSQL and Eureka)
cd backend/shopping-cart-service
.\mvnw.cmd spring-boot:run
```

## Tests

```powershell
cd backend/shopping-cart-service
.\mvnw.cmd test
```

## Swagger

- UI: http://localhost:8083/swagger-ui.html
- OpenAPI JSON: http://localhost:8083/v3/api-docs
