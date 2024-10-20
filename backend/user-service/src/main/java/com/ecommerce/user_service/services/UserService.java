package com.ecommerce.user_service.services;

import com.ecommerce.user_service.dtos.UserDTO;
import com.ecommerce.user_service.entities.User;
import com.ecommerce.user_service.exceptions.ApiException;
import com.ecommerce.user_service.mapper.UserMapper;
import com.ecommerce.user_service.repositories.UserRepo;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepo repository;
    private final UserMapper mapper;

    public UserDTO update(@Valid UserDTO dto) {

        User loggedUserToUpdate = getLoggedUser();

        log.info("[UserService:update(id, dto)] Update process started for user with id {}", loggedUserToUpdate.getId());

        loggedUserToUpdate.setFirstName(dto.getFirstName());
        loggedUserToUpdate.setLastName(dto.getLastName());
        loggedUserToUpdate.setAddress(dto.getAddress());
        loggedUserToUpdate.setPhone(dto.getPhone());

        return mapper.toDTO(repository.save(loggedUserToUpdate));
    }

    public UserDTO getLoggedUserDTO() {
        return mapper.toDTO(getLoggedUser());
    }

    public User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return repository.findByEmail(authentication.getName())
                .orElseThrow(() -> {
                    log.error("[UserService:getLoggedUser]Logged in user with email {} not found", authentication.getName());
                    return new ApiException(HttpStatus.NOT_FOUND, "Authenticated user not found.");
                });
    }

}
