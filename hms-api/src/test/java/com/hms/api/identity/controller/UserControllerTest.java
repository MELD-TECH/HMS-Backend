package com.hms.api.identity.controller;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.hms.identity.controller.UserController;
import com.hms.identity.dto.UserResponse;
import com.hms.identity.service.DatabaseUserDetailsService;
import com.hms.identity.service.UserService;
import com.hms.security.filter.JwtAuthenticationFilter;
import com.hms.security.service.JwtService;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@WebMvcTest(
        controllers = UserController.class
)
@AutoConfigureMockMvc(
        addFilters = false
)
class UserControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private JwtAuthenticationFilter jwtAuthenticationFilter;

    @MockitoBean
    private JwtService jwtService;

    @MockitoBean
    private DatabaseUserDetailsService userDetailsService;

    @Test
    @WithMockUser(
            authorities = "USER_VIEW"
    )
    void shouldGetUser()
            throws Exception {

        UUID id = UUID.randomUUID();

        when(userService.getUser(id))
                .thenReturn(
                        new UserResponse(
                                id,
                                "doctor1",
                                "doctor1@hms.com",
                                "John",
                                "Doe",
                                "ACTIVE"
                        )
                );

        mockMvc.perform(
                get("/api/v1/users/{id}", id)
        )
        .andExpect(status().isOk());
        
        MvcResult result =
                mockMvc.perform(
                        get("/api/v1/users/{id}", id)
                )
                .andDo(print())
                .andReturn();

        System.out.println(
                result.getResponse()
                      .getContentAsString()
        );
    }
}