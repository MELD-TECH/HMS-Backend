package com.hms.identity.security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.identity.security.dto.UnlockAccountRequest;
import com.hms.identity.security.service.AccountLockService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/security")
@RequiredArgsConstructor
public class AccountSecurityController {

    private final AccountLockService service;

    @PostMapping("/unlock")

    public ResponseEntity<Void> unlock(

            @RequestBody
            UnlockAccountRequest request) {

        service.unlock(request.userId());

        return ResponseEntity.noContent().build();
    }

}