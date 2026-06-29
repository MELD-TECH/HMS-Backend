package com.hms.api.controller;


import com.hms.identity.dto.LoginRequest;
import com.hms.identity.dto.LoginResponse;
import com.hms.identity.service.AuthenticationService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthenticationService authService;

    public AuthController(
            AuthenticationService authService) {

        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse>
    login(
            @Valid
            @RequestBody
            LoginRequest request, HttpServletRequest servletRequest) {

        return ResponseEntity.ok(
                authService.login(request, servletRequest)
        );
    }
}