package com.ecommerce.product_catalog_service.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record InventoryCreateDTO(
        @NotNull(message = "O ID do produto é obrigatório")
        UUID productId,
        @NotNull(message = "A quantidade é obrigatória")
        @Min(value = 0, message = "A quantidade não pode ser negativa")
        int quantity
) {
}
