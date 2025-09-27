package com.ecommerce.product_catalog_service.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

public record ProductCreateDTO(@NotBlank(message = "O nome do produto é obrigatório")
                               @Size(min = 3, max = 100, message = "O nome deve ter entre 3 e 100 caracteres")
                               String name,
                               @Size(max = 500, message = "A descrição deve ter no máximo 500 caracteres")
                               String description,
                               @NotNull(message = "O preço é obrigatório")
                               @Positive(message = "O preço deve ser maior que zero")
                               BigDecimal price,
                               @NotNull(message = "O ID da categoria é obrigatório")
                               UUID categoryId
) {
}
