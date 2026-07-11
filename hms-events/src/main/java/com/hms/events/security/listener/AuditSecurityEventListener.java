package com.hms.events.security.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import com.hms.audit.security.dto.AuditRequest;
import com.hms.audit.security.enums.AuditModule;
import com.hms.audit.security.service.AuditService;
import com.hms.events.security.SecurityEvent;

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