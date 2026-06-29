package com.hms.identity.session.controller;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.identity.dto.LoginResponse;
import com.hms.identity.service.AuthenticationService;
import com.hms.identity.session.dto.RefreshTokenRequest;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class RefreshTokenController {

    private final AuthenticationService authenticationService;

    @PostMapping("/refresh")
    public LoginResponse refresh(

            @RequestBody
            RefreshTokenRequest request,

            HttpServletRequest servletRequest){

        return authenticationService.refresh(
                request,
                servletRequest);

    }

}
