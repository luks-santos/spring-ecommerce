package com.ecommerce.payment_service.dto;

import com.ecommerce.payment_service.enums.PaymentMethod;
import com.ecommerce.payment_service.enums.PaymentProvider;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record PaymentCreateDTO(
        @NotNull(message = "Order ID is required")
        UUID orderId,

        // Derived from the JWT in the controller, never trusted from the request body
        UUID userId,

        @NotNull(message = "Amount is required")
        @Positive(message = "Amount must be greater than zero")
        BigDecimal amount,

        String currency,

        @NotNull(message = "Payment method is required")
        PaymentMethod paymentMethod,

        @NotNull(message = "Payment provider is required")
        PaymentProvider provider
) {
}
