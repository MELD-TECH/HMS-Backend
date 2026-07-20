package com.hms.api.identity.repository;

import static org.assertj.core.api.Assertions.*;

import java.util.Arrays;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.hms.api.config.AuditConfig;
import com.hms.api.config.JpaAuditConfig;
import com.hms.api.config.JpaConfig;
import com.hms.api.test.PostgreSQLContainerConfig;
import com.hms.identity.entity.User;
import com.hms.identity.enums.UserStatus;
import com.hms.identity.repository.UserRepository;

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
@Import({
	JpaAuditConfig.class,
    AuditConfig.class
})
@Sql(
	    scripts = {
	        "/db/testdata/001_cleanup.sql"
	    },
	    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	)
class UserRepositoryTest extends PostgreSQLContainerConfig  {

	/*
	 * @Container static PostgreSQLContainer<?> postgres = new
	 * PostgreSQLContainer<>("postgres:17-alpine");
	 * 
	 * @Autowired private UserRepository repository;
	 * 
	 * @DynamicPropertySource static void configure(DynamicPropertyRegistry
	 * registry) { registry.add( "spring.datasource.url", postgres::getJdbcUrl );
	 * registry.add( "spring.datasource.username", postgres::getUsername );
	 * registry.add( "spring.datasource.password", postgres::getPassword ); }
	 */
	@Autowired private UserRepository repository;
	
	@Autowired
	ApplicationContext context;

	@Test
	void printRepositories() {

	    Arrays.stream(context.getBeanDefinitionNames())
	            .filter(name -> name.toLowerCase().contains("repository"))
	            .sorted()
	            .forEach(System.out::println);
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

        User saved = repository.saveAndFlush(user);

        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();

        Optional<User> result =
                repository.findByUsernameWithRolesAndPermissions(
                        "admin");

        assertThat(result).isPresent();
        
        assertThat(result.get().getCreatedAt()).isNotNull();
        assertThat(result.get().getUpdatedAt()).isNotNull();
    }
}