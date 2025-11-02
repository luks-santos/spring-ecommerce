# User Service
The User Service is a microservice responsible for managing user-related operations in the Scalable E-Commerce Platform.
It handles user registration, authentication, profile management, and authorization with JWT-based security.

## Key Features
- User registration and profile creation
- User authentication with JWT tokens
- Password encryption with BCrypt
- Role-based access control (RBAC)
- Profile management and updates
- User search and management
- Email validation and verification

## Architecture
The User Service follows a **layered architecture** with security integration:

```
┌─────────────────────────────────────┐
│     Gateway (8080)                  │
│     /api/user/**                    │
└──────────────┬──────────────────────┘
               │
               ▼
┌─────────────────────────────────────┐
│    User Service (8081)              │
│  ┌─────────────────────────────┐   │
│  │   Security Filter Chain     │   │
│  │   - JWT Authentication      │   │
│  │   - Authorization           │   │
│  └──────────┬──────────────────┘   │
│             ▼                       │
│  ┌─────────────────────────────┐   │
│  │   REST Controllers          │   │
│  │   - UserController          │   │
│  │   - AuthController          │   │
│  └──────────┬──────────────────┘   │
│             ▼                       │
│  ┌─────────────────────────────┐   │
│  │   Service Layer             │   │
│  │   - UserService             │   │
│  │   - AuthService             │   │
│  │   - JwtService              │   │
│  └──────────┬──────────────────┘   │
│             ▼                       │
│  ┌─────────────────────────────┐   │
│  │   Repository Layer (JPA)    │   │
│  │   - UserRepository          │   │
│  │   - RoleRepository          │   │
│  └──────────┬──────────────────┘   │
└─────────────┼───────────────────────┘
              ▼
     ┌────────────────────┐
     │   MySQL Database   │
     │  e-commerce-user   │
     └────────────────────┘
```

### Security Architecture
- **JWT Authentication**: Stateless authentication using JSON Web Tokens
- **BCrypt Password Hashing**: Secure password storage
- **Role-Based Access Control**: User roles (ADMIN, USER, CUSTOMER)
- **Security Filter Chain**: Request filtering and validation

### Domain Model
- **User**: Core user entity (username, email, password, roles)
- **Role**: User roles for authorization
- **UserProfile**: Extended user information

## API Documentation
The API documentation is available at http://localhost:8081/swagger-ui.html after starting the service.
It contains details about endpoints, parameters, responses, and usage examples.

### Key Endpoints
| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/api/user/register` | Register new user | No |
| POST | `/api/user/login` | Authenticate user | No |
| GET | `/api/user/profile` | Get user profile | Yes |
| PUT | `/api/user/profile` | Update profile | Yes |
| GET | `/api/user/{id}` | Get user by ID | Yes (Admin) |
| GET | `/api/user/all` | List all users | Yes (Admin) |

## Tech Stack
- Java 23 with Maven
- Spring Boot 3.x.x
- Spring Security with JWT
- Spring Data JPA (Data access)
- MySQL 8.0 (Database)
- BCrypt (Password hashing)
- Spring Cloud Netflix Eureka Client (Service Discovery)
- Lombok (Boilerplate reduction)

## Configuration
The service is configured to run on port `8081` in development mode.

### Key Configurations
```yaml
server:
  port: 8081

spring:
  datasource:
    url: jdbc:mysql://localhost:3306/e-commerce-user
    username: root
    password: root

jwt:
  secret: your-secret-key
  expiration: 86400000
```

## Database Setup
The service uses MySQL database named `e-commerce-user`. Make sure to:
1. Have MySQL 8.0 running locally
2. Create the database `e-commerce-user`:
   ```sql
   CREATE DATABASE `e-commerce-user`;
   ```
3. Configure connection details in application.yml

## Running the Service Locally
1. Ensure Java 23 and Maven are installed.
2. Make sure MySQL is running with the `e-commerce-user` database created.
3. Make sure the Eureka Service is running on port 8761.
4. Build the project with Maven:
   ```sh
   mvn clean package
   ```
5. Run the service with Spring Boot:
   ```sh
   mvn spring-boot:run
   ```

After starting the service, the user API will be available at http://localhost:8081/

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

To run the unit and integration tests, use the following command:
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
   docker run -p 8081:8081 --name user-service-container user-service
   ```

## Docker Compose
The service is included in the main `docker-compose.yml`:
```sh
cd backend
docker-compose up user-service
```

## Environment Variables
The following environment variables can be configured:
- `DATABASE_URL`: JDBC connection string
- `DB_DRIVER`: Database driver class
- `DB_USER`: Database username
- `DB_PASSWORD`: Database password
- `JWT_SECRET`: Secret key for JWT token generation

## Authentication Flow
```
1. User Registration
   POST /api/user/register
   → Create user with encrypted password
   → Return success message

2. User Login
   POST /api/user/login
   → Validate credentials
   → Generate JWT token
   → Return token to client

3. Protected Request
   GET /api/user/profile
   → Include JWT in Authorization header
   → Validate token
   → Return user data
```

## Monitoring
- **Service Endpoint**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **Health Check**: http://localhost:8081/actuator/health

## Integration with Other Services
- **Gateway Service**: Routes requests from `/api/user/**`
- **Notification Service**: Sends welcome emails on user registration
- **Order Service**: Validates user identity for order creation

## Future Enhancements
- OAuth2 integration (Google, Facebook login)
- Two-factor authentication (2FA)
- Password reset via email
- User activity logging
- Session management
- Account verification via email
- User preferences and settings