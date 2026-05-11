package com.ecommerce.product_catalog_service.services;

import com.ecommerce.product_catalog_service.entities.Category;
import com.ecommerce.product_catalog_service.exceptions.ResourceNotFoundException;
import com.ecommerce.product_catalog_service.repositories.CategoryRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepo repo;

    public List<Category> findAll() {
        return repo.findAll();
    }

    public Category findById(UUID id) {
        return repo.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Categoria não encontrada com id: " + id));
    }

    public Category create(Category category) {
        return repo.save(category);
    }

    public Category update(UUID id, Category category) {
        Category existing = findById(id);
        existing.setName(category.getName());
        return repo.save(existing);
    }

    public void delete(UUID id) {
        Category existing = findById(id);
        repo.delete(existing);
    }
}
