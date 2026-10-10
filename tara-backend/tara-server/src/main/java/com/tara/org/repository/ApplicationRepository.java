package com.tara.org.repository;

import com.tara.org.entity.Application;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, UUID> {
    List<Application> findByProjectId(UUID projectId);
    List<Application> findByOrganizationId(UUID organizationId);
    Optional<Application> findByProjectIdAndSlug(UUID projectId, String slug);
}
