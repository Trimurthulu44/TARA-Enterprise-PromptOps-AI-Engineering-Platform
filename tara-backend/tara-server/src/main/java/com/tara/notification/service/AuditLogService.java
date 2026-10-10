package com.tara.notification.service;

import com.tara.common.context.TenantContextHolder;
import com.tara.notification.entity.AuditLog;
import com.tara.notification.repository.AuditLogRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class AuditLogService {

    private final AuditLogRepository auditLogRepository;

    public AuditLogService(AuditLogRepository auditLogRepository) {
        this.auditLogRepository = auditLogRepository;
    }

    public List<AuditLog> getAuditLogs() {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        return auditLogRepository.findByOrganizationIdOrderByCreatedAtDesc(orgId);
    }

    @Async
    public void recordAction(String action, String resourceType, String resourceId, String diffJson, String ipAddress, String userAgent) {
        UUID orgId = TenantContextHolder.getContext().flatMap(c -> java.util.Optional.ofNullable(c.organizationId())).orElse(null);
        UUID wsId = TenantContextHolder.getContext().flatMap(c -> java.util.Optional.ofNullable(c.workspaceId())).orElse(null);
        UUID userId = TenantContextHolder.getContext().flatMap(c -> java.util.Optional.ofNullable(c.userId())).orElse(null);
        String email = TenantContextHolder.getContext().map(c -> c.userEmail()).orElse("system@tara.ai");

        if (orgId != null) {
            AuditLog log = AuditLog.builder()
                .workspaceId(wsId)
                .actorId(userId)
                .actorEmail(email)
                .action(action)
                .resourceType(resourceType)
                .resourceId(resourceId)
                .diffJson(diffJson != null ? diffJson : "{}")
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();
            log.setOrganizationId(orgId);
            auditLogRepository.save(log);
        }
    }
}
