package com.tara.evaluation.repository;

import com.tara.evaluation.entity.GoldenTestCase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface GoldenTestCaseRepository extends JpaRepository<GoldenTestCase, UUID> {
    List<GoldenTestCase> findByDatasetId(UUID datasetId);
    long countByDatasetId(UUID datasetId);
}
