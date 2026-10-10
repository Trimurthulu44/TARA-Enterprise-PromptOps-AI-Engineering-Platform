from fastapi import APIRouter
from app.models.schemas import AnalyzePromptRequest, AnalysisReport
from app.services.analyzer_service import analyzer_service

router = APIRouter()

@router.post("/analyze", response_model=AnalysisReport)
async def analyze_prompt(req: AnalyzePromptRequest):
    return analyzer_service.analyze(req.prompt_text, req.expected_variables)
