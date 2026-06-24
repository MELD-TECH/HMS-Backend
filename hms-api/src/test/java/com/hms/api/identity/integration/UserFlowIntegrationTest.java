package com.hms.api.identity.integration;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import com.hms.api.test.BaseIntegrationTest;
import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;



@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserFlowIntegrationTest extends BaseIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    UserRepository userRepository;
    
    @Test
    @WithMockUser(
            username = "admin",
            authorities = {
                    "USER_CREATE"
            }
    )
    void createUserFlow()
            throws Exception {

        String request = """
        {
            "username":"doctor5",
            "email":"doctor5@hms.com",
            "password":"password",
            "firstName":"John",
            "lastName":"Doe"
        }
        """;

        mockMvc.perform(
                post("/api/v1/users")
                        .contentType(
                                MediaType.APPLICATION_JSON
                        )
                        .content(request)
        )
        .andExpect(status().isOk());

        User user =
                userRepository
                        .findByUsernameWithRolesAndPermissions("doctor5")
                        .orElseThrow();

        assertEquals(
                "doctor5@hms.com",
                user.getEmail()
        );
    }
}