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
import org.springframework.web.server.ResponseStatusException;

import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import java.util.UUID;

@RestController
@RequestMapping("/api/carts")
@RequiredArgsConstructor
@Tag(name = "Shopping Cart", description = "Shopping cart management endpoints")
public class CartController {

    private final CartService cartService;

    private UUID extractUserId(JwtAuthenticationToken token) {
        String userId = token.getToken().getClaimAsString("userId");
        if (userId == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token is missing the userId claim");
        }
        return UUID.fromString(userId);
    }

    @GetMapping
    @Operation(summary = "Get current user's cart", description = "Retrieves the shopping cart for the authenticated user")
    public ResponseEntity<CartResponseDTO> getCartByUserId(JwtAuthenticationToken token) {
        UUID userId = extractUserId(token);
        return ResponseEntity.ok(cartService.getOrCreateCart(userId));
    }

    @PostMapping("/items")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Add item to cart", description = "Adds a new item to the user's cart or updates quantity if already exists")
    public CartResponseDTO addItem(
            JwtAuthenticationToken token,
            @Valid @RequestBody CartItemCreateDTO dto) {
        UUID userId = extractUserId(token);
        return cartService.addItem(userId, dto);
    }

    @PutMapping("/items/{itemId}")
    @Operation(summary = "Update item quantity", description = "Updates the quantity of a specific item in the cart")
    public ResponseEntity<CartResponseDTO> updateItemQuantity(
            JwtAuthenticationToken token,
            @PathVariable UUID itemId,
            @Valid @RequestBody CartItemUpdateDTO dto) {
        UUID userId = extractUserId(token);
        return ResponseEntity.ok(cartService.updateItemQuantity(userId, itemId, dto));
    }

    @DeleteMapping("/items/{itemId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Remove item from cart", description = "Removes a specific item from the user's cart")
    public void removeItem(
            JwtAuthenticationToken token,
            @PathVariable UUID itemId) {
        UUID userId = extractUserId(token);
        cartService.removeItem(userId, itemId);
    }

    @DeleteMapping("/clear")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Clear cart", description = "Removes all items from the user's cart")
    public void clearCart(JwtAuthenticationToken token) {
        UUID userId = extractUserId(token);
        cartService.clearCart(userId);
    }

    @DeleteMapping
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Delete cart", description = "Deletes the entire cart for the authenticated user")
    public void deleteCart(JwtAuthenticationToken token) {
        UUID userId = extractUserId(token);
        cartService.deleteCart(userId);
    }
}
