package com.hms.identity.security.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.hms.common.exception.AccountLockedException;
import com.hms.events.security.AccountAutoUnlockedEvent;
import com.hms.events.security.AccountLockedEvent;
import com.hms.events.security.AccountUnlockedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;
import com.hms.identity.security.config.AccountSecurityProperties;
import com.hms.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class AccountLockService {

    private final UserRepository repository;
       
    private final SecurityEventPublisher publisher;
    
    private final AccountSecurityProperties properties;

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

//    @Transactional
    public void automaticUnlock(User user) {

//    	clearLock(user);
    	
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockedAt(null);
        user.setLockExpiresAt(null);

        repository.save(user);

        publisher.publish(

                new AccountAutoUnlockedEvent(

                        user.getUsername(),

                        user.getId().toString()));
    }

    public void unlock(UUID userId) {

        User user = repository.findById(userId)
                .orElseThrow();

        clearLock(user);

        repository.save(user);

        publisher.publish(
            new AccountUnlockedEvent(
                user.getUsername(),
                user.getId().toString()));
    }
   
    @Transactional
    public void lock(UUID userId) {

        User user =
                repository.findById(userId)
                        .orElseThrow();

        user.setAccountLocked(true);

        user.setLockedAt(LocalDateTime.now());

        user.setLockExpiresAt(

                LocalDateTime.now()

                        .plusMinutes(

                                properties.getLockDurationMinutes()));

        publisher.publish(

                new AccountLockedEvent(

                        SecurityUtils.getCurrentUsername(),

                        user.getId().toString()));
    }
    
    private void clearLock(User user) {
        user.setAccountLocked(false);
        user.setFailedLoginAttempts(0);
        user.setLockedAt(null);
        user.setLockExpiresAt(null);
    }

}
