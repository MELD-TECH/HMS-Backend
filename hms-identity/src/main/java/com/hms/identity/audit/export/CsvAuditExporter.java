package com.hms.identity.audit.export;

import java.io.ByteArrayOutputStream;
import java.io.PrintWriter;
import java.util.List;

import org.springframework.stereotype.Component;

import com.hms.identity.audit.entity.AuditLog;

@Component
public class CsvAuditExporter {

    public byte[] export(

            List<AuditLog> logs) {

        try (

                ByteArrayOutputStream out =
                        new ByteArrayOutputStream();

                PrintWriter writer =
                        new PrintWriter(out)

        ) {

            writer.println(
                    "Timestamp,Username,Action,Module,Entity,EntityId,IP");

            for (AuditLog log : logs) {

                writer.printf(

                        "%s,%s,%s,%s,%s,%s,%s%n",

                        log.getCreatedAt(),

                        log.getUsername(),

                        log.getAction(),

                        log.getModule(),

                        log.getEntityName(),

                        log.getEntityId(),

                        log.getIpAddress()

                );
            }

            writer.flush();

            return out.toByteArray();

        }

        catch (Exception ex) {

            throw new RuntimeException(ex);

        }

    }

}
