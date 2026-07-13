package com.hms.identity.authentication.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.hms.identity.authentication.entity.PendingAuthentication;
import com.hms.identity.authentication.enums.PendingAuthenticationStatus;

public interface PendingAuthenticationRepository
        extends JpaRepository<PendingAuthentication, UUID> {

    Optional<PendingAuthentication>
    findByChallengeToken(String challengeToken);

    Optional<PendingAuthentication>
    findTopByUserIdOrderByCreatedAtDesc(UUID userId);

    List<PendingAuthentication>
    findByStatus(PendingAuthenticationStatus status);

    void deleteByUserId(UUID userId);

    List<PendingAuthentication>
    findByExpiresAtBefore(LocalDateTime time);

    Optional<PendingAuthentication>
    findByChallengeTokenAndStatus(
            String challengeToken,
            PendingAuthenticationStatus status);
    
    boolean existsByUserIdAndStatus(
            UUID userId,
            PendingAuthenticationStatus status);
   
    List<PendingAuthentication>
    findByStatusAndExpiresAtBefore(
            PendingAuthenticationStatus status,
            LocalDateTime now);
   
    Optional<PendingAuthentication> findByUserId(UUID userId);
    
	void deleteByUserIdAndStatus(UUID userId, PendingAuthenticationStatus status);
}