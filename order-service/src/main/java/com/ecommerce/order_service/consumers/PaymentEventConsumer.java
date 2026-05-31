package com.ecommerce.order_service.consumers;

import com.ecommerce.order_service.config.RabbitMQConfig;
import com.ecommerce.order_service.entities.ProcessedEvent;
import com.ecommerce.order_service.events.PaymentProcessedEvent;
import com.ecommerce.order_service.repositories.ProcessedEventRepo;
import com.ecommerce.order_service.services.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;

import java.util.function.Consumer;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentEventConsumer {

    private final OrderService orderService;
    private final ProcessedEventRepo processedEventRepo;

    @RabbitListener(queues = RabbitMQConfig.ORDER_PAYMENT_SUCCESS_QUEUE)
    public void handlePaymentSuccess(PaymentProcessedEvent event) {
        handleIdempotently(event, e ->
                orderService.confirmPayment(e.orderId(), e.paymentId()));
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_PAYMENT_FAILED_QUEUE)
    public void handlePaymentFailed(PaymentProcessedEvent event) {
        handleIdempotently(event, e ->
                orderService.failPayment(e.orderId(), e.paymentId()));
    }

    @RabbitListener(queues = RabbitMQConfig.ORDER_PAYMENT_REFUNDED_QUEUE)
    public void handlePaymentRefunded(PaymentProcessedEvent event) {
        handleIdempotently(event, e ->
                orderService.refundPayment(e.orderId(), e.paymentId()));
    }

    /**
     * Runs {@code action} at most once per {@code eventId}. RabbitMQ delivers
     * at least once, so a re-delivered message whose id was already recorded is
     * skipped. The id is persisted only after the business logic succeeds, so a
     * failure mid-processing still allows a later retry.
     */
    private void handleIdempotently(PaymentProcessedEvent event, Consumer<PaymentProcessedEvent> action) {
        if (event.eventId() != null && processedEventRepo.existsByEventId(event.eventId())) {
            log.info("Skipping already-processed payment event: {} (order: {})",
                    event.eventId(), event.orderId());
            return;
        }
        log.info("Received payment {} event {} for order: {}, payment: {}",
                event.status(), event.eventId(), event.orderId(), event.paymentId());
        action.accept(event);
        if (event.eventId() != null) {
            processedEventRepo.save(new ProcessedEvent(event.eventId()));
        }
    }
}
