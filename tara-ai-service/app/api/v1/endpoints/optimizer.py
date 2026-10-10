from fastapi import APIRouter
from app.models.schemas import OptimizePromptRequest, OptimizationResult
from app.services.optimizer_service import optimizer_service

router = APIRouter()

@router.post("/optimize", response_model=OptimizationResult)
async def optimize_prompt(req: OptimizePromptRequest):
    return optimizer_service.optimize(req.prompt_text, req.strategy)
