package com.hms.api.identity.repository;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.hms.api.test.PostgreSQLContainerConfig;
import com.hms.identity.entity.Permission;
import com.hms.identity.repository.PermissionRepository;

@ActiveProfiles("test")
@DataJpaTest
@Testcontainers
@AutoConfigureTestDatabase(
        replace =
        AutoConfigureTestDatabase.Replace.NONE
)
@EnableJpaRepositories(
        basePackages = "com.hms.identity.repository"
)
@EntityScan(
        basePackages = "com.hms.identity.entity"
)
class PermissionRepositoryTest
        extends PostgreSQLContainerConfig {

	@Container
	static PostgreSQLContainer<?> postgres =
	        new PostgreSQLContainer<>("postgres:17-alpine");
	
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
    
    @Autowired
    PermissionRepository repository;

    @Test
    void shouldSavePermission() {

        Permission permission =
                Permission.builder()
                        .code("USER_VIEW")
                        .description("View User")
                        .build();

        Permission saved =
                repository.save(permission);

        assertThat(saved.getId())
                .isNotNull();
    }
}
