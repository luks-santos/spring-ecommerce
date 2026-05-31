package com.ecommerce.shopping_cart_service.controllers;

import com.ecommerce.shopping_cart_service.config.SecurityConfig;
import com.ecommerce.shopping_cart_service.dto.CartResponseDTO;
import com.ecommerce.shopping_cart_service.services.CartService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CartController.class)
@Import(SecurityConfig.class)
class CartControllerSecurityTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CartService cartService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static RequestPostProcessor token(UUID userId) {
        return jwt()
                .jwt(builder -> builder.claim("userId", userId.toString()))
                .authorities(new SimpleGrantedAuthority("ROLE_CLIENT"));
    }

    @Test
    void getCart_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/carts"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void getCart_withToken_returns200() throws Exception {
        when(cartService.getOrCreateCart(any())).thenReturn(new CartResponseDTO());

        mockMvc.perform(get("/api/carts").with(token(USER_ID)))
                .andExpect(status().isOk());
    }
}
