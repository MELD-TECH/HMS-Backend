package com.hms.api.identity.security;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.hms.api.HmsApplication;
import com.hms.security.service.JwtService;

@SpringBootTest (classes = HmsApplication.class)
class JwtServiceTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void shouldGenerateValidToken() {

        String token =
                jwtService.generateToken(
                        "admin"
                );

        assertThat(
                jwtService.extractUsername(token)
        ).isEqualTo("admin");
    }
}