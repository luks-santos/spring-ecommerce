package com.ecommerce.user_service.services;

import com.ecommerce.user_service.dto.UserDTO;
import com.ecommerce.user_service.entities.User;
import com.ecommerce.user_service.exceptions.ApiException;
import com.ecommerce.user_service.mapper.UserMapper;
import com.ecommerce.user_service.repositories.UserRepo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static com.ecommerce.user_service.common.UserConstants.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepo userRepo;

    @Mock
    private UserMapper userMapper;

    @Mock
    private SecurityContext securityContext;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private UserService service;

    @BeforeEach
    public void setup() {
        stubAuthentication();
    }

    //todo método-test=operacao_estado_retorno

    private void stubAuthentication() {
        // Mock SecurityContext and Authentication
        SecurityContextHolder.setContext(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.getName()).thenReturn("user@email.com");
    }

    @Test
    void update_WithValidData_ReturnsUserDTO() {
        // Mock repository and mapper behavior
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(USER));
        when(userRepo.save(any(User.class))).thenReturn(USER);
        when(userMapper.toDTO(any(User.class))).thenReturn(USER_DTO);

        // system under test
        UserDTO sut = service.update(USER_DTO);

        // Validate the result
        assertThat(sut).isEqualTo(USER_DTO);
    }

    @Test
    void update_WithInvalidData_ThrowsException() {
        // Mock repository and mapper behavior
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(USER));
        when(userRepo.save(INVALID_USER)).thenThrow(RuntimeException.class);

        // Validate the result
        assertThatThrownBy(() -> service.update(INVALID_USER_DTO)).isExactlyInstanceOf(RuntimeException.class);
    }

    @Test
    void getLoggedUserDTO_WithValidData_ReturnsUserDTO() {
        // Mock repository
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.of(USER));
        when(userMapper.toDTO(any(User.class))).thenReturn(USER_DTO);

        // system under test
        UserDTO sut = service.getLoggedUserDTO();

        // Validate the result
        assertThat(sut).isEqualTo(USER_DTO);
    }

    @Test
    void getLoggedUserDTO_WithInvalidData_ReturnsException() {
        // Mock repository
        when(userRepo.findByEmail(anyString())).thenReturn(Optional.empty());

        // Validate the result
        assertThatThrownBy(() -> service.getLoggedUserDTO())
                .isExactlyInstanceOf(ApiException.class)
                .hasMessage("404 NOT_FOUND \"Authenticated user not found.\"");
    }
}