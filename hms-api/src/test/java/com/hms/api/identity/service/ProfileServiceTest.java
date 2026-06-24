package com.hms.api.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hms.identity.dto.ProfileResponse;
import com.hms.identity.entity.Permission;
import com.hms.identity.entity.Role;
import com.hms.identity.entity.User;
import com.hms.identity.enums.UserStatus;
import com.hms.identity.repository.UserRepository;
import com.hms.identity.service.ProfileService;

@ExtendWith(MockitoExtension.class)
class ProfileServiceTest {

    @Mock
    private UserRepository repository;

    @InjectMocks
    private ProfileService service;

    @Test
    void shouldReturnProfile() {

        Permission permission =
                Permission.builder()
                        .code("USER_VIEW")
                        .description("View User")
                        .build();

        Role role =
                Role.builder()
                        .name("DOCTOR")
                        .description("Medical Doctor")
                        .permissions(
                                Set.of(permission)
                        )
                        .build();

        User user =
                User.builder()
//                        .id(UUID.randomUUID())
                        .username("doctor1")
                        .email("doctor1@hms.com")
                        .firstName("John")
                        .lastName("Doe")
                        .status(UserStatus.ACTIVE)
                        .roles(Set.of(role))
                        .build();

        when(
                repository.findByUsernameWithRolesAndPermissions(
                        "doctor1"
                )
        ).thenReturn(
                Optional.of(user)
        );

        ProfileResponse response =
                service.me("doctor1");

        assertThat(response.username())
                .isEqualTo("doctor1");

        assertThat(response.roles())
                .contains("DOCTOR");

        assertThat(response.permissions())
                .contains("USER_VIEW");
    }
}