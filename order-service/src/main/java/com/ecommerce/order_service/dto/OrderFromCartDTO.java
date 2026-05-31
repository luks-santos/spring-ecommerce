package com.ecommerce.order_service.dto;

import jakarta.validation.constraints.NotBlank;

import java.util.UUID;

public record OrderFromCartDTO(
        // Derived from the JWT in the controller, never trusted from the request body
        UUID userId,

        String userEmail,

        @NotBlank(message = "Shipping address is required")
        String shippingAddress
) {
}
