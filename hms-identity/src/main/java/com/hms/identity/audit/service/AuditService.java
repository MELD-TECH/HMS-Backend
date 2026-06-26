package com.hms.identity.audit.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.identity.audit.dto.AuditLogResponse;
import com.hms.identity.audit.entity.AuditLog;
import com.hms.identity.audit.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repository;

    @Transactional
    public void log(
            String username,
            String action,
            String entityName,
            String entityId,
            String details,
            String ipAddress) {

        AuditLog auditLog =
                AuditLog.builder()
                        .username(username)
                        .action(action)
                        .entityName(entityName)
                        .entityId(entityId)
                        .details(details)
                        .ipAddress(ipAddress)
                        .build();

        repository.save(auditLog);
    }

    public Page<AuditLogResponse> search(
            Pageable pageable) {

        return repository.findAll(pageable)
                .map(
                        audit ->
                                new AuditLogResponse(
                                        audit.getId(),
                                        audit.getUsername(),
                                        audit.getAction(),
                                        audit.getEntityName(),
                                        audit.getEntityId(),
                                        audit.getDetails(),
                                        audit.getCreatedAt()
                                )
                );
    }
}