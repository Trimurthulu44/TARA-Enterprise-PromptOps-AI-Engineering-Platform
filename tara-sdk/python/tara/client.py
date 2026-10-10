import requests
from typing import Dict, Any, Optional

class TaraException(Exception):
    pass

class PromptsApi:
    def __init__(self, client: 'TaraClient'):
        self.client = client

    def get(self, prompt_id: str) -> Dict[str, Any]:
        return self.client._get(f"/prompts/{prompt_id}")

    def render(self, prompt_id: str, variables: Dict[str, Any], version: str = "production") -> Dict[str, Any]:
        payload = {
            "promptId": prompt_id,
            "version": version,
            "variables": variables
        }
        return self.client._post("/prompts/render", payload)

class SecurityApi:
    def __init__(self, client: 'TaraClient'):
        self.client = client

    def scan(self, text: str) -> Dict[str, Any]:
        payload = {"text": text}
        return self.client._post("/security/scan", payload)

class EvaluationsApi:
    def __init__(self, client: 'TaraClient'):
        self.client = client

    def trigger_run(self, prompt_version_id: str, dataset_id: str, run_name: Optional[str] = None) -> Dict[str, Any]:
        payload = {
            "promptVersionId": prompt_version_id,
            "datasetId": dataset_id,
            "runName": run_name
        }
        return self.client._post("/evaluations/runs", payload)

class TaraClient:
    """
    Enterprise Python Client SDK for TARA PromptOps Platform.
    
    Usage:
        client = TaraClient(api_key="tara_live_...", base_url="https://api.tara.ai")
        rendered = client.prompts.render("support-ticket-resolution", variables={"customer_query": "Refund?"})
        print(rendered["renderedUserPrompt"])
    """
    def __init__(self, api_key: str, base_url: str = "http://localhost:8080/api/v1", timeout: int = 15):
        self.api_key = api_key
        self.base_url = base_url.rstrip("/")
        self.timeout = timeout
        self.session = requests.Session()
        self.session.headers.update({
            "X-Tara-API-Key": api_key,
            "Content-Type": "application/json",
            "User-Agent": "Tara-Python-SDK/1.0.0"
        })
        self.prompts = PromptsApi(self)
        self.security = SecurityApi(self)
        self.evaluations = EvaluationsApi(self)

    def _get(self, endpoint: str) -> Dict[str, Any]:
        url = f"{self.base_url}{endpoint}"
        resp = self.session.get(url, timeout=self.timeout)
        if not resp.ok:
            raise TaraException(f"TARA API Error [{resp.status_code}]: {resp.text}")
        data = resp.json()
        return data.get("data", data)

    def _post(self, endpoint: str, json_data: Dict[str, Any]) -> Dict[str, Any]:
        url = f"{self.base_url}{endpoint}"
        resp = self.session.post(url, json=json_data, timeout=self.timeout)
        if not resp.ok:
            raise TaraException(f"TARA API Error [{resp.status_code}]: {resp.text}")
        data = resp.json()
        return data.get("data", data)
