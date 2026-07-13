package com.hms.api.identity.integration;

import static org.junit.Assert.*;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.hms.api.test.BaseIntegrationTest;
import com.hms.audit.security.entity.AuditLog;
import com.hms.audit.security.repository.AuditLogRepository;
import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;
import com.hms.identity.security.scheduler.AutomaticUnlockScheduler;


@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/001_cleanup.sql",
        "/db/testdata/002_admin_user.sql",
        "/db/testdata/006_security_admin.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class AutomaticUnlockSchedulerIntegrationTest
        extends BaseIntegrationTest {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuditLogRepository auditRepository;

    @Autowired
    private AutomaticUnlockScheduler scheduler;
    
    @Test
    void shouldAutomaticallyUnlockExpiredAccount() {

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        user.setAccountLocked(true);
        user.setFailedLoginAttempts(5);
        user.setLockedAt(LocalDateTime.now().minusMinutes(45));
        user.setLockExpiresAt(LocalDateTime.now().minusMinutes(15));

        userRepository.save(user);

        scheduler.unlockExpiredAccounts();

        User updated =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        assertFalse(updated.getAccountLocked());
    }
    
    @Test
    void shouldNotUnlockActiveLock() {

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        user.setAccountLocked(true);

        user.setFailedLoginAttempts(5);

        user.setLockedAt(LocalDateTime.now());

        user.setLockExpiresAt(
                LocalDateTime.now().plusMinutes(20));

        userRepository.save(user);

        scheduler.unlockExpiredAccounts();

        User updated =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        assertTrue(updated.getAccountLocked());

        assertEquals(
                Integer.valueOf(5),
                updated.getFailedLoginAttempts());
    }
    
    @Test
    void shouldResetFailedAttempts() {

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        user.setAccountLocked(true);

        user.setFailedLoginAttempts(5);

        user.setLockedAt(LocalDateTime.now().minusMinutes(50));

        user.setLockExpiresAt(LocalDateTime.now().minusMinutes(5));

        userRepository.save(user);

        scheduler.unlockExpiredAccounts();

        User updated =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        assertEquals(
                Integer.valueOf(0),
                updated.getFailedLoginAttempts());
    }
    
    @Test
    void shouldClearLockedDates() {

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        user.setAccountLocked(true);

        user.setLockedAt(LocalDateTime.now().minusMinutes(40));

        user.setLockExpiresAt(LocalDateTime.now().minusMinutes(5));

        userRepository.save(user);

        scheduler.unlockExpiredAccounts();

        User updated =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        assertNull(updated.getLockedAt());

        assertNull(updated.getLockExpiresAt());
    }
    
    @Test
    void shouldWriteAuditRecord() {

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        user.setAccountLocked(true);

        user.setLockExpiresAt(
                LocalDateTime.now().minusMinutes(1));

        userRepository.save(user);

        scheduler.unlockExpiredAccounts();

        AuditLog audit =
                auditRepository

                        .findAll()

                        .stream()

                        .filter(a ->
                                "ACCOUNT_AUTO_UNLOCKED"
                                        .equals(a.getAction()))

                        .findFirst()

                        .orElseThrow();

        assertEquals(
                "ACCOUNT_AUTO_UNLOCKED",
                audit.getAction());

        assertEquals(
                "admin",
                audit.getUsername());
    }
    
    @Test
    void shouldUnlockMultipleAccounts() {

        User admin =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        User security =
                userRepository
                        .findByUsername("security-admin")
                        .orElseThrow();

        admin.setAccountLocked(true);

        security.setAccountLocked(true);

        admin.setLockExpiresAt(
                LocalDateTime.now().minusMinutes(2));

        security.setLockExpiresAt(
                LocalDateTime.now().minusMinutes(3));

        userRepository.save(admin);

        userRepository.save(security);

        scheduler.unlockExpiredAccounts();

        assertFalse(
                userRepository
                        .findByUsername("admin")
                        .orElseThrow()
                        .getAccountLocked());

        assertFalse(
                userRepository
                        .findByUsername("security-admin")
                        .orElseThrow()
                        .getAccountLocked());
    }
    
    @Test
    void shouldIgnoreUnlockedAccounts() {

        long before =
                auditRepository.count();

        scheduler.unlockExpiredAccounts();

        long after =
                auditRepository.count();

        assertEquals(
                before,
                after);
    }
    
    @Test
    void shouldIgnoreAccountsWithoutExpiry() {

        User user =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        user.setAccountLocked(true);

        user.setLockExpiresAt(null);

        userRepository.save(user);

        scheduler.unlockExpiredAccounts();

        User updated =
                userRepository
                        .findByUsername("admin")
                        .orElseThrow();

        assertTrue(updated.getAccountLocked());
    }
    
    
    
}
