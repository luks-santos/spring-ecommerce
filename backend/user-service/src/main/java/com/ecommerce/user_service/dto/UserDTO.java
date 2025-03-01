package com.ecommerce.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@Builder
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private Long id;

    @NotBlank(message = "User first name must not be empty")
    private String firstName;

    @NotBlank(message = "User last name must not be empty")
    private String lastName;

    @Email(message = "Invalid email format")
    @NotBlank(message = "User email must not be empty")
    private String email;

    @NotBlank(message = "User phone must not be empty")
    private String phone;

    @NotBlank(message = "User address must not be empty")
    private String address;
}
