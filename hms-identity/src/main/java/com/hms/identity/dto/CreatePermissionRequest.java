package com.hms.identity.dto;

import jakarta.validation.constraints.NotBlank;

public record CreatePermissionRequest(

        @NotBlank
        String code,

        String description
) {
}