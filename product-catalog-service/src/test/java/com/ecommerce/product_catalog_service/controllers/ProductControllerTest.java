package com.ecommerce.product_catalog_service.controllers;

import com.ecommerce.product_catalog_service.common.CategoryTestConstants;
import com.ecommerce.product_catalog_service.common.ProductTestConstants;
import com.ecommerce.product_catalog_service.dto.ProductCreateDTO;
import com.ecommerce.product_catalog_service.entities.Product;
import com.ecommerce.product_catalog_service.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    private Product product;
    private List<Product> products;
    private ProductCreateDTO productDTO;

    @BeforeEach
    void setUp() {
        product = ProductTestConstants.createProduct();
        products = List.of(product);
        productDTO = ProductTestConstants.createProductDTO();
    }

    @Test
    void shouldReturnAllProducts() throws Exception {
        when(productService.findAll()).thenReturn(products);

        mockMvc.perform(get("/products")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(ProductTestConstants.PRODUCT_ID.toString())))
                .andExpect(jsonPath("$[0].name", is(ProductTestConstants.PRODUCT_NAME)));
    }

    @Test
    void shouldReturnProductById() throws Exception {
        when(productService.findById(ProductTestConstants.PRODUCT_ID)).thenReturn(product);

        mockMvc.perform(get("/products/{id}", ProductTestConstants.PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ProductTestConstants.PRODUCT_ID.toString())))
                .andExpect(jsonPath("$.name", is(ProductTestConstants.PRODUCT_NAME)));
    }

    @Test
    void shouldCreateProduct() throws Exception {
        when(productService.create(any(ProductCreateDTO.class))).thenReturn(product);

        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(ProductTestConstants.PRODUCT_ID.toString())))
                .andExpect(jsonPath("$.name", is(ProductTestConstants.PRODUCT_NAME)));
    }

    @Test
    void shouldUpdateProduct() throws Exception {
        when(productService.update(eq(ProductTestConstants.PRODUCT_ID), any(ProductCreateDTO.class))).thenReturn(product);

        mockMvc.perform(put("/products/{id}", ProductTestConstants.PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(productDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(ProductTestConstants.PRODUCT_ID.toString())))
                .andExpect(jsonPath("$.name", is(ProductTestConstants.PRODUCT_NAME)));
    }

    @Test
    void shouldDeleteProduct() throws Exception {
        doNothing().when(productService).delete(ProductTestConstants.PRODUCT_ID);

        mockMvc.perform(delete("/products/{id}", ProductTestConstants.PRODUCT_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }

    @Test
    void shouldReturnProductsByCategory() throws Exception {
        when(productService.findByCategory(CategoryTestConstants.CATEGORY_ID)).thenReturn(products);

        mockMvc.perform(get("/products/category/{categoryId}", CategoryTestConstants.CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(ProductTestConstants.PRODUCT_ID.toString())))
                .andExpect(jsonPath("$[0].name", is(ProductTestConstants.PRODUCT_NAME)));
    }
}
