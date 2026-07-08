package com.hms.identity.security.controller;

import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.identity.security.service.AccountLockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/security/users")
@RequiredArgsConstructor
public class AccountSecurityController {

    private final AccountLockService service;

    @PostMapping("/{userId}/unlock")
    public ResponseEntity<Void> unlock(

            @PathVariable UUID userId) {

        service.unlock(userId);

        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{userId}/lock")
    public ResponseEntity<Void> lock(

            @PathVariable UUID userId) {

        service.lock(userId);

        return ResponseEntity.noContent().build();
    }
}