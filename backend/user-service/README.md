# User Service
The User Service is a microservice responsible for managing user-related operations in the Scalable E-Commerce Platform. 
It handles user registration, authentication, profile management, and other essential user functions.

## Key Features
- User registration and profile creation 
- User authentication and authorization

## API Documentation
The API documentation is available at [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html) after starting the service. 
It contains details about endpoints, parameters, responses, and usage examples.

## Tech Stack
- Java 23 with Maven
- Spring Boot 3.3.9
- Spring Security with JWT
- MySQL 8.0

## Running the Service Locally
1. Ensure Java 23 and Maven are installed. 
2. Navigate to the `/user-service` directory.  
3. Build the project with Maven:
   ```sh
    mvn clean package
   ```
4. Run the service with Spring Boot:
   ```sh
    mvn spring-boot:run
   ```
   
## Running Tests
In `maven-surefire-plugin`, there is a configuration `argLine` that must be changed based on your operating system.

For Windows:
   ```xml
   <argLine>@{argLine}
            -javaagent:${settings.localRepository}\org\mockito\mockito-core\${mockito.version}\mockito-core-${mockito.version}.jar
   </argLine>
   ```
For Unix-based systems:
   ```xml
   <argLine>@{argLine}
      -javaagent:${settings.localRepository}/org/mockito/mockito-core/${mockito.version}/mockito-core-${mockito.version}.jar
   </argLine>
   ```

1. To run the unit and integration tests, use the following command:
```sh
  mvn test 
```

## Running with Docker
To run the service using Docker, follow these steps:
1. Build the Docker image:
   ```sh
    docker build -t user-service .
   ```
2. Run the Docker container:
   ```sh
    docker run -p 8080:8080 --name user-service-container user-service
   ```