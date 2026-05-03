package com.ecommerce.order_service.dto;

import com.ecommerce.order_service.enums.OrderStatus;
import jakarta.validation.constraints.NotNull;

public record OrderUpdateStatusDTO(
        @NotNull(message = "Status is required")
        OrderStatus status,

        String notes
) {
}
