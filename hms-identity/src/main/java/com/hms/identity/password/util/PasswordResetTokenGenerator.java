package com.hms.identity.password.util;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class PasswordResetTokenGenerator {

    public String generate() {

        return UUID.randomUUID().toString();

    }

}