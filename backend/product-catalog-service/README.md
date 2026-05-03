# Product Catalog Service
The Product Catalog Service is a microservice responsible for managing product listings, categories, inventory, and product information in the Scalable E-Commerce Platform.
It provides a comprehensive product catalog with search, filtering, and inventory management capabilities.

## Key Features
- Product management (creation, update, deletion, retrieval)
- Product catalog browsing
- Category management
- Inventory tracking and stock management
- Product pricing

## Architecture
The Product Catalog Service follows a **layered architecture** pattern:

```
┌─────────────────────────────────────┐
│     Gateway (8080)                  │
│     /api/product-catalog/**         │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│  Product Catalog Service (8082)     │
│  ┌─────────────────────────────┐   │
│  │   REST Controllers          │   │
│  │   - ProductController       │   │
│  │   - CategoryController      │   │
│  └──────────┬──────────────────┘   │
│             ▼                       │
│  ┌─────────────────────────────┐   │
│  │   Service Layer             │   │
│  │   - ProductService          │   │
│  │   - CategoryService         │   │
│  │   - InventoryService        │   │
│  └──────────┬──────────────────┘   │
│             ▼                       │
│  ┌─────────────────────────────┐   │
│  │   Repository Layer (JPA)    │   │
│  │   - ProductRepository       │   │
│  │   - CategoryRepository      │   │
│  └──────────┬──────────────────┘   │
└─────────────┼───────────────────────┘
              ▼
     ┌────────────────────┐
     │ PostgreSQL Database│
     │     product_db     │
     └────────────────────┘
```

### Domain Model
- **Product**: Core product entity with name, description, price and category
- **Category**: Product categorization
- **Inventory**: Stock tracking and availability

## API Documentation
The API documentation is available at http://localhost:8082/swagger-ui.html after starting the service.
The OpenAPI JSON is available at http://localhost:8082/v3/api-docs.

### Direct Service Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/categories` | List categories |
| GET | `/categories/{id}` | Get category by ID |
| POST | `/categories` | Create category |
| PUT | `/categories/{id}` | Update category |
| DELETE | `/categories/{id}` | Delete category |
| GET | `/products` | List products |
| GET | `/products/{id}` | Get product by ID |
| GET | `/products/category/{categoryId}` | List products by category |
| POST | `/products` | Create product |
| PUT | `/products/{id}` | Update product |
| DELETE | `/products/{id}` | Delete product |
| GET | `/inventories` | List inventories |
| GET | `/inventories/product/{productId}` | Get inventory by product ID |
| POST | `/inventories` | Create inventory |
| PUT | `/inventories/product/{productId}` | Update inventory by product ID |
| PATCH | `/inventories/product/{productId}/add?qty={qty}` | Add quantity to inventory |
| PATCH | `/inventories/product/{productId}/remove?qty={qty}` | Remove quantity from inventory |

### Gateway Endpoints

The Gateway route `/api/product-catalog/**` uses `StripPrefix=2`, so external Gateway routes keep the same service paths after `/api/product-catalog`.

Examples:

| Method | Gateway Endpoint |
|--------|------------------|
| GET | `/api/product-catalog/categories` |
| POST | `/api/product-catalog/products` |
| GET | `/api/product-catalog/inventories/product/{productId}` |

## Tech Stack
- Java 25 with Maven
- Spring Boot 3.5.14
- Spring Data JPA (Data access)
- PostgreSQL 16 (Database)
- Spring Cloud Netflix Eureka Client (Service Discovery)
- Spring Validation (Request validation)
- Lombok (Boilerplate reduction)

## Configuration
The service is configured to run on port `8082` in development mode, as defined in application-dev.yml.

### Key Configurations
```yaml
server:
  port: 8082

spring:
  datasource:
    url: jdbc:postgresql://localhost:5433/product_db
    username: ecommerce
    password: ecommerce
  jpa:
    hibernate:
      ddl-auto: update
```

## Database Setup
The service uses PostgreSQL database named `product_db`. Make sure to:
1. Have PostgreSQL 16 running locally
2. Create the database `product_db`:
   ```sql
   CREATE DATABASE product_db;
   ```
3. Configure connection details in application-dev.yml (default: ecommerce/ecommerce)

## Running the Service Locally
1. Ensure Java 25 and Maven are installed.
2. Make sure PostgreSQL is running with the `product_db` database created.
3. Make sure the Eureka Service is running on port 8761.
4. Build the project with Maven:
   ```sh
   mvn clean package
   ```
5. Run the service with Spring Boot:
   ```sh
   mvn spring-boot:run
   ```

After starting the service, the product catalog API will be available at http://localhost:8082/

The service will automatically register itself with Eureka Service Discovery and be available for routing through the Gateway Service.

## Running Tests
To run the unit and integration tests, use the following command:
```sh
mvn test
```

## Running with Docker
To run the service using Docker, follow these steps:
1. Build the Docker image:
   ```sh
   docker build -t product-catalog-service .
   ```
2. Run the Docker container:
   ```sh
   docker run -p 8082:8082 --name product-catalog-container product-catalog-service
   ```

## Docker Compose
The service is included in the main `docker-compose.yml`:
```sh
cd backend
docker-compose up product-catalog-service
```

## Environment Variables
The following environment variables can be configured:
- `DATABASE_URL`: JDBC connection string
- `DB_DRIVER`: Database driver class
- `DB_USER`: Database username
- `DB_PASSWORD`: Database password

## Monitoring
- **Service Endpoint**: http://localhost:8082
- **Swagger UI**: http://localhost:8082/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8082/v3/api-docs

## Integration with Other Services
- **Gateway Service**: Routes requests from `/api/product-catalog/**`
- **Order Service**: Fetches product details and validates inventory
- **Notification Service**: (Future) Stock alerts and price change notifications

## Future Enhancements
- Product reviews and ratings
- Product recommendations engine
- Elasticsearch integration for advanced search
- Redis caching for frequently accessed products
- Image optimization and CDN integration
- Real-time inventory updates via WebSocket
