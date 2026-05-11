# Scalable E-Commerce Platform

This is a scalable e-commerce platform project based on a microservices architecture, designed to create a robust, efficient, and maintainable solution for managing an online store.

The system is built using Java, Spring Boot, PostgreSQL, RabbitMQ, Eureka, Spring Cloud Gateway, and Docker, following a microservices architecture that allows each service to be developed, deployed, and scaled independently.

The project follows the roadmap outlined in [Scalable E-Commerce Platform](https://roadmap.sh/projects/scalable-ecommerce-platform), which provides best practices and key steps for building an e-commerce platform using microservices.

## Main Microservices
- **User Service**: Manages user registration, authentication, and profiles.
- **Product Catalog Service**: Manages the product catalog, categories, and inventory.
- **Shopping Cart Service**: Manages users' shopping carts.
- **Order Service**: Processes and manages user orders.
- **Payment Service**: Handles payments.
- **Notification Service**: Sends email or SMS notifications for events like order confirmation and shipping updates.

## Additional Components
- **Service Discovery (Eureka)**: Automatically detects service instances and manages communication between them.
- **PostgreSQL**: Relational database used by stateful services, with one database per bounded service context.
- **Docker**: Uses Docker containers to isolate, package, and deploy each microservice, providing a scalable and portable solution.
- **CI Pipeline**: Automates testing of each microservice.
- **API Gateway**: Routes client requests to the appropriate microservices.
- **Centralized Logging**: Aggregates logs from all microservices to facilitate monitoring and debugging.
