package com.hms.identity.session.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.hms.events.security.SessionRevokedAllEvent;
import com.hms.events.security.SessionRevokedEvent;
import com.hms.events.security.publisher.SecurityEventPublisher;
import com.hms.identity.session.dto.SessionResponse;
import com.hms.identity.session.entity.RefreshToken;
import com.hms.identity.session.mapper.SessionMapper;
import com.hms.identity.session.repository.RefreshTokenRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class SessionService {

    private final RefreshTokenRepository repository;
    
    private final SecurityEventPublisher securityEventPublisher;

    public List<SessionResponse> sessions(
            String username){

        return repository
        		.findActiveSessions(username)
                .stream()
                .map(SessionMapper::toResponse)
                .toList();
    }

    public void revoke(UUID id){

        RefreshToken session =
                repository.findById(id)
                        .orElseThrow();

        session.setRevoked(true);

        session.setRevokedAt(
                LocalDateTime.now());

        repository.save(session);
        
		securityEventPublisher.publish(new SessionRevokedEvent(session.getUser().getUsername(),
				session.getUser().getId().toString()));
        
    }

    @Transactional
    public void revokeAll(String username) {

        UUID userId =
                repository.findByUserUsername(username)
                        .stream()
                        .findFirst()
                        .orElseThrow()
                        .getUser()
                        .getId();

        repository.revokeAll(userId);
        
        securityEventPublisher.publish(new SessionRevokedAllEvent(username, userId.toString()));
    }

}