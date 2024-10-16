package com.ecommerce.user_service.repositories;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import static com.ecommerce.user_service.common.UserConstants.USER;

@DataJpaTest(properties = "spring.main.web-application-type=servlet")
@ImportAutoConfiguration(classes = SecurityAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles(profiles = "test")
public class UserRepoTest {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private TestEntityManager testEntityManager;


    @Test
    public void findByEmail_WithValidData_ReturnsUser() {
        userRepo.save(USER);


    }
}
