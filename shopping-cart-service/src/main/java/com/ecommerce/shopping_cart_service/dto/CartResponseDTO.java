package com.ecommerce.shopping_cart_service.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CartResponseDTO {

    private UUID id;
    private UUID userId;
    private List<CartItemResponseDTO> items;
    private BigDecimal totalAmount;
    private int totalItems;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
