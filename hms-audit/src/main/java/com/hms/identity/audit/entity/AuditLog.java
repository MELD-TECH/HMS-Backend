package com.hms.identity.audit.entity;

import com.hms.common.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
    name = "audit_logs",
    schema = "identity_schema"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AuditLog extends BaseEntity {

    private String username;

    private String action;

    private String module;

    private String entityName;

    private String entityId;

    @Column(columnDefinition = "TEXT")
    private String beforeJson;

    @Column(columnDefinition = "TEXT")
    private String afterJson;

    @Column(columnDefinition = "TEXT")
    private String details;

    private String ipAddress;

    private String userAgent;
}