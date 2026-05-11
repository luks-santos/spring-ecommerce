package com.ecommerce.product_catalog_service.services;

import com.ecommerce.product_catalog_service.common.CategoryTestConstants;
import com.ecommerce.product_catalog_service.common.ProductTestConstants;
import com.ecommerce.product_catalog_service.dto.ProductCreateDTO;
import com.ecommerce.product_catalog_service.entities.Category;
import com.ecommerce.product_catalog_service.entities.Product;
import com.ecommerce.product_catalog_service.exceptions.BadRequestException;
import com.ecommerce.product_catalog_service.exceptions.ResourceNotFoundException;
import com.ecommerce.product_catalog_service.repositories.CategoryRepo;
import com.ecommerce.product_catalog_service.repositories.ProductRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.ecommerce.product_catalog_service.common.CategoryTestConstants.CATEGORY_ID;
import static com.ecommerce.product_catalog_service.common.CategoryTestConstants.CATEGORY_NOT_FOUND;
import static com.ecommerce.product_catalog_service.common.ProductTestConstants.PRICE_MUST_BE_POSITIVE;
import static com.ecommerce.product_catalog_service.common.ProductTestConstants.PRODUCT_ID;
import static com.ecommerce.product_catalog_service.common.ProductTestConstants.PRODUCT_NAME;
import static com.ecommerce.product_catalog_service.common.ProductTestConstants.PRODUCT_NOT_FOUND;
import static com.ecommerce.product_catalog_service.common.ProductTestConstants.PRODUCT_PRICE;
import static com.ecommerce.product_catalog_service.common.ProductTestConstants.UPDATED_PRODUCT_NAME;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepo productRepo;

    @Mock
    private CategoryRepo categoryRepo;

    @InjectMocks
    private ProductService productService;


    private Product product;
    private Category category;
    private ProductCreateDTO productDTO;

    @BeforeEach
    void setUp() {
        product = ProductTestConstants.createProduct();
        category = CategoryTestConstants.createCategory();
        productDTO = ProductTestConstants.createProductDTO();
    }

    @Test
    void shouldFindAllProducts() {
        when(productRepo.findAll()).thenReturn(Collections.singletonList(product));

        List<Product> products = productService.findAll();

        assertEquals(1, products.size());
        assertEquals(PRODUCT_NAME, products.getFirst().getName());
        verify(productRepo, times(1)).findAll();
    }

    @Test
    void shouldFindProductById() {
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        Product found = productService.findById(PRODUCT_ID);

        assertNotNull(found);
        assertEquals(PRODUCT_ID, found.getId());
        assertEquals(PRODUCT_NAME, found.getName());
    }

    @Test
    void shouldThrowExceptionWhenProductNotFound() {
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findById(PRODUCT_ID)
        );

        assertEquals(PRODUCT_NOT_FOUND + PRODUCT_ID, exception.getReason());
    }

    @Test
    void shouldCreateProduct() {
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(productRepo.save(any(Product.class))).thenReturn(product);

        Product created = productService.create(productDTO);

        assertNotNull(created);
        assertEquals(PRODUCT_NAME, created.getName());
        assertEquals(PRODUCT_PRICE, created.getPrice());
        assertEquals(CATEGORY_ID, created.getCategory().getId());
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenCreatingProductWithInvalidPrice() {
        ProductCreateDTO invalidDTO = ProductTestConstants.createInvalidPriceProductDTO();

        BadRequestException exception = assertThrows(
                BadRequestException.class,
                () -> productService.create(invalidDTO)
        );

        assertEquals(PRICE_MUST_BE_POSITIVE, exception.getReason());
        verify(productRepo, never()).save(any(Product.class));
    }

    @Test
    void shouldThrowExceptionWhenCategoryNotFound() {
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.empty());

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.create(productDTO)
        );

        assertEquals(CATEGORY_NOT_FOUND + CATEGORY_ID, exception.getReason());
        verify(productRepo, never()).save(any(Product.class));
    }

    @Test
    void shouldUpdateProduct() {
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(product));
        when(categoryRepo.findById(CATEGORY_ID)).thenReturn(Optional.of(category));
        when(productRepo.save(any(Product.class))).thenReturn(ProductTestConstants.createUpdatedProduct());

        Product updated = productService.update(PRODUCT_ID, productDTO);

        assertNotNull(updated);
        assertEquals(UPDATED_PRODUCT_NAME, updated.getName());
        verify(productRepo, times(1)).save(any(Product.class));
    }

    @Test
    void shouldDeleteProduct() {
        when(productRepo.findById(PRODUCT_ID)).thenReturn(Optional.of(product));

        productService.delete(PRODUCT_ID);

        verify(productRepo, times(1)).delete(product);
    }

    @Test
    void shouldFindProductsByCategory() {
        when(categoryRepo.existsById(CATEGORY_ID)).thenReturn(true);
        when(productRepo.findByCategoryId(CATEGORY_ID)).thenReturn(Collections.singletonList(product));

        List<Product> products = productService.findByCategory(CATEGORY_ID);

        assertEquals(1, products.size());
        assertEquals(PRODUCT_NAME, products.getFirst().getName());
    }

    @Test
    void shouldThrowExceptionWhenFindingProductsForNonExistingCategory() {
        when(categoryRepo.existsById(CATEGORY_ID)).thenReturn(false);

        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> productService.findByCategory(CATEGORY_ID)
        );

        assertEquals(CATEGORY_NOT_FOUND + CATEGORY_ID, exception.getReason());
    }
}
