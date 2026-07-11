package com.hms.audit.security.controller;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.hms.audit.security.dto.AuditLogResponse;
import com.hms.audit.security.service.AuditExportService;
import com.hms.audit.security.service.AuditService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/audit")
@RequiredArgsConstructor
public class AuditController {

    private final AuditService service;
    private final AuditExportService exportService;

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
    
    @GetMapping("/export")
    @PreAuthorize("hasAuthority('AUDIT_EXPORT')")
    public ResponseEntity<Resource> export(

            @RequestParam String format) {

        byte[] file;

        String filename;

        MediaType mediaType;

        if ("csv".equalsIgnoreCase(format)) {

            file =
                    exportService.exportCsv();

            filename =
                    "audit.csv";

            mediaType =
                    MediaType.TEXT_PLAIN;

        }

        else if ("xlsx".equalsIgnoreCase(format)) {

            file =
                    exportService.exportExcel();

            filename =
                    "audit.xlsx";

            mediaType =
                    MediaType.parseMediaType(

                            "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");

        }

        else {

            throw new IllegalArgumentException(

                    "Unsupported format");

        }

        return ResponseEntity.ok()

                .header(

                        HttpHeaders.CONTENT_DISPOSITION,

                        "attachment; filename=" + filename)

                .contentType(mediaType)

                .body(

                        new ByteArrayResource(file));

    }
}