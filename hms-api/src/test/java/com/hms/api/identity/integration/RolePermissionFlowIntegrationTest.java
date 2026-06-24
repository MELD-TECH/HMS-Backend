package com.hms.api.identity.integration;

import static org.junit.Assert.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.hms.api.test.BaseIntegrationTest;
import com.hms.identity.entity.Permission;
import com.hms.identity.entity.Role;
import com.hms.identity.repository.PermissionRepository;
import com.hms.identity.repository.RoleRepository;

@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/V9999__test_admin_user.sql"
    }
)
class RolePermissionFlowIntegrationTest
        extends BaseIntegrationTest {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Test
    void shouldAssignPermissionToRole()
            throws Exception {

        String token =
                obtainAdminToken();

        Role role =
                roleRepository.save(
                        Role.builder()
                                .name(
                                    "TEST_ROLE_" +
                                    UUID.randomUUID()
                                )
                                .description(
                                    "Integration Test Role"
                                )
                                .build()
                );

        Permission permission =
                permissionRepository
                        .findByCode("ROLE_VIEW")
                        .orElseThrow();

        mockMvc.perform(
                post(
                    "/api/v1/roles/"
                    + role.getId()
                    + "/permissions"
                )
                .header(
                        "Authorization",
                        "Bearer " + token
                )
                .contentType(
                        MediaType.APPLICATION_JSON
                )
                .content("""
                    {
                      "permissionId":"%s"
                    }
                """.formatted(
                        permission.getId()
                ))
        )
        .andExpect(status().isOk());

        Role updatedRole =
                roleRepository
                        .findByIdWithPermissions(role.getId())
                        .orElseThrow();

        assertTrue(
                updatedRole
                        .getPermissions()
                        .stream()
                        .anyMatch(
                                p ->
                                p.getCode()
                                 .equals("ROLE_VIEW")
                        )
        );
    }
}