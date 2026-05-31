package com.ecommerce.user_service.events;

import java.time.Instant;
import java.util.UUID;

/**
 * Event published when a user registers.
 *
 * <p>The first four fields are the shared event metadata (debt guide, Step 6):
 * a unique {@code eventId} for idempotency, a {@code correlationId} to trace the
 * flow across services, the {@code producer} service name and the
 * {@code occurredAt} timestamp.
 */
public record UserRegistrationEvent(
        String eventId,
        String correlationId,
        String producer,
        Instant occurredAt,
        UUID userId,
        String fullName,
        String username,
        String email
) {
    public static final String PRODUCER = "user-service";

    public static UserRegistrationEvent from(UUID userId, String firstName, String lastName, String email) {
        String fullName = (firstName + " " + lastName).trim();
        return new UserRegistrationEvent(
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                PRODUCER,
                Instant.now(),
                userId,
                fullName,
                email,
                email
        );
    }
}
