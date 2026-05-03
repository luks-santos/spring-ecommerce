package com.ecommerce.shopping_cart_service.repositories;

import com.ecommerce.shopping_cart_service.entities.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ShoppingCartRepo extends JpaRepository<ShoppingCart, UUID> {

    Optional<ShoppingCart> findByUserId(UUID userId);

    boolean existsByUserId(UUID userId);

    @Query("SELECT c FROM ShoppingCart c WHERE c.updatedAt < :cutoffDate")
    List<ShoppingCart> findAbandonedCarts(LocalDateTime cutoffDate);
}
