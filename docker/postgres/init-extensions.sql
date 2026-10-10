-- TARA Enterprise Database Initialization Extensions
-- Ensures pgvector and cryptographic / UUID extensions are present

CREATE EXTENSION IF NOT EXISTS "uuid-ossp";
CREATE EXTENSION IF NOT EXISTS "pgcrypto";
CREATE EXTENSION IF NOT EXISTS "vector";

-- Grant privileges
GRANT ALL ON SCHEMA public TO tara_user;
