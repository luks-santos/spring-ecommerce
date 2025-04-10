package com.ecommerce.product_catalog_service.common;

import com.ecommerce.product_catalog_service.entities.Category;

import java.util.UUID;

public class CategoryTestConstants {

    public static final UUID CATEGORY_ID = UUID.fromString("d290f1ee-6c54-4b01-90e6-d701748f0851");
    public static final String CATEGORY_NAME = "Eletrônicos";
    public static final String UPDATED_CATEGORY_NAME = "Informática";

    public static final String CATEGORY_NOT_FOUND = "Categoria não encontrada com id: ";

    public static Category createCategory() {
        Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setName(CATEGORY_NAME);
        return category;
    }

    public static Category createUpdatedCategory() {
        Category category = new Category();
        category.setId(CATEGORY_ID);
        category.setName(UPDATED_CATEGORY_NAME);
        return category;
    }
}
