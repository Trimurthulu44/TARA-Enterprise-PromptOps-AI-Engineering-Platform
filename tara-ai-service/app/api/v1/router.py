from fastapi import APIRouter
from app.api.v1.endpoints import embeddings, analysis, optimizer, judge, security

api_router = APIRouter()

api_router.include_router(embeddings.router, prefix="/ai", tags=["Embeddings & Similarity"])
api_router.include_router(analysis.router, prefix="/ai", tags=["Prompt Analysis"])
api_router.include_router(optimizer.router, prefix="/ai", tags=["Prompt Optimization"])
api_router.include_router(judge.router, prefix="/ai", tags=["LLM Judge"])
api_router.include_router(security.router, prefix="/security", tags=["Security Guardrails"])
