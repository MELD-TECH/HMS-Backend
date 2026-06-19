package com.hms.identity.dto;

import java.util.UUID;

public record RoleResponse(

        UUID id,

        String name,

        String description
) {
}