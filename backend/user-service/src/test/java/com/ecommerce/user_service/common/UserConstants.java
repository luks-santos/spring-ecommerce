package com.ecommerce.user_service.common;

import com.ecommerce.user_service.dtos.UserDTO;
import com.ecommerce.user_service.dtos.UserRegistrationDTO;
import com.ecommerce.user_service.entities.User;
import com.ecommerce.user_service.enums.UserRole;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class UserConstants {

    public static final UserDTO USER_DTO = UserDTO.builder()
            .firstName("First Name")
            .lastName("Last Name")
            .email("user@example.com")
            .phone("phone")
            .address("address")
            .build();

    public static final UserDTO INVALID_USER_DTO = UserDTO.builder()
            .firstName(" ")
            .lastName(" ")
            .email("invalidemail.com")
            .phone(" ")
            .address(" ")
            .build();


    public static final User USER = User.builder()
            .firstName("First Name")
            .lastName("Last Name")
            .email("user@example.com")
            .password("password")
            .address("address")
            .phone("phone")
            .role(UserRole.ROLE_CLIENT)
            .createdAt(LocalDateTime.now())
            .refreshTokens(new ArrayList<>())
            .build();

    public static final User INVALID_USER = User.builder()
            .firstName(" ")
            .lastName(" ")
            .email("user@example.com")
            .password("password")
            .address(" ")
            .phone(" ")
            .role(UserRole.ROLE_CLIENT)
            .createdAt(LocalDateTime.now())
            .refreshTokens(new ArrayList<>())
            .build();

    public static final UserRegistrationDTO USER_REGISTRATION_DTO = new UserRegistrationDTO(
            "First Name", "Last Name", "user@example.com", "phone", "address", "password", UserRole.ROLE_CLIENT
    );
}
