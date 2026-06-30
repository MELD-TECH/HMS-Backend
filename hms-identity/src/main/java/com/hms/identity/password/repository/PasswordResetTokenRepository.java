package com.hms.identity.password.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.*;
import org.springframework.transaction.annotation.Transactional;

import com.hms.identity.password.entity.PasswordResetToken;

public interface PasswordResetTokenRepository
        extends JpaRepository<PasswordResetToken, UUID> {

    Optional<PasswordResetToken>
    findByToken(String token);

    @Modifying
    @Transactional
    @Query("""
            delete
            from PasswordResetToken p
            where p.expiresAt < :now
            """)
    void deleteExpired(
            LocalDateTime now
    );

    List<PasswordResetToken> findByUserId(UUID userId);
   
    @Modifying
    @Transactional
    @Query("""
    update PasswordResetToken t
    set t.used = true
    where t.user.id = :userId
    and t.used = false
    """)
    void expireUnusedTokens(UUID userId);
    
    @Modifying
    @Query("""
    update PasswordResetToken t
    set t.used = true,
        t.usedAt = CURRENT_TIMESTAMP
    where t.user.id = :userId
    and t.used = false
    """)
    void expireAll(UUID userId);
}