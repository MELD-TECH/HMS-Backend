package com.hms.api.identity.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.hms.identity.controller.RoleController;
import com.hms.identity.dto.RoleResponse;
import com.hms.identity.service.DatabaseUserDetailsService;
import com.hms.identity.service.RoleService;
import com.hms.security.filter.JwtAuthenticationFilter;
import com.hms.security.service.JwtService;

@WebMvcTest(
        controllers = RoleController.class
)
@AutoConfigureMockMvc(
        addFilters = false
)
@ActiveProfiles("test")
class RoleControllerTest {

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private DatabaseUserDetailsService userDetailsService;

    @Autowired
    MockMvc mockMvc;

    @MockitoBean
    RoleService roleService;

    @Test
    @WithMockUser(
            authorities = "ROLE_VIEW"
    )
    void shouldGetRoles()
            throws Exception {

        when(roleService.getRoles())
                .thenReturn(
                        List.of(
                                new RoleResponse(
                                        UUID.randomUUID(),
                                        "DOCTOR",
                                        "Medical Doctor"
                                )
                        )
                );

        mockMvc.perform(
                get("/api/v1/roles")
        )
        .andExpect(status().isOk());
    }
}
