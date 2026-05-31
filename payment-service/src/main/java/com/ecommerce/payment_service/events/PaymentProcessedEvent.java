package com.ecommerce.payment_service.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Typed event published whenever a payment reaches a terminal state
 * (success, failure or refund).
 *
 * <p>The first four fields are the shared event metadata (debt guide, Step 6):
 * a unique {@code eventId} so consumers can be idempotent, a {@code correlationId}
 * to trace the business flow, the {@code producer} service name and the
 * {@code occurredAt} timestamp.
 */
public record PaymentProcessedEvent(
        String eventId,
        String correlationId,
        String producer,
        Instant occurredAt,
        UUID paymentId,
        UUID orderId,
        String status
) {
    public static final String PRODUCER = "payment-service";

    public static PaymentProcessedEvent of(UUID paymentId, UUID orderId, String status) {
        return new PaymentProcessedEvent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                PRODUCER,
                Instant.now(),
                paymentId,
                orderId,
                status
        );
    }
}
