package com.tara.auth.entity;

import com.tara.common.entity.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

@Entity
@Table(name = "users")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class User extends BaseEntity {

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "password_hash", nullable = false)
    private String passwordHash;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "avatar_url")
    private String avatarUrl;

    @Builder.Default
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Builder.Default
    @Column(name = "is_email_verified", nullable = false)
    private Boolean isEmailVerified = false;

    @Builder.Default
    @Column(name = "mfa_enabled", nullable = false)
    private Boolean mfaEnabled = false;

    @Column(name = "mfa_secret")
    private String mfaSecret;
}
