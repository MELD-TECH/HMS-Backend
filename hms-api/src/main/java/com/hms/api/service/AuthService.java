package com.hms.api.service;

import com.hms.api.dto.*;

import com.hms.security.service.JwtService;

import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.*;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager
            authenticationManager;

    private final JwtService jwtService;
    
	public LoginResponse login(
            LoginRequest request) {

        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.username(),
                        request.password()
                )
        );

        String token =
                jwtService.generateToken(
                        request.username()
                );

        return new LoginResponse(
                token,
                "Bearer",
                jwtService.extractExpiration(token),
                jwtService.getRemainingSeconds(token)
        );
    }

}


