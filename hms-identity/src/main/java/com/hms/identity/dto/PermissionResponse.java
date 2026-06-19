package com.hms.identity.dto;

import java.util.UUID;

public record PermissionResponse(

        UUID id,

        String code,

        String description
) {
}