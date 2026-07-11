package com.hms.identity.password.service;

import com.hms.identity.session.repository.RefreshTokenRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.domain.PageRequest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.ResourceNotFoundException;
import com.hms.events.security.PasswordChangedEvent;
import com.hms.events.security.PasswordResetCompletedEvent;
import com.hms.events.security.PasswordResetRequestedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.identity.entity.User;
import com.hms.identity.password.config.PasswordPolicyProperties;
import com.hms.identity.password.dto.ChangePasswordRequest;
import com.hms.identity.password.dto.ForgotPasswordRequest;
import com.hms.identity.password.dto.ResetPasswordRequest;
import com.hms.identity.password.entity.PasswordHistory;
import com.hms.identity.password.entity.PasswordResetToken;
import com.hms.identity.password.repository.PasswordHistoryRepository;
import com.hms.identity.password.repository.PasswordResetTokenRepository;
import com.hms.identity.password.validator.PasswordPolicyValidator;
import com.hms.identity.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PasswordService {

    private final RefreshTokenRepository refreshTokenRepository;

	private final UserRepository userRepository;

    private final PasswordHistoryRepository historyRepository;

    private final PasswordResetTokenRepository tokenRepository;

    private final PasswordEncoder passwordEncoder;

    private final PasswordPolicyValidator validator;

    private final PasswordPolicyProperties properties;
        
    private final SecurityEventPublisher securityEventPublisher;
  

    private void updatePassword(

            User user,

            String rawPassword) {

        validator.validate(rawPassword);

        validatePasswordHistory(
                user,
                rawPassword);

        String encoded =

                passwordEncoder.encode(
                        rawPassword);

        user.setPasswordHash(encoded);

        updatePasswordExpiry(user);

        userRepository.save(user);

        savePasswordHistory(
                user,
                encoded);

    }
    
    private void savePasswordHistory(
            User user,
            String encodedPassword) {

        PasswordHistory history =
                PasswordHistory.builder()
                        .user(user)
                        .passwordHash(encodedPassword)
                        .changedAt(LocalDateTime.now())
                        .build();

        historyRepository.save(history);

    }
    
    private void updatePasswordExpiry(
            User user) {

        user.setPasswordChangedAt(
                LocalDateTime.now());

        user.setPasswordExpiresAt(

                LocalDateTime.now()
                        .plusDays(
                                properties.getExpiryDays()));

    }
    
    private void validatePasswordHistory(
            User user,
            String rawPassword) {

    	List<PasswordHistory> history = historyRepository.findTopByUserIdOrderByChangedAtDesc(
    			user.getId(),
    			PageRequest.of(0, properties.getHistoryCount()));

        for (PasswordHistory password : history) {

            if (passwordEncoder.matches(
                    rawPassword,
                    password.getPasswordHash())) {

                throw new BusinessException(
                        "Password has already been used.");
            }
        }

    }
    
    public void changePassword(
            String username,
            ChangePasswordRequest request) {

        User user =
                userRepository
                        .findByUsername(username)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "User not found"));

        /*
         * Verify existing password
         */
        if (!passwordEncoder.matches(
                request.currentPassword(),
                user.getPasswordHash())) {

            throw new BusinessException(
                    "Current password is incorrect");
        }

        /*
         * Prevent same password
         */
        if (request.currentPassword()
                .equals(request.newPassword())) {

            throw new BusinessException(
                    "New password must be different from current password");
        }

        /*
         * Shared implementation
         */
        updatePassword(
                user,
                request.newPassword());

        /*
         * Publish Event
         */
        
	    securityEventPublisher.publish(

	            new PasswordChangedEvent(

	                    user.getUsername(),

	                    user.getId().toString()));
	
    }
    
    public void forgotPassword(
            ForgotPasswordRequest request) {

        Optional<User> optional =
                userRepository.findByEmail(
                        request.email());

        /*
         * Prevent email enumeration.
         */
        if (optional.isEmpty()) {
            return;
        }

        User user = optional.get();

        tokenRepository.expireUnusedTokens(
                user.getId());

        PasswordResetToken token =
                PasswordResetToken.builder()
                        .user(user)
                        .token(UUID.randomUUID().toString())
                        .expiresAt(
                                LocalDateTime.now()
                                        .plusMinutes(30))
                        .used(false)
                        .build();

        tokenRepository.save(token);

        securityEventPublisher.publish(

                new PasswordResetRequestedEvent(

                        user.getUsername(),

                        user.getId().toString())

        );

        /*
         * Email sending comes later.
         */
    }
    
    public void resetPassword(
            ResetPasswordRequest request) {

        PasswordResetToken token =

                tokenRepository
                        .findByToken(request.token())
                        .orElseThrow(() ->
                                new BusinessException(
                                        "Invalid reset token"));

        if (token.isUsed()) {

            throw new BusinessException(
                    "Reset token already used");
        }

        if (token.getExpiresAt()
                .isBefore(LocalDateTime.now())) {

            throw new BusinessException(
                    "Reset token expired");
        }

        if (!request.newPassword()
                .equals(request.confirmPassword())) {

            throw new BusinessException(
                    "Passwords do not match");
        }

        User user = token.getUser();

        updatePassword(
                user,
                request.newPassword());

        token.setUsed(true);

        token.setUsedAt(
                LocalDateTime.now());

        tokenRepository.save(token);

        tokenRepository.expireAll(
                user.getId());

        refreshTokenRepository.revokeAll(
                user.getId());

        securityEventPublisher.publish(

                new PasswordResetCompletedEvent(

                        user.getUsername(),

                        user.getId().toString())

        );
    }
    


}