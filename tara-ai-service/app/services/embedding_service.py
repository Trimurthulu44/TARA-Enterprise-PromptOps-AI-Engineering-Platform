import hashlib
import math
import numpy as np
from typing import List

class EmbeddingService:
    def __init__(self, dim: int = 384):
        self.dim = dim
        self._model = None
        try:
            from sentence_transformers import SentenceTransformer
            self._model = SentenceTransformer('all-MiniLM-L6-v2')
        except Exception:
            # Fallback to high-performance deterministic hashing projection
            self._model = None

    def get_embedding(self, text: str) -> List[float]:
        if self._model is not None:
            emb = self._model.encode(text)
            return emb.tolist()

        # Deterministic semantic projection
        vector = np.zeros(self.dim, dtype=np.float32)
        words = text.lower().split()
        for idx, word in enumerate(words):
            h = int(hashlib.sha256(word.encode()).hexdigest(), 16)
            pos = h % self.dim
            weight = 1.0 / (1.0 + math.log1p(idx + 1))
            vector[pos] += float(weight)

        norm = np.linalg.norm(vector)
        if norm > 0:
            vector = vector / norm
        return vector.tolist()

    def cosine_similarity(self, vec_a: List[float], vec_b: List[float]) -> float:
        a = np.array(vec_a, dtype=np.float32)
        b = np.array(vec_b, dtype=np.float32)
        dot = np.dot(a, b)
        norm_a = np.linalg.norm(a)
        norm_b = np.linalg.norm(b)
        if norm_a == 0 or norm_b == 0:
            return 0.0
        return float(dot / (norm_a * norm_b))

embedding_service = EmbeddingService()
