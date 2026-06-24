package com.hms.api.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hms.identity.dto.RoleResponse;
import com.hms.identity.entity.Role;
import com.hms.identity.repository.RoleRepository;
import com.hms.identity.service.RoleService;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository repository;

    @InjectMocks
    private RoleService service;

    @Test
    void shouldReturnRoles() {

        Role role =
                Role.builder()
                        .name("DOCTOR")
                        .description("Medical Doctor")
                        .build();

        when(repository.findAll())
                .thenReturn(
                        List.of(role)
                );

        List<RoleResponse> result =
                service.getRoles();

        assertThat(result)
                .hasSize(1);
    }
}
