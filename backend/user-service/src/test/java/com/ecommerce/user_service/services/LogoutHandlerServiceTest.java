package com.ecommerce.user_service.services;

import com.ecommerce.user_service.repositories.RefreshTokenRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;

import java.util.Optional;

import static com.ecommerce.user_service.services.common.AuthConstants.AUTHORIZATION;
import static com.ecommerce.user_service.services.common.AuthConstants.REFRESH_TOKEN_ENTITY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LogoutHandlerServiceTest {


    @Mock
    HttpServletRequest request;

    @Mock
    HttpServletResponse response;

    @Mock
    Authentication authentication;

    @Mock
    private RefreshTokenRepo refreshTokenRepo;

    @InjectMocks
    private LogoutHandlerService handlerService;

    @Test
    void logout_WithValidToken() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(AUTHORIZATION);
        when(refreshTokenRepo.findByRefreshToken(AUTHORIZATION.substring(7))).thenReturn(Optional.of(REFRESH_TOKEN_ENTITY));

        //sut
        handlerService.logout(request, response, authentication);

        assertThat(REFRESH_TOKEN_ENTITY.isRevoked()).isTrue();
        verify(refreshTokenRepo).save(REFRESH_TOKEN_ENTITY);
    }

    @Test
    void logout_NoAuthHeaderOrInvalidHeader_ReturnsBadCredentialsException() {
        when(request.getHeader(HttpHeaders.AUTHORIZATION)).thenReturn(null);
        assertThatThrownBy(() -> handlerService.logout(request, response, authentication))
                .isExactlyInstanceOf(BadCredentialsException.class)
                .hasMessage("Invalid authentication token type. Expected Bearer token.");
    }
}