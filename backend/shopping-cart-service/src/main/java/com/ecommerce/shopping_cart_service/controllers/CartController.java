package com.ecommerce.shopping_cart_service.controllers;

import com.ecommerce.shopping_cart_service.dto.CartItemCreateDTO;
import com.ecommerce.shopping_cart_service.dto.CartItemUpdateDTO;
import com.ecommerce.shopping_cart_service.dto.CartResponseDTO;
import com.ecommerce.shopping_cart_service.services.CartService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart", description = "Shopping cart management endpoints")
public class CartController {

    private final CartService cartService;

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get cart by user ID", description = "Retrieves the shopping cart for a specific user")
    public ResponseEntity<CartResponseDTO> getCartByUserId(@PathVariable UUID userId) {
        return ResponseEntity.ok(cartService.getOrCreateCart(userId));
    }

    @PostMapping("/user/{userId}/items")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add item to cart", description = "Adds a new item to the user's cart or updates quantity if already exists")
    public CartResponseDTO addItem(
            @PathVariable UUID userId,
            @Valid @RequestBody CartItemCreateDTO dto) {
        return cartService.addItem(userId, dto);
    }

    @PutMapping("/user/{userId}/items/{itemId}")
    @Operation(summary = "Update item quantity", description = "Updates the quantity of a specific item in the cart")
    public ResponseEntity<CartResponseDTO> updateItemQuantity(
            @PathVariable UUID userId,
            @PathVariable UUID itemId,
            @Valid @RequestBody CartItemUpdateDTO dto) {
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, itemId, dto));
    }

    @DeleteMapping("/user/{userId}/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the user's cart")
    public void removeItem(
            @PathVariable UUID userId,
            @PathVariable UUID itemId) {
        cartService.removeItem(userId, itemId);
    }

    @DeleteMapping("/user/{userId}/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Clear cart", description = "Removes all items from the user's cart")
    public void clearCart(@PathVariable UUID userId) {
        cartService.clearCart(userId);
    }

    @DeleteMapping("/user/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete cart", description = "Deletes the entire cart for a user")
    public void deleteCart(@PathVariable UUID userId) {
        cartService.deleteCart(userId);
    }
}
