package com.hms.identity.dto;

import jakarta.validation.constraints.NotBlank;

public record UpdateRoleRequest(

        @NotBlank
        String description
) {
}