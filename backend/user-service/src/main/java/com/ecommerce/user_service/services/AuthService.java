package com.ecommerce.user_service.services;

import com.ecommerce.user_service.config.jwt.JwtTokenGenerator;
import com.ecommerce.user_service.dtos.AuthResponseDTO;
import com.ecommerce.user_service.dtos.UserRegistrationDTO;
import com.ecommerce.user_service.entities.RefreshToken;
import com.ecommerce.user_service.entities.User;
import com.ecommerce.user_service.enums.TokenType;
import com.ecommerce.user_service.exceptions.ApiException;
import com.ecommerce.user_service.mapper.UserMapper;
import com.ecommerce.user_service.repositories.RefreshTokenRepo;
import com.ecommerce.user_service.repositories.UserRepo;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepo repository;
    private final JwtTokenGenerator jwtTokenGenerator;
    private final RefreshTokenRepo refreshTokenRepo;
    private final UserMapper userMapper;

    public AuthResponseDTO getJwtTokensAfterAuthentication(Authentication authentication, HttpServletResponse response) {
        try {
            var user = repository.findByEmail(authentication.getName())
                    .orElseThrow(() -> {
                        log.error("[AuthService:userSignInAuth] User :{} not found", authentication.getName());
                        return new ApiException(HttpStatus.NOT_FOUND, "User not found.");
                    });

            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

            saveUserRefreshToken(user, refreshToken);
            createRefreshTokenCookie(response, refreshToken);

            log.info("[AuthService:userSignInAuth] Access token for user:{}, has been generated", user.getEmail());
            return AuthResponseDTO.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(5 * 60)
                    .userName(user.getEmail())
                    .tokenType(TokenType.Bearer)
                    .build();

        } catch (ApiException e) {
            log.error("[AuthService:userSignInAuth]Exception while authenticating the user due to :{}", e.getMessage());
            throw e;
        }
        catch (Exception e) {
            log.error("[AuthService:userSignInAuth] Unexpected exception while sign-in in the user: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred during user sign-in", e);
        }
    }

    private void saveUserRefreshToken(User user, String refreshToken) {
        var refreshTokenEntity = RefreshToken.builder()
                .user(user)
                .refreshToken(refreshToken)
                .revoked(false)
                .build();
        refreshTokenRepo.save(refreshTokenEntity);
    }

    private void createRefreshTokenCookie(HttpServletResponse response, String refreshToken) {
        Cookie refreshTokenCookie = new Cookie("refresh_token", refreshToken);
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setSecure(true);
        refreshTokenCookie.setMaxAge(15 * 24 * 60 * 60); // in seconds
        response.addCookie(refreshTokenCookie);
    }

    public AuthResponseDTO getAccessTokenUsingRefreshToken(String authorizationHeader) {
        try {
            if (authorizationHeader == null || !authorizationHeader.startsWith(TokenType.Bearer.name())) {
                throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Please verify your token type.");
            }

            final String refreshToken = authorizationHeader.substring(7);

            //Find refreshToken from database and should not be revoked : Same thing can be done through filter.
            var refreshTokenEntity = refreshTokenRepo.findByRefreshToken(refreshToken)
                    .filter(tokens -> !tokens.isRevoked())
                    .orElseThrow(() -> new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "Refresh token revoked."));

            User user = refreshTokenEntity.getUser();

            //Now create the Authentication object
            Authentication authentication = createAuthenticationObject(user);

            //Use the authentication object to generate new accessToken as the Authentication object that we will have may not contain correct role.
            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);

            return AuthResponseDTO.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(5 * 60)
                    .userName(user.getEmail())
                    .tokenType(TokenType.Bearer)
                    .build();

        } catch (ApiException e) {
            log.error("[AuthService:getAccessTokenUsingRefreshToken]Exception while getting access token using refresh token: {}", e.getMessage());
            throw e;
        }
        catch (Exception e) {
            log.error("[AuthService:getAccessTokenUsingRefreshToken]Unexpected exception while getting access token using refresh token: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error while getting access token using refresh token", e);
        }

    }

    private static Authentication createAuthenticationObject(User user) {
        // Extract user details from UserDetailsEntity
        String username = user.getEmail();
        String password = user.getPassword();
        String roles = user.getRole().name();

        // Extract authorities from roles (comma-separated)
        String[] roleArray = roles.split(",");
        GrantedAuthority[] authorities = Arrays.stream(roleArray)
                .map(role -> (GrantedAuthority) role::trim)
                .toArray(GrantedAuthority[]::new);

        return new UsernamePasswordAuthenticationToken(username, password, Arrays.asList(authorities));
    }

    public AuthResponseDTO registerUser(UserRegistrationDTO userRegistrationDto, HttpServletResponse httpServletResponse) {

        try {
            log.info("[AuthService:registerUser]User Registration Started with :::{}", userRegistrationDto);

            Optional<User> user = repository.findByEmail(userRegistrationDto.email());
            if (user.isPresent()) {
                throw new ApiException(HttpStatus.CONFLICT, "User Already Exist.");
            }

            User userEntity = userMapper.toEntity(userRegistrationDto);
            Authentication authentication = createAuthenticationObject(userEntity);

            // Generate a JWT token
            String accessToken = jwtTokenGenerator.generateAccessToken(authentication);
            String refreshToken = jwtTokenGenerator.generateRefreshToken(authentication);

            User savedUser = repository.save(userEntity);
            saveUserRefreshToken(userEntity, refreshToken);

            createRefreshTokenCookie(httpServletResponse, refreshToken);

            log.info("[AuthService:registerUser]User: {} Successfully registered", savedUser.getEmail());
            return AuthResponseDTO.builder()
                    .accessToken(accessToken)
                    .accessTokenExpiry(5 * 60)
                    .userName(savedUser.getEmail())
                    .tokenType(TokenType.Bearer)
                    .build();

        } catch (ApiException e) {
            log.error("[AuthService:registerUser]Exception while registering the user due to : {}", e.getMessage());
            throw e;
        }
        catch (Exception e) {
            log.error("[AuthService:registerUser]Unexpected exception while registering the user: {}", e.getMessage());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, "An unexpected error occurred during user registration", e);
        }
    }
}
