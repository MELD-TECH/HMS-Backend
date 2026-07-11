package com.hms.identity.password.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.audit.security.repository.AuditLogRepository;
import com.hms.common.exception.BusinessException;
import com.hms.events.security.PasswordResetCompletedEvent;
import com.hms.events.security.PasswordResetRequestedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.identity.entity.User;
import com.hms.identity.password.config.PasswordPolicyProperties;
import com.hms.identity.password.dto.ForgotPasswordRequest;
import com.hms.identity.password.dto.ResetPasswordRequest;
import com.hms.identity.password.entity.PasswordResetToken;
import com.hms.identity.password.repository.PasswordResetTokenRepository;
import com.hms.identity.password.util.PasswordResetTokenGenerator;
import com.hms.identity.repository.UserRepository;
import com.hms.identity.session.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class PasswordResetService {
	
	private final UserRepository userRepository;

	private final PasswordResetTokenRepository tokenRepository;

	private final PasswordResetTokenGenerator tokenGenerator;
	
	private final PasswordEncoder passwordEncoder;

	private final PasswordHistoryService passwordHistoryService;

	private final PasswordPolicyService passwordPolicyService;

	private final RefreshTokenRepository refreshRepository;
	
	private final PasswordPolicyProperties properties;
	
	private final SecurityEventPublisher securityEventPublisher;
	
	private final AuditLogRepository auditRepository;
	
	@Transactional
	public void forgotPassword(
	        ForgotPasswordRequest request) {

	    Optional<User> optionalUser =
	            userRepository.findByEmail(
	                    request.email());

	    /*
	     * Prevent email enumeration.
	     */
	    if (optionalUser.isEmpty()) {
	        return;
	    }

	    User user = optionalUser.get();

	    /*
	     * Expire previous tokens.
	     */
	    tokenRepository.expireUnusedTokens(
	            user.getId());

	    PasswordResetToken token =
	            PasswordResetToken.builder()
	                    .user(user)
	                    .token(
	                            tokenGenerator.generate())
	                    .expiresAt(
	                            LocalDateTime.now()
	                                    .plusMinutes(30))
	                    .used(false)
	                    .build();

	    tokenRepository.save(token);

		securityEventPublisher.publish(new PasswordResetRequestedEvent(user.getUsername(), user.getId().toString()));
				
		/*
	     * Email sending comes later.
	     */
	}
	
	@Transactional
	public void resetPassword(
	        ResetPasswordRequest request) {
		
		PasswordResetToken resetToken =
		        tokenRepository
		                .findByToken(request.token())
		                .orElseThrow(
		                        () ->
		                                new BusinessException(
		                                        "Invalid password reset token"));
	
		if (resetToken.isUsed()) {

		    throw new BusinessException(
		            "Password reset token has already been used");

		}
		
		if (resetToken.getExpiresAt()
		        .isBefore(LocalDateTime.now())) {

		    throw new BusinessException(
		            "Password reset token has expired");

		}
		
		if (!request.newPassword()
		        .equals(request.confirmPassword())) {

		    throw new BusinessException(
		            "Passwords do not match");

		}
		
		User user =
		        resetToken.getUser();
		
		passwordPolicyService.validate(
		        request.newPassword());
		
		passwordHistoryService.validatePasswordReuse(
		        user,
		        request.newPassword());
		
		user.setPasswordHash(

		        passwordEncoder.encode(
		                request.newPassword()));
		
		LocalDateTime now =
		        LocalDateTime.now();

		user.setPasswordChangedAt(now);

		user.setPasswordExpiresAt(
		        now.plusDays(
		                properties.getExpiryDays()));
		
		userRepository.save(user);
		
		passwordHistoryService.saveHistory(user);
		
		resetToken.setUsed(true);

		resetToken.setUsedAt(
		        LocalDateTime.now());

		tokenRepository.save(resetToken);
		
		tokenRepository.expireAll(
		        user.getId());
		
		refreshRepository.revokeAll(
		        user.getId());
		

	    securityEventPublisher.publish(
	    		                new PasswordResetCompletedEvent(
                        user.getUsername(),
                        user.getId().toString())
        );
	    
	}

}
