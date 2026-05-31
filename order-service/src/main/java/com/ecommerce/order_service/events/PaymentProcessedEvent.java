package com.ecommerce.order_service.events;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.time.Instant;
import java.util.UUID;

/**
 * Payment event consumed from {@code payment.exchange}.
 *
 * <p>This is the order-service local copy of the contract published by
 * payment-service. {@code eventId} is used to make the consumer idempotent.
 * Unknown properties are ignored so the contract can evolve without breaking
 * deserialization.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentProcessedEvent(
        String eventId,
        String correlationId,
        String producer,
        Instant occurredAt,
        UUID paymentId,
        UUID orderId,
        String status
) {
}
