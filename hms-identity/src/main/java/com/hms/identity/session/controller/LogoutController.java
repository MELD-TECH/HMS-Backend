package com.hms.identity.session.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.identity.session.dto.LogoutRequest;
import com.hms.identity.session.service.RefreshTokenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class LogoutController {

    private final RefreshTokenService service;

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(

            @RequestBody
            LogoutRequest request) {

        service.revoke(
                request.refreshToken());

        return ResponseEntity.noContent()
                .build();
    }

}
