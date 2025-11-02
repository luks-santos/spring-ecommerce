# Notification Service
The Notification Service is a microservice responsible for managing email notifications in the Scalable E-Commerce Platform.
It handles asynchronous message consumption from RabbitMQ and sends emails for various events such as user registration and order confirmation.

## Key Features
- Email notification system with HTML templates
- RabbitMQ message consumption for event-driven architecture
- Support for multiple email providers (Gmail, Console for testing)
- Template Method and Strategy design patterns implementation
- Dynamic email content using Thymeleaf templates
- Notification logging for audit and monitoring

## Architecture
The service uses an **event-driven architecture** where it consumes events from RabbitMQ queues:
- **User Registration Events**: Sends welcome emails to new users
- **Order Confirmation Events**: Sends order confirmation emails with tracking links

### Design Patterns
- **Template Method Pattern**: Defines the notification sending workflow
- **Strategy Pattern**: Allows switching between different email providers
- **Observer Pattern**: Consumes events from RabbitMQ queues

## Tech Stack
- Java 25 with Maven
- Spring Boot 3.5.7
- Spring AMQP (RabbitMQ integration)
- Spring Mail (Email sending)
- Thymeleaf (HTML email templates)
- H2 Database (In-memory for notification logs)
- Spring Cloud Netflix Eureka Client

## RabbitMQ Configuration
The service listens to the following queues:
- `user.registration.queue`: User registration events
- `order.confirmation.queue`: Order confirmation events

### Exchanges and Routing Keys
- **User Exchange**: `user.exchange` with routing key `user.registration`
- **Order Exchange**: `order.exchange` with routing key `order.confirmation`

## Email Templates
Located in `src/main/resources/templates/`:
- `welcome-email.html`: Welcome email for new users
- `order-confirmation-email.html`: Order confirmation email

Templates use dynamic variables:
- **Welcome Email**: `fullName`, `username`, `email`, `loginUrl`
- **Order Confirmation**: `orderId`, `totalAmount`, `orderStatus`, `orderTrackingUrl`

## Configuration
Key configuration properties in `application.yml`:

```yaml
# RabbitMQ
spring.rabbitmq.host: localhost
spring.rabbitmq.port: 5672

# Email Provider (console or gmail)
notification.email.provider: console
notification.email.from: noreply@ecommerce.com

# Gmail SMTP (if using gmail provider)
spring.mail.host: smtp.gmail.com
spring.mail.port: 587
spring.mail.username: your-email@gmail.com
spring.mail.password: your-app-password

# Frontend URL for email links
app.frontend.url: http://localhost:8080
```

### Email Providers
- **console**: Logs emails to console (for development/testing)
- **gmail**: Sends real emails via Gmail SMTP

## Running the Service Locally
1. Ensure Java 25 and Maven are installed.
2. Start RabbitMQ:
   ```sh
   docker run -d --name rabbitmq -p 5672:5672 -p 15672:15672 rabbitmq:3-management
   ```
3. Build the project with Maven:
   ```sh
   mvn clean package
   ```
4. Run the service with Spring Boot:
   ```sh
   mvn spring-boot:run
   ```

## Running Tests
To run the unit and integration tests, use the following command:
```sh
mvn test
```

## Running with Docker
To run the service using Docker, follow these steps:
1. Build the Docker image:
   ```sh
   docker build -t notification-service .
   ```
2. Run the Docker container:
   ```sh
   docker run -p 8084:8084 --name notification-service-container notification-service
   ```

## Docker Compose
The service is included in the main `docker-compose.yml` with RabbitMQ:
```sh
cd backend
docker-compose up notification-service
```

## Environment Variables
The following environment variables can be configured:
- `SPRING_RABBITMQ_HOST`: RabbitMQ host (default: localhost)
- `SPRING_RABBITMQ_PORT`: RabbitMQ port (default: 5672)
- `APP_FRONTEND_URL`: Frontend URL for email links
- `NOTIFICATION_EMAIL_PROVIDER`: Email provider (console/gmail)
- `SPRING_MAIL_USERNAME`: SMTP username (for Gmail)
- `SPRING_MAIL_PASSWORD`: SMTP password (for Gmail)

## Monitoring
- **RabbitMQ Management UI**: http://localhost:15672 (guest/guest)
- **H2 Console**: http://localhost:8084/h2-console
- **Service Port**: 8084

## API Endpoints
This service doesn't expose REST endpoints as it operates asynchronously through RabbitMQ message consumption.

## Publishing Events (From Other Services)
To trigger notifications from other microservices, publish events to RabbitMQ:

### User Registration Event
```java
UserRegistrationEvent event = new UserRegistrationEvent();
event.setUserId(1L);
event.setEmail("user@example.com");
event.setFullName("John Doe");
event.setUsername("johndoe");

rabbitTemplate.convertAndSend(
    "user.exchange",
    "user.registration",
    event
);
```

### Order Confirmation Event
```java
OrderConfirmationEvent event = new OrderConfirmationEvent();
event.setOrderId(123L);
event.setUserId(1L);
event.setUserEmail("user@example.com");
event.setTotalAmount(new BigDecimal("99.99"));
event.setOrderStatus("CONFIRMED");

rabbitTemplate.convertAndSend(
    "order.exchange",
    "order.confirmation",
    event
);
```

## Future Enhancements
- Dead Letter Queue (DLQ) implementation for failed messages
- SMS notifications support
- Push notifications support
- Multi-language email templates
- Rate limiting for email sending
- Retry mechanism with exponential backoff
- Prometheus metrics integration
