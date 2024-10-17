package com.ecommerce.user_service.common;

import com.ecommerce.user_service.dtos.AuthResponseDTO;
import com.ecommerce.user_service.entities.RefreshToken;
import com.ecommerce.user_service.enums.TokenType;

import static com.ecommerce.user_service.common.UserConstants.USER;

public class AuthConstants {

    public final static String AUTHORIZATION = "Bearer VALID_TOKEN";

    public final static String ACCESS_TOKEN = "ACCESS_TOKEN";

    public final static String REFRESH_TOKEN = "REFRESH_TOKEN";

    public static RefreshToken REFRESH_TOKEN_ENTITY = RefreshToken.builder()
            .refreshToken(REFRESH_TOKEN)
            .revoked(false)
            .user(USER)
            .build();

    public final static RefreshToken REFRESH_TOKEN_ENTITY_REVOKED = RefreshToken.builder()
            .refreshToken(REFRESH_TOKEN)
            .revoked(true)
            .user(USER)
            .build();

    public final static AuthResponseDTO AUTH_RESPONSE_DTO = AuthResponseDTO.builder()
            .accessToken(ACCESS_TOKEN)
            .accessTokenExpiry(5 * 60)
            .tokenType(TokenType.Bearer)
            .userName("user@example.com")
            .build();
}
