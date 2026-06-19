package com.hms.identity.dto;

import java.util.Set;
import java.util.UUID;

public record ProfileResponse(

        UUID id,

        String username,

        String email,

        String firstName,

        String lastName,

        Set<String> roles,

        Set<String> permissions
) {
}