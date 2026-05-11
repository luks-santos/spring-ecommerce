package com.ecommerce.order_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record OrderFromCartDTO(
        @NotNull(message = "User ID is required")
        UUID userId,

        String userEmail,

        @NotBlank(message = "Shipping address is required")
        String shippingAddress
) {
}
