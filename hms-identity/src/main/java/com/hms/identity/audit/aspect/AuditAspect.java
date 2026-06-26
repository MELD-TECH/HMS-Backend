package com.hms.identity.audit.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

import com.hms.identity.audit.annotation.Auditable;
import com.hms.identity.audit.service.AuditService;
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

        Object result =
                joinPoint.proceed();

        auditService.log(
                SecurityUtils.getCurrentUsername(),
                auditable.action(),
                auditable.entity(),
                null,
                joinPoint.getSignature().getName(),
                null
        );

        return result;
    }
}