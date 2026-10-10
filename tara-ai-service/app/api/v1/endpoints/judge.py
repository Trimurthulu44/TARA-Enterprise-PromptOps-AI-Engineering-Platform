from fastapi import APIRouter
from app.models.schemas import JudgeEvaluationRequest, JudgeResult
from app.services.judge_service import judge_service

router = APIRouter()

@router.post("/judge", response_model=JudgeResult)
async def judge_output(req: JudgeEvaluationRequest):
    return judge_service.evaluate(
        prompt_text=req.prompt_text,
        rendered_input=req.rendered_input,
        model_output=req.model_output,
        expected_output=req.expected_output,
        criteria=req.criteria,
    )
