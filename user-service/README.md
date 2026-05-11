# User Service

Handles user registration, authentication, and profile management. Issues JWT tokens signed with RSA keys.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring Security + JWT/RSA (NimbusDS)
- Spring Data JPA, PostgreSQL 16, Flyway
- Spring Cloud Netflix Eureka Client
- Spring AMQP (RabbitMQ)
- Springdoc-OpenAPI

## Port

`8081` — database: `user_db`

## API

### Via Gateway

| Method | Route | Auth | Description |
|--------|-------|------|-------------|
| POST | `/api/user/sign-up` | Public | Register user, returns JWT |
| POST | `/api/user/sign-in` | Basic Auth | Login, returns JWT |
| POST | `/api/user/refresh-token` | Bearer refresh token | Generate new access token |
| GET | `/api/user/api/account/logged-user` | Bearer access token | Get authenticated user |
| PUT | `/api/user/api/account/update_profile` | Bearer access token | Update user profile |

### Direct (service port)

| Method | Route |
|--------|-------|
| POST | `/sign-up` |
| POST | `/sign-in` |
| POST | `/refresh-token` |
| GET | `/api/account/logged-user` |
| PUT | `/api/account/update_profile` |

## Auth flow

1. `POST /sign-up` → creates user with bcrypt-hashed password, returns access token (15 min) and sets refresh token cookie (15 days).
2. `POST /sign-in` → Basic Auth (email:password), returns access token, sets refresh token cookie.
3. `POST /refresh-token` → validates refresh token cookie, returns new access token.
4. Protected routes → send `Authorization: Bearer <access_token>`.

## Security

Three `SecurityFilterChain` beans:
- `/sign-in/**`: HTTP Basic authentication.
- `/api/**`: JWT Bearer token validation.
- `/refresh-token/**`: JWT refresh token validation.

Swagger routes (`/swagger-ui/**`, `/v3/api-docs/**`) are public.

## RabbitMQ events

Publishes `user.registration` to `user.exchange` after successful registration. Consumed by `notification-service`.

## Database schema

| Table | Description |
|-------|-------------|
| users | id (UUID), email, first_name, last_name, phone, address, password, role, timestamps |
| refresh_tokens | id (UUID), user_id (FK), refresh_token, revoked, timestamps |

Migrations in `src/main/resources/db/migration/`.

## Running

```powershell
# Via Docker Compose (recommended)
cd backend
docker compose up user-service

# Locally
cd backend/user-service
.\mvnw.cmd spring-boot:run
```

## Tests

```powershell
cd backend/user-service
.\mvnw.cmd test
```

Covers: `AuthController`, `UserController`, `UserService`, `UserRepo`, `RefreshTokenRepo`.

## Swagger

- UI: http://localhost:8081/swagger-ui.html
- OpenAPI JSON: http://localhost:8081/v3/api-docs
