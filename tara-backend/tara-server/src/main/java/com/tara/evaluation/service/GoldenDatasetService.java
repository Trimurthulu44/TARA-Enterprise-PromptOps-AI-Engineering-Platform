package com.tara.evaluation.service;

import com.tara.common.context.TenantContextHolder;
import com.tara.common.exception.ResourceNotFoundException;
import com.tara.evaluation.dto.EvaluationDtos;
import com.tara.evaluation.entity.GoldenDataset;
import com.tara.evaluation.entity.GoldenTestCase;
import com.tara.evaluation.repository.GoldenDatasetRepository;
import com.tara.evaluation.repository.GoldenTestCaseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

@Service
public class GoldenDatasetService {

    private final GoldenDatasetRepository datasetRepository;
    private final GoldenTestCaseRepository testCaseRepository;

    public GoldenDatasetService(GoldenDatasetRepository datasetRepository, GoldenTestCaseRepository testCaseRepository) {
        this.datasetRepository = datasetRepository;
        this.testCaseRepository = testCaseRepository;
    }

    public List<GoldenDataset> getDatasets(UUID projectId) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        if (projectId != null) {
            return datasetRepository.findByProjectId(projectId);
        }
        return datasetRepository.findByOrganizationId(orgId);
    }

    public GoldenDataset getDatasetById(UUID id) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        return datasetRepository.findById(id)
            .filter(d -> d.getOrganizationId().equals(orgId))
            .orElseThrow(() -> new ResourceNotFoundException("GoldenDataset", id));
    }

    public List<GoldenTestCase> getTestCases(UUID datasetId) {
        getDatasetById(datasetId); // Validate access
        return testCaseRepository.findByDatasetId(datasetId);
    }

    @Transactional
    public GoldenDataset createDataset(EvaluationDtos.CreateDatasetRequest request) {
        UUID orgId = TenantContextHolder.getRequiredOrganizationId();
        UUID userId = TenantContextHolder.getContext().flatMap(c -> java.util.Optional.ofNullable(c.userId())).orElse(null);

        String slug = request.getName().toLowerCase(Locale.ROOT)
            .replaceAll("[^a-z0-9]+", "-")
            .replaceAll("^-|-$", "");

        GoldenDataset dataset = GoldenDataset.builder()
            .projectId(request.getProjectId())
            .name(request.getName().trim())
            .slug(slug)
            .description(request.getDescription())
            .tags(request.getTags())
            .totalTestCases(0)
            .createdBy(userId)
            .build();
        dataset.setOrganizationId(orgId);

        return datasetRepository.save(dataset);
    }

    @Transactional
    public GoldenTestCase addTestCase(UUID datasetId, EvaluationDtos.CreateTestCaseRequest request) {
        GoldenDataset dataset = getDatasetById(datasetId);

        GoldenTestCase testCase = GoldenTestCase.builder()
            .datasetId(datasetId)
            .testCaseName(request.getTestCaseName() != null ? request.getTestCaseName() : "Test Case #" + (dataset.getTotalTestCases() + 1))
            .inputVariables(request.getInputVariables())
            .expectedOutput(request.getExpectedOutput())
            .groundTruthMetadata(request.getGroundTruthMetadata())
            .assertionRules(request.getAssertionRules())
            .weight(request.getWeight() != null ? request.getWeight() : BigDecimal.ONE)
            .build();
        testCase.setOrganizationId(dataset.getOrganizationId());
        testCase = testCaseRepository.save(testCase);

        dataset.setTotalTestCases(dataset.getTotalTestCases() + 1);
        datasetRepository.save(dataset);

        return testCase;
    }

    @Transactional
    public int batchImport(UUID datasetId, EvaluationDtos.BatchImportTestCasesRequest request) {
        GoldenDataset dataset = getDatasetById(datasetId);
        int count = 0;
        for (EvaluationDtos.CreateTestCaseRequest tcReq : request.getTestCases()) {
            GoldenTestCase tc = GoldenTestCase.builder()
                .datasetId(datasetId)
                .testCaseName(tcReq.getTestCaseName() != null ? tcReq.getTestCaseName() : "Batch TC " + (++count))
                .inputVariables(tcReq.getInputVariables())
                .expectedOutput(tcReq.getExpectedOutput())
                .groundTruthMetadata(tcReq.getGroundTruthMetadata())
                .assertionRules(tcReq.getAssertionRules())
                .weight(tcReq.getWeight() != null ? tcReq.getWeight() : BigDecimal.ONE)
                .build();
            tc.setOrganizationId(dataset.getOrganizationId());
            testCaseRepository.save(tc);
        }

        dataset.setTotalTestCases((int) testCaseRepository.countByDatasetId(datasetId));
        datasetRepository.save(dataset);
        return request.getTestCases().size();
    }
}
