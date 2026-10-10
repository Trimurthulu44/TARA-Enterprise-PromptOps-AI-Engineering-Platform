-- ============================================================================
-- V7__init_pgvector_prompt_embeddings.sql
-- TARA PromptOps Platform: pgvector Semantic Search & Prompt Embeddings
-- ============================================================================

CREATE EXTENSION IF NOT EXISTS vector;

-- ----------------------------------------------------------------------------
-- 1. Prompt Embeddings (Vector Storage for Semantic Clustering & Search)
-- ----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS prompt_embeddings (
    id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    organization_id UUID NOT NULL REFERENCES organizations(id) ON DELETE CASCADE,
    prompt_version_id UUID NOT NULL REFERENCES prompt_versions(id) ON DELETE CASCADE,
    embedding vector(384) NOT NULL, -- 384 dimensions from all-MiniLM-L6-v2
    content_hash VARCHAR(64) NOT NULL, -- SHA-256 of system_prompt + user_prompt_template
    model_used VARCHAR(100) NOT NULL DEFAULT 'all-MiniLM-L6-v2',
    created_at TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT uq_prompt_version_embedding UNIQUE (prompt_version_id)
);

CREATE INDEX idx_prompt_embeddings_org ON prompt_embeddings(organization_id);

-- High-performance HNSW index for sub-millisecond approximate nearest neighbor search
CREATE INDEX IF NOT EXISTS idx_prompt_embeddings_hnsw 
    ON prompt_embeddings 
    USING hnsw (embedding vector_cosine_ops)
    WITH (m = 16, ef_construction = 64);

-- ----------------------------------------------------------------------------
-- 2. Semantic Search Stored Procedure
-- ----------------------------------------------------------------------------
CREATE OR REPLACE FUNCTION find_similar_prompts(
    p_organization_id UUID,
    p_query_embedding vector(384),
    p_match_threshold FLOAT DEFAULT 0.65,
    p_match_count INT DEFAULT 10
)
RETURNS TABLE (
    prompt_id UUID,
    prompt_name VARCHAR,
    prompt_slug VARCHAR,
    prompt_version_id UUID,
    version_semver VARCHAR,
    similarity FLOAT
)
LANGUAGE plpgsql
AS $$
BEGIN
    RETURN QUERY
    SELECT 
        p.id AS prompt_id,
        p.name AS prompt_name,
        p.slug AS prompt_slug,
        pv.id AS prompt_version_id,
        pv.version_semver,
        (1 - (pe.embedding <=> p_query_embedding))::FLOAT AS similarity
    FROM prompt_embeddings pe
    JOIN prompt_versions pv ON pe.prompt_version_id = pv.id
    JOIN prompts p ON pv.prompt_id = p.id
    WHERE pe.organization_id = p_organization_id
      AND (1 - (pe.embedding <=> p_query_embedding)) >= p_match_threshold
    ORDER BY pe.embedding <=> p_query_embedding ASC
    LIMIT p_match_count;
END;
$$;
