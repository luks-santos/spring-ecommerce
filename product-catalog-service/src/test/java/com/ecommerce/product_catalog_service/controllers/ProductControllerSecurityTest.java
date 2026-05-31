package com.ecommerce.product_catalog_service.controllers;

import com.ecommerce.product_catalog_service.common.ProductTestConstants;
import com.ecommerce.product_catalog_service.config.SecurityConfig;
import com.ecommerce.product_catalog_service.services.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@Import(SecurityConfig.class)
class ProductControllerSecurityTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProductService productService;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    @Test
    void getAll_isPublic_returns200() throws Exception {
        when(productService.findAll()).thenReturn(List.of());

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk());
    }

    @Test
    void create_withoutToken_returns401() throws Exception {
        mockMvc.perform(post("/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ProductTestConstants.createProductDTO())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void create_withClientRole_returns403() throws Exception {
        mockMvc.perform(post("/products")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_CLIENT")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ProductTestConstants.createProductDTO())))
                .andExpect(status().isForbidden());
    }

    @Test
    void create_withAdminRole_returns201() throws Exception {
        when(productService.create(ProductTestConstants.createProductDTO()))
                .thenReturn(ProductTestConstants.createProduct());

        mockMvc.perform(post("/products")
                        .with(jwt().authorities(new SimpleGrantedAuthority("ROLE_ADMIN")))
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(ProductTestConstants.createProductDTO())))
                .andExpect(status().isCreated());
    }
}
