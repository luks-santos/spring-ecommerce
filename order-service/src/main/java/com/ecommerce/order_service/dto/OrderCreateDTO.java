package com.ecommerce.order_service.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;
import java.util.UUID;

public record OrderCreateDTO(
        // Derived from the JWT in the controller, never trusted from the request body
        UUID userId,

        String userEmail,

        @NotBlank(message = "Shipping address is required")
        String shippingAddress,

        @NotEmpty(message = "Order must have at least one item")
        @Valid
        List<OrderItemDTO> items
) {
}
