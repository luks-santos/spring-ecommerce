package com.ecommerce.order_service.controllers;

import com.ecommerce.order_service.config.SecurityConfig;
import com.ecommerce.order_service.dto.OrderResponseDTO;
import com.ecommerce.order_service.enums.OrderStatus;
import com.ecommerce.order_service.services.OrderService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(OrderController.class)
@Import(SecurityConfig.class)
class OrderControllerSecurityTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID ORDER_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private OrderService orderService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    /** A JWT carrying the userId claim and a single role authority. */
    private static RequestPostProcessor token(UUID userId, String role) {
        return jwt()
                .jwt(builder -> builder.claim("userId", userId.toString()))
                .authorities(new SimpleGrantedAuthority(role));
    }

    private OrderResponseDTO orderOwnedBy(UUID userId) {
        return OrderResponseDTO.builder()
                .id(ORDER_ID)
                .userId(userId)
                .status(OrderStatus.CREATED)
                .build();
    }

    @Test
    void myOrders_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/orders/my-orders"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void myOrders_withToken_returns200() throws Exception {
        when(orderService.getOrdersByUserId(USER_ID)).thenReturn(List.of());

        mockMvc.perform(get("/api/orders/my-orders").with(token(USER_ID, "ROLE_CLIENT")))
                .andExpect(status().isOk());
    }

    @Test
    void getById_ownOrder_returns200() throws Exception {
        when(orderService.getOrderById(any())).thenReturn(orderOwnedBy(USER_ID));

        mockMvc.perform(get("/api/orders/{id}", ORDER_ID).with(token(USER_ID, "ROLE_CLIENT")))
                .andExpect(status().isOk());
    }

    @Test
    void getById_otherUsersOrder_returns403() throws Exception {
        when(orderService.getOrderById(any())).thenReturn(orderOwnedBy(OTHER_USER_ID));

        mockMvc.perform(get("/api/orders/{id}", ORDER_ID).with(token(USER_ID, "ROLE_CLIENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getByStatus_nonAdmin_returns403() throws Exception {
        mockMvc.perform(get("/api/orders/status/{status}", OrderStatus.CREATED).with(token(USER_ID, "ROLE_CLIENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getByStatus_admin_returns200() throws Exception {
        when(orderService.getOrdersByStatus(any())).thenReturn(List.of());

        mockMvc.perform(get("/api/orders/status/{status}", OrderStatus.CREATED).with(token(USER_ID, "ROLE_ADMIN")))
                .andExpect(status().isOk());
    }
}
