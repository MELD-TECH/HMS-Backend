package com.hms.audit.security.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;

import com.hms.audit.security.annotation.Auditable;
import com.hms.audit.security.dto.AuditRequest;
import com.hms.audit.security.service.AuditService;
import com.hms.audit.security.util.AuditContext;
import com.hms.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditService auditService;

    @Around("@annotation(auditable)")
    public Object audit(

            ProceedingJoinPoint joinPoint,

            Auditable auditable)

            throws Throwable {

        Object result = joinPoint.proceed();

        auditService.log(

                AuditRequest.builder()

                        .username(
                                SecurityUtils.getCurrentUsername())

                        .action(
                                auditable.action())

                        .module(
                                auditable.module())

                        .entity(
                                auditable.entity())

                        .entityId(null)

                        .beforeJson(null)

                        .afterJson(null)

                        .details(
                                joinPoint
                                .getSignature()
                                .toShortString())

                        .ipAddress(
                                AuditContext.getIpAddress())

                        .userAgent(
                                AuditContext.getUserAgent())

                        .build());

        return result;
    }
}