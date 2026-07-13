package com.hms.identity.authentication.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.hms.common.BaseEntity;
import com.hms.identity.authentication.enums.PendingAuthenticationStatus;
import com.hms.notification.mfa.enums.MfaType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(
        name = "pending_authentications",
        schema = "identity_schema"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PendingAuthentication
        extends BaseEntity {

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String username;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MfaType mfaType;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PendingAuthenticationStatus status;

    @Column(nullable = false, unique = true)
    private String challengeToken;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    private LocalDateTime completedAt;
    
    private LocalDateTime lastOtpSentAt;

    @Builder.Default
    @Column(nullable = false)
    private Integer resendCount = 0;

    private String ipAddress;

    private String userAgent;

}