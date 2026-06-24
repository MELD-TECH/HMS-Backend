package com.hms.api.identity.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.hms.identity.controller.ProfileController;
import com.hms.identity.dto.ProfileResponse;
import com.hms.identity.service.DatabaseUserDetailsService;
import com.hms.identity.service.ProfileService;
import com.hms.security.config.SecurityConfig;
import com.hms.security.filter.JwtAuthenticationFilter;
import com.hms.security.service.JwtService;

@WebMvcTest(
        controllers = ProfileController.class
)
//@AutoConfigureMockMvc(
//        addFilters = false
//
@Import(SecurityConfig.class)
@ActiveProfiles("test")
class ProfileControllerTest {

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private DatabaseUserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProfileService profileService;

    @Test
    @WithMockUser(username = "doctor1")
    void shouldReturnProfile() throws Exception {

        UUID id = UUID.randomUUID();

        when(profileService.me("doctor1"))
                .thenReturn(
                        new ProfileResponse(
                                id,
                                "doctor1",
                                "doctor1@hms.com",
                                "John",
                                "Doe",
                                Set.of("DOCTOR"),
                                Set.of("USER_VIEW")
                        )
                );

        mockMvc.perform(
                get("/api/v1/profile/me")
        )
        .andDo(print())
        .andExpect(status().isOk());
    }
}