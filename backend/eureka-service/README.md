# Eureka Service
The Eureka Service is the service registry and discovery server for the Scalable E-Commerce Platform.
It enables microservices to register themselves and discover other services dynamically without hardcoded URLs.

## Key Features
- Service registration and discovery
- Health monitoring of registered services
- Load balancing support
- Real-time service registry updates
- Web dashboard for monitoring registered services

## Architecture
The Eureka Server acts as the **central registry** in the microservices architecture:

```
┌─────────────────────────────────────────┐
│         Eureka Server (8761)            │
│   Service Registry & Discovery          │
└──────────────┬──────────────────────────┘
               │
               │ Registration & Heartbeat
               │
    ┌──────────┼──────────┬──────────┬──────────┐
    │          │          │          │          │
┌───▼───┐  ┌──▼────┐  ┌──▼────┐  ┌──▼────┐  ┌──▼────┐
│Gateway│  │ User  │  │Product│  │Notif. │  │Order  │
│(8080) │  │(8081) │  │(8082) │  │(8084) │  │(8083) │
└───────┘  └───────┘  └───────┘  └───────┘  └───────┘
```

### Service Discovery Pattern
- **Self-Registration**: Services register themselves on startup
- **Client-Side Discovery**: Services query Eureka to find other services
- **Health Checks**: Eureka monitors service health via heartbeats
- **Automatic De-registration**: Unhealthy services are removed automatically

## Tech Stack
- Java 25 with Maven
- Spring Boot 3.5.14
- Spring Boot 3.x.x
- Spring Cloud Netflix Eureka Server
- Spring Boot Actuator for health monitoring

## Configuration
The service is configured to run on port `8761`, as defined in application.yml.

### Key Configurations
```yaml
server:
  port: 8761

eureka:
  client:
    register-with-eureka: false
    fetch-registry: false
  server:
    enable-self-preservation: false
```

## Registered Services
The following services register with Eureka:
- **gateway-service** (8080): API Gateway
- **user-service** (8081): User management
- **product-catalog-service** (8082): Product catalog
- **notification-service** (8084): Email notifications
- **order-service** (8083): Order management

## Running the Service Locally
1. Ensure Java 25 and Maven are installed.
2. Build the project with Maven:
   ```sh
   mvn clean package
   ```
3. Run the service with Spring Boot:
   ```sh
   mvn spring-boot:run
   ```

After starting the service, access the Eureka dashboard at http://localhost:8761/

Now, microservices can register themselves and be discovered automatically by the Eureka Server.

## Running with Docker
To run the service using Docker, follow these steps:
1. Build the Docker image:
   ```sh
   docker build -t eureka-service .
   ```
2. Run the Docker container:
   ```sh
   docker run -p 8761:8761 --name eureka-service-container eureka-service
   ```

## Docker Compose
The service is included in the main `docker-compose.yml`:
```sh
cd backend
docker-compose up eureka-service
```

## Monitoring
- **Eureka Dashboard**: http://localhost:8761
- **Service Health**: Shows registered services and their status
- **Instances**: Displays all running instances of each service

## Service Registration Example
Microservices register with Eureka by including these dependencies and configuration:

```xml
<dependency>
    <groupId>org.springframework.cloud</groupId>
    <artifactId>spring-cloud-starter-netflix-eureka-client</artifactId>
</dependency>
```

```yaml
eureka:
  client:
    service-url:
      defaultZone: http://localhost:8761/eureka/
  instance:
    prefer-ip-address: true
```

## Troubleshooting
- **Service not appearing**: Check if the service has `@EnableDiscoveryClient` annotation
- **Service marked DOWN**: Verify service health endpoint is accessible
- **Registration delay**: Normal delay of 30 seconds for registration to appear



