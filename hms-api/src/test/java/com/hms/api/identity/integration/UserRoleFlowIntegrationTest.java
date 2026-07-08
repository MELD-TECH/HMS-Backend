package com.hms.api.identity.integration;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.fasterxml.jackson.databind.JsonNode;
import com.hms.api.test.BaseIntegrationTest;
import com.hms.identity.entity.Role;
import com.hms.identity.entity.User;
import com.hms.identity.repository.RoleRepository;
import com.hms.identity.repository.UserRepository;
import com.hms.security.service.JwtService;

@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/V9999__test_admin_user.sql"
    }
)
class UserRoleFlowIntegrationTest
        extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private JwtService jwtService;

    @Test
    void shouldAssignRoleAndLogin()
            throws Exception {

        String adminToken =
                obtainAdminToken();

        //----------------------------------
        // Create User
        //----------------------------------

        mockMvc.perform(
                post("/api/v1/users")
                        .header(
                                "Authorization",
                                "Bearer " + adminToken
                        )
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content("""
                        {
                            "username":"doctor100",
                            "email":"doctor100@hms.com",
                            "password":"password",
                            "firstName":"John",
                            "lastName":"Doe"
                        }
                        """)
        )
        .andExpect(status().isOk());

        //----------------------------------
        // Assign Role
        //----------------------------------

        User user =
                userRepository
                        .findByUsername("doctor100")
                        .orElseThrow();

        Role role =
                roleRepository
                        .findByName("DOCTOR")
                        .orElseThrow();

        mockMvc.perform(
                post(
                    "/api/v1/users/"
                    + user.getId()
                    + "/roles/"
                    + role.getId()
                )
                .header(
                        "Authorization",
                        "Bearer " + adminToken
                )
                .contentType(
                        MediaType.APPLICATION_JSON
                )
                .content("""
                {
                    "roleId":"%s"
                }
                """.formatted(
                        role.getId()
                ))
        )
        .andExpect(status().isOk());

        //----------------------------------
        // Login
        //----------------------------------

        String loginResponse =
                mockMvc.perform(
                        post("/auth/login")
                                .contentType(
                                        MediaType.APPLICATION_JSON
                                )
                                .content("""
                                {
                                    "username":"doctor100",
                                    "password":"password"
                                }
                                """)
                )
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        //----------------------------------
        // Verify JWT Returned
        //----------------------------------

        JsonNode json =
                objectMapper.readTree(
                        loginResponse
                );

        assertTrue(
                json.has("accessToken")
        );

        String token =
                json.get("accessToken")
                        .asText();

        assertNotNull(token);

        //----------------------------------
        // Verify JWT Subject
        //----------------------------------

        String username =
                jwtService.extractUsername(
                        token
                );

        assertEquals(
                "doctor100",
                username
        );

        //----------------------------------
        // Verify Authorities
        //----------------------------------

        User loadedUser =
                userRepository
                        .findByUsernameWithRolesAndPermissions(
                                "doctor100"
                        )
                        .orElseThrow();

        assertTrue(
                loadedUser.getRoles()
                        .stream()
                        .anyMatch(
                                r ->
                                r.getName()
                                 .equals("DOCTOR")
                        )
        );
    }
}