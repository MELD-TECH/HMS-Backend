package com.hms.identity.audit.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponse(

        UUID id,

        String username,

        String action,

        String entityName,

        String entityId,

        String details,

        LocalDateTime createdAt
) {
}
