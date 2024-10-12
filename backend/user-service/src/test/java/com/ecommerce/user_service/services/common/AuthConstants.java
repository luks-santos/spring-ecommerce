package com.ecommerce.user_service.services.common;

import com.ecommerce.user_service.entities.RefreshToken;

public class AuthConstants {

    public final static String AUTHORIZATION = "Bearer validToken";

    public final static RefreshToken REFRESH_TOKEN_ENTITY = RefreshToken.builder()
            .id(1L)
            .refreshToken(AUTHORIZATION)
            .revoked(false)
            .build();
}
