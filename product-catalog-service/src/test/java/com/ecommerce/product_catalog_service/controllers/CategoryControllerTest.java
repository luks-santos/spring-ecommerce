package com.ecommerce.product_catalog_service.controllers;

import com.ecommerce.product_catalog_service.common.CategoryTestConstants;
import com.ecommerce.product_catalog_service.entities.Category;
import com.ecommerce.product_catalog_service.services.CategoryService;
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

@WebMvcTest(CategoryController.class)
class CategoryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CategoryService categoryService;

    private Category category;
    private List<Category> categories;

    @BeforeEach
    void setUp() {
        category = CategoryTestConstants.createCategory();
        categories = List.of(category);
    }

    @Test
    void shouldReturnAllCategories() throws Exception {
        when(categoryService.findAll()).thenReturn(categories);

        mockMvc.perform(get("/categories")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0].id", is(CategoryTestConstants.CATEGORY_ID.toString())))
                .andExpect(jsonPath("$[0].name", is(CategoryTestConstants.CATEGORY_NAME)));
    }

    @Test
    void shouldReturnCategoryById() throws Exception {
        when(categoryService.findById(CategoryTestConstants.CATEGORY_ID)).thenReturn(category);

        mockMvc.perform(get("/categories/{id}", CategoryTestConstants.CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(CategoryTestConstants.CATEGORY_ID.toString())))
                .andExpect(jsonPath("$.name", is(CategoryTestConstants.CATEGORY_NAME)));
    }

    @Test
    void shouldCreateCategory() throws Exception {
        when(categoryService.create(any(Category.class))).thenReturn(category);

        Category newCategory = new Category();
        newCategory.setName(CategoryTestConstants.CATEGORY_NAME);

        mockMvc.perform(post("/categories")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newCategory)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", is(CategoryTestConstants.CATEGORY_ID.toString())))
                .andExpect(jsonPath("$.name", is(CategoryTestConstants.CATEGORY_NAME)));
    }

    @Test
    void shouldUpdateCategory() throws Exception {
        when(categoryService.update(eq(CategoryTestConstants.CATEGORY_ID), any(Category.class))).thenReturn(category);

        Category updateCategory = new Category();
        updateCategory.setName(CategoryTestConstants.CATEGORY_NAME);

        mockMvc.perform(put("/categories/{id}", CategoryTestConstants.CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateCategory)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(CategoryTestConstants.CATEGORY_ID.toString())))
                .andExpect(jsonPath("$.name", is(CategoryTestConstants.CATEGORY_NAME)));
    }

    @Test
    void shouldDeleteCategory() throws Exception {
        doNothing().when(categoryService).delete(CategoryTestConstants.CATEGORY_ID);

        mockMvc.perform(delete("/categories/{id}", CategoryTestConstants.CATEGORY_ID)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());
    }
}
