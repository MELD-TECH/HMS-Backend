package com.hms.identity.authentication.generator;

import java.util.UUID;

import org.springframework.stereotype.Component;

@Component
public class ChallengeTokenGenerator {

    public String generate() {

        return UUID.randomUUID().toString();

    }

}