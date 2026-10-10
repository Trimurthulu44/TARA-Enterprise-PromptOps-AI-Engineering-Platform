package com.tara.prompt.repository;

import com.tara.prompt.entity.Prompt;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromptRepository extends JpaRepository<Prompt, UUID> {
    List<Prompt> findByOrganizationId(UUID organizationId);
    List<Prompt> findByProjectId(UUID projectId);
    Optional<Prompt> findByProjectIdAndSlug(UUID projectId, String slug);
    Optional<Prompt> findByOrganizationIdAndSlug(UUID organizationId, String slug);

    @Query("SELECT p FROM Prompt p WHERE p.organizationId = :orgId AND (:search IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :search, '%')) OR LOWER(p.slug) LIKE LOWER(CONCAT('%', :search, '%')))")
    List<Prompt> searchPrompts(@Param("orgId") UUID orgId, @Param("search") String search);
}
