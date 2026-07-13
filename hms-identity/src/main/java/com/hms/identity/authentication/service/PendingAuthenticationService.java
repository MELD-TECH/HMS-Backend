package com.hms.identity.authentication.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.common.exception.AuthenticationChallengeCompletedException;
import com.hms.common.exception.AuthenticationChallengeExpiredException;
import com.hms.common.exception.BusinessException;
import com.hms.common.exception.OtpCooldownException;
import com.hms.common.exception.OtpResendLimitExceededException;
import com.hms.common.exception.ResourceNotFoundException;
import com.hms.identity.authentication.config.AuthenticationProperties;
import com.hms.identity.authentication.entity.PendingAuthentication;
import com.hms.identity.authentication.enums.PendingAuthenticationStatus;
import com.hms.identity.authentication.generator.ChallengeTokenGenerator;
import com.hms.identity.authentication.repository.PendingAuthenticationRepository;
import com.hms.notification.mfa.enums.MfaType;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class PendingAuthenticationService {

    private final PendingAuthenticationRepository repository;

    private final AuthenticationProperties properties;

    private final ChallengeTokenGenerator generator;

    /**
     * Creates a new pending authentication.
     */
    public PendingAuthentication create(

            UUID userId,

            String username,

            MfaType mfaType,

            String ipAddress,

            String userAgent) {

        /*
         * Remove previous pending challenge.
         */
    	repository.deleteByUserIdAndStatus(
    	        userId,
    	        PendingAuthenticationStatus.PENDING);

        PendingAuthentication pending =

                PendingAuthentication.builder()

                        .userId(userId)

                        .username(username)

                        .mfaType(mfaType)

                        .status(
                                PendingAuthenticationStatus.PENDING)

                        .challengeToken(
                                generator.generate())

                        .expiresAt(

                                LocalDateTime.now()

                                        .plusMinutes(

                                                properties.getPendingAuthenticationMinutes()))

                        .ipAddress(ipAddress)

                        .userAgent(userAgent)
                        
                        .lastOtpSentAt(LocalDateTime.now())

                        .resendCount(0)

                        .build();

        return repository.save(pending);

    }

    /**
     * Finds an active challenge.
     */
    @Transactional(readOnly = true)
    public PendingAuthentication findByChallengeToken(

            String challengeToken) {

        return repository

                .findByChallengeTokenAndStatus(

                        challengeToken,

                        PendingAuthenticationStatus.PENDING)

                .orElseThrow(

                        () -> new ResourceNotFoundException(

                                "Pending authentication not found"));

    }

    /**
     * Marks authentication as completed.
     */
    public void complete(

            PendingAuthentication pending) {

        pending.setStatus(

                PendingAuthenticationStatus.COMPLETED);

        pending.setCompletedAt(

                LocalDateTime.now());

        repository.save(pending);

    }

    /**
     * Cancels a challenge.
     */
    public void cancel(

            PendingAuthentication pending) {

        pending.setStatus(

                PendingAuthenticationStatus.CANCELLED);

        repository.save(pending);

    }

    /**
     * Expires a challenge.
     */
    public void expire(

            PendingAuthentication pending) {

        pending.setStatus(

                PendingAuthenticationStatus.EXPIRED);

        repository.save(pending);

    }

    /**
     * Validates expiry.
     */
    public void validate(

            PendingAuthentication pending) {

        if (pending.getStatus()

                != PendingAuthenticationStatus.PENDING) {

            throw new BusinessException(

                    "Authentication challenge is no longer valid.");

        }

        if (pending.getExpiresAt()

                .isBefore(LocalDateTime.now())) {

            expire(pending);

            throw new BusinessException(

                    "Authentication challenge has expired.");

        }

    }

    /**
     * Removes expired records.
     */
    public void cleanupExpired() {

        List<PendingAuthentication> expired =

                repository.findByStatusAndExpiresAtBefore(

                        PendingAuthenticationStatus.PENDING,

                        LocalDateTime.now());

        expired.forEach(this::expire);

    }
    
    @Transactional
    public PendingAuthentication validateForResend(
            String challengeToken) {

        PendingAuthentication pending =
                getPendingChallenge(challengeToken);

        validateChallenge(pending);

        validateCooldown(pending);

        validateResendLimit(pending);
        
        if (pending.getStatus() == PendingAuthenticationStatus.CANCELLED) {

            throw new ResourceNotFoundException(
                    "Authentication challenge is no longer available.");
        }

        return pending;
    }
    
    private PendingAuthentication getPendingChallenge(
            String challengeToken) {

        return repository.findByChallengeToken(challengeToken)

                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Authentication challenge not found."));
    }
    
    private void validateChallenge(
            PendingAuthentication pending) {

        if (pending.getStatus()
                == PendingAuthenticationStatus.COMPLETED) {

            throw new AuthenticationChallengeCompletedException();
        }

        if (pending.getStatus()
                == PendingAuthenticationStatus.EXPIRED) {

            throw new AuthenticationChallengeExpiredException();
        }

        if (pending.getExpiresAt().isBefore(LocalDateTime.now())) {

            expire(pending);

            throw new AuthenticationChallengeExpiredException();
        }
    }
    
    private void validateCooldown(
            PendingAuthentication pending) {

        if (pending.getLastOtpSentAt() != null
                && pending.getLastOtpSentAt()

                        .plusSeconds(properties.getResendCooldownSeconds())

                        .isAfter(LocalDateTime.now())) {

            throw new OtpCooldownException();
        }
    }
    
    private void validateResendLimit(
            PendingAuthentication pending) {

        if (pending.getResendCount() >=
                properties.getMaximumResends()) {

            throw new OtpResendLimitExceededException();
        }
    }
    
}