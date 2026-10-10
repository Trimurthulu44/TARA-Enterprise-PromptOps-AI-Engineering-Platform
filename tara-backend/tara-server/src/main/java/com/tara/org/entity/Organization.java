package com.tara.org.entity;

import com.tara.common.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "organizations")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Organization extends BaseEntity {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "slug", nullable = false, unique = true)
    private String slug;

    @Builder.Default
    @Column(name = "tier", nullable = false)
    private String tier = "ENTERPRISE";

    @Builder.Default
    @Column(name = "status", nullable = false)
    private String status = "ACTIVE";

    @Builder.Default
    @Column(name = "max_prompts_quota", nullable = false)
    private Integer maxPromptsQuota = 5000;

    @Builder.Default
    @Column(name = "max_evals_monthly_quota", nullable = false)
    private Integer maxEvalsMonthlyQuota = 50000;

    @Builder.Default
    @Column(name = "max_members_quota", nullable = false)
    private Integer maxMembersQuota = 100;

    @Builder.Default
    @Column(name = "settings", columnDefinition = "text")
    private String settings = "{}";
}
