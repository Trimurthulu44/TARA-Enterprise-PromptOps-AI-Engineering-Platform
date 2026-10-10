# TARA: Enterprise PromptOps & AI Engineering Platform

## Directory Structure Overview

```
TaraEx/
├── docker/                             # Containerization & Orchestration
│   ├── docker-compose.yml              # Complete Multi-Service Stack
│   ├── nginx/                          # Nginx Gateway & SSL Configuration
│   │   ├── nginx.conf
│   │   └── conf.d/default.conf
│   └── postgres/                       # Postgres + pgvector initialization
│       └── init-extensions.sql
├── tara-backend/                       # Java 21 / Spring Boot Microservices Suite
│   ├── pom.xml                         # Root Parent POM (Spring Boot 3.3.x)
│   ├── tara-common/                    # Shared Kernel: TenantContext, Exceptions, DTOs, Security
│   ├── tara-gateway/                   # API Gateway (Routing, Rate Limiting, Tenant propagation)
│   ├── tara-auth-service/              # Auth, JWT, RBAC, API Keys, Sessions
│   ├── tara-org-service/               # Organization, Workspace, Projects, Apps
│   ├── tara-prompt-service/            # Prompts, Versions, Templates, Rollback
│   ├── tara-eval-service/              # Golden Datasets, Batch Eval, LLM Judge Runner, Quality Gates
│   ├── tara-security-service/          # Guardrails, Injection Detection, PII Masking
│   ├── tara-analytics-service/         # Observability, Time-Series Rollups, Cost & Token Tracking
│   └── tara-notification-service/      # Audit Logging, Webhooks (HMAC), Alerting
├── tara-database/                      # Database Migrations & Schemas
│   └── migrations/                     # Production Flyway V1__ to V7__ SQL scripts
├── tara-ai-service/                    # Python 3.11+ / FastAPI AI & Security Engines
│   ├── pyproject.toml
│   ├── requirements.txt
│   ├── Dockerfile
│   └── app/
│       ├── main.py                     # FastAPI Application Entry
│       ├── api/v1/                     # REST API Endpoints
│       ├── core/                       # Settings, Security, Embeddings config
│       ├── models/                     # Pydantic Schemas
│       └── services/                   # Semantic Search, Optimizer, Judge, Security Scanners
├── tara-frontend/                      # React 18 + TypeScript + Tailwind CSS Enterprise UI
│   ├── package.json
│   ├── tsconfig.json
│   ├── vite.config.ts
│   ├── tailwind.config.js
│   ├── index.html
│   └── src/
│       ├── components/                 # Reusable Enterprise UI Components
│       ├── pages/                      # 20+ Enterprise Pages (Monaco, Recharts, etc.)
│       ├── context/                    # AuthContext, WorkspaceContext, ThemeContext
│       ├── hooks/                      # React Query & Custom Hooks
│       ├── services/                   # API Client & Axios/Fetch Service layer
│       └── types/                      # Full TypeScript Domain Types
├── tara-sdk/                           # Enterprise Client SDKs
│   ├── python/                         # Python SDK (tara-ai)
│   ├── typescript/                     # TypeScript/JavaScript SDK (@tara-ai/sdk)
│   └── java/                           # Java SDK (com.tara.sdk:tara-client)
└── .github/
    └── workflows/
        └── ci.yml                      # Enterprise GitHub Actions CI/CD Pipeline
```
