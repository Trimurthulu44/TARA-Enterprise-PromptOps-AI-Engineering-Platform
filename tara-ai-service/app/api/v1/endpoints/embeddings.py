from fastapi import APIRouter
from typing import List
from app.models.schemas import (
    EmbeddingRequest, EmbeddingResponse,
    SimilarityRequest, SimilarityResult
)
from app.services.embedding_service import embedding_service

router = APIRouter()

@router.post("/embeddings", response_model=EmbeddingResponse)
async def generate_embedding(req: EmbeddingRequest):
    vector = embedding_service.get_embedding(req.text)
    return EmbeddingResponse(
        embedding=vector,
        dimension=len(vector),
        model="all-MiniLM-L6-v2"
    )

@router.post("/similarity", response_model=List[SimilarityResult])
async def compute_similarity(req: SimilarityRequest):
    q_vec = embedding_service.get_embedding(req.query_text)
    results = []
    for target in req.target_texts:
        t_vec = embedding_service.get_embedding(target)
        score = embedding_service.cosine_similarity(q_vec, t_vec)
        results.append(SimilarityResult(target_text=target, score=round(score, 4)))
    results.sort(key=lambda x: x.score, reverse=True)
    return results
