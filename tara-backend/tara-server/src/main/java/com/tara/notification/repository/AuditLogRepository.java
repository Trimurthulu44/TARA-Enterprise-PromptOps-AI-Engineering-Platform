package com.tara.notification.repository;

import com.tara.notification.entity.AuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, UUID> {
    List<AuditLog> findByOrganizationIdOrderByCreatedAtDesc(UUID organizationId);
    List<AuditLog> findByResourceTypeAndResourceIdOrderByCreatedAtDesc(String resourceType, String resourceId);
}
