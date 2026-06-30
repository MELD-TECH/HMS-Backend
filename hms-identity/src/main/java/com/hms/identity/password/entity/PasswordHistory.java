package com.hms.identity.password.entity;

import java.time.LocalDateTime;

import com.hms.common.BaseEntity;
import com.hms.identity.entity.User;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(
        name = "password_history",
        schema = "identity_schema"
)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PasswordHistory extends BaseEntity {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(
            name = "user_id",
            nullable = false
    )
    private User user;

    @Column(
            nullable = false,
            length = 255
    )
    private String passwordHash;

    @Column(nullable = false)
    private LocalDateTime changedAt;
}