-- ============================================================================
-- V6__init_analytics_audit_notifications.sql
-- TARA PromptOps Platform: Observability, Metrics Rollups, Audit Logs, Webhooks
-- ============================================================================

-- ----------------------------------------------------------------------------
-- 1. Analytics Rollups (Time-Series Aggregations for Fast Dashboard Querying)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS analytics_rollups (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    project_id UUID REFERENCES projects(id) ON DELETE CASCADE,
    prompt_id UUID REFERENCES prompts(id) ON DELETE CASCADE,
    prompt_version_id UUID REFERENCES prompt_versions(id) ON DELETE CASCADE,
    bucket_timestamp TIMESTAMPTZ NOT NULL,
    interval_type VARCHAR(20) NOT NULL CHECK (interval_type IN ('HOUR', 'DAY', 'MONTH')),
    total_requests INT NOT NULL DEFAULT 0,
    successful_requests INT NOT NULL DEFAULT 0,
    failed_requests INT NOT NULL DEFAULT 0,
    total_tokens BIGINT NOT NULL DEFAULT 0,
    prompt_tokens BIGINT NOT NULL DEFAULT 0,
    completion_tokens BIGINT NOT NULL DEFAULT 0,
    avg_latency_ms NUMERIC(8, 2) NOT NULL DEFAULT 0.00,
    p50_latency_ms INT NOT NULL DEFAULT 0,
    p95_latency_ms INT NOT NULL DEFAULT 0,
    p99_latency_ms INT NOT NULL DEFAULT 0,
    total_cost_usd NUMERIC(12, 6) NOT NULL DEFAULT 0.000000,
    avg_evaluation_score NUMERIC(5, 4) DEFAULT 0.0000,
    security_violations_count INT NOT NULL DEFAULT 0,
    CONSTRAINT uq_analytics_rollup UNIQUE (organization_id, prompt_version_id, bucket_timestamp, interval_type)
);

CREATE INDEX idx_analytics_rollups_org_bucket ON analytics_rollups(organization_id, bucket_timestamp);
CREATE INDEX idx_analytics_rollups_prompt_ver ON analytics_rollups(prompt_version_id);

-- ----------------------------------------------------------------------------
-- 2. Audit Logs (Immutable Append-Only Enterprise Audit Trail)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS audit_logs (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    workspace_id UUID REFERENCES workspaces(id) ON DELETE SET NULL,
    actor_id UUID REFERENCES users(id) ON DELETE SET NULL,
    actor_email VARCHAR(255) NOT NULL,
    action VARCHAR(100) NOT NULL, -- e.g. "PROMPT_VERSION_CREATED", "PROMPT_PROMOTED_PRODUCTION"
    resource_type VARCHAR(100) NOT NULL, -- e.g. "PROMPT", "PROMPT_VERSION", "API_KEY"
    resource_id VARCHAR(255) NOT NULL,
    diff_json JSONB DEFAULT '{}'::jsonb,
    ip_address VARCHAR(45),
    user_agent TEXT,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_audit_logs_org_created ON audit_logs(organization_id, created_at DESC);
CREATE INDEX idx_audit_logs_resource ON audit_logs(resource_type, resource_id);
CREATE INDEX idx_audit_logs_actor ON audit_logs(actor_id);

-- ----------------------------------------------------------------------------
-- 3. Notifications (In-App Alerting for Quality & Security Incidents)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS notifications (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title VARCHAR(255) NOT NULL,
    message TEXT NOT NULL,
    notification_type VARCHAR(50) NOT NULL CHECK (notification_type IN ('QUALITY_GATE_FAILED', 'SECURITY_BREACH_DETECTED', 'DEPLOYMENT_COMPLETED', 'QUOTA_WARNING', 'SYSTEM_UPDATE')),
    severity VARCHAR(50) NOT NULL DEFAULT 'INFO' CHECK (severity IN ('INFO', 'WARNING', 'ERROR', 'CRITICAL')),
    link_url TEXT,
    is_read BOOLEAN NOT NULL DEFAULT FALSE,
    read_at TIMESTAMPTZ,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_notifications_user_read ON notifications(user_id, is_read);
CREATE INDEX idx_notifications_org ON notifications(organization_id);

-- ----------------------------------------------------------------------------
-- 4. Webhook Subscriptions & Delivery History
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS webhook_subscriptions (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    name VARCHAR(255) NOT NULL,
    target_url TEXT NOT NULL,
    secret_hash VARCHAR(255) NOT NULL, -- Used to compute HMAC-SHA256 signature in X-Tara-Signature
    event_types TEXT[] NOT NULL DEFAULT ARRAY['evaluation.failed', 'security.violation', 'prompt.promoted']::TEXT[],
    is_active BOOLEAN NOT NULL DEFAULT TRUE,
    consecutive_failures INT NOT NULL DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_webhook_subscriptions_org ON webhook_subscriptions(organization_id);

CREATE TABLE IF NOT EXISTS webhook_deliveries (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    subscription_id UUID NOT NULL REFERENCES webhook_subscriptions(id) ON DELETE CASCADE,
    event_type VARCHAR(100) NOT NULL,
    payload JSONB NOT NULL,
    status_code INT,
    response_body TEXT,
    duration_ms INT NOT NULL DEFAULT 0,
    is_success BOOLEAN NOT NULL DEFAULT FALSE,
    error_message TEXT,
    attempted_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX idx_webhook_deliveries_sub ON webhook_deliveries(subscription_id);
CREATE INDEX idx_webhook_deliveries_attempted ON webhook_deliveries(attempted_at DESC);
