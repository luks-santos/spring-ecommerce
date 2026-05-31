package com.ecommerce.payment_service.controllers;

import com.ecommerce.payment_service.clients.OrderClient;
import com.ecommerce.payment_service.config.SecurityConfig;
import com.ecommerce.payment_service.dto.PaymentResponseDTO;
import com.ecommerce.payment_service.services.PaymentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.RequestPostProcessor;

import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PaymentController.class)
@Import(SecurityConfig.class)
class PaymentControllerSecurityTest {

    private static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");
    private static final UUID OTHER_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");
    private static final UUID PAYMENT_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");
    private static final UUID ORDER_ID = UUID.fromString("55555555-5555-5555-5555-555555555555");

    private static final String CREATE_BODY = """
            {"orderId":"55555555-5555-5555-5555-555555555555","amount":99.90,"paymentMethod":"PIX","provider":"INTERNAL"}
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentService paymentService;

    @MockitoBean
    private OrderClient orderClient;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    private static RequestPostProcessor token(UUID userId, String role) {
        return jwt()
                .jwt(builder -> builder.claim("userId", userId.toString()))
                .authorities(new SimpleGrantedAuthority(role));
    }

    private PaymentResponseDTO paymentOwnedBy(UUID userId) {
        return PaymentResponseDTO.builder()
                .id(PAYMENT_ID)
                .userId(userId)
                .build();
    }

    @Test
    void myPayments_withoutToken_returns401() throws Exception {
        mockMvc.perform(get("/api/payments/my-payments"))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void myPayments_withToken_returns200() throws Exception {
        when(paymentService.getPaymentsByUserId(USER_ID)).thenReturn(List.of());

        mockMvc.perform(get("/api/payments/my-payments").with(token(USER_ID, "ROLE_CLIENT")))
                .andExpect(status().isOk());
    }

    @Test
    void getById_ownPayment_returns200() throws Exception {
        when(paymentService.getPaymentById(any())).thenReturn(paymentOwnedBy(USER_ID));

        mockMvc.perform(get("/api/payments/{id}", PAYMENT_ID).with(token(USER_ID, "ROLE_CLIENT")))
                .andExpect(status().isOk());
    }

    @Test
    void getById_otherUsersPayment_returns403() throws Exception {
        when(paymentService.getPaymentById(any())).thenReturn(paymentOwnedBy(OTHER_USER_ID));

        mockMvc.perform(get("/api/payments/{id}", PAYMENT_ID).with(token(USER_ID, "ROLE_CLIENT")))
                .andExpect(status().isForbidden());
    }

    @Test
    void getById_otherUsersPayment_asAdmin_returns200() throws Exception {
        when(paymentService.getPaymentById(any())).thenReturn(paymentOwnedBy(OTHER_USER_ID));

        mockMvc.perform(get("/api/payments/{id}", PAYMENT_ID).with(token(USER_ID, "ROLE_ADMIN")))
                .andExpect(status().isOk());
    }

    @Test
    void create_forOwnOrder_returns201() throws Exception {
        when(orderClient.currentUserCanAccessOrder(eq(ORDER_ID), any())).thenReturn(true);
        when(paymentService.createPayment(any())).thenReturn(paymentOwnedBy(USER_ID));

        mockMvc.perform(post("/api/payments")
                        .with(token(USER_ID, "ROLE_CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY))
                .andExpect(status().isCreated());
    }

    @Test
    void create_forForeignOrder_returns403() throws Exception {
        when(orderClient.currentUserCanAccessOrder(eq(ORDER_ID), any())).thenReturn(false);

        mockMvc.perform(post("/api/payments")
                        .with(token(USER_ID, "ROLE_CLIENT"))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(CREATE_BODY))
                .andExpect(status().isForbidden());
    }
}
