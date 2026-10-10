-- ============================================================================
-- V2__init_projects_applications_apikeys.sql
-- TARA PromptOps Platform: Projects, Applications, and API Keys
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. Projects (Grouping of AI Workloads)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS projects (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    workspace_id UUID NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description TEXT,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_project_workspace_slug UNIQUE (workspace_id, slug)
);

CREATE INDEX idx_projects_organization_id ON projects(organization_id);
CREATE INDEX idx_projects_workspace_id ON projects(workspace_id);

-- ----------------------------------------------------------------------------
-- 2. Applications (Managed Target AI Systems)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS applications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id UUID NOT NULL REFERENCES projects(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description TEXT,
    app_type VARCHAR(50) NOT NULL DEFAULT 'RAG' CHECK (app_type IN ('RAG', 'AGENT', 'CHATBOT', 'COPILOT', 'CLASSIFICATION', 'EXTRACTION', 'SUMMARIZATION')),
    environment VARCHAR(50) NOT NULL DEFAULT 'DEVELOPMENT' CHECK (environment IN ('DEVELOPMENT', 'STAGING', 'PRODUCTION')),
    latency_sla_ms INT DEFAULT 2000,
    cost_budget_monthly NUMERIC(12, 4) DEFAULT 500.0000,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_app_project_slug UNIQUE (project_id, slug)
);

CREATE INDEX idx_applications_org_id ON applications(organization_id);
CREATE INDEX idx_applications_project_id ON applications(project_id);

-- ----------------------------------------------------------------------------
-- 3. API Keys (High-Security Programmatic Access for SDKs)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS api_keys (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    workspace_id UUID NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    application_id UUID REFERENCES applications(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    key_prefix VARCHAR(16) NOT NULL, -- e.g. "tara_live_"
    key_hash VARCHAR(255) NOT NULL UNIQUE, -- SHA-256 digest of the actual secret
    scopes TEXT[] NOT NULL DEFAULT ARRAY['prompts:read', 'evaluations:run']::TEXT[],
    rate_limit_per_minute INT NOT NULL DEFAULT 600,
    allowed_ip_cidrs TEXT[],
    expires_at TIMESTAMPTZ,
    last_used_at TIMESTAMPTZ,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    revoked_at TIMESTAMPTZ,
    revoked_reason TEXT,
    created_by UUID REFERENCES users(id) ON DELETE SET NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_api_keys_org_id ON api_keys(organization_id);
CREATE INDEX idx_api_keys_key_hash ON api_keys(key_hash);
CREATE INDEX idx_api_keys_prefix ON api_keys(key_prefix);
