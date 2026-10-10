package com.tara.prompt.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "prompt_templates")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PromptTemplate extends BaseTenantEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false)
    private String slug;

    @Column(name = "description")
    private String description;

    @Builder.Default
    @Column(name = "category")
    private String category = "SNIPPET";

    @Column(name = "template_body", nullable = false, columnDefinition = "text")
    private String templateBody;

    @Builder.Default
    @Column(name = "input_variables", columnDefinition = "text")
    private String inputVariables = "[]";

    @Builder.Default
    @Column(name = "is_global", nullable = false)
    private Boolean isGlobal = false;

    @Column(name = "created_by")
    private UUID createdBy;
}
