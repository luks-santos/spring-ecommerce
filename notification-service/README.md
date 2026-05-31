# Notification Service

Listens to RabbitMQ events and sends notifications by email or console. It has no REST API.

> Part of the [Scalable E-Commerce Platform](../README.md) study project.

## Stack

- Java 25, Spring Boot 3.5.14
- Spring AMQP (RabbitMQ)
- Spring Mail + Thymeleaf (HTML email templates)
- H2 in-memory (notification log)
- Spring Cloud Netflix Eureka Client

## Port

`8084` — H2 in-memory database.

## What to study here

This service is a **pure event consumer**, which makes it the cleanest example of
**decoupling side effects** from the request/response path. Sending a welcome email is not
part of the sign-up HTTP call — `user-service` just publishes a `user.registration` event
and returns; this service reacts whenever it can. The same goes for `order.confirmation`
after a payment. The payoff to internalize: the user-facing request stays fast and does not
fail just because the email system is slow or down.

It is also a small lesson in **contract evolution**. Producers added shared metadata
(`eventId`, `correlationId`, `producer`, `occurredAt`) to their events; this consumer keeps
working because deserialization **ignores unknown fields**. That tolerance is what lets you
evolve an event without breaking every consumer at once. There is still a known mismatch to
fix as an exercise — its `UserRegistrationEvent` types `userId` as `Long` while the producer
sends a `UUID` (issue #11).

Two more things worth a look: **Thymeleaf templates** drive the HTML emails
(`welcome-email`, `order-confirmation-email`), and the delivery channel is a **pluggable
strategy** — `console` (default, prints to stdout, great for local study) or `gmail` (real
SMTP).

## Configuration

```yaml
notification:
  email:
    provider: console   # or "gmail"
    from: noreply@ecommerce.com
```

For `gmail`, also set `SPRING_MAIL_USERNAME` / `SPRING_MAIL_PASSWORD` (Gmail app password).
Useful while studying: RabbitMQ management at http://localhost:15672 (guest/guest) to watch
messages flow.

## Build, run & test

See the [root README](../README.md#running-with-docker) — `docker compose up notification-service`, or `cd notification-service && ./mvnw test`.
