package com.ecommerce.notification_service.consumers;

import com.ecommerce.notification_service.config.RabbitMQConfig;
import com.ecommerce.notification_service.models.NotificationData;
import com.ecommerce.notification_service.models.events.OrderConfirmationEvent;
import com.ecommerce.notification_service.models.events.UserRegistrationEvent;
import com.ecommerce.notification_service.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    @Value("${app.frontend.url:http://localhost:8080}")
    private String frontendUrl;

    @RabbitListener(queues = RabbitMQConfig.USER_REGISTRATION_QUEUE)
    public void handleUserRegistration(UserRegistrationEvent event) {
        log.info("Received user registration event: {}", event);

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("fullName", event.getFullName());
            variables.put("username", event.getUsername());
            variables.put("email", event.getEmail());
            variables.put("loginUrl", frontendUrl + "/login");

            NotificationData notificationData = NotificationData.builder()
                    .recipient(event.getEmail())
                    .subject("Welcome to E-commerce!")
                    .templateName("welcome-email")
                    .variables(variables)
                    .notificationType("EMAIL")
                    .build();

            notificationService.sendNotification(notificationData);

        } catch (Exception e) {
            log.error("Error processing user registration event", e);
        }
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_CONFIRMATION_QUEUE)
    public void handleOrderConfirmation(OrderConfirmationEvent event) {
        log.info("Received order confirmation event: {}", event);

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("orderId", event.getOrderId());
            variables.put("totalAmount", event.getTotalAmount());
            variables.put("orderStatus", event.getOrderStatus());
            variables.put("orderTrackingUrl", frontendUrl + "/orders/" + event.getOrderId());

            NotificationData notificationData = NotificationData.builder()
                    .recipient(event.getUserEmail())
                    .subject("Order Confirmed - #" + event.getOrderId())
                    .templateName("order-confirmation-email")
                    .variables(variables)
                    .notificationType("EMAIL")
                    .build();

            notificationService.sendNotification(notificationData);

        } catch (Exception e) {
            log.error("Error processing order confirmation event", e);
        }
    }
}
