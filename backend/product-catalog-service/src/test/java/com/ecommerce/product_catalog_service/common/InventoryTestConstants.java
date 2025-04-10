package com.ecommerce.product_catalog_service.common;

import com.ecommerce.product_catalog_service.dto.InventoryCreateDTO;
import com.ecommerce.product_catalog_service.entities.Inventory;


public class InventoryTestConstants {
    public static final int INVENTORY_QUANTITY = 10;
    public static final int INVENTORY_ADD_QUANTITY = 5;
    public static final int INVENTORY_REMOVE_QUANTITY = 3;
    public static final int UPDATED_ADD_INVENTORY_QUANTITY = 15;
    public static final int UPDATED_REMOVE_INVENTORY_QUANTITY = 7;

    public static final String INVENTORY_NOT_FOUND = "Estoque não encontrado para o produto com id: ";
    public static final String INVENTORY_ALREADY_EXISTS = "Já existe um registro de estoque para este produto";
    public static final String NEGATIVE_QUANTITY = "A quantidade em estoque não pode ser negativa";
    public static final String ADD_QUANTITY_ZERO = "A quantidade a ser adicionada deve ser maior que zero";
    public static final String REMOVE_QUANTITY_ZERO = "A quantidade a ser removida deve ser maior que zero";
    public static final String INSUFFICIENT_INVENTORY = "Quantidade insuficiente em estoque. Disponível: ";

    public static Inventory createInventory() {
        Inventory inventory = new Inventory();
        inventory.setProduct(ProductTestConstants.createProduct());
        inventory.setQuantity(INVENTORY_QUANTITY);
        return inventory;
    }

    public static Inventory createInventoryAddQty() {
        Inventory inventory = new Inventory();
        inventory.setProduct(ProductTestConstants.createProduct());
        inventory.setQuantity(UPDATED_ADD_INVENTORY_QUANTITY);
        return inventory;
    }

    public static Inventory createInventoryRemoveQty() {
        Inventory inventory = new Inventory();
        inventory.setProduct(ProductTestConstants.createProduct());
        inventory.setQuantity(UPDATED_REMOVE_INVENTORY_QUANTITY);
        return inventory;
    }

    public static InventoryCreateDTO createInventoryDTO() {
        return new InventoryCreateDTO(
                ProductTestConstants.PRODUCT_ID,
                INVENTORY_QUANTITY
        );
    }
}
