package com.ecommerce.order_service.events;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

/**
 * Typed event published when an order is created.
 *
 * <p>The first four fields are the shared event metadata (see the debt guide,
 * Step 6): a unique {@code eventId} for idempotency, a {@code correlationId} to
 * trace a business flow across services, the {@code producer} service name and
 * the {@code occurredAt} timestamp.
 */
public record OrderCreatedEvent(
        String eventId,
        String correlationId,
        String producer,
        Instant occurredAt,
        UUID orderId,
        UUID userId,
        BigDecimal totalAmount
) {
    public static final String PRODUCER = "order-service";

    public static OrderCreatedEvent of(UUID orderId, UUID userId, BigDecimal totalAmount) {
        return new OrderCreatedEvent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                PRODUCER,
                Instant.now(),
                orderId,
                userId,
                totalAmount
        );
    }
}
