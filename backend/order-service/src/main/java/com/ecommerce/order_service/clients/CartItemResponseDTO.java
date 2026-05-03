package com.ecommerce.order_service.clients;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

public record CartItemResponseDTO(
        UUID id,
        UUID productId,
        Integer quantity,
        BigDecimal price,
        BigDecimal subtotal,
        LocalDateTime createdAt
) {
}
