package com.tara.auth.repository;

import com.tara.auth.entity.UserWorkspaceRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserWorkspaceRoleRepository extends JpaRepository<UserWorkspaceRole, UUID> {
    List<UserWorkspaceRole> findByUserId(UUID userId);
    Optional<UserWorkspaceRole> findByUserIdAndWorkspaceId(UUID userId, UUID workspaceId);
    Optional<UserWorkspaceRole> findFirstByUserId(UUID userId);
}
