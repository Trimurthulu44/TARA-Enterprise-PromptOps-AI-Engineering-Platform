package com.tara.prompt.repository;

import com.tara.prompt.entity.PromptVersion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromptVersionRepository extends JpaRepository<PromptVersion, UUID> {
    List<PromptVersion> findByPromptIdOrderByCreatedAtDesc(UUID promptId);
    Optional<PromptVersion> findByPromptIdAndVersionSemver(UUID promptId, String versionSemver);
    Optional<PromptVersion> findFirstByPromptIdOrderByCreatedAtDesc(UUID promptId);
}
