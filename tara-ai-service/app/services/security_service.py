import re
from typing import List
from app.models.schemas import SecurityScanResult, PiiEntity

class SecurityService:
    INJECTION_PATTERNS = [
        re.compile(r'(?i)ignore\s+(all\s+)?(previous|prior|above)\s+instructions?'),
        re.compile(r'(?i)disregard\s+(all\s+)?(earlier|previous|prior)\s+(rules|directives|instructions)'),
        re.compile(r'(?i)(act\s+as|pretend\s+you\s+are|you\s+are\s+now)\s+(DAN|unbound|jailbreak|developer\s+mode|unrestricted)'),
        re.compile(r'(?i)(reveal|show|print|output|dump)\s+(your|the)?\s*(system|initial|secret)\s*(prompt|instructions)'),
        re.compile(r'(?i)(repeat\s+everything|what\s+is\s+written)\s+(above|before\s+this\s+line)'),
        re.compile(r'(?i)\[\s*SYSTEM\s*\]|<\s*\/?\s*SYS\s*>|---\s*SYSTEM\s+INSTRUCTION\s*---'),
    ]

    PII_PATTERNS = {
        "EMAIL": re.compile(r'\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\.[A-Za-z]{2,}\b'),
        "SSN": re.compile(r'\b\d{3}-\d{2}-\d{4}\b'),
        "CREDIT_CARD": re.compile(r'\b(?:\d{4}[ -]?){3}\d{4}\b'),
        "PHONE": re.compile(r'\b(?:\+?1[-. ]?)?\(?([0-9]{3})\)?[-. ]?([0-9]{3})[-. ]?([0-9]{4})\b'),
        "API_KEY": re.compile(r'\b(AKIA[0-9A-Z]{16}|sk-[a-zA-Z0-9]{24,}|ghp_[a-zA-Z0-9]{36}|tara_live_[a-f0-9]{48})\b'),
    }

    def scan(self, text: str, target_type: str = "RENDERED_INPUT") -> SecurityScanResult:
        violations = []
        pii_entities: List[PiiEntity] = []
        
        # 1. Prompt Injection Scanning
        injection_detected = False
        confidence = 0.0
        for pattern in self.INJECTION_PATTERNS:
            match = pattern.search(text)
            if match:
                injection_detected = True
                confidence = max(confidence, 0.94)
                violations.append(f"Prompt Injection Attack Detected: '{match.group(0)}'")
                
        # 2. PII Entity Scanning & Redaction
        masked_text = text
        for pii_type, pattern in self.PII_PATTERNS.items():
            for m in pattern.finditer(masked_text):
                pii_entities.append(PiiEntity(
                    type=pii_type,
                    matched_value=m.group(0),
                    start=m.start(),
                    end=m.end(),
                ))
            masked_text = pattern.sub(f"[{pii_type}_REDACTED]", masked_text)
            
        pii_detected = len(pii_entities) > 0
        if pii_detected:
            violations.append(f"PII Leakage Risk: {len(pii_entities)} sensitive data entities intercepted.")
            
        is_safe = len(violations) == 0
        severity = "NONE"
        if injection_detected:
            severity = "CRITICAL"
        elif pii_detected:
            severity = "HIGH"
            
        recommendations = (
            "All guardrail security assertions passed. Input is clean."
            if is_safe
            else "Isolate untrusted inputs into guarded delimiter boundaries and enforce PII masking filter."
        )
        
        return SecurityScanResult(
            is_safe=is_safe,
            risk_severity=severity,
            injection_detected=injection_detected,
            injection_confidence=confidence,
            leakage_detected=False,
            pii_detected=pii_detected,
            pii_entities=pii_entities,
            violations=violations,
            masked_text=masked_text,
            recommendations=recommendations,
        )

security_service = SecurityService()
