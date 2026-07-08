package com.hms.identity.audit.export;

import java.io.ByteArrayOutputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Component;

import com.hms.identity.audit.entity.AuditLog;

@Component
public class ExcelAuditExporter {

    public byte[] export(

            List<AuditLog> logs) {

        try (

                Workbook workbook =
                        new XSSFWorkbook();

                ByteArrayOutputStream out =
                        new ByteArrayOutputStream()

        ) {

            Sheet sheet =
                    workbook.createSheet(
                            "Audit Logs");

            Row header =
                    sheet.createRow(0);

            header.createCell(0)
                    .setCellValue("Timestamp");

            header.createCell(1)
                    .setCellValue("Username");

            header.createCell(2)
                    .setCellValue("Action");

            header.createCell(3)
                    .setCellValue("Module");

            header.createCell(4)
                    .setCellValue("Entity");

            header.createCell(5)
                    .setCellValue("Entity Id");

            header.createCell(6)
                    .setCellValue("IP");

            int rowIndex = 1;

            for (AuditLog log : logs) {

                Row row =
                        sheet.createRow(
                                rowIndex++);

                row.createCell(0)
                        .setCellValue(
                                String.valueOf(
                                        log.getCreatedAt()));

                row.createCell(1)
                        .setCellValue(
                                log.getUsername());

                row.createCell(2)
                        .setCellValue(
                                log.getAction());

                row.createCell(3)
                        .setCellValue(
                                log.getModule());

                row.createCell(4)
                        .setCellValue(
                                log.getEntityName());

                row.createCell(5)
                        .setCellValue(
                                log.getEntityId());

                row.createCell(6)
                        .setCellValue(
                                log.getIpAddress());

            }

            workbook.write(out);

            return out.toByteArray();

        }

        catch (Exception ex) {

            throw new RuntimeException(ex);

        }

    }

}
