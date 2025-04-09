package com.ecommerce.product_catalog_service.services;

import com.ecommerce.product_catalog_service.dto.InventoryCreateDTO;
import com.ecommerce.product_catalog_service.entities.Inventory;
import com.ecommerce.product_catalog_service.entities.Product;
import com.ecommerce.product_catalog_service.exceptions.BadRequestException;
import com.ecommerce.product_catalog_service.exceptions.InsufficientInventoryException;
import com.ecommerce.product_catalog_service.exceptions.ResourceNotFoundException;
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
                .orElseThrow(() -> new ResourceNotFoundException("Estoque não encontrado para o produto com id: " + productId));
    }

    public Inventory create(InventoryCreateDTO dto) {
        if (dto.quantity() < 0) {
            throw new BadRequestException("A quantidade em estoque não pode ser negativa");
        }

        Product product = productRepo.findById(dto.productId())
                .orElseThrow(() -> new ResourceNotFoundException("Produto não encontrado com id: " + dto.productId()));

        // Verificar se já existe um inventário para este produto
        if (repository.findByProductId(dto.productId()).isPresent()) {
            throw new BadRequestException("Já existe um registro de estoque para este produto");
        }

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
        if (quantity <= 0) {
            throw new BadRequestException("A quantidade a ser adicionada deve ser maior que zero");
        }

        Inventory inventory = findByProductId(productId);
        inventory.setQuantity(inventory.getQuantity() + quantity);
        return repository.save(inventory);
    }

    public Inventory removeQuantity(UUID productId, int quantity) {
        if (quantity <= 0) {
            throw new BadRequestException("A quantidade a ser removida deve ser maior que zero");
        }

        Inventory inventory = findByProductId(productId);
        if (inventory.getQuantity() < quantity) {
            throw new InsufficientInventoryException("Quantidade insuficiente em estoque. Disponível: " +
                    inventory.getQuantity() + ", Solicitado: " + quantity);
        }
        inventory.setQuantity(inventory.getQuantity() - quantity);
        return repository.save(inventory);
    }
}
