package com.ecommerce.product_catalog_service.repositories;

import com.ecommerce.product_catalog_service.entities.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface InventoryRepo extends JpaRepository<Inventory, UUID> {

    Optional<Inventory> findByProductId(UUID productId);
}
