package com.hms.identity.entity;


import com.hms.common.BaseEntity;
import com.hms.identity.enums.UserStatus;
import com.hms.notification.mfa.enums.MfaType;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(
        name = "users",
        schema = "identity_schema"
)
public class User extends BaseEntity {

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(
            name = "password_hash",
            nullable = false
    )
    private String passwordHash;

    @Column(
            name = "first_name",
            nullable = false
    )
    private String firstName;

    @Column(
            name = "last_name",
            nullable = false
    )
    private String lastName;

    @Enumerated(EnumType.STRING)
    private UserStatus status;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            schema = "identity_schema",
            name = "user_roles",
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();
    // getters and setters
    
    @Column(name = "password_changed_at")
    private LocalDateTime passwordChangedAt;

    @Column(name = "password_expires_at")
    private LocalDateTime passwordExpiresAt;
   
    @Column(nullable = false)
    @Builder.Default
    private Integer failedLoginAttempts = 0;

    @Column(nullable = false)
    @Builder.Default
    private Boolean accountLocked = false;

    private LocalDateTime lockedAt;

    private LocalDateTime lockExpiresAt;
    
    @Column(name = "mfa_enabled")
    @Builder.Default
    private Boolean mfaEnabled = false;

    @Enumerated(EnumType.STRING)
    @Column(name = "mfa_type")
    @Builder.Default
    private MfaType mfaType = MfaType.NONE;

    @Column(name = "mfa_secret")
    private String mfaSecret;
    
}