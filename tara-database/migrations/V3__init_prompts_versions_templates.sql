-- ============================================================================
-- V3__init_prompts_versions_templates.sql
-- TARA PromptOps Platform: Prompts, Semantic Versions, Templates & Deployments
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. Prompts (Logical Catalog Entity)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS prompts (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(100) DEFAULT 'GENERAL',
    tags TEXT[] DEFAULT ARRAY[]::TEXT[],
    current_production_version_id UUID, -- Foreign key added below
    current_staging_version_id UUID,    -- Foreign key added below
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_prompt_project_slug UNIQUE (project_id, slug)
);

CREATE INDEX idx_prompts_org_id ON prompts(organization_id);
CREATE INDEX idx_prompts_project_id ON prompts(project_id);
CREATE INDEX idx_prompts_tags ON prompts USING gin(tags);

-- ----------------------------------------------------------------------------
-- 2. Prompt Versions (Immutable Semantic Versioning: v1.0.0, v1.1.0, etc.)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS prompt_versions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    prompt_id UUID NOT NULL REFERENCES prompts(id) ON DELETE CASCADE,
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    version_semver VARCHAR(50) NOT NULL, -- e.g. "v1.0.0", "v1.1.0"
    commit_message TEXT NOT NULL DEFAULT 'Initial version',
    system_prompt TEXT NOT NULL DEFAULT '',
    user_prompt_template TEXT NOT NULL,
    variable_schema JSONB NOT NULL DEFAULT '[]'::jsonb, -- Array of {name, type, required, default_value, description}
    model_provider VARCHAR(50) NOT NULL DEFAULT 'OPENAI' CHECK (model_provider IN ('OPENAI', 'ANTHROPIC', 'GOOGLE', 'MISTRAL', 'COHERE', 'CUSTOM_LLM')),
    model_name VARCHAR(100) NOT NULL DEFAULT 'gpt-4o',
    temperature NUMERIC(3, 2) NOT NULL DEFAULT 0.70 CHECK (temperature >= 0.00 AND temperature <= 2.00),
    top_p NUMERIC(3, 2) NOT NULL DEFAULT 1.00 CHECK (top_p >= 0.00 AND top_p <= 1.00),
    max_tokens INT NOT NULL DEFAULT 2048,
    presence_penalty NUMERIC(3, 2) NOT NULL DEFAULT 0.00,
    frequency_penalty NUMERIC(3, 2) NOT NULL DEFAULT 0.00,
    stop_sequences TEXT[] DEFAULT ARRAY[]::TEXT[],
    token_count_estimate INT NOT NULL DEFAULT 0,
    status VARCHAR(50) NOT NULL DEFAULT 'DRAFT' CHECK (status IN ('DRAFT', 'STAGING', 'PRODUCTION', 'ARCHIVED', 'DEPRECATED')),
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_prompt_version_semver UNIQUE (prompt_id, version_semver)
);

CREATE INDEX idx_prompt_versions_prompt_id ON prompt_versions(prompt_id);
CREATE INDEX idx_prompt_versions_org_id ON prompt_versions(organization_id);
CREATE INDEX idx_prompt_versions_status ON prompt_versions(status);

-- Circular Foreign Keys linking prompts to current deployment versions
ALTER TABLE prompts 
    ADD CONSTRAINT fk_prompts_prod_version 
    FOREIGN KEY (current_production_version_id) 
    REFERENCES prompt_versions(id) ON DELETE SET NULL;

ALTER TABLE prompts 
    ADD CONSTRAINT fk_prompts_staging_version 
    FOREIGN KEY (current_staging_version_id) 
    REFERENCES prompt_versions(id) ON DELETE SET NULL;

-- ----------------------------------------------------------------------------
-- 3. Prompt Templates (Reusable Macros and Blocks)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS prompt_templates (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description TEXT,
    category VARCHAR(100) DEFAULT 'SNIPPET',
    template_body TEXT NOT NULL,
    input_variables JSONB NOT NULL DEFAULT '[]'::jsonb,
    is_global BOOLEAN NOT NULL DEFAULT FALSE,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_prompt_template_org_slug UNIQUE (organization_id, slug)
);

CREATE INDEX idx_prompt_templates_org_id ON prompt_templates(organization_id);

-- ----------------------------------------------------------------------------
-- 4. Deployment History & Audit (Deployment Governance & Rollback)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS deployment_history (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    prompt_id UUID NOT NULL REFERENCES prompts(id) ON DELETE CASCADE,
    prompt_version_id UUID NOT NULL REFERENCES prompt_versions(id) ON DELETE RESTRICT,
    environment VARCHAR(50) NOT NULL CHECK (environment IN ('STAGING', 'PRODUCTION')),
    previous_version_id UUID REFERENCES prompt_versions(id) ON DELETE SET NULL,
    deployment_type VARCHAR(50) NOT NULL DEFAULT 'PROMOTION' CHECK (deployment_type IN ('PROMOTION', 'ROLLBACK', 'EMERGENCY_OVERRIDE')),
    deployment_notes TEXT,
    deployed_by UUID REFERENCES users(id) ON DELETE SET NULL,
    deployed_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_deployment_history_prompt ON deployment_history(prompt_id);
CREATE INDEX idx_deployment_history_org ON deployment_history(organization_id);
