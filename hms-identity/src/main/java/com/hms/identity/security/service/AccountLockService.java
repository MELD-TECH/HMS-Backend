package com.hms.identity.security.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.LockedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hms.common.exception.AccountLockedException;
import com.hms.identity.audit.dto.AuditRequest;
import com.hms.identity.audit.enums.AuditAction;
import com.hms.identity.audit.enums.AuditModule;
import com.hms.identity.audit.service.AuditService;
import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountLockService {

    private final UserRepository repository;
    
    private final AuditService auditService;

    public void validate(User user) {

        if (!Boolean.TRUE.equals(user.getAccountLocked())) {
            return;
        }

        if (user.getLockExpiresAt() == null) {

        	throw new AccountLockedException(
        	        "Account is locked");
        }

        if (user.getLockExpiresAt()
                .isAfter(LocalDateTime.now())) {

        	throw new AccountLockedException(
        	        "Account is locked");
        }

        /*
         * lock expired
         */

        automaticUnlock(user);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void automaticUnlock(User user) {

    	clearLock(user);
    	
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockedAt(null);
        user.setLockExpiresAt(null);

        repository.save(user);

        auditService.log(

                AuditRequest.builder()

                        .username(user.getUsername())

                        .action(
                                AuditAction.ACCOUNT_AUTO_UNLOCKED.name())

                        .module(
                                AuditModule.IDENTITY.name())

                        .entity("USER")

                        .entityId(user.getId().toString())

                        .details(
                                "Automatically unlocked after lock duration")

                        .build());
    }

    public void unlock(UUID userId) {

        User user =
                repository.findById(userId)
                        .orElseThrow();

    	clearLock(user);
    	
        unlockByAdministrator(user);
    }
    
    private void unlockByAdministrator(User user) {
	
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockedAt(null);
        user.setLockExpiresAt(null);

        repository.save(user);

        auditService.log(

                AuditRequest.builder()

                        .username(user.getUsername())

                        .action(
                                AuditAction.ACCOUNT_UNLOCKED.name())

                        .module(
                                AuditModule.IDENTITY.name())

                        .entity("USER")

                        .entityId(user.getId().toString())

                        .details(
                                "Account unlocked by administrator")

                        .build());
    }
    
    private void clearLock(User user) {
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockedAt(null);
        user.setLockExpiresAt(null);
        repository.save(user);
    }

}
