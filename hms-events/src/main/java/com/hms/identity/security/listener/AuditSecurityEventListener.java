package com.hms.identity.security.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.hms.identity.audit.dto.AuditRequest;
import com.hms.identity.audit.enums.AuditModule;
import com.hms.identity.audit.service.AuditService;
import com.hms.identity.security.event.SecurityEvent;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditSecurityEventListener {

    private final AuditService auditService;

    @EventListener
    public void handle(SecurityEvent event) {

        auditService.log(

                AuditRequest.builder()

                        .username(event.getUsername())

                        .action(event.action())

                        .module(AuditModule.IDENTITY.name())

                        .entity(event.getEntity())

                        .entityId(event.getEntityId())

                        .details(event.details())

                        .build());
    }
}