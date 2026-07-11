package com.hms.audit.security.repository;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.hms.audit.security.entity.AuditLog;

@Repository
public interface AuditLogRepository
        extends JpaRepository<AuditLog, UUID> {

    Page<AuditLog> findByUsername(
            String username,
            Pageable pageable
    );

    Page<AuditLog> findByAction(
            String action,
            Pageable pageable
    );
    
    Page<AuditLog> findByModule(
            String module,
            Pageable pageable);
    
    Page<AuditLog> findByEntityName(
            String entityName,
            Pageable pageable);
   
    Page<AuditLog> findByCreatedAtBetween(
            LocalDateTime from,
            LocalDateTime to,
            Pageable pageable);
   
    Optional<AuditLog> findFirstByAction(String action);
   
    Optional<AuditLog> findTopByActionOrderByCreatedAtDesc(String action);
    
    Optional<AuditLog> findTopByActionAndUsernameOrderByCreatedAtDesc(
            String action,
            String username);

    Optional<AuditLog> findTopByActionAndEntityIdOrderByCreatedAtDesc(
            String action,
            String entityId);
}
