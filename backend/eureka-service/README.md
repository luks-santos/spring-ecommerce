# Eureka Service

This project is an Eureka server, used for service registration and discovery within a microservices architecture.

## Tech Stack

- Java 23 with Maven
- Spring Boot 3.x.x
- Spring Cloud Netflix Eureka

## Configuration

The service is configured to run on port `8761`, as defined in application.yml.


## Running the Service Locally
1. Ensure Java 23 and Maven are installed.
2Build the project with Maven:
   ```sh
    mvn clean package
   ```
4. Run the service with Spring Boot:
   ```sh
    mvn spring-boot:run
   ```

After starting the service, access the Eureka dashboard at http://localhost:8761/

Now, microservices can register themselves and be discovered automatically by the Eureka Server.



