package com.tara.evaluation.repository;

import com.tara.evaluation.entity.GoldenDataset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface GoldenDatasetRepository extends JpaRepository<GoldenDataset, UUID> {
    List<GoldenDataset> findByOrganizationId(UUID organizationId);
    List<GoldenDataset> findByProjectId(UUID projectId);
    Optional<GoldenDataset> findByProjectIdAndSlug(UUID projectId, String slug);
}
