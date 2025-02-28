## Backend - Scalable E-Commerce Platform

The backend is responsible for managing the core logic of the e-commerce platform, providing API services for communication between microservices and clients. It is implemented using Java and Spring Boot.

## Main Microservices
- **User Service**: Manages user registration, authentication, and profiles.  
~~- **Product Catalog Service**: Manages the product catalog, categories, and inventory.~~  
~~- **Shopping Cart Service**: Manages users' shopping carts.~~  
~~- **Order Service**: Processes and manages user orders.~~  
~~- **Payment Service**: Handles payments, integrating with payment gateways such as Stripe or PayPal.~~  
~~- **Notification Service**: Sends email or SMS notifications for events like order confirmation and shipping updates.~~  

## Additional Components
- **Service Discovery (Eureka)**: Automatically detects service instances and manages communication between them.  
- **Docker**: Uses Docker containers to isolate, package, and deploy each microservice, providing a scalable and portable solution. 
- **CI Pipeline**: Automates testing of each microservice. 
~~- **API Gateway**: Routes client requests to the appropriate microservices.~~  
~~- **Centralized Logging**: Aggregates logs from all microservices to facilitate monitoring and debugging.~~  


