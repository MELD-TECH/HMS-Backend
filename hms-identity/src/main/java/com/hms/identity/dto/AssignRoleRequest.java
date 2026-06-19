package com.hms.identity.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AssignRoleRequest(
		@NotNull(message = "Role ID is required")
        UUID roleId
) {
}