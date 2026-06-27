package com.hms.identity.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;

import org.springframework.stereotype.Component;

import com.hms.identity.audit.annotation.Auditable;
import com.hms.identity.audit.dto.AuditRequest;
import com.hms.identity.audit.service.AuditService;
import com.hms.identity.audit.util.AuditContext;
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