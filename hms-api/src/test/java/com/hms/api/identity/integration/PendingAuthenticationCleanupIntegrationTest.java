package com.hms.api.identity.integration;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.time.LocalDateTime;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;

import com.hms.api.test.BaseIntegrationTest;
import com.hms.audit.security.repository.AuditLogRepository;
import com.hms.identity.authentication.entity.PendingAuthentication;
import com.hms.identity.authentication.enums.PendingAuthenticationStatus;
import com.hms.identity.authentication.repository.PendingAuthenticationRepository;
import com.hms.identity.authentication.service.PendingAuthenticationService;
import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;
import com.hms.notification.mfa.enums.MfaType;

@ActiveProfiles("test")
@Sql(
    scripts = {
        "/db/testdata/001_cleanup.sql",
        "/db/testdata/002_admin_user.sql",
        "/db/testdata/006_security_admin.sql",
        "/db/testdata/007_mfa_admin.sql"
    },
    executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class PendingAuthenticationCleanupIntegrationTest
        extends BaseIntegrationTest {
	
	@Autowired
	private PendingAuthenticationRepository repository;

	@Autowired
	private PendingAuthenticationService service;

	@Autowired
	private AuditLogRepository auditRepository;

	@Autowired
	private UserRepository userRepository;
	
	private PendingAuthentication createPending(
	        PendingAuthenticationStatus status,
	        LocalDateTime expiresAt) {

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    PendingAuthentication pending =

	            PendingAuthentication.builder()

	                    .userId(user.getId())

	                    .username(user.getUsername())

	                    .mfaType(MfaType.EMAIL)

	                    .challengeToken(UUID.randomUUID().toString())

	                    .status(status)

	                    .expiresAt(expiresAt)

	                    .ipAddress("127.0.0.1")

	                    .userAgent("JUnit")

	                    .lastOtpSentAt(LocalDateTime.now())

	                    .resendCount(0)

	                    .build();

	    return repository.save(pending);
	}
	
	@Test
	void shouldExpireExpiredPendingAuthentication() {

	    PendingAuthentication pending =

	            createPending(

	                    PendingAuthenticationStatus.PENDING,

	                    LocalDateTime.now().minusMinutes(5));

	    service.cleanupExpired();

	    PendingAuthentication updated =

	            repository.findById(pending.getId())

	                    .orElseThrow();

	    assertEquals(

	            PendingAuthenticationStatus.EXPIRED,

	            updated.getStatus());
	}
	
	@Test
	void shouldIgnoreActivePendingAuthentication() {

	    PendingAuthentication pending =

	            createPending(

	                    PendingAuthenticationStatus.PENDING,

	                    LocalDateTime.now().plusMinutes(5));

	    service.cleanupExpired();

	    PendingAuthentication updated =

	            repository.findById(pending.getId())

	                    .orElseThrow();

	    assertEquals(

	            PendingAuthenticationStatus.PENDING,

	            updated.getStatus());
	}
	
	@Test
	void shouldExpireMultiplePendingChallenges() {

	    PendingAuthentication first =

	            createPending(
	                    PendingAuthenticationStatus.PENDING,
	                    LocalDateTime.now().minusMinutes(10));

	    PendingAuthentication second =

	            createPending(
	                    PendingAuthenticationStatus.PENDING,
	                    LocalDateTime.now().minusMinutes(3));

	    service.cleanupExpired();

	    assertEquals(
	            PendingAuthenticationStatus.EXPIRED,
	            repository.findById(first.getId())
	                    .orElseThrow()
	                    .getStatus());

	    assertEquals(
	            PendingAuthenticationStatus.EXPIRED,
	            repository.findById(second.getId())
	                    .orElseThrow()
	                    .getStatus());
	}
	
	@Test
	void shouldIgnoreCompletedChallenges() {

	    PendingAuthentication pending =

	            createPending(

	                    PendingAuthenticationStatus.COMPLETED,

	                    LocalDateTime.now().minusHours(2));

	    service.cleanupExpired();

	    PendingAuthentication updated =

	            repository.findById(pending.getId())

	                    .orElseThrow();

	    assertEquals(

	            PendingAuthenticationStatus.COMPLETED,

	            updated.getStatus());
	}
	
	@Test
	void shouldIgnoreCancelledChallenges() {

	    PendingAuthentication pending =

	            createPending(

	                    PendingAuthenticationStatus.CANCELLED,

	                    LocalDateTime.now().minusHours(2));

	    service.cleanupExpired();

	    PendingAuthentication updated =

	            repository.findById(pending.getId())

	                    .orElseThrow();

	    assertEquals(

	            PendingAuthenticationStatus.CANCELLED,

	            updated.getStatus());
	}
	
	@Test
	void shouldNotFailWhenNoExpiredChallenges() {

	    PendingAuthentication pending =

	            createPending(
	                    PendingAuthenticationStatus.PENDING,
	                    LocalDateTime.now().plusMinutes(10));

	    service.cleanupExpired();

	    PendingAuthentication updated =

	            repository.findById(pending.getId())
	                    .orElseThrow();

	    assertEquals(
	            PendingAuthenticationStatus.PENDING,
	            updated.getStatus());
	}
	
	@Test
	void shouldSetExpiredStatus() {

	    PendingAuthentication pending =

	            createPending(

	                    PendingAuthenticationStatus.PENDING,

	                    LocalDateTime.now().minusMinutes(2));

	    service.cleanupExpired();

	    assertEquals(

	            PendingAuthenticationStatus.EXPIRED,

	            repository.findById(pending.getId())

	                    .orElseThrow()

	                    .getStatus());
	}
	
	@Test
	void shouldPreserveChallengeToken() {

	    PendingAuthentication pending =

	            createPending(

	                    PendingAuthenticationStatus.PENDING,

	                    LocalDateTime.now().minusMinutes(2));

	    String token = pending.getChallengeToken();

	    service.cleanupExpired();

	    PendingAuthentication updated =

	            repository.findById(pending.getId())

	                    .orElseThrow();

	    assertEquals(

	            token,

	            updated.getChallengeToken());
	}
	
	@Test
	void shouldPreserveCompletedAt() {

	    PendingAuthentication pending =

	            createPending(

	                    PendingAuthenticationStatus.COMPLETED,

	                    LocalDateTime.now().minusMinutes(30));

	    pending.setCompletedAt(

	            LocalDateTime.now().minusMinutes(20));

	    repository.save(pending);

	    service.cleanupExpired();

	    PendingAuthentication updated =

	            repository.findById(pending.getId())

	                    .orElseThrow();

	    assertNotNull(

	            updated.getCompletedAt());
	}
	
	@Test
	void shouldExpireOnlyPendingRecords() {

		PendingAuthentication first = createPending(

	            PendingAuthenticationStatus.PENDING,

	            LocalDateTime.now().minusMinutes(5));

		PendingAuthentication second = createPending(

	            PendingAuthenticationStatus.COMPLETED,

	            LocalDateTime.now().minusMinutes(5));

		PendingAuthentication third = createPending(

	            PendingAuthenticationStatus.CANCELLED,

	            LocalDateTime.now().minusMinutes(5));

	    service.cleanupExpired();

	    assertEquals(
	            PendingAuthenticationStatus.EXPIRED,
	            repository.findById(first.getId())
	                    .orElseThrow()
	                    .getStatus());

	    assertEquals(
	            PendingAuthenticationStatus.COMPLETED,
	            repository.findById(second.getId())
	                    .orElseThrow()
	                    .getStatus());

	    assertEquals(

	            1,

	            repository.findAll()

	                    .stream()

	                    .filter(p ->

	                            p.getStatus()

	                                    == PendingAuthenticationStatus.CANCELLED)

	                    .count());
	    
	    assertEquals(
	            PendingAuthenticationStatus.CANCELLED,
	            repository.findById(third.getId())
	                    .orElseThrow()
	                    .getStatus());
	}	
	
}