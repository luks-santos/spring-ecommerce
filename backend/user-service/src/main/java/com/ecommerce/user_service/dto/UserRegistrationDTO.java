package com.ecommerce.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;

@Builder
public record UserRegistrationDTO(
        @NotBlank(message = "User first name must not be empty")
        String firstName,
        @NotBlank(message = "User last name must not be empty")
        String lastName,
        @Email(message = "Invalid email format")
        @NotBlank(message = "User email must not be empty")
        String email,
        @NotBlank(message = "User phone must not be empty")
        String phone,
        @NotBlank(message = "User address must not be empty")
        String address,
        @NotBlank(message = "User password must not be empty")
        String password
) {
}
