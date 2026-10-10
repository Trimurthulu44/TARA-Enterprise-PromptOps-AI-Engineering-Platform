-- ============================================================================
-- V4__init_golden_datasets_evaluations.sql
-- TARA PromptOps Platform: Golden Datasets, Evaluation Runs, LLM Judges & Quality Gates
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. Golden Datasets (Curated Test Suites for Prompt Quality)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS golden_datasets (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description TEXT,
    version VARCHAR(50) NOT NULL DEFAULT 'v1.0.0',
    total_test_cases INT NOT NULL DEFAULT 0,
    tags TEXT[] DEFAULT ARRAY[]::TEXT[],
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_dataset_project_slug UNIQUE (project_id, slug)
);

CREATE INDEX idx_golden_datasets_org_id ON golden_datasets(organization_id);
CREATE INDEX idx_golden_datasets_project_id ON golden_datasets(project_id);

-- ----------------------------------------------------------------------------
-- 2. Golden Test Cases
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS golden_test_cases (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    dataset_id UUID NOT NULL REFERENCES golden_datasets(id) ON DELETE CASCADE,
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    test_case_name VARCHAR(255),
    input_variables JSONB NOT NULL DEFAULT '{}'::jsonb, -- Key-value map of variable substitutes
    expected_output TEXT,                               -- Ground truth reference
    ground_truth_metadata JSONB DEFAULT '{}'::jsonb,   -- Context chunks, reference citations
    assertion_rules JSONB DEFAULT '[]'::jsonb,         -- Array of assertions: contains, not_contains, regex, json_schema
    weight NUMERIC(3, 2) NOT NULL DEFAULT 1.00,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_golden_test_cases_dataset ON golden_test_cases(dataset_id);
CREATE INDEX idx_golden_test_cases_org ON golden_test_cases(organization_id);

-- ----------------------------------------------------------------------------
-- 3. Evaluation Runs (Batch Test Executions)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS evaluation_runs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    prompt_version_id UUID NOT NULL REFERENCES prompt_versions(id) ON DELETE RESTRICT,
    dataset_id UUID NOT NULL REFERENCES golden_datasets(id) ON DELETE RESTRICT,
    run_name VARCHAR(255) NOT NULL,
    run_type VARCHAR(50) NOT NULL DEFAULT 'AUTOMATED_BENCHMARK' CHECK (run_type IN ('AUTOMATED_BENCHMARK', 'REGRESSION_CHECK', 'LLM_JUDGE', 'MANUAL_TEST')),
    status VARCHAR(50) NOT NULL DEFAULT 'PENDING' CHECK (status IN ('PENDING', 'RUNNING', 'COMPLETED', 'FAILED', 'CANCELLED')),
    judge_model VARCHAR(100) DEFAULT 'gpt-4o',
    total_cases INT NOT NULL DEFAULT 0,
    completed_cases INT NOT NULL DEFAULT 0,
    passed_cases INT NOT NULL DEFAULT 0,
    failed_cases INT NOT NULL DEFAULT 0,
    average_score NUMERIC(5, 4) DEFAULT 0.0000, -- 0.0000 to 1.0000
    p50_latency_ms INT DEFAULT 0,
    p95_latency_ms INT DEFAULT 0,
    p99_latency_ms INT DEFAULT 0,
    total_tokens_used INT DEFAULT 0,
    total_cost_usd NUMERIC(10, 6) DEFAULT 0.000000,
    error_message TEXT,
    initiated_by UUID REFERENCES users(id) ON DELETE SET NULL,
    started_at TIMESTAMPTZ,
    completed_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_evaluation_runs_org ON evaluation_runs(organization_id);
CREATE INDEX idx_evaluation_runs_prompt_ver ON evaluation_runs(prompt_version_id);
CREATE INDEX idx_evaluation_runs_dataset ON evaluation_runs(dataset_id);
CREATE INDEX idx_evaluation_runs_status ON evaluation_runs(status);

-- ----------------------------------------------------------------------------
-- 4. Evaluation Results (Granular Test Case Results)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS evaluation_results (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    evaluation_run_id UUID NOT NULL REFERENCES evaluation_runs(id) ON DELETE CASCADE,
    test_case_id UUID NOT NULL REFERENCES golden_test_cases(id) ON DELETE CASCADE,
    rendered_prompt TEXT NOT NULL,
    model_output TEXT,
    latency_ms INT NOT NULL DEFAULT 0,
    prompt_tokens INT NOT NULL DEFAULT 0,
    completion_tokens INT NOT NULL DEFAULT 0,
    cost_usd NUMERIC(10, 6) DEFAULT 0.000000,
    judge_score NUMERIC(5, 4) DEFAULT 0.0000, -- 0.0 to 1.0
    judge_reasoning TEXT,
    rubric_scores JSONB DEFAULT '{}'::jsonb, -- e.g. {"relevance": 0.95, "faithfulness": 0.90, "tone": 1.0}
    passed BOOLEAN NOT NULL DEFAULT TRUE,
    failure_reason TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_evaluation_results_run ON evaluation_results(evaluation_run_id);
CREATE INDEX idx_evaluation_results_test_case ON evaluation_results(test_case_id);

-- ----------------------------------------------------------------------------
-- 5. Quality Gates (Enterprise Deployment Constraints)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS quality_gates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    description TEXT,
    min_judge_score NUMERIC(5, 4) NOT NULL DEFAULT 0.8500,
    max_p95_latency_ms INT NOT NULL DEFAULT 2000,
    max_cost_per_run_usd NUMERIC(10, 4) NOT NULL DEFAULT 10.0000,
    require_zero_security_violations BOOLEAN NOT NULL DEFAULT TRUE,
    min_pass_rate_percentage NUMERIC(5, 2) NOT NULL DEFAULT 95.00,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_quality_gate_project_name UNIQUE (project_id, name)
);

CREATE INDEX idx_quality_gates_org ON quality_gates(organization_id);
CREATE INDEX idx_quality_gates_project ON quality_gates(project_id);

-- ----------------------------------------------------------------------------
-- 6. Regression Reports (Side-by-Side Version Comparison)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS regression_reports (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    dataset_id UUID NOT NULL REFERENCES golden_datasets(id) ON DELETE RESTRICT,
    candidate_version_id UUID NOT NULL REFERENCES prompt_versions(id) ON DELETE RESTRICT,
    baseline_version_id UUID NOT NULL REFERENCES prompt_versions(id) ON DELETE RESTRICT,
    candidate_run_id UUID REFERENCES evaluation_runs(id) ON DELETE SET NULL,
    baseline_run_id UUID REFERENCES evaluation_runs(id) ON DELETE SET NULL,
    candidate_avg_score NUMERIC(5, 4) NOT NULL,
    baseline_avg_score NUMERIC(5, 4) NOT NULL,
    delta_score NUMERIC(5, 4) NOT NULL, -- candidate - baseline
    candidate_p95_latency_ms INT NOT NULL,
    baseline_p95_latency_ms INT NOT NULL,
    delta_latency_ms INT NOT NULL,
    regression_detected BOOLEAN NOT NULL DEFAULT FALSE,
    quality_gate_passed BOOLEAN NOT NULL DEFAULT TRUE,
    summary_report JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_regression_reports_org ON regression_reports(organization_id);
CREATE INDEX idx_regression_reports_candidate ON regression_reports(candidate_version_id);
CREATE INDEX idx_regression_reports_baseline ON regression_reports(baseline_version_id);
