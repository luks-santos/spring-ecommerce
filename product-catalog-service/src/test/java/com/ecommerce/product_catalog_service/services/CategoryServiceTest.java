package com.ecommerce.product_catalog_service.services;

import com.ecommerce.product_catalog_service.common.CategoryTestConstants;
import com.ecommerce.product_catalog_service.entities.Category;
import com.ecommerce.product_catalog_service.exceptions.ResourceNotFoundException;
import com.ecommerce.product_catalog_service.repositories.CategoryRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.ecommerce.product_catalog_service.common.CategoryTestConstants.UPDATED_CATEGORY_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @Mock
    private CategoryRepo categoryRepo;

    @InjectMocks
    private CategoryService categoryService;

    private Category category;

    @BeforeEach
    void setUp() {
        category = CategoryTestConstants.createCategory();
    }

    @Test
    void shouldFindAllCategories() {
        when(categoryRepo.findAll()).thenReturn(Collections.singletonList(category));

        List<Category> categories = categoryService.findAll();

        assertEquals(1, categories.size());
        assertEquals(CategoryTestConstants.CATEGORY_NAME, categories.getFirst().getName());
        verify(categoryRepo, times(1)).findAll();
    }

    @Test
    void shouldFindCategoryById() {
        when(categoryRepo.findById(CategoryTestConstants.CATEGORY_ID)).thenReturn(Optional.of(category));

        Category found = categoryService.findById(CategoryTestConstants.CATEGORY_ID);

        assertNotNull(found);
        assertEquals(CategoryTestConstants.CATEGORY_ID, found.getId());
        assertEquals(CategoryTestConstants.CATEGORY_NAME, found.getName());
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        UUID id = UUID.randomUUID();
        when(categoryRepo.findById(id)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> categoryService.findById(id)
        );

        assertEquals(CategoryTestConstants.CATEGORY_NOT_FOUND + id, exception.getReason());
    }

    @Test
    void shouldCreateCategory() {
        when(categoryRepo.save(any(Category.class))).thenReturn(category);

        Category newCategory = new Category();
        newCategory.setName(CategoryTestConstants.CATEGORY_NAME);

        Category created = categoryService.create(newCategory);

        assertNotNull(created);
        assertEquals(CategoryTestConstants.CATEGORY_NAME, created.getName());
        verify(categoryRepo, times(1)).save(any(Category.class));
    }

    @Test
    void shouldUpdateCategory() {
        Category updatedCategory = CategoryTestConstants.createUpdatedCategory();

        when(categoryRepo.findById(CategoryTestConstants.CATEGORY_ID)).thenReturn(Optional.of(category));
        when(categoryRepo.save(any(Category.class))).thenReturn(updatedCategory);

        Category result = categoryService.update(CategoryTestConstants.CATEGORY_ID, updatedCategory);

        assertNotNull(result);
        assertEquals(UPDATED_CATEGORY_NAME, result.getName());
        verify(categoryRepo, times(1)).save(any(Category.class));
    }

    @Test
    void shouldDeleteCategory() {
        when(categoryRepo.findById(CategoryTestConstants.CATEGORY_ID)).thenReturn(Optional.of(category));

        categoryService.delete(CategoryTestConstants.CATEGORY_ID);

        verify(categoryRepo, times(1)).delete(category);
    }
}
