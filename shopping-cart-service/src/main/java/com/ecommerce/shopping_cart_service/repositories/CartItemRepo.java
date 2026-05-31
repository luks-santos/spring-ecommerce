package com.ecommerce.shopping_cart_service.repositories;

import com.ecommerce.shopping_cart_service.entities.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CartItemRepo extends JpaRepository<CartItem, UUID> {

    Optional<CartItem> findByCartIdAndProductId(UUID cartId, UUID productId);

    void deleteByCartIdAndProductId(UUID cartId, UUID productId);
}
