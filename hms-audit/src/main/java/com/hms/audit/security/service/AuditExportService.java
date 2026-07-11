package com.hms.audit.security.service;

import org.springframework.stereotype.Service;

import com.hms.audit.security.export.CsvAuditExporter;
import com.hms.audit.security.export.ExcelAuditExporter;
import com.hms.audit.security.repository.AuditLogRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuditExportService {

    private final AuditLogRepository repository;

    private final CsvAuditExporter csvExporter;

    private final ExcelAuditExporter excelExporter;

    public byte[] exportCsv() {

        return csvExporter.export(
                repository.findAll());
    }

    public byte[] exportExcel() {

        return excelExporter.export(
                repository.findAll());
    }

}
