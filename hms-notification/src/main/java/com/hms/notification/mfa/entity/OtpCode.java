package com.hms.notification.mfa.entity;

import java.time.LocalDateTime;
import java.util.UUID;

import com.hms.common.BaseEntity;
import com.hms.notification.mfa.enums.MfaType;
import com.hms.notification.mfa.enums.OtpStatus;

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
        name = "otp_codes",
        schema = "notification_schema"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpCode extends BaseEntity {

    @Column(nullable = false)
    private UUID userId;

    @Column(nullable = false)
    private String recipient;

    @Column(nullable = false)
    private String code;

    @Enumerated(EnumType.STRING)
    private MfaType type;

    @Enumerated(EnumType.STRING)
    private OtpStatus status;

    private Integer attempts;

    private Integer resendCount;

    private LocalDateTime expiresAt;

    private LocalDateTime verifiedAt;
    
    private LocalDateTime lastResentAt;

}