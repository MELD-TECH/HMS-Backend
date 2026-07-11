package com.hms.notification.mfa.repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.hms.notification.mfa.entity.OtpCode;
import com.hms.notification.mfa.enums.MfaType;
import com.hms.notification.mfa.enums.OtpStatus;

public interface OtpRepository
extends JpaRepository<OtpCode, UUID> {

    Optional<OtpCode>

    findTopByUserIdAndStatusOrderByCreatedAtDesc(

            UUID userId,

            OtpStatus status);

    @Modifying

    @Query("""

            update OtpCode

               set status='EXPIRED'

             where expiresAt < CURRENT_TIMESTAMP

               and status='ACTIVE'

            """)

    int expireOtp();
    
    Optional<OtpCode> findTopByUserIdAndTypeAndStatusOrderByCreatedAtDesc(

            UUID userId,

            MfaType type,

            OtpStatus status);
    
	List<OtpCode> findByUserIdAndStatus(

			UUID userId,

			OtpStatus status);

}