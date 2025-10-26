package com.ecommerce.notification_service.consumers;

import com.ecommerce.notification_service.config.RabbitMQConfig;
import com.ecommerce.notification_service.models.NotificationData;
import com.ecommerce.notification_service.models.events.OrderConfirmationEvent;
import com.ecommerce.notification_service.models.events.UserRegistrationEvent;
import com.ecommerce.notification_service.services.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationConsumer {

    private final NotificationService notificationService;

    /**
     * Escuta eventos de registro de usuário
     */
    @RabbitListener(queues = RabbitMQConfig.USER_REGISTRATION_QUEUE)
    public void handleUserRegistration(UserRegistrationEvent event) {
        log.info("Received user registration event: {}", event);

        try {
            // Preparar variáveis do template
            Map<String, Object> variables = new HashMap<>();
            variables.put("fullName", event.getFullName());
            variables.put("username", event.getUsername());
            variables.put("email", event.getEmail());

            // Criar dados da notificação
            NotificationData notificationData = NotificationData.builder()
                    .recipient(event.getEmail())
                    .subject("Bem-vindo ao E-commerce!")
                    .templateName("welcome-email")
                    .variables(variables)
                    .notificationType("EMAIL")
                    .build();

            // Enviar notificação
            notificationService.sendNotification(notificationData);

        } catch (Exception e) {
            log.error("Error processing user registration event", e);
            // Aqui você pode implementar retry ou enviar para DLQ
        }
    }

    /**
     * Escuta eventos de confirmação de pedido
     */
    @RabbitListener(queues = RabbitMQConfig.ORDER_CONFIRMATION_QUEUE)
    public void handleOrderConfirmation(OrderConfirmationEvent event) {
        log.info("Received order confirmation event: {}", event);

        try {
            Map<String, Object> variables = new HashMap<>();
            variables.put("orderId", event.getOrderId());
            variables.put("totalAmount", event.getTotalAmount());
            variables.put("orderStatus", event.getOrderStatus());

            NotificationData notificationData = NotificationData.builder()
                    .recipient(event.getUserEmail())
                    .subject("Pedido Confirmado - #" + event.getOrderId())
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
