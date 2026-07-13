package com.hms.identity.service;

import com.hms.common.exception.BusinessException;
import com.hms.events.security.LoginFailureEvent;
import com.hms.events.security.LoginSuccessEvent;
import com.hms.events.security.MfaAuthenticationCompletedEvent;
import com.hms.events.security.MfaVerifiedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.identity.authentication.dto.CompleteAuthenticationRequest;
import com.hms.identity.authentication.dto.ResendAuthenticationOtpRequest;
import com.hms.identity.authentication.entity.PendingAuthentication;
import com.hms.identity.authentication.repository.PendingAuthenticationRepository;
import com.hms.identity.authentication.service.PendingAuthenticationService;
import com.hms.identity.dto.LoginRequest;
import com.hms.identity.dto.LoginResponse;
import com.hms.identity.entity.User;
import com.hms.identity.password.service.PasswordExpiryService;
import com.hms.identity.repository.UserRepository;
import com.hms.identity.security.service.AccountLockService;
import com.hms.identity.security.service.LoginAttemptService;
import com.hms.identity.session.dto.RefreshTokenRequest;
import com.hms.identity.session.entity.RefreshToken;
import com.hms.identity.session.service.RefreshTokenService;
import com.hms.identity.session.util.DeviceUtil;
import com.hms.identity.session.util.IpUtil;
import com.hms.notification.dto.GenerateOtpRequest;
import com.hms.notification.dto.OtpResponse;
import com.hms.notification.dto.ResendOtpRequest;
import com.hms.notification.dto.VerifyOtpRequest;
import com.hms.notification.mfa.enums.MfaType;
import com.hms.notification.mfa.service.EmailOtpService;
import com.hms.notification.sms.service.SmsOtpService;
import com.hms.security.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;

import org.springframework.security.authentication.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class AuthenticationService {

    private final AuthenticationManager
            authenticationManager;

    private final JwtService jwtService;
    
    private final UserRepository userRepository;
    
    private final RefreshTokenService refreshTokenService;
    
    private final LoginAttemptService loginAttemptService;
   
    private final AccountLockService accountLockService;
    
    private final SecurityEventPublisher securityEventPublisher;
    
    private final PasswordExpiryService passwordExpiryService;
   
    private final PendingAuthenticationService pendingService;

    private final EmailOtpService emailOtpService;

    private final SmsOtpService smsOtpService;
    
    private final PendingAuthenticationRepository pendingRepository;
    
    public LoginResponse login(
            LoginRequest request,
            HttpServletRequest servletRequest) {

        User user =
                userRepository
                        .findByUsername(request.username())
                        .orElse(null);

        validateAccount(user);

        authenticateCredentials(request, user);

        /*
         * Authentication succeeded.
         */

        handleAuthenticationSuccess(user);

        /*
         * MFA?
         */

        if (Boolean.TRUE.equals(user.getMfaEnabled())) {

            return startMfaLogin(
                    user,
                    servletRequest);
        }

        /*
         * Complete login immediately.
         */

        return completeLogin(
                user,
                servletRequest);
    }
	

    @Transactional
    public LoginResponse refresh(
            RefreshTokenRequest request,
            HttpServletRequest servletRequest) {

        RefreshToken existing =
                refreshTokenService.validate(
                        request.refreshToken());

        RefreshToken session =
                refreshTokenService.rotate(
                        request.refreshToken(),
                        UUID.randomUUID().toString(),
                        DeviceUtil.deviceName(servletRequest),
                        IpUtil.ip(servletRequest),
                        servletRequest.getHeader("User-Agent"));

        String accessToken =
                generateAccessToken(
                        existing.getUser(),
                        session);

        return buildLoginResponse(
                accessToken,
                session);
    }
	

    private LoginResponse startMfaLogin(
            User user,
            HttpServletRequest request) {

        PendingAuthentication pending =

                pendingService.create(

                        user.getId(),

                        user.getUsername(),

                        user.getMfaType(),

                        IpUtil.ip(request),

                        request.getHeader("User-Agent"));

        sendOtp(

                user.getMfaType(),

                new GenerateOtpRequest(

                        user.getId(),

                        resolveRecipient(user),

                        user.getMfaType(),
                        
                        user.getUsername()
                		));

        return LoginResponse.builder()

                .mfaRequired(true)

                .challengeToken(
                        pending.getChallengeToken())

                .mfaType(
                        pending.getMfaType())

                .expiresAt(
                        pending.getExpiresAt())

                .build();
    }
    
    
	private String resolveRecipient(User user) {

	    return switch (user.getMfaType()) {

	        case EMAIL -> {

	            if (user.getEmail() == null || user.getEmail().isBlank()) {
	                throw new BusinessException(
	                        "User does not have an email address configured.");
	            }

	            yield user.getEmail();
	        }

	        case SMS -> {

	            if (user.getPhoneNumber() == null || user.getPhoneNumber().isBlank()) {
	                throw new BusinessException(
	                        "User does not have a phone number configured.");
	            }

	            yield user.getPhoneNumber();
	        }

	        default -> throw new BusinessException(
	                "Unsupported MFA type.");
	    };
	}
	
	private void authenticateCredentials(
	        LoginRequest request,
	        User user) {

	    try {

	        authenticationManager.authenticate(

	                new UsernamePasswordAuthenticationToken(

	                        request.username(),

	                        request.password()));

	        passwordExpiryService.validate(user);

	    }

	    catch (BadCredentialsException ex) {

	        if (user != null) {

	            handleAuthenticationFailure(user);
	        }

	        throw ex;
	    }
	}
	
	private LoginResponse completeLogin(
	        User user,
	        HttpServletRequest request) {

	    RefreshToken session =

	            createSession(
	                    user,
	                    request);

	    String accessToken =

	            generateAccessToken(
	                    user,
	                    session);

	    return buildLoginResponse(
	            accessToken,
	            session);
	}
	
	private RefreshToken createSession(
	        User user,
	        HttpServletRequest request) {

	    return refreshTokenService.create(

	            user,

	            UUID.randomUUID().toString(),

	            DeviceUtil.deviceName(request),

	            IpUtil.ip(request),

	            request.getHeader("User-Agent"));
	}
	
	private String generateAccessToken(

	        User user,

	        RefreshToken session) {

	    return jwtService.generateToken(

	            user.getUsername(),

	            session.getId());

	}
	
	private LoginResponse buildLoginResponse(

	        String accessToken,

	        RefreshToken session) {

	    return LoginResponse.builder()

	            .mfaRequired(false)

	            .accessToken(accessToken)

	            .refreshToken(session.getToken())

	            .sessionId(session.getId())

	            .tokenType("Bearer")

	            .expiresAt(

	                    jwtService.extractExpiration(

	                            accessToken))

	            .expiresInSeconds(

	                    jwtService.getRemainingSeconds(

	                            accessToken))

	            .build();

	}
	
	private void sendOtp(
	        MfaType type,
	        GenerateOtpRequest request) {

	    switch (type) {

	        case EMAIL -> emailOtpService.generate(request);

	        case SMS -> smsOtpService.generate(request);

	        default ->
	                throw new BusinessException(
	                        "Unsupported MFA type.");
	    }
	}
	
	
	private void validateAccount(User user) {

	    if (user == null) {
	        return;
	    }

	    accountLockService.validate(user);
	}

	private void handleAuthenticationFailure(
	        User user) {

	    loginAttemptService.loginFailed(
	            user.getUsername());

	    securityEventPublisher.publish(

	            new LoginFailureEvent(

	                    user.getUsername(),

	                    user.getId().toString()));
	}
	
	private void handleAuthenticationSuccess(
	        User user) {

		loginAttemptService.resetLoginAttempts(user);
		
	    user.setLastLoginAt(
	            LocalDateTime.now());

	    userRepository.save(user);

	    securityEventPublisher.publish(

	            new LoginSuccessEvent(

	                    user.getUsername(),

	                    user.getId().toString()));
	}
	
	@Transactional
	public LoginResponse completeAuthentication(

	        CompleteAuthenticationRequest request,

	        HttpServletRequest servletRequest) {

	    PendingAuthentication pending =

	            pendingService.findByChallengeToken(

	                    request.challengeToken());

	    pendingService.validate(pending);

	    User user =

	            userRepository

	                    .findById(pending.getUserId())

	                    .orElseThrow();

	    verifyOtp(

	            user,

	            pending,

	            request.otp());

	    pendingService.complete(pending);
	    
	   
	    securityEventPublisher.publish(

	    	    new MfaVerifiedEvent(

	    	        user.getUsername(),

	    	        user.getId().toString()));
	    
	    securityEventPublisher.publish(

	            new MfaAuthenticationCompletedEvent(

	                    user.getUsername(),

	                    user.getId().toString()));

	    handleAuthenticationSuccess(user);

	    RefreshToken session =

	            createSession(

	                    user,

	                    servletRequest);

	    String accessToken =

	            generateAccessToken(

	                    user,

	                    session);

	    return buildLoginResponse(

	            accessToken,

	            session);
	}
	
	private void verifyOtp(

	        User user,

	        PendingAuthentication pending,

	        String otp) {

	    VerifyOtpRequest request =

	            new VerifyOtpRequest(

	                    user.getId(),
	                    otp,
	                    pending.getMfaType(),
	                    user.getUsername());

	    switch (pending.getMfaType()) {

	        case EMAIL ->

	                emailOtpService.verify(request);

	        case SMS ->

	                smsOtpService.verify(request);

	        default ->

	                throw new BusinessException(

	                        "Unsupported MFA type.");
	    }
	}
	
	@Transactional
	public OtpResponse resendOtp(

	        ResendAuthenticationOtpRequest request) {

	    PendingAuthentication pending =

	            pendingService.validateForResend(
	                    request.challengeToken());
	    	    
	    User user =
	            userRepository.findById(
	                    pending.getUserId())
	                    .orElseThrow();

	    ResendOtpRequest otpRequest =

	            new ResendOtpRequest(

	                    user.getId(),
	                    
	                    pending.getMfaType(),
	                    
	                    resolveRecipient(user),
	                    
	            		user.getUsername());

	    OtpResponse response =  switch (pending.getMfaType()) {

	        case EMAIL ->
	                emailOtpService.resend(otpRequest);

	        case SMS ->
	                smsOtpService.resend(otpRequest);

	    	        default ->

	    	                throw new BusinessException(

	    	                        "Unsupported MFA type.");
	    	 };	    

	    	 pending.setResendCount(
	    		        pending.getResendCount() + 1);

	    		pending.setLastOtpSentAt(
	    		        LocalDateTime.now());

	    		pendingRepository.save(pending);

	    		return response;
	}
}


