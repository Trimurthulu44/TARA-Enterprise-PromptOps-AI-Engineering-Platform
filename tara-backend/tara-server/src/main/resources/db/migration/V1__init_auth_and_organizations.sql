-- ============================================================================
-- V1__init_auth_and_organizations.sql
-- TARA PromptOps Platform: Organizations, Workspaces, Users, Roles & Permissions
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- ----------------------------------------------------------------------------
-- 1. Organizations (Multi-Tenant Root)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS organizations (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL UNIQUE,
    tier VARCHAR(50) NOT NULL DEFAULT 'ENTERPRISE' CHECK (tier IN ('FREE', 'STARTER', 'GROWTH', 'ENTERPRISE')),
    status VARCHAR(50) NOT NULL DEFAULT 'ACTIVE' CHECK (status IN ('ACTIVE', 'SUSPENDED', 'PENDING_VERIFICATION')),
    max_prompts_quota INT NOT NULL DEFAULT 5000,
    max_evals_monthly_quota INT NOT NULL DEFAULT 50000,
    max_members_quota INT NOT NULL DEFAULT 100,
    settings JSONB NOT NULL DEFAULT '{}'::jsonb,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_organizations_slug ON organizations(slug);
CREATE INDEX idx_organizations_status ON organizations(status);

-- ----------------------------------------------------------------------------
-- 2. Workspaces (Sub-Tenant Organizational Boundaries)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS workspaces (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    slug VARCHAR(100) NOT NULL,
    description TEXT,
    is_default BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_workspace_org_slug UNIQUE (organization_id, slug)
);

CREATE INDEX idx_workspaces_organization_id ON workspaces(organization_id);

-- ----------------------------------------------------------------------------
-- 3. Users
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS users (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    full_name VARCHAR(255) NOT NULL,
    avatar_url TEXT,
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    is_email_verified BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_enabled BOOLEAN NOT NULL DEFAULT FALSE,
    mfa_secret VARCHAR(255),
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_users_email ON users(email);

-- ----------------------------------------------------------------------------
-- 4. Roles & Permissions (Enterprise RBAC)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(50) NOT NULL UNIQUE,
    description TEXT,
    is_system_role BOOLEAN NOT NULL DEFAULT TRUE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS permissions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL UNIQUE,
    resource VARCHAR(50) NOT NULL,
    action VARCHAR(50) NOT NULL,
    description TEXT
);

CREATE TABLE IF NOT EXISTS role_permissions (
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE CASCADE,
    permission_id UUID NOT NULL REFERENCES permissions(id) ON DELETE CASCADE,
    PRIMARY KEY (role_id, permission_id)
);

-- Seed System Roles
INSERT INTO roles (id, name, description, is_system_role) VALUES
    ('00000000-0000-0000-0000-000000000001', 'ADMIN', 'Tenant Administrator with full access to workspace, users, and resources', TRUE),
    ('00000000-0000-0000-0000-000000000002', 'DEVELOPER', 'Prompt Engineer & Developer with write access to prompts, datasets, and evaluations', TRUE),
    ('00000000-0000-0000-0000-000000000003', 'VIEWER', 'Read-only access to prompts, analytics, evaluation reports, and logs', TRUE)
ON CONFLICT (name) DO NOTHING;

-- Seed Standard Permissions
INSERT INTO permissions (name, resource, action, description) VALUES
    ('org:admin', 'ORGANIZATION', 'ADMIN', 'Manage organization settings and billing'),
    ('users:manage', 'USER', 'MANAGE', 'Invite, remove, and manage workspace users'),
    ('prompts:read', 'PROMPT', 'READ', 'View prompts and version history'),
    ('prompts:write', 'PROMPT', 'WRITE', 'Create and edit prompts and templates'),
    ('prompts:deploy', 'PROMPT', 'DEPLOY', 'Promote prompts to staging or production'),
    ('evaluations:run', 'EVALUATION', 'RUN', 'Execute evaluation runs and regression tests'),
    ('security:scan', 'SECURITY', 'SCAN', 'Execute security and prompt injection scans'),
    ('apikeys:manage', 'API_KEY', 'MANAGE', 'Generate, rotate, and revoke tenant API keys'),
    ('analytics:view', 'ANALYTICS', 'READ', 'Access analytics metrics and reports')
ON CONFLICT (name) DO NOTHING;

-- ----------------------------------------------------------------------------
-- 5. User Workspace Roles (Tenant Membership & Binding)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS user_workspace_roles (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    workspace_id UUID NOT NULL REFERENCES workspaces(id) ON DELETE CASCADE,
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    role_id UUID NOT NULL REFERENCES roles(id) ON DELETE RESTRICT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_user_workspace UNIQUE (user_id, workspace_id)
);

CREATE INDEX idx_user_workspace_roles_user ON user_workspace_roles(user_id);
CREATE INDEX idx_user_workspace_roles_workspace ON user_workspace_roles(workspace_id);
CREATE INDEX idx_user_workspace_roles_org ON user_workspace_roles(organization_id);

-- ----------------------------------------------------------------------------
-- 6. Refresh Tokens & Active Sessions
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS refresh_tokens (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    token_hash VARCHAR(255) NOT NULL UNIQUE,
    device_info TEXT,
    ip_address VARCHAR(45),
    expires_at TIMESTAMPTZ NOT NULL,
    is_revoked BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_refresh_tokens_user_id ON refresh_tokens(user_id);
CREATE INDEX idx_refresh_tokens_hash ON refresh_tokens(token_hash);

CREATE TABLE IF NOT EXISTS user_sessions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    session_token VARCHAR(255) NOT NULL UNIQUE,
    ip_address VARCHAR(45),
    user_agent TEXT,
    last_active_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    expires_at TIMESTAMPTZ NOT NULL,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_user_sessions_user_id ON user_sessions(user_id);
CREATE INDEX idx_user_sessions_token ON user_sessions(session_token);
