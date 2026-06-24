package com.hms.api.identity.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.hms.identity.service.UserService;


@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserSecurityTest {

	@MockitoBean
	private UserService userService;
	
    @Autowired
    MockMvc mockMvc;

    @Test
    void anonymousShouldFail()
            throws Exception {

        mockMvc.perform(
                get("/api/v1/users")
        )
        .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser(
            authorities = "USER_VIEW"
    )
    void authorizedShouldPass()
            throws Exception {

        mockMvc.perform(
                get("/api/v1/users")
        )
        .andExpect(status().isOk());
    }
}
