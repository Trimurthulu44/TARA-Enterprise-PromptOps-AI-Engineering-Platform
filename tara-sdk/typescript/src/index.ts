export interface TaraConfig {
  apiKey: string;
  baseUrl?: string;
  timeoutMs?: number;
}

export interface RenderPromptOptions {
  promptId: string;
  variables: Record<string, any>;
  version?: 'production' | 'staging' | string;
}

export interface RenderedPrompt {
  promptId: string;
  promptName: string;
  versionSemver: string;
  systemPrompt: string;
  renderedUserPrompt: string;
  estimatedTokens: number;
  modelProvider: string;
  modelName: string;
}

export interface SecurityScanResponse {
  scanId: string;
  safe: boolean;
  riskSeverity: 'NONE' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  injectionDetected: boolean;
  injectionConfidence: number;
  maskedText: string;
  violations: string[];
}

export class TaraClient {
  private apiKey: string;
  private baseUrl: string;
  private timeoutMs: number;

  constructor(config: TaraConfig) {
    this.apiKey = config.apiKey;
    this.baseUrl = (config.baseUrl || 'http://localhost:8080/api/v1').replace(/\/$/, '');
    this.timeoutMs = config.timeoutMs || 15000;
  }

  private async fetch<T>(path: string, options: RequestInit = {}): Promise<T> {
    const url = `${this.baseUrl}${path}`;
    const controller = new AbortController();
    const timer = setTimeout(() => controller.abort(), this.timeoutMs);

    try {
      const response = await fetch(url, {
        ...options,
        signal: controller.signal,
        headers: {
          'Content-Type': 'application/json',
          'X-Tara-API-Key': this.apiKey,
          'User-Agent': '@tara-ai/sdk-js/1.0.0',
          ...(options.headers || {}),
        },
      });

      if (!response.ok) {
        const errText = await response.text();
        throw new Error(`TARA SDK Error [${response.status}]: ${errText}`);
      }

      const json = await response.json();
      return (json.data !== undefined ? json.data : json) as T;
    } finally {
      clearTimeout(timer);
    }
  }

  // Prompts API
  public readonly prompts = {
    render: async (options: RenderPromptOptions): Promise<RenderedPrompt> => {
      return this.fetch<RenderedPrompt>('/prompts/render', {
        method: 'POST',
        body: JSON.stringify({
          promptId: options.promptId,
          version: options.version || 'production',
          variables: options.variables,
        }),
      });
    },

    get: async (promptId: string): Promise<any> => {
      return this.fetch<any>(`/prompts/${promptId}`);
    },
  };

  // Security Guardrails API
  public readonly security = {
    scan: async (text: string): Promise<SecurityScanResponse> => {
      return this.fetch<SecurityScanResponse>('/security/scan', {
        method: 'POST',
        body: JSON.stringify({ text }),
      });
    },
  };

  // Evaluations API
  public readonly evaluations = {
    trigger: async (promptVersionId: string, datasetId: string, runName?: string): Promise<any> => {
      return this.fetch<any>('/evaluations/runs', {
        method: 'POST',
        body: JSON.stringify({ promptVersionId, datasetId, runName }),
      });
    },
  };
}
