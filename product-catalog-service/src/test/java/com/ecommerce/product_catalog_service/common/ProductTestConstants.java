package com.ecommerce.product_catalog_service.common;

import com.ecommerce.product_catalog_service.dto.ProductCreateDTO;
import com.ecommerce.product_catalog_service.entities.Product;

import java.math.BigDecimal;
import java.util.UUID;


public class ProductTestConstants {

    public static final UUID PRODUCT_ID = UUID.fromString("a290f1ee-6c54-4b01-90e6-d701748f0852");

    public static final String PRODUCT_NAME = "Smartphone";
    public static final String PRODUCT_DESCRIPTION = "Um smartphone moderno";
    public static final BigDecimal PRODUCT_PRICE = BigDecimal.valueOf(999.99);
    public static final String UPDATED_PRODUCT_NAME = "Smartphone Pro";
    public static final String UPDATED_PRODUCT_DESCRIPTION = "Um smartphone profissional";
    public static final BigDecimal UPDATED_PRODUCT_PRICE = BigDecimal.valueOf(1299.99);

    public static final String PRODUCT_NOT_FOUND = "Produto não encontrado com id: ";
    public static final String PRICE_MUST_BE_POSITIVE = "O preço do produto deve ser maior que zero";

    public static Product createProduct() {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setName(PRODUCT_NAME);
        product.setDescription(PRODUCT_DESCRIPTION);
        product.setPrice(PRODUCT_PRICE);
        product.setCategory(CategoryTestConstants.createCategory());
        return product;
    }

    public static Product createUpdatedProduct() {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setName(UPDATED_PRODUCT_NAME);
        product.setDescription(UPDATED_PRODUCT_DESCRIPTION);
        product.setPrice(UPDATED_PRODUCT_PRICE);
        product.setCategory(CategoryTestConstants.createCategory());
        return product;
    }

    public static ProductCreateDTO createProductDTO() {
        return new ProductCreateDTO(
                PRODUCT_NAME,
                PRODUCT_DESCRIPTION,
                PRODUCT_PRICE,
                CategoryTestConstants.CATEGORY_ID
        );
    }

    public static ProductCreateDTO createInvalidPriceProductDTO() {
        return new ProductCreateDTO(
                PRODUCT_NAME,
                PRODUCT_DESCRIPTION,
                BigDecimal.valueOf(-10.0),
                CategoryTestConstants.CATEGORY_ID
        );
    }
}
