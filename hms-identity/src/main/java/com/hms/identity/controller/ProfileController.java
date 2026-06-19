package com.hms.identity.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.identity.dto.ProfileResponse;
import com.hms.identity.service.ProfileService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService service;

    @GetMapping("/me")
    public ResponseEntity<ProfileResponse>
    me(Authentication authentication) {

        return ResponseEntity.ok(
                service.me(
                        authentication.getName()
                )
        );
    }
}