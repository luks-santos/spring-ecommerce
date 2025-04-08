package com.ecommerce.product_catalog_service.dto;

import java.util.UUID;

public record InventoryCreateDTO(UUID productId, int quantity) {
}
