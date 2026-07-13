package com.hms.api.controller;


import com.hms.identity.authentication.dto.CompleteAuthenticationRequest;
import com.hms.identity.authentication.dto.ResendAuthenticationOtpRequest;
import com.hms.identity.dto.LoginRequest;
import com.hms.identity.dto.LoginResponse;
import com.hms.identity.service.AuthenticationService;
import com.hms.notification.dto.OtpResponse;

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
    
    @PostMapping("/mfa/complete")
    public ResponseEntity<LoginResponse> completeAuthentication(

            @Valid

            @RequestBody

            CompleteAuthenticationRequest request,

            HttpServletRequest servletRequest) {

        return ResponseEntity.ok(

                authService.completeAuthentication(

                        request,

                        servletRequest));
    }
    
    @PostMapping("/mfa/resend")
    public ResponseEntity<OtpResponse> resendOtp(

            @Valid
            @RequestBody
            ResendAuthenticationOtpRequest request) {

        return ResponseEntity.ok(

                authService.resendOtp(request));
    }
}