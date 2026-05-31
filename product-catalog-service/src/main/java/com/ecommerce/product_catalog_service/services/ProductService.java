package com.ecommerce.product_catalog_service.services;

import com.ecommerce.product_catalog_service.dto.ProductCreateDTO;
import com.ecommerce.product_catalog_service.entities.Category;
import com.ecommerce.product_catalog_service.entities.Product;
import com.ecommerce.product_catalog_service.exceptions.BadRequestException;
import com.ecommerce.product_catalog_service.exceptions.ResourceNotFoundException;
import com.ecommerce.product_catalog_service.repositories.CategoryRepo;
import com.ecommerce.product_catalog_service.repositories.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
    }

    public Product create(ProductCreateDTO dto) {
        if (dto.price().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException("O preço do produto deve ser maior que zero");
        }

        Category category = categoryRepo.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com id: " + dto.categoryId()));

        Product product = new Product();
        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCategory(category);

        return productRepo.save(product);
    }

    public Product update(UUID id, ProductCreateDTO dto) {
        if (dto.price().compareTo(BigDecimal.ZERO) < 0) {
            throw new BadRequestException("O preço do produto deve ser maior que zero");
        }

        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));

        Category category = categoryRepo.findById(dto.categoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com id: " + dto.categoryId()));

        product.setName(dto.name());
        product.setDescription(dto.description());
        product.setPrice(dto.price());
        product.setCategory(category);

        return productRepo.save(product);
    }

    public void delete(UUID id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + id));
        productRepo.delete(product);
    }

    public List<Product> findByCategory(UUID categoryId) {
        // Verificar se a categoria existe antes de buscar produtos
        if (!categoryRepo.existsById(categoryId)) {
            throw new ResourceNotFoundException("Categoria não encontrada com id: " + categoryId);
        }
        return productRepo.findByCategoryId(categoryId);
    }
}
