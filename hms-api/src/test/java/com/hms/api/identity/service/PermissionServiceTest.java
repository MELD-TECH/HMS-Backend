package com.hms.api.identity.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import java.util.List;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.hms.identity.dto.PermissionResponse;
import com.hms.identity.entity.Permission;
import com.hms.identity.repository.PermissionRepository;
import com.hms.identity.service.PermissionService;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository repository;

    @InjectMocks
    private PermissionService service;

    @Test
    void shouldReturnPermissions() {

        Permission permission =
                Permission.builder()
                        .code("USER_VIEW")
                        .description("View User")
                        .build();

        when(repository.findAll())
                .thenReturn(
                        List.of(permission)
                );

        List<PermissionResponse> result =
                service.getPermissions();

        assertThat(result)
                .hasSize(1);
    }
}