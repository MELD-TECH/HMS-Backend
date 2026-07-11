package com.hms.audit.security.event;

import lombok.Getter;

@Getter
public class AuditEvent {

    private final String username;

    private final String action;

    private final String entityName;

    private final String entityId;

    private final String details;

    public AuditEvent(
            String username,
            String action,
            String entityName,
            String entityId,
            String details) {

        this.username = username;
        this.action = action;
        this.entityName = entityName;
        this.entityId = entityId;
        this.details = details;
    }
}
