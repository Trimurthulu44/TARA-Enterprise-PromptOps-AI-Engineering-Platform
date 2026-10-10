package com.tara.org.service;

import com.tara.common.context.TenantContextHolder;
import com.tara.common.exception.ResourceNotFoundException;
import com.tara.org.dto.OrgDtos;
import com.tara.org.entity.Application;
import com.tara.org.repository.ApplicationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ApplicationService {

    private final ApplicationRepository applicationRepository;

    public ApplicationService(ApplicationRepository applicationRepository) {
        this.applicationRepository = applicationRepository;
    }

    public List<Application> getApplications() {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        return applicationRepository.findByOrganizationId(orgId);
    }

    public List<Application> getApplicationsByProject(UUID projectId) {
        return applicationRepository.findByProjectId(projectId);
    }

    public Application getApplicationById(UUID id) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        return applicationRepository.findById(id)
            .filter(a -> a.getOrganizationId().equals(orgId))
            .orElseThrow(() -> new ResourceNotFoundException("Application", id));
    }

    @Transactional
    public Application createApplication(OrgDtos.CreateApplicationRequest request) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        UUID userId = TenantContextHolder.getContext().flatMap(c -> java.util.Optional.ofNullable(c.userId())).orElse(null);

        String slug = request.getName().toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "");

        Application app = Application.builder()
            .projectId(request.getProjectId())
            .name(request.getName().trim())
            .slug(slug)
            .description(request.getDescription())
            .appType(request.getAppType() != null ? request.getAppType() : "RAG")
            .environment(request.getEnvironment() != null ? request.getEnvironment() : "DEVELOPMENT")
            .latencySlaMs(request.getLatencySlaMs() != null ? request.getLatencySlaMs() : 2000)
            .costBudgetMonthly(request.getCostBudgetMonthly())
            .createdBy(userId)
            .build();
        app.setOrganizationId(orgId);

        return applicationRepository.save(app);
    }
}
