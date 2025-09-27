package com.ecommerce.product_catalog_service.controllers;

import com.ecommerce.product_catalog_service.common.InventoryTestConstants;
import com.ecommerce.product_catalog_service.dto.InventoryCreateDTO;
import com.ecommerce.product_catalog_service.entities.Inventory;
import com.ecommerce.product_catalog_service.services.InventoryService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static com.ecommerce.product_catalog_service.common.ProductTestConstants.PRODUCT_ID;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(InventoryController.class)
class InventoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private InventoryService inventoryService;

    private Inventory inventory;
    private List<Inventory> inventories;
    private InventoryCreateDTO inventoryCreateDTO;

    @BeforeEach
    void setUp() {
        inventory = InventoryTestConstants.createInventory();
        inventories = List.of(inventory);
        inventoryCreateDTO = InventoryTestConstants.createInventoryDTO();
    }

    @Test
    void shouldReturnAllInventories() throws Exception {
        when(inventoryService.findAll()).thenReturn(inventories);

        mockMvc.perform(get("/inventories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].product.id", is(inventory.getProduct().getId().toString())))
                .andExpect(jsonPath("$[0].quantity", is(inventory.getQuantity())));
    }

    @Test
    void shouldReturnInventoryByProductId() throws Exception {
        when(inventoryService.findByProductId(PRODUCT_ID)).thenReturn(inventory);

        mockMvc.perform(get("/inventories/product/{productId}", PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.id", is(inventory.getProduct().getId().toString())))
                .andExpect(jsonPath("$.quantity", is(inventory.getQuantity())));
    }

    @Test
    void shouldCreateInventory() throws Exception {
        when(inventoryService.create(any(InventoryCreateDTO.class))).thenReturn(inventory);

        mockMvc.perform(post("/inventories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventoryCreateDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.product.id", is(inventory.getProduct().getId().toString())))
                .andExpect(jsonPath("$.quantity", is(inventory.getQuantity())));
    }

    @Test
    void shouldUpdateInventory() throws Exception {
        when(inventoryService.update(eq(PRODUCT_ID), any(InventoryCreateDTO.class))).thenReturn(inventory);

        mockMvc.perform(put("/inventories/product/{productId}", PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(inventoryCreateDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.id", is(inventory.getProduct().getId().toString())))
                .andExpect(jsonPath("$.quantity", is(inventory.getQuantity())));
    }

    @Test
    void shouldAddQuantityToInventory() throws Exception {
        int quantityToAdd = InventoryTestConstants.INVENTORY_ADD_QUANTITY;
        Inventory inventoryAddQty= InventoryTestConstants.createInventoryAddQty();

        when(inventoryService.addQuantity(PRODUCT_ID, quantityToAdd)).thenReturn(inventoryAddQty);

        mockMvc.perform(patch("/inventories/product/{productId}/add", PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("qty", String.valueOf(quantityToAdd)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.id", is(inventoryAddQty.getProduct().getId().toString())))
                .andExpect(jsonPath("$.quantity", is(inventoryAddQty.getQuantity())));
    }

    @Test
    void shouldRemoveQuantityFromInventory() throws Exception {
        int quantityToRemove = InventoryTestConstants.INVENTORY_REMOVE_QUANTITY;
        Inventory inventoryRemoveQty = InventoryTestConstants.createInventoryRemoveQty();

        when(inventoryService.removeQuantity(PRODUCT_ID, quantityToRemove)).thenReturn(inventoryRemoveQty);

        mockMvc.perform(patch("/inventories/product/{productId}/remove", PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .param("qty", String.valueOf(quantityToRemove)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.id", is(inventoryRemoveQty.getProduct().getId().toString())))
                .andExpect(jsonPath("$.quantity", is(inventoryRemoveQty.getQuantity())));
    }
}
