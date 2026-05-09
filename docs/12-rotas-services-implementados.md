# HTTP Routes

All routes are exposed via the Gateway at `http://localhost:8080`.

## Access points

| Component | URL |
|-----------|-----|
| Gateway | http://localhost:8080 |
| Eureka Dashboard | http://localhost:8761 |
| User Service (direct) | http://localhost:8081 |
| Product Catalog Service (direct) | http://localhost:8082 |
| Shopping Cart Service (direct) | http://localhost:8083 |
| Notification Service (direct) | http://localhost:8084 |
| Order Service (direct) | http://localhost:8085 |
| Payment Service (direct) | http://localhost:8086 |
| RabbitMQ Management | http://localhost:15672 |

## Swagger UI

| Service | URL |
|---------|-----|
| User Service | http://localhost:8081/swagger-ui.html |
| Product Catalog | http://localhost:8082/swagger-ui.html |
| Shopping Cart | http://localhost:8083/swagger-ui.html |
| Order Service | http://localhost:8085/swagger-ui.html |
| Payment Service | http://localhost:8086/swagger-ui.html |

Notes:
- `notification-service` has no REST endpoints. It operates via RabbitMQ.
- `eureka-service` and `gateway-service` are infrastructure services with no business API.
- The gateway uses `StripPrefix=2`, so some user-service routes appear with a doubled `/api` prefix externally (e.g. `/api/user/api/account/logged-user`).

---

## User Service

| Method | Route (via Gateway) | Auth | Description |
|--------|---------------------|------|-------------|
| POST | `/api/user/sign-up` | Public | Register user, returns JWT |
| POST | `/api/user/sign-in` | Basic Auth | Login, returns JWT |
| POST | `/api/user/refresh-token` | Bearer refresh token | Generate new access token |
| GET | `/api/user/api/account/logged-user` | Bearer access token | Get authenticated user |
| PUT | `/api/user/api/account/update_profile` | Bearer access token | Update user profile |

**Sign up:**
```json
POST /api/user/sign-up
Content-Type: application/json

{
  "firstName": "Lucas",
  "lastName": "Silva",
  "email": "lucas@example.com",
  "phone": "11999999999",
  "address": "Rua Exemplo, 123",
  "password": "123456"
}
```

**Update profile:**
```json
PUT /api/user/api/account/update_profile
Authorization: Bearer <access_token>
Content-Type: application/json

{
  "firstName": "Lucas",
  "lastName": "Silva",
  "email": "lucas@example.com",
  "phone": "11888888888",
  "address": "Rua Atualizada, 456"
}
```

---

## Product Catalog Service

### Categories

| Method | Route | Description |
|--------|-------|-------------|
| GET | `/api/product-catalog/categories` | List categories |
| GET | `/api/product-catalog/categories/{id}` | Get category by ID |
| POST | `/api/product-catalog/categories` | Create category |
| PUT | `/api/product-catalog/categories/{id}` | Update category |
| DELETE | `/api/product-catalog/categories/{id}` | Delete category |

```json
POST /api/product-catalog/categories
{ "name": "Electronics" }
```

### Products

| Method | Route | Description |
|--------|-------|-------------|
| GET | `/api/product-catalog/products` | List products |
| GET | `/api/product-catalog/products/{id}` | Get product by ID |
| GET | `/api/product-catalog/products/category/{categoryId}` | List products by category |
| POST | `/api/product-catalog/products` | Create product |
| PUT | `/api/product-catalog/products/{id}` | Update product |
| DELETE | `/api/product-catalog/products/{id}` | Delete product |

```json
POST /api/product-catalog/products
{
  "name": "Notebook",
  "description": "Gaming notebook",
  "price": 5999.90,
  "categoryId": "<category-uuid>"
}
```

### Inventory

| Method | Route | Description |
|--------|-------|-------------|
| GET | `/api/product-catalog/inventories` | List inventories |
| GET | `/api/product-catalog/inventories/product/{productId}` | Get inventory by product |
| POST | `/api/product-catalog/inventories` | Create inventory |
| PUT | `/api/product-catalog/inventories/product/{productId}` | Update inventory |
| PATCH | `/api/product-catalog/inventories/product/{productId}/add?qty={n}` | Add stock |
| PATCH | `/api/product-catalog/inventories/product/{productId}/remove?qty={n}` | Remove stock |

```json
POST /api/product-catalog/inventories
{ "productId": "<product-uuid>", "quantity": 10 }
```

---

## Shopping Cart Service

| Method | Route | Description |
|--------|-------|-------------|
| GET | `/api/carts/user/{userId}` | Get or create cart for user |
| POST | `/api/carts/user/{userId}/items` | Add item to cart |
| PUT | `/api/carts/user/{userId}/items/{itemId}` | Update item quantity |
| DELETE | `/api/carts/user/{userId}/items/{itemId}` | Remove item from cart |
| DELETE | `/api/carts/user/{userId}/clear` | Clear cart |
| DELETE | `/api/carts/user/{userId}` | Delete cart |

```json
POST /api/carts/user/{userId}/items
{ "productId": "<product-uuid>", "quantity": 2, "price": 199.90 }

PUT /api/carts/user/{userId}/items/{itemId}
{ "quantity": 3 }
```

---

## Order Service

| Method | Route | Description |
|--------|-------|-------------|
| POST | `/api/orders` | Create order manually |
| POST | `/api/orders/from-cart` | Create order from cart (deducts stock) |
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
  "shippingAddress": "Rua Exemplo, 123"
}
```

**Create order manually:**
```json
POST /api/orders
{
  "userId": "<user-uuid>",
  "userEmail": "user@example.com",
  "shippingAddress": "Rua Exemplo, 123",
  "items": [
    { "productId": "<product-uuid>", "quantity": 1, "unitPrice": 199.90 }
  ]
}
```

**Update status:**
```json
PATCH /api/orders/{orderId}/status
{ "status": "SHIPPED", "notes": "Shipped via Correios" }
```

**Order statuses:** `CREATED`, `WAITING_PAYMENT`, `PAYMENT_CONFIRMED`, `PAID`, `FAILED`, `CANCELLED`, `SHIPPED`.

---

## Payment Service

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

**Payment methods:** `CREDIT_CARD`, `DEBIT_CARD`, `PIX`, `BANK_TRANSFER`.  
**Providers:** `INTERNAL`, `STRIPE`, `PAYPAL`.  
**Payment statuses:** `PENDING`, `PROCESSING`, `SUCCESS`, `FAILED`, `REFUNDED`.

---

## Full MVP flow (recommended test order)

1. `POST /api/user/sign-up` — register user.
2. `POST /api/product-catalog/categories` — create category.
3. `POST /api/product-catalog/products` — create product.
4. `POST /api/product-catalog/inventories` — create inventory with stock.
5. `GET /api/carts/user/{userId}` — get or create cart.
6. `POST /api/carts/user/{userId}/items` — add item.
7. `POST /api/orders/from-cart` — create order (deducts stock, waits for payment).
8. `POST /api/payments` — create payment for the order.
9. `POST /api/payments/{paymentId}/confirm` — confirm payment.
   - `payment-service` publishes `payment.success`.
   - `order-service` consumes it, sets order to `PAYMENT_CONFIRMED`, publishes `order.confirmation`.
   - `notification-service` consumes `order.confirmation` and notifies user.
