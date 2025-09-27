# Gateway Service

The Gateway Service is a microservice responsible for request routing, centralized authentication, and security management in the Scalable E-Commerce Platform. It acts as the entry point for all client requests and routes them to the appropriate microservices.

## Key Features
- Request routing to microservices
- Centralized authentication and authorization
- Load balancing across service instances
- Cross-cutting concerns like logging and monitoring

## API Documentation
The API documentation is available at http://localhost:8080/actuator after starting the service. 
It contains details about endpoints, parameters, responses, and usage examples.

## Tech Stack
- Java 23 with Maven
- Spring Boot 3.x.x
- Spring Cloud Gateway
- Spring Cloud Netflix Eureka (Service Discovery)

## Configuration
The service is configured to run on port `8080` in development mode, as defined in application-dev.yml.

### Configured Routes
- **User Service**: Routes requests from `/api/user/**` to the user-service
- **Product Catalog Service**: Routes requests from `/api/product-catalog/**` to the product-catalog-service

## Running the Service Locally
1. Ensure Java and Maven are installed.
2. Make sure the Eureka Service is running on port 8761.
3. Build the project with Maven:
   ```sh
    mvn clean package
   ```
4. Run the service with Spring Boot:
   ```sh
    mvn spring-boot:run
   ```

After starting the service, the gateway will be available at http://localhost:8080/

The gateway will automatically discover and route requests to registered microservices via Eureka Service Discovery.

## Running Tests
1. To run the unit and integration tests, use the following command:
```sh
  mvn test 
```