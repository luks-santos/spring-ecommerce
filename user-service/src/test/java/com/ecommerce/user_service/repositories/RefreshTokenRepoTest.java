package com.ecommerce.user_service.repositories;

import com.ecommerce.user_service.entities.RefreshToken;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static com.ecommerce.user_service.common.AuthConstants.REFRESH_TOKEN_ENTITY;
import static com.ecommerce.user_service.common.UserConstants.USER;
import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest(properties = "spring.main.web-application-type=servlet")
@ImportAutoConfiguration(classes = SecurityAutoConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles(profiles = "test")
public class RefreshTokenRepoTest {

    @Autowired
    private RefreshTokenRepo refreshTokenRepo;

    @Autowired
    private TestEntityManager testEntityManager;

    @Test
    public void findByRefreshToken_WithValidData_ReturnsRefreshToken() {
        testEntityManager.persistFlushFind(USER);
        RefreshToken refreshToken = testEntityManager.persistFlushFind(REFRESH_TOKEN_ENTITY);

        Optional<RefreshToken> sut = refreshTokenRepo.findByRefreshToken(refreshToken.getRefreshToken());

        assertThat(sut).isPresent();
        assertThat(sut.get()).isNotNull();
        assertThat(sut.get().getRefreshToken()).isEqualTo(REFRESH_TOKEN_ENTITY.getRefreshToken());
        assertThat(sut.get()).isEqualTo(REFRESH_TOKEN_ENTITY);
    }
}
