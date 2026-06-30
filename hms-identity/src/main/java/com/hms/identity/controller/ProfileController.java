package com.hms.identity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


import com.hms.identity.dto.ProfileResponse;
import com.hms.identity.dto.UpdateProfileRequest;
import com.hms.identity.password.dto.ChangePasswordRequest;
import com.hms.identity.service.ProfileService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse>
    me(Authentication authentication) {

        return ResponseEntity.ok(
                service.me(
                        authentication.getName()
                )
        );
    }
    
    @PutMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProfileResponse>
    updateProfile(

            Authentication authentication,

            @Valid
            @RequestBody
            UpdateProfileRequest request) {

        return ResponseEntity.ok(
                service.updateProfile(
                        authentication.getName(),
                        request
                )
        );
    }
    
    @PutMapping("/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void>
    changePassword(

            Authentication authentication,

            @Valid
            @RequestBody
            ChangePasswordRequest request) {

        service.changePassword(
                authentication.getName(),
                request
        );

        return ResponseEntity.noContent()
                .build();
    }
}