package com.ecommerce.user_service.repositories;

import com.ecommerce.user_service.entities.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.ecommerce.user_service.common.UserConstants.USER;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

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
    public void save_WithExistingEmail_ThrowsException() {
        User user = testEntityManager.persistFlushFind(USER);
        testEntityManager.detach(user);
        user.setId(null);

        assertThatThrownBy(() -> userRepo.save(user)).isExactlyInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    public void findByEmail_WithValidData_ReturnsUser() {
        User user = testEntityManager.persistFlushFind(USER);

        Optional<User> sut = userRepo.findByEmail(user.getEmail());

        assertThat(sut).isPresent();
        assertThat(sut.get()).isNotNull();
        assertThat(sut.get().getEmail()).isEqualTo(USER.getEmail());
        assertThat(sut.get()).isEqualTo(USER);
    }
}
