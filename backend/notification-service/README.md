# Notification Service

Consumes RabbitMQ events and sends notifications via email or console. No REST endpoints exposed.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring AMQP (RabbitMQ)
- Spring Mail + Thymeleaf (HTML email templates)
- H2 in-memory (notification log)
- Spring Cloud Netflix Eureka Client

## Port

`8084`

## RabbitMQ events consumed

| Event | Exchange | Routing key | Action |
|-------|----------|------------|--------|
| user.registration | user.exchange | user.registration | Sends welcome notification |
| order.confirmation | order.exchange | order.confirmation | Sends order confirmation notification |

## Email templates

Located in `src/main/resources/templates/`:
- `welcome-email.html`: variables — `fullName`, `username`, `email`, `loginUrl`.
- `order-confirmation-email.html`: variables — `orderId`, `totalAmount`, `orderStatus`, `orderTrackingUrl`.

## Notification strategies

Controlled by `notification.email.provider` config:
- `console` (default for development): prints notification to stdout.
- `gmail`: sends via Gmail SMTP.

## Configuration

```yaml
notification:
  email:
    provider: console   # or gmail
    from: noreply@ecommerce.com

# Gmail SMTP (only needed when provider=gmail)
spring:
  mail:
    host: smtp.gmail.com
    port: 587
    username: your-email@gmail.com
    password: your-app-password
```

## Environment variables

| Variable | Description |
|----------|-------------|
| `SPRING_RABBITMQ_HOST` | RabbitMQ host |
| `NOTIFICATION_EMAIL_PROVIDER` | `console` or `gmail` |
| `SPRING_MAIL_USERNAME` | SMTP username (gmail only) |
| `SPRING_MAIL_PASSWORD` | SMTP password (gmail only) |

## Running

```powershell
# Via Docker Compose (recommended)
cd backend
docker compose up notification-service

# Locally (requires RabbitMQ)
cd backend/notification-service
.\mvnw.cmd spring-boot:run
```

## Tests

```powershell
cd backend/notification-service
.\mvnw.cmd test
```

## Monitoring

- RabbitMQ Management: http://localhost:15672 (guest/guest)
- H2 Console: http://localhost:8084/h2-console (notification log, dev only)
