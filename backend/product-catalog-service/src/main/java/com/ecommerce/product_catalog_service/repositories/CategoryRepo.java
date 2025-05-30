package com.ecommerce.product_catalog_service.repositories;

import com.ecommerce.product_catalog_service.entities.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryRepo extends JpaRepository<Category, Long> {
}
