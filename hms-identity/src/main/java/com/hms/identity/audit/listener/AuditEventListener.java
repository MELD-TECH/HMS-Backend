package com.hms.identity.audit.listener;

import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import com.hms.identity.audit.entity.AuditLog;
import com.hms.identity.audit.event.AuditEvent;
import com.hms.identity.audit.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditEventListener {

    private final AuditLogRepository repository;

    @Transactional
    @EventListener
    public void handle(
            AuditEvent event) {

        AuditLog log =
                AuditLog.builder()
                        .username(
                                event.getUsername()
                        )
                        .action(
                                event.getAction()
                        )
                        .entityName(
                                event.getEntityName()
                        )
                        .entityId(
                                event.getEntityId()
                        )
                        .details(
                                event.getDetails()
                        )
                        .build();

        repository.save(log);
    }
}
