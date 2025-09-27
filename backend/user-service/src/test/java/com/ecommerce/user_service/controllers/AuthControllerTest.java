package com.ecommerce.user_service.controllers;

import com.ecommerce.user_service.exceptions.ApiException;
import com.ecommerce.user_service.services.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static com.ecommerce.user_service.common.AuthConstants.AUTH_RESPONSE_DTO;
import static com.ecommerce.user_service.common.UserConstants.INVALID_USER_REGISTRATION_DTO;
import static com.ecommerce.user_service.common.UserConstants.USER_REGISTRATION_DTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private AuthService authService;

    @Test
    void registerUser_WithValidData_ReturnsOkWithAuthResponseDTO() throws Exception {
        when(authService.registerUser(eq(USER_REGISTRATION_DTO), any(HttpServletResponse.class))).thenReturn(AUTH_RESPONSE_DTO);

        mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(USER_REGISTRATION_DTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.access_token").value(AUTH_RESPONSE_DTO.getAccessToken()))
                .andExpect(jsonPath("$.access_token_expiry").value(AUTH_RESPONSE_DTO.getAccessTokenExpiry()))
                .andExpect(jsonPath("$.token_type").value(AUTH_RESPONSE_DTO.getTokenType().name()))
                .andExpect(jsonPath("$.user_name").value(AUTH_RESPONSE_DTO.getUserName()));
    }

    @Test
    void registerUser_WithInvalidData_ReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(INVALID_USER_REGISTRATION_DTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.firstName").value("User first name must not be empty"))
                .andExpect(jsonPath("$.lastName").value("User last name must not be empty"))
                .andExpect(jsonPath("$.email").value("Invalid email format"))
                .andExpect(jsonPath("$.phone").value("User phone must not be empty"))
                .andExpect(jsonPath("$.address").value("User address must not be empty"));
    }

    @Test
    void registerUser_WithExistingEmail_ReturnsConflict() throws Exception {
        when(authService.registerUser(eq(USER_REGISTRATION_DTO), any(HttpServletResponse.class)))
                .thenThrow(new ApiException(HttpStatus.CONFLICT, "User Already Exist."));

        mockMvc.perform(post("/sign-up")
                        .content(objectMapper.writeValueAsString(USER_REGISTRATION_DTO))
                        .contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andDo(print())
                .andExpect(status().isConflict());

    }
}