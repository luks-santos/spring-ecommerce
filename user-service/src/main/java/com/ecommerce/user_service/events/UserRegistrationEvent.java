package com.ecommerce.user_service.events;

import java.time.LocalDateTime;
import java.util.UUID;

public record UserRegistrationEvent(
        String eventId,
        LocalDateTime timestamp,
        UUID userId,
        String fullName,
        String username,
        String email
) {
    public static UserRegistrationEvent from(UUID userId, String firstName, String lastName, String email) {
        String fullName = (firstName + " " + lastName).trim();
        return new UserRegistrationEvent(
                UUID.randomUUID().toString(),
                LocalDateTime.now(),
                userId,
                fullName,
                email,
                email
        );
    }
}
