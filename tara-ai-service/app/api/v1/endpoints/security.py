from fastapi import APIRouter
from app.models.schemas import SecurityScanRequest, SecurityScanResult
from app.services.security_service import security_service

router = APIRouter()

@router.post("/scan", response_model=SecurityScanResult)
async def scan_security(req: SecurityScanRequest):
    return security_service.scan(req.text, req.target_type)
