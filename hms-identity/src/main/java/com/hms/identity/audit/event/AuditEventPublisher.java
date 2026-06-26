package com.hms.identity.audit.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class AuditEventPublisher {

    private final ApplicationEventPublisher publisher;

    public void publish(
            String username,
            String action,
            String entityName,
            String entityId,
            String details) {

        publisher.publishEvent(
                new AuditEvent(
                        username,
                        action,
                        entityName,
                        entityId,
                        details
                )
        );
    }
}