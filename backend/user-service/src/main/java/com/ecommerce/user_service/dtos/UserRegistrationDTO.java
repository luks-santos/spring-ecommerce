package com.ecommerce.user_service.dtos;

import com.ecommerce.user_service.enums.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

public record UserRegistrationDTO(
        @NotEmpty(message = "User first name must not be empty")
        String firstName,
        @NotEmpty(message = "User last name must not be empty")
        String lastName,
        @Email(message = "Invalid email format")
        @NotEmpty(message = "User email must not be empty")
        String email,
        String phone,
        String address,
        @NotEmpty(message = "User password must not be empty")
        String password,
        @NotNull(message = "User role must not be null")
        UserRole role) {
}
