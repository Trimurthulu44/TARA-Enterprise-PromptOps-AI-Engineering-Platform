package com.tara.org.repository;

import com.tara.org.entity.ApiKey;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApiKeyRepository extends JpaRepository<ApiKey, UUID> {
    Optional<ApiKey> findByKeyHashAndIsRevokedFalse(String keyHash);
    List<ApiKey> findByOrganizationId(UUID organizationId);
    List<ApiKey> findByWorkspaceId(UUID workspaceId);
}
