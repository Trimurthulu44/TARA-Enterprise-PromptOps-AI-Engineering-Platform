import React from 'react';
import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import { QueryClient, QueryClientProvider } from '@tanstack/react-query';
import { AuthProvider } from './context/AuthContext';
import { WorkspaceProvider } from './context/WorkspaceContext';
import { AppLayout } from './components/layout/AppLayout';

import { LandingPage } from './pages/LandingPage';
import { LoginPage } from './pages/LoginPage';
import { RegisterPage } from './pages/RegisterPage';
import { DashboardPage } from './pages/DashboardPage';
import { WorkspacesPage } from './pages/WorkspacesPage';
import { ProjectsPage } from './pages/ProjectsPage';
import { ApplicationsPage } from './pages/ApplicationsPage';
import { PromptCatalogPage } from './pages/PromptCatalogPage';
import { PromptEditorPage } from './pages/PromptEditorPage';
import { PromptVersionHistoryPage } from './pages/PromptVersionHistoryPage';
import { PromptAnalyzerPage } from './pages/PromptAnalyzerPage';
import { PromptOptimizerPage } from './pages/PromptOptimizerPage';
import { EvaluationPage } from './pages/EvaluationPage';
import { GoldenDatasetPage } from './pages/GoldenDatasetPage';
import { RegressionPage } from './pages/RegressionPage';
import { SecurityTestingPage } from './pages/SecurityTestingPage';
import { AnalyticsPage } from './pages/AnalyticsPage';
import { QualityGatesPage } from './pages/QualityGatesPage';
import { AuditLogsPage } from './pages/AuditLogsPage';
import { SettingsAdminPage } from './pages/SettingsAdminPage';

const queryClient = new QueryClient({
  defaultOptions: {
    queries: {
      refetchOnWindowFocus: false,
      retry: 1,
    },
  },
});

export const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <AuthProvider>
        <WorkspaceProvider>
          <BrowserRouter>
            <Routes>
              {/* Public Routes */}
              <Route path="/" element={<LandingPage />} />
              <Route path="/login" element={<LoginPage />} />
              <Route path="/register" element={<RegisterPage />} />

              {/* Authenticated Platform App Layout */}
              <Route element={<AppLayout />}>
                <Route path="/dashboard" element={<DashboardPage />} />
                <Route path="/workspaces" element={<WorkspacesPage />} />
                <Route path="/projects" element={<ProjectsPage />} />
                <Route path="/applications" element={<ApplicationsPage />} />
                <Route path="/prompts" element={<PromptCatalogPage />} />
                <Route path="/prompts/editor" element={<PromptEditorPage />} />
                <Route path="/prompts/history" element={<PromptVersionHistoryPage />} />
                <Route path="/analyzer" element={<PromptAnalyzerPage />} />
                <Route path="/optimizer" element={<PromptOptimizerPage />} />
                <Route path="/evaluations" element={<EvaluationPage />} />
                <Route path="/datasets" element={<GoldenDatasetPage />} />
                <Route path="/regression" element={<RegressionPage />} />
                <Route path="/security" element={<SecurityTestingPage />} />
                <Route path="/analytics" element={<AnalyticsPage />} />
                <Route path="/quality-gates" element={<QualityGatesPage />} />
                <Route path="/audit-logs" element={<AuditLogsPage />} />
                <Route path="/api-keys" element={<SettingsAdminPage />} />
                <Route path="/settings" element={<SettingsAdminPage />} />
              </Route>

              {/* Fallback */}
              <Route path="*" element={<Navigate to="/dashboard" replace />} />
            </Routes>
          </BrowserRouter>
        </WorkspaceProvider>
      </AuthProvider>
    </QueryClientProvider>
  );
};
