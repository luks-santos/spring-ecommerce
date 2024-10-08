package com.ecommerce.user_service.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;

@Builder
public record UserDTO(Long id, String firstName, String lastName, String email, String phone, String address) {
}
