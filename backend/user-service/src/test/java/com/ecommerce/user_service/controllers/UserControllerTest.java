package com.ecommerce.user_service.controllers;

import com.ecommerce.user_service.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static com.ecommerce.user_service.common.UserConstants.INVALID_USER_DTO;
import static com.ecommerce.user_service.common.UserConstants.USER_DTO;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;

    @Test
    public void getLoggedUser_WithValidRequest_ReturnsOkWithUserDTO() throws Exception {
        when(userService.getLoggedUserDTO()).thenReturn(USER_DTO);

        mockMvc.perform(get("/api/account/logged-user")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(USER_DTO));
    }

    @Test
    public void update_WithValidData_ReturnsOkWithUserDTO() throws Exception {
        when(userService.update(USER_DTO)).thenReturn(USER_DTO);

        mockMvc.perform(put("/api/account/update_profile")
                        .content(objectMapper.writeValueAsString(USER_DTO)).contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value(USER_DTO));
    }

    @Test
    public void update_WithInvalidData_ReturnsBadRequest() throws Exception {
        mockMvc.perform(put("/api/account/update_profile")
                        .content(objectMapper.writeValueAsString(INVALID_USER_DTO)).contentType(MediaType.APPLICATION_JSON)
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andDo(print())
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.firstName").value("User first name must not be empty"))
                .andExpect(jsonPath("$.lastName").value("User last name must not be empty"))
                .andExpect(jsonPath("$.email").value("Invalid email format"))
                .andExpect(jsonPath("$.phone").value("User phone must not be empty"))
                .andExpect(jsonPath("$.address").value("User address must not be empty"));
    }
}