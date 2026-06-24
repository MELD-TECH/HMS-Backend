package com.hms.identity.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdatePermissionRequest(

        @NotBlank
        String description

) {
}