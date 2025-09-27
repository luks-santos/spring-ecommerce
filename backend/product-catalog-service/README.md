# Product Catalog Service

The Product Catalog Service is a microservice responsible for managing product listings, categories, and inventory in the Scalable E-Commerce Platform. It handles all product-related operations and provides product information to other services.

## Key Features
- Product management (creation, update, deletion)
- Product catalog browsing and search
- Category management
- Inventory tracking and management

## API Documentation
The API documentation is available at http://localhost:8082/swagger-ui.html after starting the service. 
It contains details about endpoints, parameters, responses, and usage examples.

## Tech Stack
- Java 23 with Maven
- Spring Boot 3.x.x
- Spring Data JPA
- MySQL 8.0
- Spring Cloud Netflix Eureka (Service Discovery)

## Configuration
The service is configured to run on port `8082` in development mode, as defined in application-dev.yml.

## Database Setup
The service uses MySQL database named `e-commerce-product`. Make sure to:
1. Have MySQL 8.0 running locally
2. Create the database `e-commerce-product`
3. Configure connection details in application-dev.yml (default: root/root)

## Running the Service Locally
1. Ensure Java and Maven are installed.
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
1. To run the unit and integration tests, use the following command:
```sh
  mvn test 
```