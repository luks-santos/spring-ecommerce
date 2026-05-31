package com.ecommerce.shopping_cart_service.services;

import com.ecommerce.shopping_cart_service.dto.CartItemCreateDTO;
import com.ecommerce.shopping_cart_service.dto.CartItemResponseDTO;
import com.ecommerce.shopping_cart_service.dto.CartItemUpdateDTO;
import com.ecommerce.shopping_cart_service.dto.CartResponseDTO;
import com.ecommerce.shopping_cart_service.entities.CartItem;
import com.ecommerce.shopping_cart_service.entities.ShoppingCart;
import com.ecommerce.shopping_cart_service.exceptions.BadRequestException;
import com.ecommerce.shopping_cart_service.exceptions.ResourceNotFoundException;
import com.ecommerce.shopping_cart_service.repositories.CartItemRepo;
import com.ecommerce.shopping_cart_service.repositories.ShoppingCartRepo;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class CartService {

    private final ShoppingCartRepo cartRepo;
    private final CartItemRepo cartItemRepo;

    /**
     * Get or create cart for a user
     */
    public CartResponseDTO getOrCreateCart(UUID userId) {
        log.info("Getting or creating cart for user: {}", userId);

        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    log.info("Creating new cart for user: {}", userId);
                    ShoppingCart newCart = ShoppingCart.builder()
                            .userId(userId)
                            .build();
                    return cartRepo.save(newCart);
                });

        return toCartResponseDTO(cart);
    }

    /**
     * Get cart by user ID
     */
    public CartResponseDTO getCartByUserId(UUID userId) {
        log.info("Getting cart for user: {}", userId);

        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for user: " + userId));

        return toCartResponseDTO(cart);
    }

    /**
     * Add item to cart or update quantity if already exists
     */
    @Transactional
    public CartResponseDTO addItem(UUID userId, CartItemCreateDTO dto) {
        log.info("Adding item to cart for user: {}, product: {}", userId, dto.productId());

        // Validate
        if (dto.quantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        if (dto.price().signum() <= 0) {
            throw new BadRequestException("Price must be greater than zero");
        }

        // Get or create cart
        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseGet(() -> {
                    ShoppingCart newCart = ShoppingCart.builder()
                            .userId(userId)
                            .build();
                    return cartRepo.save(newCart);
                });

        // Check if item already exists
        CartItem existingItem = cartItemRepo.findByCartIdAndProductId(cart.getId(), dto.productId())
                .orElse(null);

        if (existingItem != null) {
            // Update quantity
            log.info("Item already exists in cart, updating quantity");
            existingItem.setQuantity(existingItem.getQuantity() + dto.quantity());
            existingItem.setPrice(dto.price()); // Update price
            cartItemRepo.save(existingItem);
        } else {
            // Add new item
            CartItem newItem = CartItem.builder()
                    .cart(cart)
                    .productId(dto.productId())
                    .quantity(dto.quantity())
                    .price(dto.price())
                    .build();
            cart.addItem(newItem);
            cartItemRepo.save(newItem);
        }

        cart = cartRepo.save(cart);
        return toCartResponseDTO(cart);
    }

    /**
     * Update item quantity
     */
    @Transactional
    public CartResponseDTO updateItemQuantity(UUID userId, UUID itemId, CartItemUpdateDTO dto) {
        log.info("Updating item quantity for user: {}, item: {}", userId, itemId);

        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for user: " + userId));

        CartItem item = cartItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found: " + itemId));

        // Verify item belongs to user's cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Item does not belong to user's cart");
        }

        if (dto.quantity() <= 0) {
            throw new BadRequestException("Quantity must be greater than zero");
        }

        item.setQuantity(dto.quantity());
        cartItemRepo.save(item);

        cart = cartRepo.save(cart);
        return toCartResponseDTO(cart);
    }

    /**
     * Remove item from cart
     */
    @Transactional
    public CartResponseDTO removeItem(UUID userId, UUID itemId) {
        log.info("Removing item from cart for user: {}, item: {}", userId, itemId);

        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for user: " + userId));

        CartItem item = cartItemRepo.findById(itemId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart item not found: " + itemId));

        // Verify item belongs to user's cart
        if (!item.getCart().getId().equals(cart.getId())) {
            throw new BadRequestException("Item does not belong to user's cart");
        }

        cart.removeItem(item);
        cartItemRepo.delete(item);

        cart = cartRepo.save(cart);
        return toCartResponseDTO(cart);
    }

    /**
     * Clear all items from cart
     */
    @Transactional
    public void clearCart(UUID userId) {
        log.info("Clearing cart for user: {}", userId);

        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for user: " + userId));

        cart.clearItems();
        cartRepo.save(cart);
    }

    /**
     * Delete cart
     */
    @Transactional
    public void deleteCart(UUID userId) {
        log.info("Deleting cart for user: {}", userId);

        ShoppingCart cart = cartRepo.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Cart not found for user: " + userId));

        cartRepo.delete(cart);
    }

    // Mapper methods
    private CartResponseDTO toCartResponseDTO(ShoppingCart cart) {
        List<CartItemResponseDTO> itemDTOs = cart.getItems().stream()
                .map(this::toCartItemResponseDTO)
                .collect(Collectors.toList());

        return CartResponseDTO.builder()
                .id(cart.getId())
                .userId(cart.getUserId())
                .items(itemDTOs)
                .totalAmount(cart.getTotalAmount())
                .totalItems(cart.getTotalItems())
                .createdAt(cart.getCreatedAt())
                .updatedAt(cart.getUpdatedAt())
                .build();
    }

    private CartItemResponseDTO toCartItemResponseDTO(CartItem item) {
        return CartItemResponseDTO.builder()
                .id(item.getId())
                .productId(item.getProductId())
                .quantity(item.getQuantity())
                .price(item.getPrice())
                .subtotal(item.getSubtotal())
                .createdAt(item.getCreatedAt())
                .build();
    }
}
