package com.ecommerce.user_service.services;

import com.ecommerce.user_service.enums.TokenType;
import com.ecommerce.user_service.repositories.RefreshTokenRepo;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LogoutHandlerService implements LogoutHandler {

    private final RefreshTokenRepo refreshTokenRepo;

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        final String authHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

        if (authHeader == null || !authHeader.startsWith(TokenType.Bearer.name())) {
            throw new BadCredentialsException("Invalid authentication token type. Expected Bearer token.");
        }

        final String refreshToken = authHeader.substring(7);

        refreshTokenRepo.findByRefreshToken(refreshToken)
                .map(token -> {
                    token.setRevoked(true);
                    refreshTokenRepo.save(token);
                    return token;
                });
    }
}
