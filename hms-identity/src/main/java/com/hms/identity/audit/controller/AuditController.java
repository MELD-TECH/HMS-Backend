package com.hms.identity.audit.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.hms.identity.audit.dto.AuditLogResponse;
import com.hms.identity.audit.service.AuditService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService service;

    @GetMapping
    @PreAuthorize(
            "hasAuthority('AUDIT_VIEW')"
    )
    public ResponseEntity<Page<AuditLogResponse>>
    search(
            Pageable pageable) {

        return ResponseEntity.ok(
                service.search(pageable)
        );
    }
}