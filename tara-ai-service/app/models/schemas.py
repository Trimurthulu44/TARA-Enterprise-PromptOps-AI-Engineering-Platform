from pydantic import BaseModel, Field
from typing import List, Dict, Any, Optional

class EmbeddingRequest(BaseModel):
    text: str = Field(..., description="Prompt or query text to embed")

class EmbeddingResponse(BaseModel):
    embedding: List[float]
    dimension: int = 384
    model: str = "all-MiniLM-L6-v2"

class SimilarityRequest(BaseModel):
    query_text: str
    target_texts: List[str]

class SimilarityResult(BaseModel):
    target_text: str
    score: float

class AnalyzePromptRequest(BaseModel):
    prompt_text: str
    expected_variables: Optional[List[str]] = []

class CostEstimate(BaseModel):
    model: str
    cost_per_million_prompt: float
    estimated_cost_per_call: float
    monthly_cost_100k: float
    estimated_latency_ms: int

class AnalysisReport(BaseModel):
    token_count: int
    readability_score: float
    clarity_grade: str
    detected_variables: List[str]
    structural_findings: List[str]
    cost_projections: List[CostEstimate]

class OptimizePromptRequest(BaseModel):
    prompt_text: str
    strategy: str = Field(default="hardening", description="hardening, compression, fewshot, or clarity")

class OptimizationResult(BaseModel):
    original_text: str
    optimized_text: str
    tokens_saved: int
    strategy_applied: str
    improvements_made: List[str]

class JudgeEvaluationRequest(BaseModel):
    prompt_text: str
    rendered_input: str
    model_output: str
    expected_output: Optional[str] = None
    criteria: List[str] = ["relevance", "faithfulness", "conciseness", "tone"]

class JudgeResult(BaseModel):
    overall_score: float
    passed: bool
    rubric_breakdown: Dict[str, float]
    reasoning: str
    identified_issues: List[str]

class SecurityScanRequest(BaseModel):
    text: str
    target_type: str = "RENDERED_INPUT"

class PiiEntity(BaseModel):
    type: str
    matched_value: str
    start: int
    end: int

class SecurityScanResult(BaseModel):
    is_safe: bool
    risk_severity: str
    injection_detected: bool
    injection_confidence: float
    leakage_detected: bool
    pii_detected: bool
    pii_entities: List[PiiEntity]
    violations: List[str]
    masked_text: str
    recommendations: str
