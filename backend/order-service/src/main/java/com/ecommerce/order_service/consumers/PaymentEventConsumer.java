package com.ecommerce.order_service.consumers;

import com.ecommerce.order_service.config.RabbitMQConfig;
import com.ecommerce.order_service.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderService orderService;

    @RabbitListener(queues = RabbitMQConfig.ORDER_PAYMENT_SUCCESS_QUEUE)
    public void handlePaymentSuccess(Map<String, Object> event) {
        UUID orderId = getUuid(event, "orderId");
        UUID paymentId = getUuid(event, "id");
        log.info("Received payment success event for order: {}, payment: {}", orderId, paymentId);
        orderService.confirmPayment(orderId, paymentId);
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_PAYMENT_FAILED_QUEUE)
    public void handlePaymentFailed(Map<String, Object> event) {
        UUID orderId = getUuid(event, "orderId");
        UUID paymentId = getUuid(event, "id");
        log.info("Received payment failed event for order: {}, payment: {}", orderId, paymentId);
        orderService.failPayment(orderId, paymentId);
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_PAYMENT_REFUNDED_QUEUE)
    public void handlePaymentRefunded(Map<String, Object> event) {
        UUID orderId = getUuid(event, "orderId");
        UUID paymentId = getUuid(event, "id");
        log.info("Received payment refunded event for order: {}, payment: {}", orderId, paymentId);
        orderService.refundPayment(orderId, paymentId);
    }

    private UUID getUuid(Map<String, Object> event, String key) {
        Object value = event.get(key);
        if (value instanceof UUID uuid) {
            return uuid;
        }
        if (value instanceof String text) {
            return UUID.fromString(text);
        }
        throw new IllegalArgumentException("Event field is not a UUID: " + key);
    }
}
