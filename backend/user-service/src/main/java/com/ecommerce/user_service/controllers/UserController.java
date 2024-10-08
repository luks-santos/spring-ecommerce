package com.ecommerce.user_service.controllers;

import com.ecommerce.user_service.dtos.UserDTO;
import com.ecommerce.user_service.services.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/user")
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @GetMapping(value = "/logged-user")
    public ResponseEntity<UserDTO> getLoggedUser() {
        return ResponseEntity.ok(service.getLoggedUserDTO());
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<UserDTO> update(@PathVariable Long id, @RequestBody UserDTO dto) {
        return ResponseEntity.ok(service.update(id, dto));
    }


}
