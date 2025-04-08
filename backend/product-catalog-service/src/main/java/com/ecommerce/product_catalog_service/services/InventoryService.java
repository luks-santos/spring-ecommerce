package com.ecommerce.product_catalog_service.services;

import com.ecommerce.product_catalog_service.dto.InventoryCreateDTO;
import com.ecommerce.product_catalog_service.entities.Inventory;
import com.ecommerce.product_catalog_service.entities.Product;
import com.ecommerce.product_catalog_service.repositories.InventoryRepo;
import com.ecommerce.product_catalog_service.repositories.ProductRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class InventoryService {

    private final InventoryRepo repository;
    private final ProductRepo productRepo;

    public List<Inventory> findAll() {
        return repository.findAll();
    }

    public Inventory findByProductId(UUID productId) {
        return repository.findByProductId(productId)
                .orElseThrow(() -> new RuntimeException("Estoque não encontrado"));
    }

    public Inventory create(InventoryCreateDTO dto) {
        Product product = productRepo.findById(dto.productId())
                .orElseThrow(() -> new RuntimeException("Produto não encontrado"));

        Inventory inventory = new Inventory();
        inventory.setProduct(product);
        inventory.setQuantity(dto.quantity());

        return repository.save(inventory);
    }

    public Inventory update(UUID productId, InventoryCreateDTO dto) {
        Inventory existing = findByProductId(productId);
        existing.setQuantity(dto.quantity());
        return repository.save(existing);
    }

    public Inventory addQuantity(UUID productId, int quantity) {
        Inventory inventory = findByProductId(productId);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        return repository.save(inventory);
    }

    public Inventory removeQuantity(UUID productId, int quantity) {
        Inventory inventory = findByProductId(productId);
        if (inventory.getQuantity() < quantity) {
            throw new IllegalArgumentException("Quantidade insuficiente em estoque");
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        return repository.save(inventory);
    }
}
