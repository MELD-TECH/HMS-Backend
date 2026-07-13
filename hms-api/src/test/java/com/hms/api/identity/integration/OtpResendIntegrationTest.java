package com.hms.api.identity.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hms.api.test.BaseIntegrationTest;
import com.hms.audit.security.entity.AuditLog;
import com.hms.audit.security.repository.AuditLogRepository;
import com.hms.common.exception.OtpCooldownException;
import com.hms.common.exception.OtpResendLimitExceededException;
import com.hms.identity.entity.User;
import com.hms.identity.repository.UserRepository;
import com.hms.notification.dto.ResendOtpRequest;
import com.hms.notification.mfa.entity.OtpCode;
import com.hms.notification.mfa.enums.MfaType;
import com.hms.notification.mfa.enums.OtpStatus;
import com.hms.notification.mfa.repository.OtpRepository;
import com.hms.notification.mfa.service.OtpService;

import jakarta.transaction.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@ActiveProfiles("test")
@Sql(
        scripts = {
                "/db/testdata/001_cleanup.sql",
                "/db/testdata/002_admin_user.sql",
                "/db/testdata/003_roles.sql",
                "/db/testdata/004_role_permissions.sql",
                "/db/testdata/005_password_history.sql",
                "/db/testdata/006_security_admin.sql"
        },
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
)
class OtpResendIntegrationTest extends BaseIntegrationTest {
	
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private OtpRepository otpRepository;

	@Autowired
	private AuditLogRepository auditRepository;

	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private OtpService otpService;
	
	private OtpCode createOtp() {

	    User user =
	            userRepository
	                    .findByUsername("admin")
	                    .orElseThrow();

	    OtpCode otp =
	            OtpCode.builder()

	                    .userId(user.getId())

	                    .recipient(user.getEmail())

	                    .type(MfaType.EMAIL)

	                    .status(OtpStatus.ACTIVE)

	                    .code("123456")

	                    .attempts(0)

	                    .resendCount(0)

	                    .expiresAt(
	                            LocalDateTime.now()
	                                    .plusMinutes(5))

	                    .build();

	    return otpRepository.save(otp);

	}
	
	private AuditLog findAudit(
	        String action,
	        UUID entityId) {

	    return auditRepository

	            .findTopByActionAndEntityIdOrderByCreatedAtDesc(

	                    action,

	                    entityId.toString())

	            .orElseThrow();

	}
	
	private String getUsernameById(UUID userId) {

		return userRepository

				.findById(userId)

				.orElseThrow()

				.getUsername();

	}
	
	@Test
	void shouldResendOtp() {

	    OtpCode otp =
	            createOtp();
	    
	    String username = getUsernameById(otp.getUserId());

	    ResendOtpRequest request =

	            new ResendOtpRequest(

	                    otp.getUserId(),	                    

	                    MfaType.EMAIL,
	                   
	                    otp.getRecipient(),
	                    
	                    username
	            		);

	    OtpCode resent =
	            otpService.resend(request);

	    assertNotNull(resent);

	    assertNotEquals(

	            otp.getId(),

	            resent.getId());

	}
	
	@Test
	void shouldExpirePreviousOtp() {

	    OtpCode otp =
	            createOtp();

	    String username = getUsernameById(otp.getUserId());

	    ResendOtpRequest request =

	            new ResendOtpRequest(

	                    otp.getUserId(),	                    

	                    MfaType.EMAIL,
	                   
	                    otp.getRecipient(),
	                    
	                    username
	            		);

	    otpService.resend(request);

	    OtpCode expired =

	            otpRepository

	                    .findById(

	                            otp.getId())

	                    .orElseThrow();

	    assertEquals(

	            OtpStatus.EXPIRED,

	            expired.getStatus());

	}
	
	@Test
	void shouldHaveOnlyOneActiveOtp() {

	    OtpCode otp =
	            createOtp();

	    String username = getUsernameById(otp.getUserId());

	    ResendOtpRequest request =

	            new ResendOtpRequest(

	                    otp.getUserId(),	                    

	                    MfaType.EMAIL,
	                   
	                    otp.getRecipient(),
	                    
	                    username
	            		);
	    List<OtpCode> active =

	            otpRepository

	                    .findByUserIdAndStatus(

	                            otp.getUserId(),

	                            OtpStatus.ACTIVE);

	    assertEquals(

	            1,

	            active.size());

	}
	
	@Test
	void shouldIncrementResendCount() {

		OtpCode otp = createOtp();

		String username = getUsernameById(otp.getUserId());

		ResendOtpRequest request =
		        new ResendOtpRequest(
		                otp.getUserId(),
		                MfaType.EMAIL,
		                otp.getRecipient(),
		                username);

		otpService.resend(request);

		OtpCode expired =
		        otpRepository.findById(otp.getId())
		                     .orElseThrow();

		assertEquals(1, expired.getResendCount());
	}
	
	@Test
	void shouldRejectResendDuringCooldown() {

	    OtpCode otp =
	            createOtp();

	    otp.setLastResentAt(

	            LocalDateTime.now());

	    otpRepository.save(otp);

	    String username = getUsernameById(otp.getUserId());
	    		
	    assertThrows(

	            OtpCooldownException.class,

	            () ->

	                    otpService.resend(            		
	                    	    
	            	            new ResendOtpRequest(

	            	                    otp.getUserId(),	                    

	            	                    MfaType.EMAIL,
	            	                    
	            	                    otp.getRecipient(),
	            	                    
	            	                    username
	            	            		)
	            	            ));

	}
	
	@Test
	void shouldRejectWhenMaximumResendsReached() {

	    OtpCode otp =
	            createOtp();

	    otp.setResendCount(5);

	    String username = getUsernameById(otp.getUserId());
	    
	    otpRepository.save(otp);

	    assertThrows(

	            OtpResendLimitExceededException.class,

	            () ->

	                    otpService.resend(
	            	            new ResendOtpRequest(

	            	                    otp.getUserId(),	                    

	            	                    MfaType.EMAIL,
	            	                    
	            	                    otp.getRecipient(),
	            	                    
	            	            		username)));

	}
	
	@Test
	void shouldAuditOtpResent() {

	    OtpCode otp =
	            createOtp();

	    String username = getUsernameById(otp.getUserId());

	    ResendOtpRequest request =

	            new ResendOtpRequest(

	                    otp.getUserId(),	                    

	                    MfaType.EMAIL,
	                   
	                    otp.getRecipient(),
	                    
	                    username
	            		);

	    otpService.resend(request);
	    
	    AuditLog audit =

	            findAudit(

	                    "OTP_RESENT",

	                    otp.getUserId());

	    assertEquals(

	            "OTP_RESENT",

	            audit.getAction());

	}
	
	@Test
	void shouldAuditOtpExpiredByResend() {

	    OtpCode otp =
	            createOtp();

	    String username = getUsernameById(otp.getUserId());

	    ResendOtpRequest request =

	            new ResendOtpRequest(

	                    otp.getUserId(),	                    

	                    MfaType.EMAIL,
	                   
	                    otp.getRecipient(),
	                    
	                    username
	            		);
	    
	    otpService.resend(request);
	    
	    AuditLog audit =

	            findAudit(

	                    "OTP_EXPIRED_BY_RESEND",

	                    otp.getUserId());

	    assertEquals(

	            "OTP_EXPIRED_BY_RESEND",

	            audit.getAction());

	}
	
	@Test
	void shouldAuditCooldownViolation() {

	    OtpCode otp =
	            createOtp();

	    otp.setLastResentAt(

	            LocalDateTime.now());

	    String username = getUsernameById(otp.getUserId());
	    
	    otpRepository.save(otp);

	    assertThrows(

	            OtpCooldownException.class,

	            () ->

	                    otpService.resend(
	            	            new ResendOtpRequest(

	            	                    otp.getUserId(),	                    

	            	                    MfaType.EMAIL,
	            	                    
	            	                    otp.getRecipient(),
	            	                    
	            	            		username)));

	    AuditLog audit =

	            findAudit(

	                    "OTP_RESEND_COOLDOWN",

	                    otp.getUserId());

	    assertEquals(

	            "OTP_RESEND_COOLDOWN",

	            audit.getAction());

	}
	
	@Test
	void shouldAuditResendLimitExceeded() {

	    OtpCode otp =
	            createOtp();

	    otp.setResendCount(5);

	    String username = getUsernameById(otp.getUserId());
	    
	    otpRepository.save(otp);

	    assertThrows(

	            OtpResendLimitExceededException.class,

	            () ->

	                    otpService.resend(
	            	            new ResendOtpRequest(

	            	                    otp.getUserId(),	                    

	            	                    MfaType.EMAIL,
	            	                    
	            	                    otp.getRecipient(),
	            	                    
	            	            		username)));

	    AuditLog audit =

	            findAudit(

	                    "OTP_RESEND_LIMIT_EXCEEDED",

	                    otp.getUserId());

	    assertEquals(

	            "OTP_RESEND_LIMIT_EXCEEDED",

	            audit.getAction());

	}
	
	@Test
	void shouldAuditCorrectUsername()  throws Exception {
   
	    OtpCode otp =
	            createOtp();

	    String username = getUsernameById(otp.getUserId());

	    ResendOtpRequest request =

	            new ResendOtpRequest(

	                    otp.getUserId(),	                    

	                    MfaType.EMAIL,
	                   
	                    otp.getRecipient(),
	                    
	                    username
	            		);

	    otpService.resend(request);
	    
	    AuditLog audit =

	            findAudit(

	                    "OTP_RESENT",

	                    otp.getUserId());

	    assertEquals(

	            "admin",

	            audit.getUsername());

	}
	
	@Test
	void shouldAuditEntityName() {

	    OtpCode otp =
	            createOtp();

	    String username = getUsernameById(otp.getUserId());

	    ResendOtpRequest request =

	            new ResendOtpRequest(

	                    otp.getUserId(),	                    

	                    MfaType.EMAIL,
	                   
	                    otp.getRecipient(),
	                    
	                    username
	            		);

	    otpService.resend(request);
	    
	    AuditLog audit =

	            findAudit(

	                    "OTP_RESENT",

	                    otp.getUserId());

	    assertEquals(

	            "USER",

	            audit.getEntityName());

	}
	
	@Test
	void shouldAuditEntityId() {

	    OtpCode otp =
	            createOtp();

	    String username = getUsernameById(otp.getUserId());

	    ResendOtpRequest request =

	            new ResendOtpRequest(

	                    otp.getUserId(),	                    

	                    MfaType.EMAIL,
	                   
	                    otp.getRecipient(),
	                    
	                    username
	            		);
	    
	    otpService.resend(request);
	    
	    AuditLog audit =

	            findAudit(

	                    "OTP_RESENT",

	                    otp.getUserId());

	    assertEquals(

	            otp.getUserId().toString(),

	            audit.getEntityId());

	}
}
