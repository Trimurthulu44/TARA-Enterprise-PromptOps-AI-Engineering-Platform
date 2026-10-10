package com.tara.prompt.repository;

import com.tara.prompt.entity.DeploymentHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface DeploymentHistoryRepository extends JpaRepository<DeploymentHistory, UUID> {
    List<DeploymentHistory> findByPromptIdOrderByDeployedAtDesc(UUID promptId);
}
