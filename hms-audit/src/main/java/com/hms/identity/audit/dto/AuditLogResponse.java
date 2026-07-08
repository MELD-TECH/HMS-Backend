package com.hms.identity.audit.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public record AuditLogResponse(

        UUID id,

        String username,

        String action,

        String module,

        String entityName,

        String entityId,

        String beforeJson,

        String afterJson,

        String details,

        String ipAddress,

        String userAgent,

        LocalDateTime createdAt
) {}