package com.hms.api.identity.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.hms.identity.entity.User;
import com.hms.identity.enums.UserStatus;
import com.hms.identity.repository.UserRepository;

@DataJpaTest
@ActiveProfiles("test")
@Testcontainers
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EnableJpaRepositories(
        basePackages = "com.hms.identity.repository"
)
@EntityScan(
        basePackages = "com.hms.identity.entity"
)
class UserRepositoryTest {

	@Container
	static PostgreSQLContainer<?> postgres =
	        new PostgreSQLContainer<>("postgres:17-alpine");
	
    @Autowired
    private UserRepository repository;

    @DynamicPropertySource
    static void configure(DynamicPropertyRegistry registry) {
        registry.add(
                "spring.datasource.url",
                postgres::getJdbcUrl
        );
        registry.add(
                "spring.datasource.username",
                postgres::getUsername
        );
        registry.add(
                "spring.datasource.password",
                postgres::getPassword
        );
    }
    
    @Test
    void shouldFindUserByUsername() {

        User user = User.builder()
                .username("admin")
                .email("admin@hms.com")
                .passwordHash("password")
                .firstName("System")
                .lastName("Admin")
                .status(UserStatus.ACTIVE)
                .build();

        repository.save(user);

        Optional<User> result =
                repository.findByUsernameWithRolesAndPermissions(
                        "admin");

        assertThat(result).isPresent();
    }
}