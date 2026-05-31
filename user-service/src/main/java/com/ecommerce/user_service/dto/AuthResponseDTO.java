package com.ecommerce.user_service.dto;

import com.ecommerce.user_service.enums.TokenType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
public class AuthResponseDTO {

    @JsonProperty("access_token")
    private String accessToken;

    @JsonProperty("access_token_expiry")
    private int accessTokenExpiry;

    @JsonProperty("token_type")
    private TokenType tokenType;

    @JsonProperty("user_name")
    private String userName;

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AuthResponseDTO that = (AuthResponseDTO) o;
        return accessTokenExpiry == that.accessTokenExpiry && Objects.equals(accessToken, that.accessToken) && tokenType == that.tokenType && Objects.equals(userName, that.userName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(accessToken, accessTokenExpiry, tokenType, userName);
    }
}
