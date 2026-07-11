package com.hms.identity.security.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.audit.security.dto.AuditRequest;
import com.hms.audit.security.enums.AuditAction;
import com.hms.audit.security.enums.AuditModule;
import com.hms.audit.security.service.AuditService;
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

    @Transactional
    public void loginFailed(String username) {

    	User user =
    	        repository.findByUsername(username)
    	                .orElseThrow();;

        int attempts =
                user.getFailedLoginAttempts() + 1;

        user.setFailedLoginAttempts(attempts);

        if (attempts >= properties.getMaxFailedAttempts()) {

            user.setAccountLocked(true);

            user.setLockedAt(LocalDateTime.now());

            user.setLockExpiresAt(
                    LocalDateTime.now()
                            .plusMinutes(properties.getLockDurationMinutes()));

            auditService.log(

                    AuditRequest.builder()

                            .username(user.getUsername())

                            .action(AuditAction.ACCOUNT_LOCKED.name())

                            .module(AuditModule.IDENTITY.name())

                            .entity("USER")

                            .entityId(user.getId().toString())

                            .details("Maximum failed login attempts reached")

                            .build());
        }
    }

    public void loginSucceeded(String username) {

    	User user =
    	        repository.findByUsername(username)
    	                .orElseThrow();

        user.setFailedLoginAttempts(0);

        user.setAccountLocked(false);

        user.setLockedAt(null);

        user.setLockExpiresAt(null);
    }
}