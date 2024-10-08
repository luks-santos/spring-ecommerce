package com.ecommerce.user_service.services;

import com.ecommerce.user_service.dtos.UserDTO;
import com.ecommerce.user_service.entities.User;
import com.ecommerce.user_service.mapper.UserMapper;
import com.ecommerce.user_service.repositories.UserRepo;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@RequiredArgsConstructor
@Service
@Slf4j
public class UserService {

    private final UserRepo repository;
    private final UserMapper mapper;

    public UserDTO update(Long id, UserDTO dto) {

        log.info("[UserService:update(id, dto)] Update process started for user with id {}", id);

        User loggedUser = getLoggedUser();

        User userToUpdate = repository.findById(id)
                .orElseThrow(() -> {
                    log.error("[UserService:update(id, dto)] User with id {} not found", id);
                    return new EntityNotFoundException("User with the provided ID does not exist.");
                });

        if (!loggedUser.getId().equals(userToUpdate.getId())) {
            log.warn("[UserService:update(id, dto)] User {} attempted to update another user's information.", loggedUser.getId());
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You do not have permission to modify another user's data.");
        }

        userToUpdate.setFirstName(dto.firstName());
        userToUpdate.setLastName(dto.lastName());
        userToUpdate.setPhone(dto.phone());
        userToUpdate.setAddress(dto.address());

        return mapper.toDTO(repository.save(userToUpdate));
    }


    public User getLoggedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return repository.findByEmail(authentication.getName())
                .orElseThrow(() -> {
                    log.error("[UserService:getLoggedUser] Logged in user with email {} not found", authentication.getName());
                    return new EntityNotFoundException("Authenticated user not found.");
                });
    }

}
