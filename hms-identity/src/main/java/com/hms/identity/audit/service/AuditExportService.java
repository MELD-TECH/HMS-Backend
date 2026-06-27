package com.hms.identity.audit.service;

import org.springframework.stereotype.Service;

import com.hms.identity.audit.export.CsvAuditExporter;
import com.hms.identity.audit.export.ExcelAuditExporter;
import com.hms.identity.audit.repository.AuditLogRepository;

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
