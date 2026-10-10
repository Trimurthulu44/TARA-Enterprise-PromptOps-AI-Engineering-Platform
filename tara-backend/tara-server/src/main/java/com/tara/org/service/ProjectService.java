package com.tara.org.service;

import com.tara.common.context.TenantContextHolder;
import com.tara.common.exception.ResourceNotFoundException;
import com.tara.org.dto.OrgDtos;
import com.tara.org.entity.Project;
import com.tara.org.repository.ProjectRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class ProjectService {

    private final ProjectRepository projectRepository;

    public ProjectService(ProjectRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    public List<Project> getProjects() {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        return projectRepository.findByOrganizationId(orgId);
    }

    public Project getProjectById(UUID id) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        return projectRepository.findById(id)
            .filter(p -> p.getOrganizationId().equals(orgId))
            .orElseThrow(() -> new ResourceNotFoundException("Project", id));
    }

    @Transactional
    public Project createProject(OrgDtos.CreateProjectRequest request) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        UUID workspaceId = TenantContextHolder.getRequiredWorkspaceId();
        UUID userId = TenantContextHolder.getContext().flatMap(c -> java.util.Optional.ofNullable(c.userId())).orElse(null);

        String slug = request.getName().toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "");

        Project project = Project.builder()
            .workspaceId(workspaceId)
            .name(request.getName().trim())
            .slug(slug)
            .description(request.getDescription())
            .createdBy(userId)
            .build();
        project.setOrganizationId(orgId);

        return projectRepository.save(project);
    }
}
