package com.tara;

import com.tara.common.context.TenantContext;
import com.tara.common.context.TenantContextHolder;
import com.tara.prompt.dto.PromptDtos;
import com.tara.prompt.entity.Prompt;
import com.tara.prompt.entity.PromptVersion;
import com.tara.prompt.repository.DeploymentHistoryRepository;
import com.tara.prompt.repository.PromptRepository;
import com.tara.prompt.repository.PromptVersionRepository;
import com.tara.prompt.service.PromptService;
import com.tara.prompt.service.TemplateEngineService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PromptServiceTest {

    @Mock
    private PromptRepository promptRepository;

    @Mock
    private PromptVersionRepository promptVersionRepository;

    @Mock
    private DeploymentHistoryRepository deploymentHistoryRepository;

    @Mock
    private TemplateEngineService templateEngineService;

    @InjectMocks
    private PromptService promptService;

    private final UUID orgId = UUID.randomUUID();
    private final UUID workspaceId = UUID.randomUUID();
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        TenantContextHolder.setContext(TenantContext.of(orgId, workspaceId, userId, "admin@tara.ai", "ADMIN"));
    }

    @AfterEach
    void tearDown() {
        TenantContextHolder.clear();
    }

    @Test
    void testTemplateEngineVariableExtractionAndRendering() {
        TemplateEngineService engine = new TemplateEngineService();
        String template = "Hello {{customer_name}}, your order {{order_id}} is verified.";

        Set<String> vars = engine.extractVariables(template);
        assertEquals(2, vars.size());
        assertTrue(vars.contains("customer_name"));
        assertTrue(vars.contains("order_id"));

        Map<String, Object> values = Map.of(
            "customer_name", "Sarah",
            "order_id", "ORD-994"
        );

        String rendered = engine.render(template, values);
        assertEquals("Hello Sarah, your order ORD-994 is verified.", rendered);
    }

    @Test
    void testCreatePromptGeneratesInitialSemverVersion() {
        UUID projectId = UUID.randomUUID();
        PromptDtos.CreatePromptRequest request = PromptDtos.CreatePromptRequest.builder()
            .projectId(projectId)
            .name("Support Resolution Copilot")
            .userPromptTemplate("User Query: {{query}}")
            .systemPrompt("You are an AI assistant")
            .modelProvider("OPENAI")
            .modelName("gpt-4o")
            .temperature(BigDecimal.valueOf(0.70))
            .build();

        Prompt savedPrompt = Prompt.builder()
            .projectId(projectId)
            .name("Support Resolution Copilot")
            .slug("support-resolution-copilot")
            .build();
        savedPrompt.setId(UUID.randomUUID());
        savedPrompt.setOrganizationId(orgId);

        when(promptRepository.save(any(Prompt.class))).thenReturn(savedPrompt);
        when(templateEngineService.estimateTokens(anyString())).thenReturn(24);

        PromptVersion savedVersion = PromptVersion.builder()
            .promptId(savedPrompt.getId())
            .versionSemver("v1.0.0")
            .status("PRODUCTION")
            .build();
        savedVersion.setId(UUID.randomUUID());
        savedVersion.setOrganizationId(orgId);

        when(promptVersionRepository.save(any(PromptVersion.class))).thenReturn(savedVersion);

        Prompt result = promptService.createPrompt(request);

        assertNotNull(result);
        assertEquals("Support Resolution Copilot", result.getName());
        verify(promptRepository, atLeastOnce()).save(any(Prompt.class));
        verify(promptVersionRepository, times(1)).save(any(PromptVersion.class));
    }
}
