package com.ecommerce.user_service.common;

import com.ecommerce.user_service.entities.RefreshToken;

import static com.ecommerce.user_service.common.UserConstants.USER;

public class LogoutHandlerConstants {

    public final static String AUTHORIZATION = "Bearer VALID_TOKEN";

    public final static String REFRESH_TOKEN = "REFRESH_TOKEN";

    public final static RefreshToken REFRESH_TOKEN_ENTITY = RefreshToken.builder()
            .refreshToken(REFRESH_TOKEN)
            .revoked(false)
            .user(USER)
            .build();
}
