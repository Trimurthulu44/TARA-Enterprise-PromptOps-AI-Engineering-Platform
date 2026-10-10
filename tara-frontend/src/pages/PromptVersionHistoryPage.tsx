import React, { useState } from 'react';
import { MonacoDiffViewer } from '../components/editor/MonacoDiffViewer';
import { GitBranch, RotateCcw, CheckCircle2, ArrowRight, ShieldCheck, Clock } from 'lucide-react';

const BASELINE_PROMPT = `User Query: {{customer_query}}

Retrieved Knowledge Base Documentation:
{{knowledge_context}}

Task: Provide a resolution. Cite specific KB sections. If information is missing, ask questions.`;

const CANDIDATE_PROMPT = `User Query: {{customer_query}}

Retrieved Knowledge Base Documentation:
{{knowledge_context}}

Customer Account Tier: {{account_tier}}

Task: Provide an actionable, step-by-step resolution. Cite specific KB sections. If information is missing, politely ask clarifying questions.`;

export const PromptVersionHistoryPage: React.FC = () => {
  const [selectedBaseline, setSelectedBaseline] = useState('v1.0.0');
  const [selectedCandidate, setSelectedCandidate] = useState('v1.1.0');
  const [showRollbackModal, setShowRollbackModal] = useState(false);

  const versions = [
    {
      version: 'v1.1.0',
      commitMessage: 'Added account_tier variable and polite clarification directive',
      environment: 'STAGING',
      author: 'Alex Murphy',
      createdAt: '2026-10-10 12:40 UTC',
      judgeScore: 0.96,
      latency: '340ms',
    },
    {
      version: 'v1.0.0',
      commitMessage: 'Initial production prompt for enterprise support resolution',
      environment: 'PRODUCTION',
      author: 'Sarah Connor',
      createdAt: '2026-10-08 09:15 UTC',
      judgeScore: 0.91,
      latency: '385ms',
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Prompt Version Diff & History</h1>
          <p className="text-xs text-slate-400 mt-1">
            Compare semantic versions side-by-side using git-like Monaco diffing and manage rollback governance
          </p>
        </div>

        <div className="flex items-center gap-3">
          <button
            onClick={() => setShowRollbackModal(true)}
            className="px-3.5 py-2 rounded-lg bg-rose-500/10 hover:bg-rose-500/20 text-rose-300 border border-rose-500/30 text-xs font-semibold flex items-center gap-2 transition-all"
          >
            <RotateCcw className="w-3.5 h-3.5" />
            One-Click Rollback
          </button>
          <button
            onClick={() => alert('v1.1.0 promoted to PRODUCTION environment!')}
            className="px-3.5 py-2 rounded-lg bg-gradient-to-r from-emerald-600 to-teal-600 hover:from-emerald-500 hover:to-teal-500 text-white font-semibold text-xs shadow-glow-cyan flex items-center gap-2 transition-all"
          >
            <CheckCircle2 className="w-3.5 h-3.5" />
            Promote to Production
          </button>
        </div>
      </div>

      {/* Monaco Side-by-Side Diff Viewer */}
      <MonacoDiffViewer
        original={BASELINE_PROMPT}
        modified={CANDIDATE_PROMPT}
        originalTitle={`Baseline: ${selectedBaseline} (Production)`}
        modifiedTitle={`Candidate: ${selectedCandidate} (Staging)`}
        height="420px"
      />

      {/* Version Commit Timeline Table */}
      <div className="glass-card rounded-xl p-5 border border-tara-border">
        <h3 className="text-sm font-semibold text-white mb-3">Immutable Commit History</h3>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-900/80 text-slate-400 border-b border-slate-800 uppercase font-mono text-[10px]">
              <tr>
                <th className="py-2.5 px-3">Semantic Version</th>
                <th className="py-2.5 px-3">Commit Message</th>
                <th className="py-2.5 px-3">Environment</th>
                <th className="py-2.5 px-3">Author</th>
                <th className="py-2.5 px-3">Judge Score</th>
                <th className="py-2.5 px-3">Date</th>
                <th className="py-2.5 px-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 text-slate-200">
              {versions.map((ver) => (
                <tr key={ver.version} className="hover:bg-slate-800/40 transition-colors">
                  <td className="py-3 px-3 font-mono font-bold text-cyan-400">{ver.version}</td>
                  <td className="py-3 px-3 font-medium text-white">{ver.commitMessage}</td>
                  <td className="py-3 px-3">
                    <span className={`px-2 py-0.5 rounded-full text-[10px] font-bold ${
                      ver.environment === 'PRODUCTION'
                        ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                        : 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                    }`}>
                      {ver.environment}
                    </span>
                  </td>
                  <td className="py-3 px-3 text-slate-400">{ver.author}</td>
                  <td className="py-3 px-3 font-mono text-emerald-400 font-semibold">{ver.judgeScore * 100}%</td>
                  <td className="py-3 px-3 text-slate-400 font-mono text-[11px]">{ver.createdAt}</td>
                  <td className="py-3 px-3 text-right">
                    <button
                      onClick={() => setSelectedCandidate(ver.version)}
                      className="text-brand-400 hover:text-brand-300 font-medium"
                    >
                      Compare
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Rollback Confirmation Modal */}
      {showRollbackModal && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="glass-card w-full max-w-md rounded-2xl p-6 border border-rose-500/30 shadow-2xl">
            <div className="flex items-center gap-3 text-rose-400 mb-2">
              <RotateCcw className="w-5 h-5" />
              <h3 className="text-base font-bold text-white">Emergency Rollback Confirmation</h3>
            </div>
            <p className="text-xs text-slate-300 mb-4 leading-relaxed">
              Are you sure you want to rollback Production pointer to <span className="font-mono font-bold text-cyan-400">v1.0.0</span>?
              This will update the active prompt alias immediately for all connected runtime AI applications.
            </p>

            <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-800">
              <button
                onClick={() => setShowRollbackModal(false)}
                className="px-4 py-2 rounded-lg text-xs font-medium text-slate-400 hover:text-white"
              >
                Cancel
              </button>
              <button
                onClick={() => {
                  alert('Production successfully rolled back to v1.0.0');
                  setShowRollbackModal(false);
                }}
                className="px-4 py-2 rounded-lg bg-rose-600 hover:bg-rose-500 text-white font-semibold text-xs"
              >
                Execute Rollback
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
