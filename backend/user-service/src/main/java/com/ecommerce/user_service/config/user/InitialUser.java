package com.ecommerce.user_service.config.user;

import com.ecommerce.user_service.entities.User;
import com.ecommerce.user_service.enums.UserRole;
import com.ecommerce.user_service.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.List;


@RequiredArgsConstructor
@Component
@Slf4j
public class InitialUser implements CommandLineRunner {

    private final UserRepository repository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) throws Exception {
        User client = new User();
        client.setFirstName("Client");
        client.setLastName("1");
        client.setEmail("client@email.com");
        client.setPassword(passwordEncoder.encode("password"));
        client.setAddress("Rua 1");
        client.setPhone("+55 37 99999-9999");
        client.setRole(UserRole.ROLE_CLIENT);

        User admin = new User();
        admin.setFirstName("Admin");
        admin.setLastName("1");
        admin.setEmail("admin@email.com");
        admin.setPassword(passwordEncoder.encode("password"));
        admin.setAddress("Rua 1");
        admin.setPhone("+55 37 99999-9999");
        admin.setRole(UserRole.ROLE_ADMIN);

        repository.saveAll(List.of(client, admin));
    }
}
