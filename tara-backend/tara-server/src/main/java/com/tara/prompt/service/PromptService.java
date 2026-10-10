package com.tara.prompt.service;

import com.tara.common.context.TenantContextHolder;
import com.tara.common.exception.ResourceNotFoundException;
import com.tara.common.exception.ValidationException;
import com.tara.prompt.dto.PromptDtos;
import com.tara.prompt.entity.DeploymentHistory;
import com.tara.prompt.entity.Prompt;
import com.tara.prompt.entity.PromptVersion;
import com.tara.prompt.repository.DeploymentHistoryRepository;
import com.tara.prompt.repository.PromptRepository;
import com.tara.prompt.repository.PromptVersionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class PromptService {

    private final PromptRepository promptRepository;
    private final PromptVersionRepository promptVersionRepository;
    private final DeploymentHistoryRepository deploymentHistoryRepository;
    private final TemplateEngineService templateEngineService;

    public PromptService(
        PromptRepository promptRepository,
        PromptVersionRepository promptVersionRepository,
        DeploymentHistoryRepository deploymentHistoryRepository,
        TemplateEngineService templateEngineService
    ) {
        this.promptRepository = promptRepository;
        this.promptVersionRepository = promptVersionRepository;
        this.deploymentHistoryRepository = deploymentHistoryRepository;
        this.templateEngineService = templateEngineService;
    }

    public List<Prompt> getPrompts(UUID projectId, String search) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        if (projectId != null) {
            return promptRepository.findByProjectId(projectId);
        }
        if (search != null && !search.isBlank()) {
            return promptRepository.searchPrompts(orgId, search);
        }
        return promptRepository.findByOrganizationId(orgId);
    }

    public Prompt getPromptById(UUID promptId) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        return promptRepository.findById(promptId)
            .filter(p -> p.getOrganizationId().equals(orgId))
            .orElseThrow(() -> new ResourceNotFoundException("Prompt", promptId));
    }

    public List<PromptVersion> getVersionHistory(UUID promptId) {
        getPromptById(promptId); // Validate tenant access
        return promptVersionRepository.findByPromptIdOrderByCreatedAtDesc(promptId);
    }

    public PromptVersion getVersion(UUID promptId, String versionSemver) {
        getPromptById(promptId);
        return promptVersionRepository.findByPromptIdAndVersionSemver(promptId, versionSemver)
            .orElseThrow(() -> new ResourceNotFoundException("PromptVersion " + versionSemver + " for prompt", promptId));
    }

    @Transactional
    public Prompt createPrompt(PromptDtos.CreatePromptRequest request) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        UUID userId = TenantContextHolder.getContext().flatMap(c -> java.util.Optional.ofNullable(c.userId())).orElse(null);

        String slug = request.getName().toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "");

        Prompt prompt = Prompt.builder()
            .projectId(request.getProjectId())
            .name(request.getName().trim())
            .slug(slug)
            .description(request.getDescription())
            .category(request.getCategory() != null ? request.getCategory() : "GENERAL")
            .tags(request.getTags() != null ? request.getTags() : "")
            .createdBy(userId)
            .build();
        prompt.setOrganizationId(orgId);
        prompt = promptRepository.save(prompt);

        // Compute Token Estimate for User Template + System Prompt
        int tokenEst = templateEngineService.estimateTokens(
            (request.getSystemPrompt() != null ? request.getSystemPrompt() : "") + " " + request.getUserPromptTemplate()
        );

        // Create initial semantic version v1.0.0
        PromptVersion initialVersion = PromptVersion.builder()
            .promptId(prompt.getId())
            .versionSemver("v1.0.0")
            .commitMessage("Initial prompt creation")
            .systemPrompt(request.getSystemPrompt() != null ? request.getSystemPrompt() : "")
            .userPromptTemplate(request.getUserPromptTemplate())
            .variableSchema(request.getVariableSchema() != null ? request.getVariableSchema() : "[]")
            .modelProvider(request.getModelProvider())
            .modelName(request.getModelName())
            .temperature(request.getTemperature())
            .tokenCountEstimate(tokenEst)
            .status("PRODUCTION")
            .createdBy(userId)
            .build();
        initialVersion.setOrganizationId(orgId);
        initialVersion = promptVersionRepository.save(initialVersion);

        // Point current production to v1.0.0
        prompt.setCurrentProductionVersionId(initialVersion.getId());
        return promptRepository.save(prompt);
    }

    @Transactional
    public PromptVersion createVersion(UUID promptId, PromptDtos.CreatePromptVersionRequest request) {
        Prompt prompt = getPromptById(promptId);
        UUID orgId = prompt.getOrganizationId();
        UUID userId = TenantContextHolder.getContext().flatMap(c -> java.util.Optional.ofNullable(c.userId())).orElse(null);

        // Derive next semver
        List<PromptVersion> existing = promptVersionRepository.findByPromptIdOrderByCreatedAtDesc(promptId);
        String nextSemver = computeNextSemver(existing);

        int tokenEst = templateEngineService.estimateTokens(
            (request.getSystemPrompt() != null ? request.getSystemPrompt() : "") + " " + request.getUserPromptTemplate()
        );

        PromptVersion version = PromptVersion.builder()
            .promptId(prompt.getId())
            .versionSemver(nextSemver)
            .commitMessage(request.getCommitMessage())
            .systemPrompt(request.getSystemPrompt() != null ? request.getSystemPrompt() : "")
            .userPromptTemplate(request.getUserPromptTemplate())
            .variableSchema(request.getVariableSchema() != null ? request.getVariableSchema() : "[]")
            .modelProvider(request.getModelProvider() != null ? request.getModelProvider() : "OPENAI")
            .modelName(request.getModelName() != null ? request.getModelName() : "gpt-4o")
            .temperature(request.getTemperature())
            .topP(request.getTopP())
            .maxTokens(request.getMaxTokens() != null ? request.getMaxTokens() : 2048)
            .tokenCountEstimate(tokenEst)
            .status("DRAFT")
            .createdBy(userId)
            .build();
        version.setOrganizationId(orgId);

        return promptVersionRepository.save(version);
    }

    @Transactional
    public Prompt promoteVersion(UUID promptId, PromptDtos.PromoteVersionRequest request) {
        Prompt prompt = getPromptById(promptId);
        PromptVersion targetVersion = getVersion(promptId, request.getVersionSemver());
        UUID userId = TenantContextHolder.getContext().flatMap(c -> java.util.Optional.ofNullable(c.userId())).orElse(null);

        UUID previousVersionId = null;
        String env = request.getEnvironment().toUpperCase(Locale.ROOT);

        if ("PRODUCTION".equals(env)) {
            previousVersionId = prompt.getCurrentProductionVersionId();
            prompt.setCurrentProductionVersionId(targetVersion.getId());
            targetVersion.setStatus("PRODUCTION");
        } else if ("STAGING".equals(env)) {
            previousVersionId = prompt.getCurrentStagingVersionId();
            prompt.setCurrentStagingVersionId(targetVersion.getId());
            targetVersion.setStatus("STAGING");
        } else {
            throw new ValidationException("Environment must be either STAGING or PRODUCTION");
        }

        promptVersionRepository.save(targetVersion);
        Prompt updatedPrompt = promptRepository.save(prompt);

        DeploymentHistory history = DeploymentHistory.builder()
            .promptId(promptId)
            .promptVersionId(targetVersion.getId())
            .environment(env)
            .previousVersionId(previousVersionId)
            .deploymentType("PROMOTION")
            .deploymentNotes(request.getNotes())
            .deployedBy(userId)
            .build();
        history.setOrganizationId(prompt.getOrganizationId());
        deploymentHistoryRepository.save(history);

        return updatedPrompt;
    }

    @Transactional
    public Prompt rollback(UUID promptId, PromptDtos.RollbackRequest request) {
        Prompt prompt = getPromptById(promptId);
        PromptVersion targetVersion = getVersion(promptId, request.getTargetVersionSemver());
        UUID userId = TenantContextHolder.getContext().flatMap(c -> java.util.Optional.ofNullable(c.userId())).orElse(null);

        String env = request.getEnvironment().toUpperCase(Locale.ROOT);
        UUID previousVersionId = null;

        if ("PRODUCTION".equals(env)) {
            previousVersionId = prompt.getCurrentProductionVersionId();
            prompt.setCurrentProductionVersionId(targetVersion.getId());
        } else if ("STAGING".equals(env)) {
            previousVersionId = prompt.getCurrentStagingVersionId();
            prompt.setCurrentStagingVersionId(targetVersion.getId());
        } else {
            throw new ValidationException("Environment must be either STAGING or PRODUCTION");
        }

        Prompt updatedPrompt = promptRepository.save(prompt);

        DeploymentHistory history = DeploymentHistory.builder()
            .promptId(promptId)
            .promptVersionId(targetVersion.getId())
            .environment(env)
            .previousVersionId(previousVersionId)
            .deploymentType("ROLLBACK")
            .deploymentNotes(request.getReason())
            .deployedBy(userId)
            .build();
        history.setOrganizationId(prompt.getOrganizationId());
        deploymentHistoryRepository.save(history);

        return updatedPrompt;
    }

    public PromptDtos.RenderedPromptResponse renderPrompt(PromptDtos.RenderPromptRequest request) {
        Prompt prompt = getPromptById(request.getPromptId());
        PromptVersion version;

        String target = request.getVersion();
        if (target == null || target.equalsIgnoreCase("production")) {
            if (prompt.getCurrentProductionVersionId() == null) {
                version = promptVersionRepository.findFirstByPromptIdOrderByCreatedAtDesc(prompt.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("No versions found for prompt", prompt.getId()));
            } else {
                version = promptVersionRepository.findById(prompt.getCurrentProductionVersionId())
                    .orElseThrow(() -> new ResourceNotFoundException("Production version not found", prompt.getId()));
            }
        } else if (target.equalsIgnoreCase("staging")) {
            if (prompt.getCurrentStagingVersionId() == null) {
                throw new ValidationException("No staging version currently configured for this prompt");
            }
            version = promptVersionRepository.findById(prompt.getCurrentStagingVersionId())
                .orElseThrow(() -> new ResourceNotFoundException("Staging version not found", prompt.getId()));
        } else {
            version = getVersion(prompt.getId(), target);
        }

        String renderedUser = templateEngineService.render(version.getUserPromptTemplate(), request.getVariables());
        String renderedSystem = templateEngineService.render(version.getSystemPrompt(), request.getVariables());

        int tokenEst = templateEngineService.estimateTokens(renderedSystem + " " + renderedUser);

        return PromptDtos.RenderedPromptResponse.builder()
            .promptId(prompt.getId())
            .promptName(prompt.getName())
            .versionSemver(version.getVersionSemver())
            .systemPrompt(renderedSystem)
            .renderedUserPrompt(renderedUser)
            .estimatedTokens(tokenEst)
            .modelProvider(version.getModelProvider())
            .modelName(version.getModelName())
            .build();
    }

    private String computeNextSemver(List<PromptVersion> versions) {
        if (versions.isEmpty()) {
            return "v1.0.0";
        }
        String latest = versions.get(0).getVersionSemver(); // e.g. "v1.2.3"
        try {
            String clean = latest.startsWith("v") ? latest.substring(1) : latest;
            String[] parts = clean.split("\\.");
            int major = Integer.parseInt(parts[0]);
            int minor = Integer.parseInt(parts[1]);
            int patch = Integer.parseInt(parts[2]);
            return String.format("v%d.%d.%d", major, minor + 1, 0);
        } catch (Exception e) {
            return "v" + (versions.size() + 1) + ".0.0";
        }
    }
}
