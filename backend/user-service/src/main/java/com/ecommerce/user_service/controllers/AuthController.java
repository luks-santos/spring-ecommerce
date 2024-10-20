package com.ecommerce.user_service.controllers;

import com.ecommerce.user_service.dtos.AuthResponseDTO;
import com.ecommerce.user_service.dtos.UserRegistrationDTO;
import com.ecommerce.user_service.services.AuthService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AuthController {

    private final AuthService service;

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(Authentication authentication, HttpServletResponse response) {
        return ResponseEntity.ok(service.getJwtTokensAfterAuthentication(authentication, response));
    }

    @PreAuthorize("hasAuthority('SCOPE_REFRESH_TOKEN')")
    @PostMapping("/refresh-token")
    public ResponseEntity<?> getAccessToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String authorizationHeader) {
        return ResponseEntity.ok(service.getAccessTokenUsingRefreshToken(authorizationHeader));
    }

    @PostMapping("/sign-up")
    public ResponseEntity<AuthResponseDTO> registerUser(@RequestBody @Valid UserRegistrationDTO userRegistrationDto,
                                                        HttpServletResponse httpServletResponse) {

        log.info("[AuthController:registerUser]Signup Process Started for user:{}", userRegistrationDto.email());
        AuthResponseDTO response = service.registerUser(userRegistrationDto, httpServletResponse);
        return ResponseEntity.ok(response);
    }
}
