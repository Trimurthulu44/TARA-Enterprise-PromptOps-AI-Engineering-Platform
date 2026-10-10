package com.tara.org.service;

import com.tara.common.context.TenantContextHolder;
import com.tara.common.exception.ResourceNotFoundException;
import com.tara.org.dto.OrgDtos;
import com.tara.org.entity.Organization;
import com.tara.org.entity.Workspace;
import com.tara.org.repository.OrganizationRepository;
import com.tara.org.repository.WorkspaceRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final WorkspaceRepository workspaceRepository;

    public OrganizationService(OrganizationRepository organizationRepository, WorkspaceRepository workspaceRepository) {
        this.organizationRepository = organizationRepository;
        this.workspaceRepository = workspaceRepository;
    }

    public Organization getCurrentOrganization() {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        return organizationRepository.findById(orgId)
            .orElseThrow(() -> new ResourceNotFoundException("Organization", orgId));
    }

    public List<Workspace> getWorkspaces() {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        return workspaceRepository.findByOrganizationId(orgId);
    }

    @Transactional
    public Workspace createWorkspace(OrgDtos.CreateWorkspaceRequest request) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        String slug = request.getName().toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "");

        Workspace workspace = Workspace.builder()
            .name(request.getName().trim())
            .slug(slug)
            .description(request.getDescription())
            .isDefault(false)
            .build();
        workspace.setOrganizationId(orgId);
        return workspaceRepository.save(workspace);
    }
}
