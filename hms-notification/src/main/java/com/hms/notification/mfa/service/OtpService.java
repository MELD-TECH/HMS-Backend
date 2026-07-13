package com.hms.notification.mfa.service;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.common.exception.InvalidOtpException;
import com.hms.common.exception.OtpCooldownException;
import com.hms.common.exception.OtpExpiredException;
import com.hms.common.exception.OtpResendLimitExceededException;
import com.hms.events.security.OtpCooldownEvent;
import com.hms.events.security.OtpExpiredByResendEvent;
import com.hms.events.security.OtpExpiredEvent;
import com.hms.events.security.OtpGeneratedEvent;
import com.hms.events.security.OtpResendLimitExceededEvent;
import com.hms.events.security.OtpResentEvent;
import com.hms.events.security.OtpRetryLimitExceededEvent;
import com.hms.events.security.OtpVerificationFailedEvent;
import com.hms.events.security.OtpVerifiedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.notification.dto.GenerateOtpRequest;
import com.hms.notification.dto.ResendOtpRequest;
import com.hms.notification.dto.VerifyOtpRequest;
import com.hms.notification.mfa.config.MfaProperties;
import com.hms.notification.mfa.entity.OtpCode;
import com.hms.notification.mfa.enums.MfaType;
import com.hms.notification.mfa.enums.OtpStatus;
import com.hms.notification.mfa.generator.OtpGenerator;
import com.hms.notification.mfa.repository.OtpRepository;
import com.hms.notification.mfa.validator.OtpResendValidator;
import com.hms.notification.mfa.validator.OtpValidator;
import com.hms.security.util.SecurityUtils;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class OtpService {

    private final OtpRepository repository;

    private final OtpGenerator generator;

    private final OtpValidator validator;

    private final SecurityEventPublisher publisher;

    private final MfaProperties properties;
    
    private final OtpResendValidator resendValidator;

    /**
     * Generates a new OTP.
     */
    public OtpCode generate(
            GenerateOtpRequest request) {

        repository.findTopByUserIdAndStatusOrderByCreatedAtDesc(
                request.userId(),
                OtpStatus.ACTIVE)
                .ifPresent(this::expire);

        OtpCode otp = createOtp(request);

        OtpCode savedOtp = repository.save(otp);

        publishOtpGenerated(request, savedOtp);

        return savedOtp;
    }

    /**
     * Verifies an OTP.
     */
    public void verify(
            VerifyOtpRequest request) {

        OtpCode otp =
                findActiveOtp(
                        request.userId(),
                        request.type());

        try {
        validator.validate(
                otp,
                request.code());

        markVerified(otp);

        publishOtpVerified(request, otp);
        
        }        
        catch (OtpExpiredException ex) {

            expire(otp);

            publishOtpExpired(request, otp);

            throw ex;
        }        
        catch (InvalidOtpException ex) {

            markFailed(otp);

            publishOtpFailed(request, otp);

            if (otp.getAttempts() >= properties.getMaxAttempts()) {

                publishOtpRetryLimitExceeded(request, otp);

            }

            validator.validateAttempts(otp);

            throw ex;
        }
    }

    /**
     * Will be implemented in Milestone 3.
     */
    public OtpCode resend(
            ResendOtpRequest request) {

        OtpCode activeOtp =

                findActiveOtp(

                        request.userId(),

                        request.type());

        resendValidator.validate(activeOtp);

        incrementResend(activeOtp);

        expireByResend(request, activeOtp);

        OtpCode newOtp = generate(

                toGenerateRequest(request));
        
        publisher.publish(

                new OtpResentEvent(

                        request.initiatedBy(),

                        newOtp.getUserId().toString()));

        return newOtp;

    }

    /**
     * Finds the currently active OTP.
     */
    public OtpCode findActiveOtp(
            UUID userId,
            MfaType type) {

        return repository
                .findTopByUserIdAndTypeAndStatusOrderByCreatedAtDesc(
                        userId,
                        type,
                        OtpStatus.ACTIVE)
                .orElseThrow(
                        InvalidOtpException::new);
    }

    /**
     * Marks an OTP as verified.
     */
    private void markVerified(
            OtpCode otp) {

        otp.setStatus(
                OtpStatus.VERIFIED);

        otp.setVerifiedAt(
                LocalDateTime.now());

        repository.save(otp);
    }

    /**
     * Marks an OTP as expired.
     */
    private void expire(OtpCode otp) {

        otp.setStatus(OtpStatus.EXPIRED);

        repository.save(otp);
    }
    
    private void expireByResend(ResendOtpRequest request, OtpCode otp) {

        expire(otp);

        publisher.publish(

            new OtpExpiredByResendEvent(

                request.initiatedBy(),

                otp.getUserId().toString()));
    }

    /**
     * Records an invalid verification attempt.
     */
    private void markFailed(
            OtpCode otp) {

    	int attempts = otp.getAttempts() + 1;

    	otp.setAttempts(attempts);

        repository.save(otp);

    }

    /**
     * Creates a fresh OTP entity.
     */
    private OtpCode createOtp(
            GenerateOtpRequest request) {

        return OtpCode.builder()
                .userId(request.userId())
                .recipient(request.recipient())
                .type(request.type())
                .code(generator.generate())
                .status(OtpStatus.ACTIVE)
                .attempts(0)
                .resendCount(0)
                .expiresAt(
                        LocalDateTime.now()
                                .plusMinutes(
                                        properties.getOtpExpiryMinutes()))
                .build();
    }
    
    private GenerateOtpRequest toGenerateRequest(

            ResendOtpRequest request) {

        return new GenerateOtpRequest(

                request.userId(),

                request.recipient(),

                request.type(),
                
                request.initiatedBy()
        		);

    }
    
    private void incrementResend(
            OtpCode otp) {

        otp.setResendCount(

                otp.getResendCount() + 1);

        otp.setLastResentAt(

                LocalDateTime.now());

        repository.save(otp);

    }
   
    /**
     * Publishes OTP generated event.
     */
    private void publishOtpGenerated(
    		GenerateOtpRequest request, OtpCode otp) {

        publisher.publish(

                new OtpGeneratedEvent(

                		request.initiatedBy(),

                        otp.getUserId().toString()));
    }

    /**
     * Publishes OTP verified event.
     */
    private void publishOtpVerified(
    		VerifyOtpRequest request, OtpCode otp) {

        publisher.publish(

                new OtpVerifiedEvent(

                		request.initiatedBy(),

                        otp.getUserId().toString()));
    }
   
    private void publishOtpFailed(VerifyOtpRequest request, OtpCode otp) {
    	
        publisher.publish(

                new OtpVerificationFailedEvent(

                		request.initiatedBy(),

                        otp.getUserId().toString()));
   	
    }
    
    private void publishOtpRetryLimitExceeded(
    		VerifyOtpRequest request, OtpCode otp) {

        publisher.publish(

            new OtpRetryLimitExceededEvent(

            		request.initiatedBy(),

                otp.getUserId().toString()));
    }
    
    private void publishOtpExpired(VerifyOtpRequest request, OtpCode otp) {
    	
        publisher.publish(

                new OtpExpiredEvent(
                		request.initiatedBy(),

                        otp.getUserId().toString()));
    	
    }
}