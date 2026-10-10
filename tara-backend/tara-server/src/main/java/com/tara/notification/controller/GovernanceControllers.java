package com.tara.notification.controller;

import com.tara.common.context.TenantContextHolder;
import com.tara.common.dto.ApiResponse;
import com.tara.notification.entity.AuditLog;
import com.tara.notification.entity.Notification;
import com.tara.notification.entity.WebhookSubscription;
import com.tara.notification.repository.WebhookSubscriptionRepository;
import com.tara.notification.service.AuditLogService;
import com.tara.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1")
@Tag(name = "Governance & Notifications", description = "Immutable audit trails, notifications, and webhooks")
public class GovernanceControllers {

    @RestController
    @RequestMapping("/api/v1/audit-logs")
    public static class AuditLogController {
        private final AuditLogService auditLogService;

        public AuditLogController(AuditLogService auditLogService) {
            this.auditLogService = auditLogService;
        }

        @GetMapping
        @Operation(summary = "Get immutable audit trail records for current tenant")
        public ResponseEntity<ApiResponse<List<AuditLog>>> getAuditLogs() {
            return ResponseEntity.ok(ApiResponse.ok(auditLogService.getAuditLogs()));
        }
    }

    @RestController
    @RequestMapping("/api/v1/notifications")
    public static class NotificationController {
        private final NotificationService notificationService;

        public NotificationController(NotificationService notificationService) {
            this.notificationService = notificationService;
        }

        @GetMapping
        @Operation(summary = "Get current user and workspace notifications")
        public ResponseEntity<ApiResponse<List<Notification>>> getNotifications() {
            return ResponseEntity.ok(ApiResponse.ok(notificationService.getNotifications()));
        }

        @PatchMapping("/{id}/read")
        @Operation(summary = "Mark a notification as read")
        public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable UUID id) {
            notificationService.markAsRead(id);
            return ResponseEntity.ok(ApiResponse.ok("Notification marked as read", null));
        }
    }

    @RestController
    @RequestMapping("/api/v1/webhooks")
    public static class WebhookController {
        private final WebhookSubscriptionRepository webhookRepository;

        public WebhookController(WebhookSubscriptionRepository webhookRepository) {
            this.webhookRepository = webhookRepository;
        }

        @GetMapping
        @Operation(summary = "List webhook subscriptions")
        public ResponseEntity<ApiResponse<List<WebhookSubscription>>> getWebhooks() {
            UUID orgId = TenantContextHolder.getRequiredOrganizationId();
            return ResponseEntity.ok(ApiResponse.ok(webhookRepository.findByOrganizationId(orgId)));
        }

        @PostMapping
        @Operation(summary = "Create a new webhook subscription")
        public ResponseEntity<ApiResponse<WebhookSubscription>> createWebhook(@Valid @RequestBody WebhookSubscription sub) {
            UUID orgId = TenantContextHolder.getRequiredOrganizationId();
            sub.setOrganizationId(orgId);
            WebhookSubscription saved = webhookRepository.save(sub);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.created("Webhook subscription created successfully", saved));
        }
    }
}
