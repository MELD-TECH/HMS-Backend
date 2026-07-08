package com.hms.identity.session.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.common.exception.BusinessException;
import com.hms.common.exception.InvalidRefreshTokenException;
import com.hms.identity.entity.User;
import com.hms.identity.security.event.RefreshTokenCreatedEvent;
import com.hms.identity.security.event.RefreshTokenRevokedEvent;
import com.hms.identity.security.publisher.SecurityEventPublisher;
import com.hms.identity.session.entity.RefreshToken;
import com.hms.identity.session.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class RefreshTokenService {

    @Value("${security.jwt.refresh-expiration-days:30}")
    private long refreshDays;

    private final RefreshTokenRepository repository;
    
    private final SecurityEventPublisher securityEventPublisher;

    public RefreshToken create(
            User user,
            String deviceId,
            String deviceName,
            String ipAddress,
            String userAgent) {

        RefreshToken token =
                RefreshToken.builder()
                        .token(UUID.randomUUID().toString())
                        .user(user)
                        .expiresAt(
                                LocalDateTime.now()
                                        .plusDays(refreshDays))
                        .revoked(false)
                        .deviceId(deviceId)
                        .deviceName(deviceName)
                        .ipAddress(ipAddress)
                        .userAgent(userAgent)
                        .build();

       
		securityEventPublisher
				.publish(new RefreshTokenCreatedEvent(
						user.getUsername(), 
						user.getId().toString()));
		
        return repository.save(token);
    }

    @Transactional(readOnly = true)
    public RefreshToken validate(String token) {

        RefreshToken refresh =
                repository.findByToken(token)
                        .orElseThrow(
                                () ->
                                        new InvalidRefreshTokenException(
                                                "Invalid refresh token"));

        if (refresh.isRevoked()) {

            throw new InvalidRefreshTokenException(
                    "Refresh token revoked");
        }

        if (refresh.getExpiresAt().isBefore(LocalDateTime.now())) {

            throw new InvalidRefreshTokenException(
                    "Refresh token expired");
        }

        return refresh;
    }

    public RefreshToken rotate(
            String oldToken,
            String deviceId,
            String deviceName,
            String ip,
            String userAgent) {

        RefreshToken current =
                validate(oldToken);

        current.setRevoked(true);

        current.setRevokedAt(
                LocalDateTime.now());

        repository.save(current);

        return create(
                current.getUser(),
                deviceId,
                deviceName,
                ip,
                userAgent);
    }

    public void revoke(String token) {

        RefreshToken refresh =
                validate(token);

        refresh.setRevoked(true);

        refresh.setRevokedAt(
                LocalDateTime.now());

        repository.save(refresh);
        
        securityEventPublisher.publish(

                new RefreshTokenRevokedEvent(

                        refresh.getUser().getUsername(),

                        refresh.getId().toString()));
    }

    public void revoke(UUID sessionId) {

        RefreshToken refresh =
                repository.findById(sessionId)
                        .orElseThrow();

        refresh.setRevoked(true);

        refresh.setRevokedAt(
                LocalDateTime.now());

        repository.save(refresh);
        
        securityEventPublisher.publish(

                new RefreshTokenRevokedEvent(

                        refresh.getUser().getUsername(),

                        refresh.getId().toString()));
    }

    public void revokeAll(UUID userId) {

        repository.findByUserId(userId)
                .forEach(session -> {

                    session.setRevoked(true);

                    session.setRevokedAt(
                            LocalDateTime.now());

                });
    }

    @Transactional(readOnly = true)
    public List<RefreshToken> activeSessions(UUID userId){

        return repository.findByUserId(userId)
                .stream()
                .filter(s ->
                        !s.isRevoked())
                .toList();
    }
}
