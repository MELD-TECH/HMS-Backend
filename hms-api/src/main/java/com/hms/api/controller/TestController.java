package com.hms.api.controller;

import org.springframework.security.core.Authentication;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/test")
public class TestController {

    @GetMapping("/public")
    public String publicApi() {

        return "Public Endpoint";
    }

    @GetMapping("/private")
    public String privateApi(
            Authentication authentication) {

        return "Authenticated User: "
                + authentication.getName();
    }
}