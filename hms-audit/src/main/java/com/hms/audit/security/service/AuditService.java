package com.hms.audit.security.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hms.audit.security.dto.AuditLogResponse;
import com.hms.audit.security.dto.AuditRequest;
import com.hms.audit.security.entity.AuditLog;
import com.hms.audit.security.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository repository;

    	@Transactional
    	public void log(AuditRequest request) {

    	    AuditLog auditLog =
    	            AuditLog.builder()
    	                    .username(request.username())
    	                    .action(request.action())
    	                    .module(request.module())
    	                    .entityName(request.entity())
    	                    .entityId(request.entityId())
    	                    .beforeJson(request.beforeJson())
    	                    .afterJson(request.afterJson())
    	                    .details(request.details())
    	                    .ipAddress(request.ipAddress())
    	                    .userAgent(request.userAgent())
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
                                        audit.getModule(),
                                        audit.getEntityName(),
                                        audit.getEntityId(),
                                        audit.getBeforeJson(),
                                        audit.getAfterJson(),
                                        audit.getDetails(),
                                        audit.getIpAddress(),
                                        audit.getUserAgent(),
                                        audit.getCreatedAt()                                                                               
                                )
                );
    }
}