package com.ecommerce.product_catalog_service.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductCreateDTO(String name,
                               String description,
                               BigDecimal price,
                               UUID categoryId
) {
}
