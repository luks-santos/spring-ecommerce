package com.ecommerce.product_catalog_service.services;

import com.ecommerce.product_catalog_service.common.InventoryTestConstants;
import com.ecommerce.product_catalog_service.common.ProductTestConstants;
import com.ecommerce.product_catalog_service.dto.InventoryCreateDTO;
import com.ecommerce.product_catalog_service.entities.Inventory;
import com.ecommerce.product_catalog_service.entities.Product;
import com.ecommerce.product_catalog_service.exceptions.BadRequestException;
import com.ecommerce.product_catalog_service.exceptions.InsufficientInventoryException;
import com.ecommerce.product_catalog_service.exceptions.ResourceNotFoundException;
import com.ecommerce.product_catalog_service.repositories.InventoryRepo;
import com.ecommerce.product_catalog_service.repositories.ProductRepo;
import lombok.Setter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class InventoryServiceTest {

    @Mock
    private InventoryRepo inventoryRepo;

    @Mock
    private ProductRepo productRepo;

    @InjectMocks
    private InventoryService inventoryService;

    private Inventory inventory;
    private Product product;
    private InventoryCreateDTO inventoryDTO;

    @BeforeEach
    void setUp() {
        product = ProductTestConstants.createProduct();
        inventory = InventoryTestConstants.createInventory();
        inventoryDTO = InventoryTestConstants.createInventoryDTO();
    }

    @Test
    void shouldFindAllInventories() {
        when(inventoryRepo.findAll()).thenReturn(Collections.singletonList(inventory));

        List<Inventory> inventories = inventoryService.findAll();

        assertEquals(1, inventories.size());
        assertEquals(InventoryTestConstants.INVENTORY_QUANTITY, inventories.getFirst().getQuantity());
        verify(inventoryRepo, times(1)).findAll();
    }

    @Test
    void shouldFindInventoryByProductId() {
        when(inventoryRepo.findByProductId(ProductTestConstants.PRODUCT_ID)).thenReturn(Optional.of(inventory));

        Inventory found = inventoryService.findByProductId(ProductTestConstants.PRODUCT_ID);

        assertNotNull(found);
        assertEquals(InventoryTestConstants.INVENTORY_QUANTITY, found.getQuantity());
        assertEquals(ProductTestConstants.PRODUCT_ID, found.getProduct().getId());
    }

    @Test
    void shouldThrowExceptionWhenInventoryNotFound() {
        when(inventoryRepo.findByProductId(ProductTestConstants.PRODUCT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.findByProductId(ProductTestConstants.PRODUCT_ID)
        );

        assertEquals(InventoryTestConstants.INVENTORY_NOT_FOUND + ProductTestConstants.PRODUCT_ID, exception.getReason());
    }

    @Test
    void shouldCreateInventory() {
        when(productRepo.findById(ProductTestConstants.PRODUCT_ID)).thenReturn(Optional.of(product));
        when(inventoryRepo.findByProductId(ProductTestConstants.PRODUCT_ID)).thenReturn(Optional.empty());
        when(inventoryRepo.save(any(Inventory.class))).thenReturn(inventory);

        Inventory created = inventoryService.create(inventoryDTO);

        assertNotNull(created);
        assertEquals(InventoryTestConstants.INVENTORY_QUANTITY, created.getQuantity());
        verify(inventoryRepo, times(1)).save(any(Inventory.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingInventoryWithNegativeQuantity() {
        @SuppressWarnings("unchecked")
        InventoryCreateDTO invalidDTO = new InventoryCreateDTO(
                ProductTestConstants.PRODUCT_ID,
                -5  // quantidade negativa
        );

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> inventoryService.create(invalidDTO)
        );

        assertEquals(InventoryTestConstants.NEGATIVE_QUANTITY, exception.getReason());
        verify(inventoryRepo, never()).save(any(Inventory.class));
    }

    @Test
    void shouldThrowExceptionWhenProductNotFoundForInventory() {
        when(productRepo.findById(ProductTestConstants.PRODUCT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> inventoryService.create(inventoryDTO)
        );

        assertEquals(ProductTestConstants.PRODUCT_NOT_FOUND + ProductTestConstants.PRODUCT_ID, exception.getReason());
        verify(inventoryRepo, never()).save(any(Inventory.class));
    }

    @Test
    void shouldThrowExceptionWhenInventoryAlreadyExists() {
        when(productRepo.findById(ProductTestConstants.PRODUCT_ID)).thenReturn(Optional.of(product));
        when(inventoryRepo.findByProductId(ProductTestConstants.PRODUCT_ID)).thenReturn(Optional.of(inventory));

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> inventoryService.create(inventoryDTO)
        );

        assertEquals(InventoryTestConstants.INVENTORY_ALREADY_EXISTS, exception.getReason());
        verify(inventoryRepo, never()).save(any(Inventory.class));
    }

    @Test
    void shouldUpdateInventory() {
        when(inventoryRepo.findByProductId(ProductTestConstants.PRODUCT_ID)).thenReturn(Optional.of(inventory));
        when(inventoryRepo.save(any(Inventory.class))).thenReturn(inventory);

        InventoryCreateDTO updateDTO = new InventoryCreateDTO(
                ProductTestConstants.PRODUCT_ID,
                20  // nova quantidade
        );

        Inventory updated = inventoryService.update(ProductTestConstants.PRODUCT_ID, updateDTO);

        assertNotNull(updated);
        verify(inventoryRepo, times(1)).save(any(Inventory.class));
    }

    @Test
    void shouldAddQuantity() {
        when(inventoryRepo.findByProductId(ProductTestConstants.PRODUCT_ID)).thenReturn(Optional.of(inventory));

        Inventory updatedInventory = new Inventory();
        updatedInventory.setProduct(product);
        updatedInventory.setQuantity(InventoryTestConstants.INVENTORY_QUANTITY + InventoryTestConstants.INVENTORY_ADD_QUANTITY);

        when(inventoryRepo.save(any(Inventory.class))).thenReturn(updatedInventory);

        Inventory result = inventoryService.addQuantity(ProductTestConstants.PRODUCT_ID, InventoryTestConstants.INVENTORY_ADD_QUANTITY);

        assertNotNull(result);
        assertEquals(InventoryTestConstants.INVENTORY_QUANTITY + InventoryTestConstants.INVENTORY_ADD_QUANTITY, result.getQuantity());
        verify(inventoryRepo, times(1)).save(any(Inventory.class));
    }

    @Test
    void shouldThrowExceptionWhenAddingZeroQuantity() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> inventoryService.addQuantity(ProductTestConstants.PRODUCT_ID, 0)
        );

        assertEquals(InventoryTestConstants.ADD_QUANTITY_ZERO, exception.getReason());
        verify(inventoryRepo, never()).save(any(Inventory.class));
    }

    @Test
    void shouldRemoveQuantity() {
        when(inventoryRepo.findByProductId(ProductTestConstants.PRODUCT_ID)).thenReturn(Optional.of(inventory));

        // Configurar o comportamento do save para retornar uma inventory com a quantidade atualizada
        Inventory updatedInventory = new Inventory();
        updatedInventory.setProduct(product);
        updatedInventory.setQuantity(InventoryTestConstants.INVENTORY_QUANTITY - InventoryTestConstants.INVENTORY_REMOVE_QUANTITY);

        when(inventoryRepo.save(any(Inventory.class))).thenReturn(updatedInventory);

        Inventory result = inventoryService.removeQuantity(ProductTestConstants.PRODUCT_ID, InventoryTestConstants.INVENTORY_REMOVE_QUANTITY);

        assertNotNull(result);
        assertEquals(InventoryTestConstants.INVENTORY_QUANTITY - InventoryTestConstants.INVENTORY_REMOVE_QUANTITY, result.getQuantity());
        verify(inventoryRepo, times(1)).save(any(Inventory.class));
    }

    @Test
    void shouldThrowExceptionWhenRemovingZeroQuantity() {
        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> inventoryService.removeQuantity(ProductTestConstants.PRODUCT_ID, 0)
        );

        assertEquals(InventoryTestConstants.REMOVE_QUANTITY_ZERO, exception.getReason());
        verify(inventoryRepo, never()).save(any(Inventory.class));
    }

    @Test
    void shouldThrowExceptionWhenInsufficientInventory() {
        when(inventoryRepo.findByProductId(ProductTestConstants.PRODUCT_ID)).thenReturn(Optional.of(inventory));
        int excessiveQuantity = InventoryTestConstants.INVENTORY_QUANTITY + 5;

        InsufficientInventoryException exception = assertThrows(
                InsufficientInventoryException.class,
                () -> inventoryService.removeQuantity(ProductTestConstants.PRODUCT_ID, excessiveQuantity)
        );

        assertEquals(InventoryTestConstants.INSUFFICIENT_INVENTORY +
                        InventoryTestConstants.INVENTORY_QUANTITY + ", Solicitado: " + excessiveQuantity,
                exception.getReason());
        verify(inventoryRepo, never()).save(any(Inventory.class));
    }
}
