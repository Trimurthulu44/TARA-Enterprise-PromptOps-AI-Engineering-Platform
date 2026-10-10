package com.tara.evaluation.repository;

import com.tara.evaluation.entity.EvaluationResult;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface EvaluationResultRepository extends JpaRepository<EvaluationResult, UUID> {
    List<EvaluationResult> findByEvaluationRunId(UUID evaluationRunId);
}
