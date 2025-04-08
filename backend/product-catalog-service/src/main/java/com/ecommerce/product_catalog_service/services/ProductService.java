package com.ecommerce.product_catalog_service.services;

import com.ecommerce.product_catalog_service.dto.ProductCreateDTO;
import com.ecommerce.product_catalog_service.entities.Category;
import com.ecommerce.product_catalog_service.entities.Product;
import com.ecommerce.product_catalog_service.repositories.CategoryRepo;
import com.ecommerce.product_catalog_service.repositories.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepo productRepo;
    private final CategoryRepo categoryRepo;

    public List<Product> findAll() {
        return productRepo.findAll();
    }

    public Product findById(UUID id) {
        return productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
    }

    public Product create(ProductCreateDTO dto) {
        Category category = categoryRepo.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCategory(category);

        return productRepo.save(product);
    }

    public Product update(UUID id, ProductCreateDTO dto) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Category category = categoryRepo.findById(dto.categoryId())
                .orElseThrow(() -> new RuntimeException("Categoria não encontrada"));

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCategory(category);

        return productRepo.save(product);
    }

    public void delete(UUID id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));
        productRepo.delete(product);
    }

    public List<Product> findByCategory(UUID categoryId) {
        return productRepo.findByCategoryId(categoryId);
    }
}
