package com.ecommerce.user_service.services;

import com.ecommerce.user_service.config.jwt.JwtTokenGenerator;
import com.ecommerce.user_service.dto.AuthResponseDTO;
import com.ecommerce.user_service.entities.RefreshToken;
import com.ecommerce.user_service.exceptions.ApiException;
import com.ecommerce.user_service.mapper.UserMapper;
import com.ecommerce.user_service.repositories.RefreshTokenRepo;
import com.ecommerce.user_service.repositories.UserRepo;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static com.ecommerce.user_service.common.AuthConstants.*;
import static com.ecommerce.user_service.common.UserConstants.USER;
import static com.ecommerce.user_service.common.UserConstants.USER_REGISTRATION_DTO;
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
    private HttpServletResponse response;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private AuthService authService;

    @Test
    void getJwtTokensAfterAuthentication_WithValidData_ReturnsAuthResponseDTO() {
        when(repository.findByEmail(any())).thenReturn(Optional.of(USER));
        when(jwtTokenGenerator.generateAccessToken(any())).thenReturn(ACCESS_TOKEN);
        when(jwtTokenGenerator.generateRefreshToken(any())).thenReturn(REFRESH_TOKEN);
        when(refreshTokenRepo.save(any(RefreshToken.class))).thenReturn(REFRESH_TOKEN_ENTITY);

        AuthResponseDTO sut = authService.getJwtTokensAfterAuthentication(authentication, response);

        assertThat(sut).isEqualTo(AUTH_RESPONSE_DTO);
    }

    @Test
    void getJwtTokensAfterAuthentication_WhenUserNotExists_ReturnsApiException() {
        when(repository.findByEmail(any())).thenReturn(Optional.empty());
        when(authentication.getName()).thenReturn("user@example.com");

        assertThatThrownBy(() -> authService.getJwtTokensAfterAuthentication(authentication, response))
                .isExactlyInstanceOf(ApiException.class)
                .hasMessage(HttpStatus.NOT_FOUND + " \"User not found.\"");
    }

    @Test
    void getAccessTokenUsingRefreshToken_WithValidData_ReturnsAuthResponseDTO() {
        when(refreshTokenRepo.findByRefreshToken(any())).thenReturn(Optional.of(REFRESH_TOKEN_ENTITY));
        when(jwtTokenGenerator.generateAccessToken(any(Authentication.class))).thenReturn(ACCESS_TOKEN);

        AuthResponseDTO sut = authService.getAccessTokenUsingRefreshToken(AUTHORIZATION);

        assertThat(sut).isEqualTo(AUTH_RESPONSE_DTO);
    }

    @Test
    void getAccessTokenUsingRefreshToken_RefreshTokenIsRevoked_ReturnsApiException() {
        when(refreshTokenRepo.findByRefreshToken(any())).thenReturn(Optional.of(REFRESH_TOKEN_ENTITY_REVOKED));

        assertThatThrownBy(() -> authService.getAccessTokenUsingRefreshToken(AUTHORIZATION))
                .isExactlyInstanceOf(ApiException.class)
                .hasMessage(HttpStatus.INTERNAL_SERVER_ERROR + " \"Refresh token revoked.\"");
    }

    @Test
    void getAccessTokenUsingRefreshToken_NoRefreshTokenOrInvalidRefreshToken_ReturnsApiException() {
        assertThatThrownBy(() -> authService.getAccessTokenUsingRefreshToken(null))
                .isExactlyInstanceOf(ApiException.class)
                .hasMessage(HttpStatus.INTERNAL_SERVER_ERROR + " \"Please verify your token type.\"");
    }

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
    void registerUser_WhenUserAlreadyExists_returnsApiException() {
        when(repository.findByEmail(any())).thenReturn(Optional.of(USER));

        assertThatThrownBy(() -> authService.registerUser(USER_REGISTRATION_DTO, response))
                .isExactlyInstanceOf(ApiException.class)
                .hasMessage(HttpStatus.CONFLICT + " \"User Already Exist.\"");

    }
}