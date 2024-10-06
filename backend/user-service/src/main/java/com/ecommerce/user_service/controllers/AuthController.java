package com.ecommerce.user_service.controllers;

import com.ecommerce.user_service.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class AuthController {

    private final AuthService service;

    @PostMapping("/sign-in")
    public ResponseEntity<?> authenticateUser(Authentication authentication){

        return ResponseEntity.ok(service.getJwtTokensAfterAuthentication(authentication));
    }
}
