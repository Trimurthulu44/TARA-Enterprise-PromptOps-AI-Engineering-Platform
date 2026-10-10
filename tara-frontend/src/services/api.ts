import {
  User, Organization, Workspace, Project, Application, ApiKey,
  Prompt, PromptVersion, GoldenDataset, GoldenTestCase,
  EvaluationRun, EvaluationResult, RegressionReport, SecurityScan,
  QualityGate, AuditLog, NotificationItem, ApiResponse
} from '../types';

const BASE_URL = import.meta.env.VITE_API_BASE_URL || '/api/v1';


class ApiService {
  private getHeaders(): HeadersInit {
    const token = localStorage.getItem('tara_access_token');
    const tenantId = localStorage.getItem('tara_tenant_id');
    const workspaceId = localStorage.getItem('tara_workspace_id');

    const headers: Record<string, string> = {
      'Content-Type': 'application/json',
    };

    if (token) {
      headers['Authorization'] = `Bearer ${token}`;
    }
    if (tenantId) {
      headers['X-Tenant-ID'] = tenantId;
    }
    if (workspaceId) {
      headers['X-Workspace-ID'] = workspaceId;
    }

    return headers;
  }

  private async request<T>(endpoint: string, options: RequestInit = {}): Promise<T> {
    try {
      const res = await fetch(`${BASE_URL}${endpoint}`, {
        ...options,
        headers: {
          ...this.getHeaders(),
          ...(options.headers || {}),
        },
      });

      if (!res.ok) {
        const errorBody = await res.json().catch(() => ({}));
        throw new Error(errorBody.message || `API Error: ${res.statusText}`);
      }

      const json: ApiResponse<T> = await res.json();
      return json.data;
    } catch (err) {
      console.warn(`Fetch failed for ${endpoint}, using resilient fallback data:`, err);
      throw err;
    }
  }

  // Auth
  async login(email: string, password: string) {
    return this.request<{ accessToken: string; refreshToken: string; user: User }>('/auth/login', {
      method: 'POST',
      body: JSON.stringify({ email, password }),
    });
  }

  async register(data: { fullName: string; email: string; password: string; organizationName: string }) {
    return this.request<{ accessToken: string; refreshToken: string; user: User }>('/auth/register', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async getCurrentUser() {
    return this.request<User>('/auth/me');
  }

  // Organizations & Workspaces
  async getCurrentOrg() {
    return this.request<Organization>('/organizations/current');
  }

  async getWorkspaces() {
    return this.request<Workspace[]>('/organizations/workspaces');
  }

  async createWorkspace(data: { name: string; description?: string }) {
    return this.request<Workspace>('/organizations/workspaces', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  // Projects
  async getProjects() {
    return this.request<Project[]>('/projects');
  }

  async createProject(data: { name: string; description?: string }) {
    return this.request<Project>('/projects', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  // Applications
  async getApplications(projectId?: string) {
    const query = projectId ? `?projectId=${projectId}` : '';
    return this.request<Application[]>(`/applications${query}`);
  }

  async createApplication(data: any) {
    return this.request<Application>('/applications', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  // Prompts
  async getPrompts(search?: string) {
    const query = search ? `?search=${encodeURIComponent(search)}` : '';
    return this.request<Prompt[]>(`/prompts${query}`);
  }

  async getPromptById(id: string) {
    return this.request<Prompt>(`/prompts/${id}`);
  }

  async createPrompt(data: any) {
    return this.request<Prompt>('/prompts', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async getVersions(promptId: string) {
    return this.request<PromptVersion[]>(`/prompts/${promptId}/versions`);
  }

  async createVersion(promptId: string, data: any) {
    return this.request<PromptVersion>(`/prompts/${promptId}/versions`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async promoteVersion(promptId: string, data: { versionSemver: string; environment: string; notes?: string }) {
    return this.request<Prompt>(`/prompts/${promptId}/promote`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async rollbackVersion(promptId: string, data: { targetVersionSemver: string; environment: string; reason?: string }) {
    return this.request<Prompt>(`/prompts/${promptId}/rollback`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async renderPrompt(data: { promptId: string; version?: string; variables: Record<string, any> }) {
    return this.request<any>('/prompts/render', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  // Datasets & Evaluations
  async getDatasets(projectId?: string) {
    const query = projectId ? `?projectId=${projectId}` : '';
    return this.request<GoldenDataset[]>(`/datasets${query}`);
  }

  async getDatasetById(id: string) {
    return this.request<GoldenDataset>(`/datasets/${id}`);
  }

  async createDataset(data: any) {
    return this.request<GoldenDataset>('/datasets', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async getTestCases(datasetId: string) {
    return this.request<GoldenTestCase[]>(`/datasets/${datasetId}/test-cases`);
  }

  async addTestCase(datasetId: string, data: any) {
    return this.request<GoldenTestCase>(`/datasets/${datasetId}/test-cases`, {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async getEvaluationRuns() {
    return this.request<EvaluationRun[]>('/evaluations/runs');
  }

  async getEvaluationResults(runId: string) {
    return this.request<EvaluationResult[]>(`/evaluations/runs/${runId}/results`);
  }

  async triggerEvaluation(data: { promptVersionId: string; datasetId: string; runName?: string }) {
    return this.request<EvaluationRun>('/evaluations/runs', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async getRegressionReports() {
    return this.request<RegressionReport[]>('/evaluations/regressions');
  }

  async triggerRegression(data: { candidateVersionId: string; baselineVersionId: string; datasetId: string }) {
    return this.request<RegressionReport>('/evaluations/regressions', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  // Security
  async scanText(data: { text: string; promptVersionId?: string; targetType?: string }) {
    return this.request<any>('/security/scan', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async getSecurityScans() {
    return this.request<SecurityScan[]>('/security/scans');
  }

  // Quality Gates
  async getQualityGates() {
    return this.request<QualityGate[]>('/quality-gates');
  }

  // Analytics
  async getDashboardAnalytics(days: number = 14) {
    return this.request<any>(`/analytics/dashboard?days=${days}`);
  }

  // Audit Logs & Notifications
  async getAuditLogs() {
    return this.request<AuditLog[]>('/audit-logs');
  }

  async getNotifications() {
    return this.request<NotificationItem[]>('/notifications');
  }

  async markNotificationRead(id: string) {
    return this.request<void>(`/notifications/${id}/read`, {
      method: 'PATCH',
    });
  }

  // API Keys
  async getApiKeys() {
    return this.request<ApiKey[]>('/api-keys');
  }

  async createApiKey(data: { name: string; applicationId?: string }) {
    return this.request<any>('/api-keys', {
      method: 'POST',
      body: JSON.stringify(data),
    });
  }

  async revokeApiKey(id: string) {
    return this.request<void>(`/api-keys/${id}`, {
      method: 'DELETE',
    });
  }
}

export const api = new ApiService();
