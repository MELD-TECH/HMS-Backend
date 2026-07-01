package com.hms.identity.security.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.identity.audit.dto.AuditRequest;
import com.hms.identity.audit.enums.AuditAction;
import com.hms.identity.audit.enums.AuditModule;
import com.hms.identity.audit.service.AuditService;
import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;
import com.hms.identity.security.config.AccountSecurityProperties;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class LoginAttemptService {

    private final UserRepository repository;

    private final AccountSecurityProperties properties;
    
    private final AuditService auditService;

    public void loginFailed(User user) {

        int attempts =
                user.getFailedLoginAttempts() + 1;

        user.setFailedLoginAttempts(attempts);

        if (attempts >= properties.getMaxFailedAttempts()) {

            user.setAccountLocked(true);

            user.setLockedAt(
                    LocalDateTime.now());

            user.setLockExpiresAt(

                    LocalDateTime.now()

                            .plusMinutes(

                                    properties.getLockDurationMinutes()));
        }

        auditService.log(
                AuditRequest.builder()
                        .username(user.getUsername())
                        .action(
                                AuditAction.ACCOUNT_LOCKED.name())
                        .module(
                                AuditModule.IDENTITY.name())
                        .entity("USER")
                        .entityId(
                                user.getId().toString())
                        .details(
                                "Maximum failed login attempts reached")
                        .build());
        repository.save(user);
    }

    public void loginSucceeded(User user) {

        user.setFailedLoginAttempts(0);

        user.setAccountLocked(false);

        user.setLockedAt(null);

        user.setLockExpiresAt(null);

        repository.save(user);
    }
}