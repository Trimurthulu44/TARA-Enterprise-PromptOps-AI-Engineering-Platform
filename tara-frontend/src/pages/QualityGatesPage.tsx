import React, { useState } from 'react';
import { Sliders, Plus, CheckCircle2, ShieldCheck, Gauge, Coins } from 'lucide-react';

export const QualityGatesPage: React.FC = () => {
  const [gates, setGates] = useState([
    {
      id: '1',
      name: 'Production Release Gate',
      description: 'Strict gate enforced before automatic promotion to Production',
      minJudgeScore: 0.85,
      maxP95LatencyMs: 1500,
      maxCostPerRunUsd: 10.0,
      requireZeroSecurityViolations: true,
      minPassRatePercentage: 95.0,
      isActive: true,
    },
    {
      id: '2',
      name: 'Staging Integration Gate',
      description: 'Relaxed gate for early developer builds and branch previews',
      minJudgeScore: 0.75,
      maxP95LatencyMs: 2500,
      maxCostPerRunUsd: 25.0,
      requireZeroSecurityViolations: false,
      minPassRatePercentage: 85.0,
      isActive: true,
    },
  ]);

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Quality Gate Policies</h1>
          <p className="text-xs text-slate-400 mt-1">Automated compliance criteria that prompts must satisfy before deployment</p>
        </div>
        <button
          onClick={() => alert('Add new quality gate')}
          className="px-3.5 py-2 rounded-lg bg-gradient-to-r from-brand-600 to-indigo-600 hover:from-brand-500 hover:to-indigo-500 text-white font-medium text-xs shadow-glow-violet flex items-center gap-2"
        >
          <Plus className="w-4 h-4" />
          Create Quality Gate
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        {gates.map((g) => (
          <div key={g.id} className="glass-card rounded-xl p-5 border border-tara-border flex flex-col justify-between">
            <div>
              <div className="flex items-center justify-between mb-3">
                <span className="p-2 rounded-lg bg-brand-500/10 text-brand-400">
                  <Sliders className="w-4 h-4" />
                </span>
                <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                  ENFORCED
                </span>
              </div>

              <h3 className="font-semibold text-white text-base mb-1">{g.name}</h3>
              <p className="text-xs text-slate-400 mb-4">{g.description}</p>

              <div className="space-y-2.5 p-3 rounded-lg bg-slate-950/60 border border-slate-900 text-xs">
                <div className="flex items-center justify-between">
                  <span className="text-slate-400">Min LLM Judge Score</span>
                  <span className="font-mono text-cyan-400 font-semibold">&gt;= {(g.minJudgeScore * 100).toFixed(0)}%</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-400">Max P95 Latency</span>
                  <span className="font-mono text-cyan-400 font-semibold">&lt;= {g.maxP95LatencyMs}ms</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-400">Min Test Pass Rate</span>
                  <span className="font-mono text-emerald-400 font-semibold">&gt;= {g.minPassRatePercentage}%</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-400">Zero Security Violations</span>
                  <span className={`font-mono font-semibold ${g.requireZeroSecurityViolations ? 'text-emerald-400' : 'text-slate-400'}`}>
                    {g.requireZeroSecurityViolations ? 'Required' : 'Optional'}
                  </span>
                </div>
              </div>
            </div>

            <div className="flex items-center justify-between pt-4 border-t border-slate-800 text-xs">
              <span className="text-slate-400">Applies to all production promotions</span>
              <button className="text-brand-400 hover:text-brand-300 font-medium">Edit Thresholds</button>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
