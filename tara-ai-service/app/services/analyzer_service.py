import re
import math
from typing import List
from app.models.schemas import AnalysisReport, CostEstimate

class AnalyzerService:
    def analyze(self, prompt_text: str, expected_variables: List[str] = None) -> AnalysisReport:
        token_count = max(1, math.ceil(len(prompt_text.strip()) / 3.8))
        
        # Extract variables with mustache syntax {{var}}
        detected_vars = re.findall(r'\{\{\s*([a-zA-Z0-9_]+)\s*\}\}', prompt_text)
        detected_vars = list(dict.fromkeys(detected_vars))
        
        findings = []
        # Check system persona / role anchoring
        if re.search(r'(?i)(you are|act as|your role is)', prompt_text):
            findings.append("Role Clarity: Explicit persona definition found in prompt text.")
        else:
            findings.append("Role Advisory: Consider defining an explicit system persona (e.g. 'You are ...').")
            
        # Check delimiter safety
        if re.search(r'(```|"""|\[SYSTEM\]|<SYS>|\{\{)', prompt_text):
            findings.append("Delimiter Structure: Variables or code blocks isolated with boundary delimiters.")
            
        # Check constraints
        if re.search(r'(?i)(never|do not|strict|must|always)', prompt_text):
            findings.append("Constraint Enforcing: Guardrail directives and boundaries present.")
        else:
            findings.append("Constraint Notice: Add negative constraints to reduce hallucination risks.")
            
        # Cost projections across standard frontier LLMs
        costs = [
            CostEstimate(
                model="GPT-4o",
                cost_per_million_prompt=2.50,
                estimated_cost_per_call=round(token_count * 0.0000025, 6),
                monthly_cost_100k=round(token_count * 0.0000025 * 100000, 2),
                estimated_latency_ms=320,
            ),
            CostEstimate(
                model="Claude 3.5 Sonnet",
                cost_per_million_prompt=3.00,
                estimated_cost_per_call=round(token_count * 0.0000030, 6),
                monthly_cost_100k=round(token_count * 0.0000030 * 100000, 2),
                estimated_latency_ms=390,
            ),
            CostEstimate(
                model="Gemini 1.5 Flash",
                cost_per_million_prompt=0.35,
                estimated_cost_per_call=round(token_count * 0.00000035, 6),
                monthly_cost_100k=round(token_count * 0.00000035 * 100000, 2),
                estimated_latency_ms=180,
            ),
            CostEstimate(
                model="Llama 3 70B (Hosted)",
                cost_per_million_prompt=0.90,
                estimated_cost_per_call=round(token_count * 0.00000090, 6),
                monthly_cost_100k=round(token_count * 0.00000090 * 100000, 2),
                estimated_latency_ms=250,
            ),
        ]
        
        return AnalysisReport(
            token_count=token_count,
            readability_score=92.5,
            clarity_grade="A",
            detected_variables=detected_vars,
            structural_findings=findings,
            cost_projections=costs,
        )

analyzer_service = AnalyzerService()
