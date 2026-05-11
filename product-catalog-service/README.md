# Product Catalog Service

Manages categories, products, and inventory. Called synchronously by `order-service` to deduct stock when an order is created.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Data JPA, PostgreSQL 16, Flyway
- Spring Cloud Netflix Eureka Client
- Springdoc-OpenAPI

## Port

`8082` — database: `product_db`

## API

### Via Gateway (prefix: `/api/product-catalog`)

**Categories:**

| Method | Route | Description |
|--------|-------|-------------|
| GET | `/api/product-catalog/categories` | List categories |
| GET | `/api/product-catalog/categories/{id}` | Get category by ID |
| POST | `/api/product-catalog/categories` | Create category |
| PUT | `/api/product-catalog/categories/{id}` | Update category |
| DELETE | `/api/product-catalog/categories/{id}` | Delete category |

**Products:**

| Method | Route | Description |
|--------|-------|-------------|
| GET | `/api/product-catalog/products` | List products |
| GET | `/api/product-catalog/products/{id}` | Get product by ID |
| GET | `/api/product-catalog/products/category/{categoryId}` | List by category |
| POST | `/api/product-catalog/products` | Create product |
| PUT | `/api/product-catalog/products/{id}` | Update product |
| DELETE | `/api/product-catalog/products/{id}` | Delete product |

**Inventory:**

| Method | Route | Description |
|--------|-------|-------------|
| GET | `/api/product-catalog/inventories` | List inventories |
| GET | `/api/product-catalog/inventories/product/{productId}` | Get by product |
| POST | `/api/product-catalog/inventories` | Create inventory |
| PUT | `/api/product-catalog/inventories/product/{productId}` | Update inventory |
| PATCH | `/api/product-catalog/inventories/product/{productId}/add?qty={n}` | Add stock |
| PATCH | `/api/product-catalog/inventories/product/{productId}/remove?qty={n}` | Remove stock |

## Database schema

| Table | Description |
|-------|-------------|
| categories | id (UUID), name, description, timestamps |
| products | id (UUID), name, description, price, category_id (FK), timestamps |
| inventories | id (UUID), product_id (FK, unique), quantity, reserved_quantity, timestamps |

Migrations in `src/main/resources/db/migration/`.

## Integration

- `order-service` calls `PATCH .../remove?qty={n}` to deduct inventory when creating an order.

## Running

```powershell
# Via Docker Compose (recommended)
cd backend
docker compose up product-catalog-service

# Locally
cd backend/product-catalog-service
.\mvnw.cmd spring-boot:run
```

## Tests

```powershell
cd backend/product-catalog-service
.\mvnw.cmd test
```

Covers: `CategoryController`, `ProductController`, `InventoryController`, `CategoryService`, `ProductService`, `InventoryService`.

## Swagger

- UI: http://localhost:8082/swagger-ui.html
- OpenAPI JSON: http://localhost:8082/v3/api-docs
