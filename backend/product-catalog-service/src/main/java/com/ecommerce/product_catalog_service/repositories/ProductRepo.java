package com.ecommerce.product_catalog_service.repositories;

import com.ecommerce.product_catalog_service.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ProductRepo extends JpaRepository<Product, UUID> {

    List<Product> findByCategoryId(UUID categoryId);
}
