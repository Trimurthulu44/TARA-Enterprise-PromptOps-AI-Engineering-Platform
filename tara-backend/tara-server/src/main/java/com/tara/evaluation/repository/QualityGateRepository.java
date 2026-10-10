package com.tara.evaluation.repository;

import com.tara.evaluation.entity.QualityGate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface QualityGateRepository extends JpaRepository<QualityGate, UUID> {
    List<QualityGate> findByOrganizationId(UUID organizationId);
    List<QualityGate> findByProjectId(UUID projectId);
    Optional<QualityGate> findFirstByProjectIdAndIsActiveTrue(UUID projectId);
}
