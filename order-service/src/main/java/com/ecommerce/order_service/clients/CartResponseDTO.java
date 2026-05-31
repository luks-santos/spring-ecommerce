package com.ecommerce.order_service.clients;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record CartResponseDTO(
        UUID id,
        UUID userId,
        List<CartItemResponseDTO> items,
        BigDecimal totalAmount,
        Integer totalItems,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {
}
