from pydantic_settings import BaseSettings
from typing import List

class Settings(BaseSettings):
    PROJECT_NAME: str = "TARA AI & Security Engine"
    API_V1_STR: str = "/api/v1"
    PORT: int = 8090
    HOST: str = "0.0.0.0"
    
    # AI Engine Configuration
    EMBEDDING_MODEL: str = "sentence-transformers/all-MiniLM-L6-v2"
    EMBEDDING_DIM: int = 384
    DEFAULT_JUDGE_MODEL: str = "gpt-4o"
    
    # External LLM Keys
    OPENAI_API_KEY: str = ""
    ANTHROPIC_API_KEY: str = ""
    
    # Backend URL
    TARA_BACKEND_URL: str = "http://localhost:8080"
    
    # CORS
    BACKEND_CORS_ORIGINS: List[str] = ["*"]

    class Config:
        env_file = ".env"
        case_sensitive = True

settings = Settings()
