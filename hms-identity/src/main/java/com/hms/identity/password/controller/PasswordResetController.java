package com.hms.identity.password.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.hms.identity.password.dto.ForgotPasswordRequest;
import com.hms.identity.password.dto.ResetPasswordRequest;
import com.hms.identity.password.service.PasswordResetService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
@Validated
public class PasswordResetController {

    private final PasswordResetService service;

    @PostMapping("/forgot-password")
    public ResponseEntity<Void> forgotPassword(

            @Valid
            @RequestBody
            ForgotPasswordRequest request) {

        service.forgotPassword(request);

        return ResponseEntity.noContent().build();
    }
    
    @PostMapping("/reset-password")
    public ResponseEntity<Void> resetPassword(

            @Valid
            @RequestBody
            ResetPasswordRequest request){

        service.resetPassword(request);

        return ResponseEntity.noContent().build();

    }
}