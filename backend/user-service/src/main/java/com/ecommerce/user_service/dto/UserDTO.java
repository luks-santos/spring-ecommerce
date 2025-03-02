package com.ecommerce.user_service.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Objects;

@Getter
@Setter
@Builder
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserDTO userDTO = (UserDTO) o;
        return Objects.equals(id, userDTO.id) && Objects.equals(firstName, userDTO.firstName) && Objects.equals(lastName, userDTO.lastName) && Objects.equals(email, userDTO.email) && Objects.equals(phone, userDTO.phone) && Objects.equals(address, userDTO.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, firstName, lastName, email, phone, address);
    }
}
