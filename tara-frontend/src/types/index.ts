export interface User {
  id: string;
  email: string;
  fullName: string;
  avatarUrl?: string;
  role: 'ADMIN' | 'DEVELOPER' | 'VIEWER';
  organizationId: string;
  organizationName: string;
  workspaceId: string;
  workspaceName: string;
}

export interface Organization {
  id: string;
  name: string;
  slug: string;
  tier: string;
  status: string;
  maxPromptsQuota: number;
  maxEvalsMonthlyQuota: number;
}

export interface Workspace {
  id: string;
  organizationId: string;
  name: string;
  slug: string;
  description?: string;
  isDefault: boolean;
}

export interface Project {
  id: string;
  organizationId: string;
  workspaceId: string;
  name: string;
  slug: string;
  description?: string;
  createdAt: string;
}

export interface Application {
  id: string;
  organizationId: string;
  projectId: string;
  name: string;
  slug: string;
  description?: string;
  appType: string;
  environment: string;
  latencySlaMs: number;
  costBudgetMonthly: number;
  createdAt: string;
}

export interface ApiKey {
  id: string;
  name: string;
  keyPrefix: string;
  scopes: string;
  rateLimitPerMinute: number;
  isRevoked: boolean;
  createdAt: string;
  expiresAt?: string;
}

export interface Prompt {
  id: string;
  organizationId: string;
  projectId: string;
  name: string;
  slug: string;
  description?: string;
  category: string;
  tags?: string;
  currentProductionVersionId?: string;
  currentStagingVersionId?: string;
  createdAt: string;
  updatedAt: string;
}

export interface PromptVersion {
  id: string;
  promptId: string;
  organizationId: string;
  versionSemver: string;
  commitMessage: string;
  systemPrompt: string;
  userPromptTemplate: string;
  variableSchema: string;
  modelProvider: string;
  modelName: string;
  temperature: number;
  topP: number;
  maxTokens: number;
  tokenCountEstimate: number;
  status: 'DRAFT' | 'STAGING' | 'PRODUCTION' | 'ARCHIVED';
  createdAt: string;
}

export interface GoldenDataset {
  id: string;
  organizationId: string;
  projectId: string;
  name: string;
  slug: string;
  description?: string;
  version: string;
  totalTestCases: number;
  tags?: string;
  createdAt: string;
}

export interface GoldenTestCase {
  id: string;
  datasetId: string;
  testCaseName?: string;
  inputVariables: string;
  expectedOutput?: string;
  weight: number;
}

export interface EvaluationRun {
  id: string;
  promptVersionId: string;
  datasetId: string;
  runName: string;
  runType: string;
  status: 'PENDING' | 'RUNNING' | 'COMPLETED' | 'FAILED';
  judgeModel: string;
  totalCases: number;
  completedCases: number;
  passedCases: number;
  failedCases: number;
  averageScore: number;
  p50LatencyMs: number;
  p95LatencyMs: number;
  p99LatencyMs: number;
  totalTokensUsed: number;
  totalCostUsd: number;
  startedAt?: string;
  completedAt?: string;
}

export interface EvaluationResult {
  id: string;
  evaluationRunId: string;
  testCaseId: string;
  renderedPrompt: string;
  modelOutput: string;
  latencyMs: number;
  promptTokens: number;
  completionTokens: number;
  costUsd: number;
  judgeScore: number;
  judgeReasoning: string;
  passed: boolean;
}

export interface RegressionReport {
  id: string;
  projectId: string;
  datasetId: string;
  candidateVersionId: string;
  baselineVersionId: string;
  candidateAvgScore: number;
  baselineAvgScore: number;
  deltaScore: number;
  candidateP95LatencyMs: number;
  baselineP95LatencyMs: number;
  deltaLatencyMs: number;
  regressionDetected: boolean;
  qualityGatePassed: boolean;
  summaryReport: string;
  createdAt: string;
}

export interface SecurityScan {
  id: string;
  scanTargetType: string;
  scannedText: string;
  isSafe: boolean;
  riskSeverity: 'NONE' | 'LOW' | 'MEDIUM' | 'HIGH' | 'CRITICAL';
  injectionConfidence: number;
  leakageConfidence: number;
  piiEntitiesDetected: string;
  violationsDetected: string;
  remediationRecommendations?: string;
  scannedAt: string;
}

export interface QualityGate {
  id: string;
  projectId: string;
  name: string;
  description?: string;
  minJudgeScore: number;
  maxP95LatencyMs: number;
  maxCostPerRunUsd: number;
  requireZeroSecurityViolations: boolean;
  minPassRatePercentage: number;
  isActive: boolean;
}

export interface AuditLog {
  id: string;
  actorEmail: string;
  action: string;
  resourceType: string;
  resourceId: string;
  diffJson?: string;
  ipAddress?: string;
  createdAt: string;
}

export interface NotificationItem {
  id: string;
  title: string;
  message: string;
  notificationType: string;
  severity: 'INFO' | 'WARNING' | 'ERROR' | 'CRITICAL';
  linkUrl?: string;
  isRead: boolean;
  createdAt: string;
}

export interface ApiResponse<T> {
  success: boolean;
  message?: string;
  data: T;
  timestamp: string;
  traceId?: string;
}
