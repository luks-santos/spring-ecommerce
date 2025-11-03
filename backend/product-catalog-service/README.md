# Product Catalog Service
The Product Catalog Service is a microservice responsible for managing product listings, categories, inventory, and product information in the Scalable E-Commerce Platform.
It provides a comprehensive product catalog with search, filtering, and inventory management capabilities.

## Key Features
- Product management (creation, update, deletion, retrieval)
- Product catalog browsing with pagination
- Advanced product search and filtering
- Category management and hierarchies
- Inventory tracking and stock management
- Product pricing and discounts
- Product images and media management

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
     │   MySQL Database   │
     │ e-commerce-product │
     └────────────────────┘
```

### Domain Model
- **Product**: Core product entity with details (name, description, price, SKU)
- **Category**: Product categorization and hierarchies
- **Inventory**: Stock tracking and availability
- **ProductImage**: Product media and images

## API Documentation
The API documentation is available at http://localhost:8082/swagger-ui.html after starting the service.
It contains details about endpoints, parameters, responses, and usage examples.

### Key Endpoints
| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/product-catalog/products` | List all products with pagination |
| GET | `/api/product-catalog/products/{id}` | Get product details |
| POST | `/api/product-catalog/products` | Create new product |
| PUT | `/api/product-catalog/products/{id}` | Update product |
| DELETE | `/api/product-catalog/products/{id}` | Delete product |
| GET | `/api/product-catalog/categories` | List categories |
| GET | `/api/product-catalog/products/search` | Search products |

## Tech Stack
- Java 23 with Maven
- Spring Boot 3.x.x
- Spring Data JPA (Data access)
- MySQL 8.0 (Database)
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
    url: jdbc:mysql://localhost:3306/e-commerce-product
    username: root
    password: root
  jpa:
    hibernate:
      ddl-auto: update
```

## Database Setup
The service uses MySQL database named `e-commerce-product`. Make sure to:
1. Have MySQL 8.0 running locally
2. Create the database `e-commerce-product`:
   ```sql
   CREATE DATABASE `e-commerce-product`;
   ```
3. Configure connection details in application-dev.yml (default: root/root)

## Running the Service Locally
1. Ensure Java 23 and Maven are installed.
2. Make sure MySQL is running with the `e-commerce-product` database created.
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
- **Health Check**: http://localhost:8082/actuator/health

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