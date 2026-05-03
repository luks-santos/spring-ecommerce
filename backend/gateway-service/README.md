# Gateway Service
The Gateway Service is the API Gateway and entry point for the Scalable E-Commerce Platform.
It handles request routing, load balancing, and provides a unified interface for all backend microservices.

## Key Features
- Request routing to microservices via Eureka discovery
- Load balancing across service instances
- Cross-cutting concerns like logging and monitoring
- Circuit breaker and resilience patterns
- Centralized API management

## Architecture
The Gateway acts as the **single entry point** for all client requests:

```
┌─────────────┐
│   Clients   │
│ (Web, App)  │
└──────┬──────┘
       │
       │ HTTP Requests
       ▼
┌─────────────────────────────────┐
│     Gateway Service (8080)      │
│   - Route Resolution            │
│   - Load Balancing              │
│   - Service Discovery           │
└──────┬──────────────────────────┘
       │
       │ Discovers services from Eureka
       ▼
┌─────────────────────────────────┐
│   Eureka Server (8761)          │
└──────┬──────────────────────────┘
       │
       │ Routes to registered services
       │
   ┌───┼────┬─────────┬──────────┐
   │   │    │         │          │
   ▼   ▼    ▼         ▼          ▼
┌─────┐ ┌──────┐ ┌─────────┐ ┌────────┐
│User │ │Product│ │Notif.   │ │Order   │
│8081 │ │8082   │ │8084     │ │8083    │
└─────┘ └───────┘ └─────────┘ └────────┘
```

### Gateway Pattern Benefits
- **Single Entry Point**: Clients only need to know one URL
- **Service Discovery Integration**: Automatically routes to healthy instances
- **Dynamic Routing**: No hardcoded service URLs
- **Load Balancing**: Distributes requests across multiple instances
- **Resilience**: Circuit breaker for failing services

## Tech Stack
- Java 25 with Maven
- Spring Boot 3.5.14
- Spring Boot 3.x.x
- Spring Cloud Gateway (Reactive gateway)
- Spring Cloud Netflix Eureka Client (Service Discovery)
- Spring Boot Actuator for monitoring

## Configuration
The service is configured to run on port `8080` in development mode, as defined in application-dev.yml.

### Configured Routes
The gateway automatically routes requests to microservices using service discovery:

| Route Pattern | Target Service | Port | Description |
|--------------|----------------|------|-------------|
| `/api/user/**` | user-service | 8081 | User management endpoints |
| `/api/product-catalog/**` | product-catalog-service | 8082 | Product catalog endpoints |
| `/api/order/**` | order-service | 8083 | Order management endpoints |
| `/api/notification/**` | notification-service | 8084 | Notification endpoints |

### Route Configuration Example
```yaml
spring:
  cloud:
    gateway:
      routes:
        - id: user-service
          uri: lb://user-service
          predicates:
            - Path=/api/user/**
```

## API Documentation
The API documentation is available at http://localhost:8080/actuator after starting the service.

## Running the Service Locally
1. Ensure Java 25 and Maven are installed.
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
To run the unit and integration tests, use the following command:
```sh
mvn test
```

## Running with Docker
To run the service using Docker, follow these steps:
1. Build the Docker image:
   ```sh
   docker build -t gateway-service .
   ```
2. Run the Docker container:
   ```sh
   docker run -p 8080:8080 --name gateway-service-container gateway-service
   ```

## Docker Compose
The service is included in the main `docker-compose.yml`:
```sh
cd backend
docker-compose up gateway-service
```

## Monitoring
- **Gateway Endpoint**: http://localhost:8080
- **Actuator**: http://localhost:8080/actuator
- **Health Check**: http://localhost:8080/actuator/health
- **Gateway Routes**: http://localhost:8080/actuator/gateway/routes

## Request Flow Example
```
1. Client → GET http://localhost:8080/api/user/profile
2. Gateway → Resolves route pattern /api/user/**
3. Gateway → Queries Eureka for user-service instances
4. Gateway → Load balances to healthy instance
5. Gateway → Forwards to http://user-service-instance:8081/profile
6. Gateway → Returns response to client
```

## Troubleshooting
- **503 Service Unavailable**: Target service not registered with Eureka
- **404 Not Found**: No route matches the request path
- **Connection Timeout**: Target service is down or unreachable
- Check Eureka dashboard at http://localhost:8761 to verify service registration
