package com.tara.evaluation.repository;

import com.tara.evaluation.entity.EvaluationRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvaluationRunRepository extends JpaRepository<EvaluationRun, UUID> {
    List<EvaluationRun> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);
    List<EvaluationRun> findByPromptVersionIdOrderByCreatedAtDesc(UUID promptVersionId);
    List<EvaluationRun> findByDatasetIdOrderByCreatedAtDesc(UUID datasetId);
}
