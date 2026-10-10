import React, { useState } from 'react';
import { GitCompare, Play, CheckCircle2, AlertTriangle, ArrowRight, ShieldCheck, TrendingUp, TrendingDown } from 'lucide-react';

export const RegressionPage: React.FC = () => {
  const [candidateVer, setCandidateVer] = useState('v1.1.0');
  const [baselineVer, setBaselineVer] = useState('v1.0.0');

  const regressionReports = [
    {
      id: '1',
      candidate: 'v1.1.0',
      baseline: 'v1.0.0',
      dataset: 'Customer Support Golden Suite',
      candidateScore: 0.965,
      baselineScore: 0.912,
      deltaScore: 0.053, // +5.3%
      candidateP95Latency: 340,
      baselineP95Latency: 385,
      deltaLatency: -45, // 45ms faster
      regressionDetected: false,
      gatePassed: true,
      testedAt: '2026-10-10 14:15 UTC',
    },
    {
      id: '2',
      candidate: 'v2.0.0-alpha',
      baseline: 'v1.0.0',
      dataset: 'Adversarial & Edge Cases Suite',
      candidateScore: 0.820,
      baselineScore: 0.895,
      deltaScore: -0.075, // -7.5% REGRESSION
      candidateP95Latency: 620,
      baselineP95Latency: 390,
      deltaLatency: 230,
      regressionDetected: true,
      gatePassed: false,
      testedAt: '2026-10-09 11:30 UTC',
    },
  ];

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Regression Testing Suite</h1>
          <p className="text-xs text-slate-400 mt-1">
            Detect subtle accuracy regressions, latency drift, and quality degradation prior to production deployment
          </p>
        </div>
        <button
          onClick={() => alert('Regression comparison test executed!')}
          className="px-3.5 py-2 rounded-lg bg-gradient-to-r from-brand-600 to-indigo-600 hover:from-brand-500 hover:to-indigo-500 text-white font-medium text-xs shadow-glow-violet flex items-center gap-2"
        >
          <Play className="w-4 h-4" />
          Trigger Regression Run
        </button>
      </div>

      {/* Regression Matrix Cards */}
      <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
        {regressionReports.map((rep) => (
          <div
            key={rep.id}
            className={`glass-card rounded-xl p-5 border flex flex-col justify-between ${
              rep.regressionDetected ? 'border-rose-500/50 shadow-glow-rose' : 'border-emerald-500/40'
            }`}
          >
            <div>
              <div className="flex items-center justify-between mb-3">
                <span className="flex items-center gap-1.5 text-xs font-mono font-bold text-white">
                  <span className="text-cyan-400">{rep.candidate}</span> (Candidate) vs{' '}
                  <span className="text-slate-400">{rep.baseline}</span> (Baseline)
                </span>
                <span
                  className={`text-[10px] font-bold px-2 py-0.5 rounded-full ${
                    rep.regressionDetected
                      ? 'bg-rose-500/20 text-rose-400 border border-rose-500/30'
                      : 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                  }`}
                >
                  {rep.regressionDetected ? 'REGRESSION DETECTED' : 'PASSING GATE'}
                </span>
              </div>

              <p className="text-xs text-slate-400 mb-4">{rep.dataset}</p>

              {/* Delta KPI Grid */}
              <div className="grid grid-cols-2 gap-3 mb-4">
                <div className="p-3 rounded-lg bg-slate-950/70 border border-slate-900">
                  <div className="text-[11px] text-slate-400 mb-1">Judge Score Delta</div>
                  <div className="flex items-center gap-2">
                    <span className="text-lg font-bold font-mono text-white">
                      {(rep.candidateScore * 100).toFixed(1)}%
                    </span>
                    <span
                      className={`text-xs font-semibold flex items-center ${
                        rep.deltaScore >= 0 ? 'text-emerald-400' : 'text-rose-400'
                      }`}
                    >
                      {rep.deltaScore >= 0 ? <TrendingUp className="w-3.5 h-3.5 mr-0.5" /> : <TrendingDown className="w-3.5 h-3.5 mr-0.5" />}
                      {rep.deltaScore >= 0 ? '+' : ''}
                      {(rep.deltaScore * 100).toFixed(1)}%
                    </span>
                  </div>
                </div>

                <div className="p-3 rounded-lg bg-slate-950/70 border border-slate-900">
                  <div className="text-[11px] text-slate-400 mb-1">P95 Latency Delta</div>
                  <div className="flex items-center gap-2">
                    <span className="text-lg font-bold font-mono text-white">{rep.candidateP95Latency}ms</span>
                    <span
                      className={`text-xs font-semibold ${
                        rep.deltaLatency <= 0 ? 'text-emerald-400' : 'text-rose-400'
                      }`}
                    >
                      {rep.deltaLatency <= 0 ? '' : '+'}
                      {rep.deltaLatency}ms
                    </span>
                  </div>
                </div>
              </div>
            </div>

            <div className="flex items-center justify-between pt-3 border-t border-slate-800 text-xs">
              <span className="text-slate-400 font-mono text-[11px]">Tested: {rep.testedAt}</span>
              <span className="text-brand-400 hover:text-brand-300 font-medium cursor-pointer">
                View Granular Test Cases →
              </span>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
