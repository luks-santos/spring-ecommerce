# User Service
The User Service is a microservice responsible for managing user-related operations in the Scalable E-Commerce Platform.
It handles user registration, authentication, profile management, and authorization with JWT-based security.

## Key Features
- User registration and profile creation
- User authentication with JWT tokens
- Password encryption with BCrypt
- Role-based access control (RBAC)
- Profile management and updates
- Email validation
- Refresh token persistence and logout token revocation

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
│  │   - RefreshTokenRepository  │   │
│  └──────────┬──────────────────┘   │
└─────────────┼───────────────────────┘
              ▼
     ┌────────────────────┐
     │ PostgreSQL Database│
     │      user_db       │
     └────────────────────┘
```

### Security Architecture
- **JWT Authentication**: Stateless authentication using JSON Web Tokens
- **BCrypt Password Hashing**: Secure password storage
- **Role-Based Access Control**: User roles (ADMIN, USER, CUSTOMER)
- **Security Filter Chain**: Request filtering and validation

### Domain Model
- **User**: Core user entity with first name, last name, email, password, address, phone and role
- **RefreshToken**: Refresh token persistence and revocation state
- **UserRole**: User role enum for authorization

## API Documentation
The API documentation is available at http://localhost:8081/swagger-ui.html after starting the service.
The OpenAPI JSON is available at http://localhost:8081/v3/api-docs.

### Direct Service Endpoints

| Method | Endpoint | Description | Auth Required |
|--------|----------|-------------|---------------|
| POST | `/sign-up` | Register a new user and return JWT tokens | No |
| POST | `/sign-in` | Authenticate using HTTP Basic and return JWT tokens | Yes, Basic Auth |
| POST | `/refresh-token` | Generate a new access token from a refresh token | Yes, Bearer refresh token |
| GET | `/api/account/logged-user` | Get authenticated user profile | Yes, Bearer access token |
| PUT | `/api/account/update_profile` | Update authenticated user profile | Yes, Bearer access token |

### Gateway Endpoints

The Gateway route `/api/user/**` uses `StripPrefix=2`, so external Gateway routes are:

| Method | Gateway Endpoint |
|--------|------------------|
| POST | `/api/user/sign-up` |
| POST | `/api/user/sign-in` |
| POST | `/api/user/refresh-token` |
| GET | `/api/user/api/account/logged-user` |
| PUT | `/api/user/api/account/update_profile` |

## Tech Stack
- Java 25 with Maven
- Spring Boot 3.5.14
- Spring Security with JWT
- Spring Data JPA (Data access)
- PostgreSQL 16 (Database)
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
    url: jdbc:postgresql://localhost:5433/user_db
    username: ecommerce
    password: ecommerce

jwt:
  secret: your-secret-key
  expiration: 86400000
```

## Database Setup
The service uses PostgreSQL database named `user_db`. Make sure to:
1. Have PostgreSQL 16 running locally
2. Create the database `user_db`:
   ```sql
   CREATE DATABASE user_db;
   ```
3. Configure connection details in application.yml

## Running the Service Locally
1. Ensure Java 25 and Maven are installed.
2. Make sure PostgreSQL is running with the `user_db` database created.
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
   POST /sign-up
   → Create user with encrypted password
   → Return access token and refresh token cookie

2. User Login
   POST /sign-in
   → Validate credentials
   → Generate JWT tokens
   → Return access token and refresh token cookie

3. Protected Request
   GET /api/account/logged-user
   → Include JWT in Authorization header
   → Validate token
   → Return user data
```

## Monitoring
- **Service Endpoint**: http://localhost:8081
- **Swagger UI**: http://localhost:8081/swagger-ui.html
- **OpenAPI JSON**: http://localhost:8081/v3/api-docs

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
