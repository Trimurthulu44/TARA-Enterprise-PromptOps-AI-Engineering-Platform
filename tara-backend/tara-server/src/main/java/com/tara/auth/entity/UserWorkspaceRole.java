package com.tara.auth.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "user_workspace_roles")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserWorkspaceRole {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "user_id", nullable = false)
    private UUID userId;

    @Column(name = "workspace_id", nullable = false)
    private UUID workspaceId;

    @Column(name = "organization_id", nullable = false)
    private UUID organizationId;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Builder.Default
    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt = Instant.now();
}
