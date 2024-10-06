package com.ecommerce.user_service.services;

import com.ecommerce.user_service.config.jwt.JwtTokenGenerator;
import com.ecommerce.user_service.dtos.AuthResponseDTO;
import com.ecommerce.user_service.enums.TokenType;
import com.ecommerce.user_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository repository;
    private final JwtTokenGenerator jwtTokenGenerator;

    public AuthResponseDTO getJwtTokensAfterAuthentication(Authentication authentication) {
        try {
            var user = repository.findByEmail(authentication.getName())
                    .orElseThrow(() -> {
                        log.error("[AuthService:userSignInAuth] User :{} not found", authentication.getName());
                        return new ResponseStatusException(HttpStatus.NOT_FOUND, "USER NOT FOUND ");
                    });


            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

            log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated", user.getEmail());
            return AuthResponseDTO.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(15 * 60)
                    .userName(user.getEmail())
                    .tokenType(TokenType.Bearer)
                    .build();

        } catch (Exception e) {
            log.error("[AuthService:userSignInAuth]Exception while authenticating the user due to :{}", e.getMessage());
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Please Try Again");
        }
    }
}
