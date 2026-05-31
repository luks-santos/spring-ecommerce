package com.ecommerce.order_service.consumers;

import com.ecommerce.order_service.events.PaymentProcessedEvent;
import com.ecommerce.order_service.repositories.ProcessedEventRepo;
import com.ecommerce.order_service.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.time.Instant;
import java.util.UUID;

import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class PaymentEventConsumerTest {

    @Mock
    private OrderService orderService;

    @Mock
    private ProcessedEventRepo processedEventRepo;

    private PaymentEventConsumer consumer;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        consumer = new PaymentEventConsumer(orderService, processedEventRepo);
    }

    private PaymentProcessedEvent event(String eventId) {
        return new PaymentProcessedEvent(
                eventId, UUID.randomUUID().toString(), "payment-service",
                Instant.now(), UUID.randomUUID(), UUID.randomUUID(), "SUCCESS");
    }

    @Test
    void firstDeliveryIsProcessedAndRecorded() {
        PaymentProcessedEvent e = event("evt-1");
        when(processedEventRepo.existsByEventId("evt-1")).thenReturn(false);

        consumer.handlePaymentSuccess(e);

        verify(orderService).confirmPayment(e.orderId(), e.paymentId());
        verify(processedEventRepo).save(org.mockito.ArgumentMatchers.any());
    }

    @Test
    void redeliveryOfSameEventIsSkipped() {
        PaymentProcessedEvent e = event("evt-1");
        when(processedEventRepo.existsByEventId("evt-1")).thenReturn(true);

        consumer.handlePaymentSuccess(e);

        verify(orderService, never()).confirmPayment(org.mockito.ArgumentMatchers.any(), org.mockito.ArgumentMatchers.any());
        verify(processedEventRepo, never()).save(org.mockito.ArgumentMatchers.any());
    }
}
