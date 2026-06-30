package com.hms.identity.password.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.identity.password.dto.ChangePasswordRequest;
import com.hms.identity.password.service.PasswordService;
import com.hms.security.util.SecurityUtils;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/password")
@RequiredArgsConstructor
public class PasswordController {

    private final PasswordService passwordService;
    
    @PostMapping("/change")

    @PreAuthorize("isAuthenticated()")

    public ResponseEntity<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request){

    passwordService.changePassword(SecurityUtils.getCurrentUsername(), request);

    return ResponseEntity.noContent().build();

    }

}