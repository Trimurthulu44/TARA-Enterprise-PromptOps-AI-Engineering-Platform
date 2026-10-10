package com.tara.evaluation.entity;

import com.tara.common.entity.BaseTenantEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "golden_test_cases")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GoldenTestCase extends BaseTenantEntity {

    @Column(name = "dataset_id", nullable = false)
    private UUID datasetId;

    @Column(name = "test_case_name")
    private String testCaseName;

    @Column(name = "input_variables", nullable = false, columnDefinition = "text")
    private String inputVariables; // JSON map of key-value variables

    @Column(name = "expected_output", columnDefinition = "text")
    private String expectedOutput;

    @Column(name = "ground_truth_metadata", columnDefinition = "text")
    private String groundTruthMetadata;

    @Column(name = "assertion_rules", columnDefinition = "text")
    private String assertionRules;

    @Builder.Default
    @Column(name = "weight")
    private BigDecimal weight = BigDecimal.ONE;
}
