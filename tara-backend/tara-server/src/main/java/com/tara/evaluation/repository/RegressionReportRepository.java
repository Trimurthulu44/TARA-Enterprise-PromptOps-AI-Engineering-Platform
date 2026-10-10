package com.tara.evaluation.repository;

import com.tara.evaluation.entity.RegressionReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface RegressionReportRepository extends JpaRepository<RegressionReport, UUID> {
    List<RegressionReport> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);
    List<RegressionReport> findByProjectIdOrderByCreatedAtDesc(UUID projectId);
}
