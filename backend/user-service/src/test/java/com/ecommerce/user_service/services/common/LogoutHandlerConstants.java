package com.ecommerce.user_service.services.common;

import com.ecommerce.user_service.dtos.AuthResponseDTO;
import com.ecommerce.user_service.entities.RefreshToken;
import com.ecommerce.user_service.enums.TokenType;

import static com.ecommerce.user_service.services.common.UserConstants.USER;

public class LogoutHandlerConstants {

    public final static String AUTHORIZATION = "Bearer VALID_TOKEN";

    public final static String ACCESS_TOKEN = "ACCESS_TOKEN";

    public final static String REFRESH_TOKEN = "REFRESH_TOKEN";

    public final static RefreshToken REFRESH_TOKEN_ENTITY = RefreshToken.builder()
            .id(1L)
            .refreshToken(REFRESH_TOKEN)
            .revoked(false)
            .user(USER)
            .build();
}
