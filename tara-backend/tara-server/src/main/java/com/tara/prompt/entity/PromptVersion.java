package com.tara.prompt.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "prompt_versions")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptVersion extends BaseTenantEntity {

    @Column(name = "prompt_id", nullable = false)
    private UUID promptId;

    @Column(name = "version_semver", nullable = false)
    private String versionSemver; // e.g. "v1.0.0"

    @Builder.Default
    @Column(name = "commit_message", nullable = false)
    private String commitMessage = "Initial commit";

    @Builder.Default
    @Column(name = "system_prompt", columnDefinition = "text")
    private String systemPrompt = "";

    @Column(name = "user_prompt_template", nullable = false, columnDefinition = "text")
    private String userPromptTemplate;

    @Builder.Default
    @Column(name = "variable_schema", columnDefinition = "text")
    private String variableSchema = "[]"; // JSON array of variable metadata

    @Builder.Default
    @Column(name = "model_provider", nullable = false)
    private String modelProvider = "OPENAI";

    @Builder.Default
    @Column(name = "model_name", nullable = false)
    private String modelName = "gpt-4o";

    @Builder.Default
    @Column(name = "temperature")
    private BigDecimal temperature = BigDecimal.valueOf(0.70);

    @Builder.Default
    @Column(name = "top_p")
    private BigDecimal topP = BigDecimal.valueOf(1.00);

    @Builder.Default
    @Column(name = "max_tokens")
    private Integer maxTokens = 2048;

    @Builder.Default
    @Column(name = "presence_penalty")
    private BigDecimal presencePenalty = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "frequency_penalty")
    private BigDecimal frequencyPenalty = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "token_count_estimate")
    private Integer tokenCountEstimate = 0;

    @Builder.Default
    @Column(name = "status", nullable = false)
    private String status = "DRAFT"; // DRAFT, STAGING, PRODUCTION, ARCHIVED

    @Column(name = "created_by")
    private UUID createdBy;
}
