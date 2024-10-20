package com.ecommerce.user_service.controllers;

import com.ecommerce.user_service.services.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;

import static com.ecommerce.user_service.common.UserConstants.USER_DTO;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(UserController.class)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserService userService;


    @Test
    public void getLoggedUser_WithValidRequest_ReturnsOkWithUserDTO() throws Exception {
        when(userService.getLoggedUserDTO()).thenReturn(USER_DTO);

        mockMvc.perform(get("/api/account/logged-user")
                        .with(SecurityMockMvcRequestPostProcessors.jwt()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.firstName").value("First Name"))
                .andExpect(jsonPath("$.lastName").value("Last Name"))
                .andExpect(jsonPath("$.email").value("user@example.com"));
    }
}