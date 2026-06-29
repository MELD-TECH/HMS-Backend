package com.hms.identity.session.repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.hms.identity.session.entity.RefreshToken;

@Repository
public interface RefreshTokenRepository
extends JpaRepository<RefreshToken, UUID> {

    Optional<RefreshToken> findByToken(String token);

    List<RefreshToken> findByUserId(UUID userId);

    List<RefreshToken> findByUserUsername(String username);

    @Query("""
        select r
        from RefreshToken r
        where
            r.revoked = false
        and
            r.expiresAt > CURRENT_TIMESTAMP
    """)
    List<RefreshToken> findActiveSessions();
    
    @Modifying
    @Query("""
        delete from RefreshToken r
        where r.revoked = true
           or r.expiresAt < CURRENT_TIMESTAMP
    """)
    void deleteExpiredAndRevoked();
    
    List<RefreshToken> findByUserIdAndRevokedFalse(UUID userId);
    
    long deleteByUserId(UUID userId);
    
    @Modifying
    @Query("""
    update RefreshToken r
    set r.revoked = true
    where r.user.id = :userId
    """)
    void revokeAll(UUID userId);

    @Modifying
    @Query("""
    update RefreshToken r
    set r.revoked = true
    where r.id = :sessionId
    """)
    void revoke(UUID sessionId);
    
    @Modifying
    @Transactional
    @Query("""
        update RefreshToken r
           set r.expiresAt = :expiresAt
         where r.token = :token
    """)
    void expireToken(
            String token,
            LocalDateTime expiresAt);
    
    @Query("""
    		select r
    		from RefreshToken r
    		where r.user.username = :username
    		  and r.revoked = false
    		  and r.expiresAt > CURRENT_TIMESTAMP
    		""")
    		List<RefreshToken> findActiveSessions(String username);

}