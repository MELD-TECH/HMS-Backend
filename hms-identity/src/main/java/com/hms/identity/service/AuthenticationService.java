package com.hms.identity.service;


import com.hms.audit.security.dto.AuditRequest;
import com.hms.audit.security.enums.AuditAction;
import com.hms.audit.security.enums.AuditModule;
import com.hms.audit.security.service.AuditService;
import com.hms.common.exception.BusinessException;
import com.hms.events.security.LoginFailureEvent;
import com.hms.events.security.LoginSuccessEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
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
import com.hms.security.service.JwtService;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

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
    
    private final AuditService auditService;
   
    private final AccountLockService accountLockService;
    
    private final SecurityEventPublisher securityEventPublisher;
    
    private final PasswordExpiryService passwordExpiryService;
    
	public LoginResponse login(
            LoginRequest request, HttpServletRequest servletRequest) {

		User user =
		        userRepository
		                .findByUsername(request.username())
		                .orElse(null);

		if (user != null) {

		    accountLockService.validate(user);

		}
		
		try {

		    authenticationManager.authenticate(
		            new UsernamePasswordAuthenticationToken(
		                    request.username(),
		                    request.password()));

		    passwordExpiryService.validate(user);
		    
		    loginAttemptService.loginSucceeded(request.username());
		    
		    securityEventPublisher.publish(

		            new LoginSuccessEvent(

		                    user.getUsername(),

		                    user.getId().toString()));

		}
		catch (BadCredentialsException ex) {

			if (user != null) {
			    loginAttemptService.loginFailed(request.username());
			    
			    securityEventPublisher.publish(

			            new LoginFailureEvent(

			                    user.getUsername(),

			                    user.getId().toString()));
			}

		    throw ex;
		}
		
        String ip =
                IpUtil.ip(servletRequest);

        String deviceName =
                DeviceUtil.deviceName(servletRequest);

        String userAgent =
                servletRequest.getHeader("User-Agent");

        String deviceId =
                UUID.randomUUID().toString();
        
        RefreshToken session =
                refreshTokenService.create(
                        user,
                        deviceId,
                        deviceName,
                        ip,
                        userAgent);

        
        String token =
                jwtService.generateToken(
                        request.username(),
                        session.getId()
                );

        return new LoginResponse(
                token,
                session.getToken(),
                session.getId(),
                "Bearer",
                jwtService.extractExpiration(token),
                jwtService.getRemainingSeconds(token)
        );
    }
	
	@Transactional
	public LoginResponse refresh(

	        RefreshTokenRequest request,

	        HttpServletRequest servletRequest) {

	    // Validate existing refresh token
	    RefreshToken existingSession =
	            refreshTokenService.validate(
	                    request.refreshToken());

	    User user =
	            existingSession.getUser();

	    // Rotate the refresh token
	    RefreshToken newSession =
	            refreshTokenService.rotate(

	                    request.refreshToken(),

	                    UUID.randomUUID().toString(),

	                    DeviceUtil.deviceName(servletRequest),

	                    IpUtil.ip(servletRequest),

	                    servletRequest.getHeader("User-Agent")
	            );

	    // Generate a new access token
	    String accessToken =
	            jwtService.generateToken(

	                    user.getUsername(),

	                    newSession.getId()
	            );

	    return new LoginResponse(

	            accessToken,

	            newSession.getToken(),
	            
	            newSession.getId(),

	            "Bearer",

	            jwtService.extractExpiration(
	                    accessToken),

	            jwtService.getRemainingSeconds(
	                    accessToken)
	    );
	}
	
	

}


