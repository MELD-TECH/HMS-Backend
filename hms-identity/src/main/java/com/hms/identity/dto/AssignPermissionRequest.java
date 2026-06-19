package com.hms.identity.dto;

import java.util.UUID;

import jakarta.validation.constraints.NotNull;

public record AssignPermissionRequest(
		@NotNull(message = "Permission ID is required")
		UUID permissionId) {

}
