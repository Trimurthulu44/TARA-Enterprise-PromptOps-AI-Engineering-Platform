package com.tara.prompt.repository;

import com.tara.prompt.entity.PromptTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PromptTemplateRepository extends JpaRepository<PromptTemplate, UUID> {
    List<PromptTemplate> findByOrganizationId(UUID organizationId);
    Optional<PromptTemplate> findByOrganizationIdAndSlug(UUID organizationId, String slug);
}
