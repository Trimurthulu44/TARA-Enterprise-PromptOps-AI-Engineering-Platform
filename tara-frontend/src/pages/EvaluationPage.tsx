import React, { useState } from 'react';
import { ClipboardCheck, Play, CheckCircle2, XCircle, Clock, Coins, Eye, ArrowRight } from 'lucide-react';

export const EvaluationPage: React.FC = () => {
  const [showRunModal, setShowRunModal] = useState(false);
  const [selectedModel, setSelectedModel] = useState('gpt-4o');

  const runs = [
    {
      id: '1',
      name: 'Customer Support v1.0.0 Release Verification',
      datasetName: 'Customer Support Golden Suite (100 Cases)',
      version: 'v1.0.0',
      judgeModel: 'gpt-4o',
      status: 'COMPLETED',
      totalCases: 100,
      passedCases: 98,
      failedCases: 2,
      averageScore: 0.941,
      p95LatencyMs: 385,
      costUsd: 1.42,
      completedAt: '2026-10-10 13:10 UTC',
    },
    {
      id: '2',
      name: 'Regression Run: Candidate v1.1.0 vs Baseline',
      datasetName: 'Customer Support Golden Suite (100 Cases)',
      version: 'v1.1.0',
      judgeModel: 'claude-3-5-sonnet',
      status: 'COMPLETED',
      totalCases: 100,
      passedCases: 100,
      failedCases: 0,
      averageScore: 0.982,
      p95LatencyMs: 340,
      costUsd: 1.65,
      completedAt: '2026-10-10 14:00 UTC',
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Batch Evaluation Runner</h1>
          <p className="text-xs text-slate-400 mt-1">Orchestrate automated LLM-as-a-judge quality evaluations across golden datasets</p>
        </div>
        <button
          onClick={() => setShowRunModal(true)}
          className="px-3.5 py-2 rounded-lg bg-gradient-to-r from-brand-600 to-cyan-600 hover:from-brand-500 hover:to-cyan-500 text-white font-medium text-xs shadow-glow-cyan flex items-center gap-2"
        >
          <Play className="w-4 h-4" />
          Trigger Evaluation Run
        </button>
      </div>

      {/* Evaluation Runs Grid/Table */}
      <div className="glass-card rounded-xl p-5 border border-tara-border">
        <h3 className="text-sm font-semibold text-white mb-4">Historical Evaluation Runs</h3>
        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-900/80 text-slate-400 border-b border-slate-800 uppercase font-mono text-[10px]">
              <tr>
                <th className="py-2.5 px-3">Run Name</th>
                <th className="py-2.5 px-3">Dataset</th>
                <th className="py-2.5 px-3">Version</th>
                <th className="py-2.5 px-3">Judge Model</th>
                <th className="py-2.5 px-3">Pass Rate</th>
                <th className="py-2.5 px-3">Avg Score</th>
                <th className="py-2.5 px-3">P95 Latency</th>
                <th className="py-2.5 px-3">Cost</th>
                <th className="py-2.5 px-3">Status</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 text-slate-200">
              {runs.map((r) => (
                <tr key={r.id} className="hover:bg-slate-800/40 transition-colors">
                  <td className="py-3 px-3 font-medium text-white">{r.name}</td>
                  <td className="py-3 px-3 text-slate-400">{r.datasetName}</td>
                  <td className="py-3 px-3 font-mono font-semibold text-cyan-400">{r.version}</td>
                  <td className="py-3 px-3 font-mono text-slate-300">{r.judgeModel}</td>
                  <td className="py-3 px-3 font-mono text-emerald-400 font-bold">
                    {r.passedCases} / {r.totalCases} ({((r.passedCases / r.totalCases) * 100).toFixed(0)}%)
                  </td>
                  <td className="py-3 px-3 font-mono text-white font-bold">{(r.averageScore * 100).toFixed(1)}%</td>
                  <td className="py-3 px-3 font-mono text-slate-300">{r.p95LatencyMs}ms</td>
                  <td className="py-3 px-3 font-mono text-amber-400 font-medium">${r.costUsd}</td>
                  <td className="py-3 px-3">
                    <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                      COMPLETED
                    </span>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Trigger Run Modal */}
      {showRunModal && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="glass-card w-full max-w-md rounded-2xl p-6 border border-slate-700 shadow-2xl">
            <h3 className="text-lg font-bold text-white mb-1">Trigger Batch Evaluation</h3>
            <p className="text-xs text-slate-400 mb-4">Run comprehensive test suite against target prompt version.</p>

            <form
              onSubmit={(e) => {
                e.preventDefault();
                alert('Evaluation run queued. Test cases executing in worker pool.');
                setShowRunModal(false);
              }}
              className="space-y-4"
            >
              <div>
                <label className="text-xs font-medium text-slate-300 block mb-1">Target Prompt Version</label>
                <select className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-500 font-mono">
                  <option value="v1.0.0">Support Ticket Resolution Copilot - v1.0.0</option>
                  <option value="v1.1.0">Support Ticket Resolution Copilot - v1.1.0 (Candidate)</option>
                </select>
              </div>

              <div>
                <label className="text-xs font-medium text-slate-300 block mb-1">Golden Dataset</label>
                <select className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-500">
                  <option value="1">Customer Support Golden Suite (100 Test Cases)</option>
                  <option value="2">Edge Case & Adversarial Suite (25 Test Cases)</option>
                </select>
              </div>

              <div>
                <label className="text-xs font-medium text-slate-300 block mb-1">LLM Judge Model</label>
                <select
                  value={selectedModel}
                  onChange={(e) => setSelectedModel(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-500 font-mono"
                >
                  <option value="gpt-4o">gpt-4o (Strict Rubric Judge)</option>
                  <option value="claude-3-5-sonnet">claude-3-5-sonnet (High Precision)</option>
                  <option value="consensus">Dual-Model Consensus (GPT-4o + Claude)</option>
                </select>
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setShowRunModal(false)}
                  className="px-4 py-2 rounded-lg text-xs font-medium text-slate-400 hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-lg bg-cyan-600 hover:bg-cyan-500 text-white font-semibold text-xs shadow-glow-cyan"
                >
                  Start Run
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
