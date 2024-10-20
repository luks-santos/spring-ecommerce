package com.ecommerce.user_service.controllers;

import com.ecommerce.user_service.dtos.UserDTO;
import com.ecommerce.user_service.services.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/account")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping(value = "/logged-user")
    public ResponseEntity<UserDTO> getLoggedUser() {
        return ResponseEntity.ok(service.getLoggedUserDTO());
    }

    @PutMapping(value = "/update_profile")
    public ResponseEntity<UserDTO> update(@RequestBody @Valid UserDTO dto) {
        return ResponseEntity.ok(service.update(dto));
    }
}
