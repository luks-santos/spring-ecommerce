package com.ecommerce.shopping_cart_service.services;

import com.ecommerce.shopping_cart_service.dto.CartItemCreateDTO;
import com.ecommerce.shopping_cart_service.dto.CartResponseDTO;
import com.ecommerce.shopping_cart_service.entities.ShoppingCart;
import com.ecommerce.shopping_cart_service.exceptions.BadRequestException;
import com.ecommerce.shopping_cart_service.repositories.CartItemRepo;
import com.ecommerce.shopping_cart_service.repositories.ShoppingCartRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class CartServiceTest {

    @Mock
    private ShoppingCartRepo cartRepo;

    @Mock
    private CartItemRepo cartItemRepo;

    private CartService cartService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        cartService = new CartService(cartRepo, cartItemRepo);
    }

    @Test
    void getOrCreateCartCreatesCartWhenMissing() {
        UUID userId = UUID.randomUUID();
        when(cartRepo.findByUserId(userId)).thenReturn(Optional.empty());
        when(cartRepo.save(any(ShoppingCart.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CartResponseDTO response = cartService.getOrCreateCart(userId);

        assertThat(response.getUserId()).isEqualTo(userId);
        assertThat(response.getTotalItems()).isZero();
    }

    @Test
    void addItemRejectsInvalidQuantity() {
        UUID userId = UUID.randomUUID();
        CartItemCreateDTO dto = new CartItemCreateDTO(UUID.randomUUID(), 0, BigDecimal.TEN);

        assertThatThrownBy(() -> cartService.addItem(userId, dto))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Quantity");
    }
}
