package com.hms.identity.password.entity;

import java.time.LocalDateTime;

import com.hms.common.BaseEntity;
import com.hms.identity.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "password_reset_tokens",
        schema = "identity_schema"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordResetToken extends BaseEntity {

    @Column(
            nullable = false,
            unique = true,
            length = 100
    )
    private String token;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(nullable = false)
    private LocalDateTime expiresAt;

    @Column(nullable = false)
    private boolean used;

    private LocalDateTime usedAt;
}