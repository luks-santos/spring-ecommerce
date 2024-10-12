package com.ecommerce.user_service.services;

import com.ecommerce.user_service.config.jwt.JwtTokenGenerator;
import com.ecommerce.user_service.dtos.AuthResponseDTO;
import com.ecommerce.user_service.entities.RefreshToken;
import com.ecommerce.user_service.mapper.UserMapper;
import com.ecommerce.user_service.repositories.RefreshTokenRepo;
import com.ecommerce.user_service.repositories.UserRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

import static com.ecommerce.user_service.services.common.AuthConstants.*;
import static com.ecommerce.user_service.services.common.UserConstants.USER;
import static com.ecommerce.user_service.services.common.UserConstants.USER_REGISTRATION_DTO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepo repository;

    @Mock
    private JwtTokenGenerator jwtTokenGenerator;

    @Mock
    private RefreshTokenRepo refreshTokenRepo;

    @Mock
    private UserMapper userMapper;

    @Mock
    HttpServletResponse response;

    @InjectMocks
    private AuthService authService;


    @Test
    void registerUser_WithValidData_ReturnsAuthResponseDTO() {
        when(repository.findByEmail(any())).thenReturn(Optional.empty());
        when(userMapper.toEntity(USER_REGISTRATION_DTO)).thenReturn(USER);
        when(jwtTokenGenerator.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);
        when(jwtTokenGenerator.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(repository.save(USER)).thenReturn(USER);
        when(refreshTokenRepo.save(any(RefreshToken.class))).thenReturn(REFRESH_TOKEN_ENTITY);


        AuthResponseDTO sut = authService.registerUser(USER_REGISTRATION_DTO, response);

        assertThat(sut).isEqualTo(AUTH_RESPONSE_DTO);
    }

    @Test
    void registerUser_UserAlreadyExists_returnsEntityExistsException() {
        when(repository.findByEmail(any())).thenReturn(Optional.of(USER));

        assertThatThrownBy(() -> authService.registerUser(USER_REGISTRATION_DTO, response))
                .isExactlyInstanceOf(ResponseStatusException.class)
                .hasMessage("400 BAD_REQUEST \"User Already Exist.\"");

    }
}