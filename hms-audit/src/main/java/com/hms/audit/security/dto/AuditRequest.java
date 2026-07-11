package com.hms.audit.security.dto;

import lombok.Builder;

@Builder
public record AuditRequest(

        String username,

        String action,

        String module,

        String entity,

        String entityId,

        String beforeJson,

        String afterJson,

        String details,

        String ipAddress,

        String userAgent

) {}