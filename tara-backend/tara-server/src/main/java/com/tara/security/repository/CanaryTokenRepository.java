package com.tara.security.repository;

import com.tara.security.entity.CanaryToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CanaryTokenRepository extends JpaRepository<CanaryToken, UUID> {
    List<CanaryToken> findByOrganizationId(UUID organizationId);
    List<CanaryToken> findByPromptVersionId(UUID promptVersionId);
    Optional<CanaryToken> findByTokenValue(String tokenValue);
}
