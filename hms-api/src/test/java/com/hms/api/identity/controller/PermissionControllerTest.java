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

import com.hms.identity.controller.PermissionController;
import com.hms.identity.dto.PermissionResponse;
import com.hms.identity.service.DatabaseUserDetailsService;
import com.hms.identity.service.PermissionService;
import com.hms.security.filter.JwtAuthenticationFilter;
import com.hms.security.service.JwtService;

@WebMvcTest(
        controllers = PermissionController.class
)
@AutoConfigureMockMvc(
        addFilters = false
)
@ActiveProfiles("test")
class PermissionControllerTest {

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private DatabaseUserDetailsService userDetailsService;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PermissionService permissionService;

    @Test
    @WithMockUser(
            authorities = "PERMISSION_VIEW"
    )
    void shouldGetPermissions()
            throws Exception {

        when(permissionService.getPermissions())
                .thenReturn(
                        List.of(
                                new PermissionResponse(
                                        UUID.randomUUID(),
                                        "USER_VIEW",
                                        "View User"
                                )
                        )
                );

        mockMvc.perform(
                get("/api/v1/permissions")
        )
        .andExpect(status().isOk());
    }
}
