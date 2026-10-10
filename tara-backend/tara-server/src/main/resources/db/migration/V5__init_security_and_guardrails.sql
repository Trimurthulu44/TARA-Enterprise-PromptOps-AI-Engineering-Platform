-- ============================================================================
-- V5__init_security_and_guardrails.sql
-- TARA PromptOps Platform: Security Guardrails, Injection & Leakage Detection, PII
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. Security Policies (Tenant Guardrail Configuration)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS security_policies (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    enable_prompt_injection_detection BOOLEAN NOT NULL DEFAULT TRUE,
    enable_leakage_detection BOOLEAN NOT NULL DEFAULT TRUE,
    enable_pii_masking BOOLEAN NOT NULL DEFAULT TRUE,
    enable_unsafe_output_check BOOLEAN NOT NULL DEFAULT TRUE,
    severity_threshold VARCHAR(50) NOT NULL DEFAULT 'MEDIUM' CHECK (severity_threshold IN ('LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    action_on_violation VARCHAR(50) NOT NULL DEFAULT 'BLOCK_AND_ALERT' CHECK (action_on_violation IN ('LOG_ONLY', 'ALERT_ONLY', 'BLOCK_AND_ALERT', 'MASK_AND_PROCEED')),
    custom_forbidden_regexes TEXT[] DEFAULT ARRAY[]::TEXT[],
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_security_policies_org ON security_policies(organization_id);

-- ----------------------------------------------------------------------------
-- 2. Security Scans & Reports (Audit of Prompt & Output Security)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS security_scans (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
    prompt_version_id UUID REFERENCES prompt_versions(id) ON DELETE SET NULL,
    scan_target_type VARCHAR(50) NOT NULL CHECK (scan_target_type IN ('PROMPT_TEMPLATE', 'RENDERED_INPUT', 'MODEL_OUTPUT')),
    scanned_text TEXT NOT NULL,
    is_safe BOOLEAN NOT NULL DEFAULT TRUE,
    risk_severity VARCHAR(50) NOT NULL DEFAULT 'NONE' CHECK (risk_severity IN ('NONE', 'LOW', 'MEDIUM', 'HIGH', 'CRITICAL')),
    injection_confidence NUMERIC(5, 4) DEFAULT 0.0000, -- 0.0 to 1.0
    leakage_confidence NUMERIC(5, 4) DEFAULT 0.0000,   -- 0.0 to 1.0
    pii_entities_detected JSONB DEFAULT '[]'::jsonb,   -- Array of {entity_type, text_snippet, start, end}
    violations_detected JSONB DEFAULT '[]'::jsonb,     -- Array of {rule_name, reason, severity}
    remediation_recommendations TEXT,
    scanned_by UUID REFERENCES users(id) ON DELETE SET NULL,
    scanned_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_security_scans_org ON security_scans(organization_id);
CREATE INDEX idx_security_scans_prompt_ver ON security_scans(prompt_version_id);
CREATE INDEX idx_security_scans_risk ON security_scans(risk_severity);
CREATE INDEX idx_security_scans_is_safe ON security_scans(is_safe);

-- ----------------------------------------------------------------------------
-- 3. Canary Tokens (Dynamic Tokens Embedded into System Prompts)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS canary_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    prompt_version_id UUID NOT NULL REFERENCES prompt_versions(id) ON DELETE CASCADE,
    token_value VARCHAR(128) NOT NULL UNIQUE,
    description VARCHAR(255),
    triggered_count INT NOT NULL DEFAULT 0,
    last_triggered_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_canary_tokens_org ON canary_tokens(organization_id);
CREATE INDEX idx_canary_tokens_val ON canary_tokens(token_value);
