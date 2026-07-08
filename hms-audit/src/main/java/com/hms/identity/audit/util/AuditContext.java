package com.hms.identity.audit.util;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;

public final class AuditContext {

    private AuditContext() {}

    public static String getIpAddress() {

        ServletRequestAttributes attr =
            (ServletRequestAttributes)
            RequestContextHolder.getRequestAttributes();

        if (attr == null) {
            return null;
        }

        HttpServletRequest request =
                attr.getRequest();

        return request.getRemoteAddr();
    }

    public static String getUserAgent() {

        ServletRequestAttributes attr =
            (ServletRequestAttributes)
            RequestContextHolder.getRequestAttributes();

        if (attr == null) {
            return null;
        }

        return attr
                .getRequest()
                .getHeader("User-Agent");
    }
}