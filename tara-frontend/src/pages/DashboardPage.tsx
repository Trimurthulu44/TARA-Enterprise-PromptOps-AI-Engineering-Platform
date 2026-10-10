import React from 'react';
import { Link } from 'react-router-dom';
import { StatCard } from '../components/ui/StatCard';
import {
  FileCode2,
  CheckCircle2,
  Gauge,
  Coins,
  ArrowUpRight,
  ShieldCheck,
  GitBranch,
  Play,
  TrendingUp,
  Cpu
} from 'lucide-react';
import {
  AreaChart, Area, XAxis, YAxis, Tooltip, ResponsiveContainer,
  BarChart, Bar
} from 'recharts';

const TREND_DATA = [
  { time: '10/01', requests: 1240, latency: 410, cost: 14.2 },
  { time: '10/02', requests: 1480, latency: 395, cost: 16.8 },
  { time: '10/03', requests: 1620, latency: 380, cost: 18.5 },
  { time: '10/04', requests: 1390, latency: 390, cost: 15.1 },
  { time: '10/05', requests: 1950, latency: 360, cost: 22.4 },
  { time: '10/06', requests: 2100, latency: 375, cost: 24.1 },
  { time: '10/07', requests: 2450, latency: 350, cost: 28.0 },
];

export const DashboardPage: React.FC = () => {
  return (
    <div className="space-y-6">
      {/* Top Banner */}
      <div className="flex flex-col md:flex-row md:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">PromptOps Command Center</h1>
          <p className="text-xs text-slate-400 mt-1">Real-time governance, evaluation metrics, and deployment pipelines</p>
        </div>

        <div className="flex items-center gap-3">
          <Link
            to="/evaluations"
            className="px-3.5 py-2 rounded-lg bg-slate-900 hover:bg-slate-800 text-xs text-slate-200 border border-tara-border flex items-center gap-2 transition-all"
          >
            <Play className="w-3.5 h-3.5 text-cyan-400" />
            Run Evaluation
          </Link>
          <Link
            to="/prompts/editor"
            className="px-3.5 py-2 rounded-lg bg-gradient-to-r from-brand-600 to-indigo-600 hover:from-brand-500 hover:to-indigo-500 text-white font-medium text-xs shadow-glow-violet flex items-center gap-2 transition-all"
          >
            <FileCode2 className="w-3.5 h-3.5" />
            New Prompt
          </Link>
        </div>
      </div>

      {/* 4 KPI Stat Cards */}
      <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4">
        <StatCard
          title="Active Prompts"
          value="48 Managed"
          change="+6 this week"
          icon={FileCode2}
          accentColor="cyan"
          subtitle="across 3 projects"
        />
        <StatCard
          title="Success Rate"
          value="99.42%"
          change="+0.8% vs last week"
          icon={CheckCircle2}
          accentColor="emerald"
          subtitle="LLM Judge verified"
        />
        <StatCard
          title="P95 Latency"
          value="385 ms"
          change="-45ms faster"
          icon={Gauge}
          accentColor="violet"
          subtitle="SLA budget 2000ms"
        />
        <StatCard
          title="Monthly Cost"
          value="$312.45"
          change="Within budget"
          icon={Coins}
          accentColor="amber"
          subtitle="62.5M tokens processed"
        />
      </div>

      {/* Visual Analytics Chart & Quality Gate Status */}
      <div className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* Recharts Execution Volume Curve */}
        <div className="lg:col-span-2 glass-card rounded-xl p-5 border border-tara-border">
          <div className="flex items-center justify-between mb-4">
            <div>
              <h3 className="text-sm font-semibold text-white">Daily Prompt Invocations & Latency</h3>
              <p className="text-xs text-slate-400">Total API traffic passed through TARA PromptOps gateway</p>
            </div>
            <div className="flex items-center gap-3 text-xs">
              <span className="flex items-center gap-1.5 text-cyan-400">
                <span className="w-2.5 h-2.5 rounded-full bg-cyan-400"></span> Requests
              </span>
              <span className="flex items-center gap-1.5 text-indigo-400">
                <span className="w-2.5 h-2.5 rounded-full bg-indigo-400"></span> Latency (ms)
              </span>
            </div>
          </div>

          <div className="h-64 w-full">
            <ResponsiveContainer width="100%" height="100%">
              <AreaChart data={TREND_DATA}>
                <defs>
                  <linearGradient id="colorRequests" x1="0" y1="0" x2="0" y2="1">
                    <stop offset="5%" stopColor="#06b6d4" stopOpacity={0.3}/>
                    <stop offset="95%" stopColor="#06b6d4" stopOpacity={0}/>
                  </linearGradient>
                </defs>
                <XAxis dataKey="time" stroke="#475569" fontSize={11} tickLine={false} />
                <YAxis stroke="#475569" fontSize={11} tickLine={false} />
                <Tooltip
                  contentStyle={{ backgroundColor: '#111726', borderColor: '#24304b', borderRadius: '8px', fontSize: '12px' }}
                />
                <Area type="monotone" dataKey="requests" stroke="#06b6d4" strokeWidth={2} fillOpacity={1} fill="url(#colorRequests)" />
              </AreaChart>
            </ResponsiveContainer>
          </div>
        </div>

        {/* Quality Gate Health Card */}
        <div className="glass-card rounded-xl p-5 border border-tara-border flex flex-col justify-between">
          <div>
            <div className="flex items-center justify-between mb-3">
              <h3 className="text-sm font-semibold text-white">Quality Gate Health</h3>
              <span className="px-2 py-0.5 rounded text-[10px] font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                PASSING
              </span>
            </div>
            <p className="text-xs text-slate-400 mb-4">
              Automated gate evaluation rules enforced before promotion to Production.
            </p>

            <div className="space-y-3">
              <div className="flex items-center justify-between p-2.5 rounded-lg bg-slate-900/60 border border-slate-800 text-xs">
                <span className="text-slate-300">Min LLM Judge Score</span>
                <span className="font-mono text-emerald-400 font-semibold">&gt; 0.85 (Current: 0.94)</span>
              </div>
              <div className="flex items-center justify-between p-2.5 rounded-lg bg-slate-900/60 border border-slate-800 text-xs">
                <span className="text-slate-300">Max P95 Latency</span>
                <span className="font-mono text-emerald-400 font-semibold">&lt; 1500ms (Current: 385ms)</span>
              </div>
              <div className="flex items-center justify-between p-2.5 rounded-lg bg-slate-900/60 border border-slate-800 text-xs">
                <span className="text-slate-300">Security Injections</span>
                <span className="font-mono text-emerald-400 font-semibold">0 Detected (Safe)</span>
              </div>
              <div className="flex items-center justify-between p-2.5 rounded-lg bg-slate-900/60 border border-slate-800 text-xs">
                <span className="text-slate-300">Regression Threshold</span>
                <span className="font-mono text-emerald-400 font-semibold">&lt; -2.0% (Current: +4.2%)</span>
              </div>
            </div>
          </div>

          <Link
            to="/quality-gates"
            className="mt-4 pt-3 border-t border-slate-800 text-xs text-brand-400 hover:text-brand-300 font-medium flex items-center justify-between"
          >
            <span>Configure gate rules</span>
            <ArrowUpRight className="w-3.5 h-3.5" />
          </Link>
        </div>
      </div>

      {/* Recent Prompts & Deployment Pipelines Table */}
      <div className="glass-card rounded-xl p-5 border border-tara-border">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h3 className="text-sm font-semibold text-white">Production Prompt Deployments</h3>
            <p className="text-xs text-slate-400">Currently active semantic prompt versions deployed across environments</p>
          </div>
          <Link to="/prompts" className="text-xs text-brand-400 hover:text-brand-300 font-medium">
            View All Prompts →
          </Link>
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-900/80 text-slate-400 border-b border-slate-800 uppercase font-mono text-[10px]">
              <tr>
                <th className="py-2.5 px-3">Prompt Name</th>
                <th className="py-2.5 px-3">Active Version</th>
                <th className="py-2.5 px-3">Model</th>
                <th className="py-2.5 px-3">Environment</th>
                <th className="py-2.5 px-3">Judge Score</th>
                <th className="py-2.5 px-3">Latency</th>
                <th className="py-2.5 px-3 text-right">Actions</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 text-slate-200">
              <tr className="hover:bg-slate-800/40 transition-colors">
                <td className="py-3 px-3 font-medium text-white flex items-center gap-2">
                  <FileCode2 className="w-4 h-4 text-cyan-400" />
                  <span>Support Ticket Resolution Copilot</span>
                </td>
                <td className="py-3 px-3 font-mono text-brand-400 font-semibold">v1.0.0</td>
                <td className="py-3 px-3 font-mono text-slate-400">gpt-4o</td>
                <td className="py-3 px-3">
                  <span className="px-2 py-0.5 rounded-full text-[10px] font-semibold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                    PRODUCTION
                  </span>
                </td>
                <td className="py-3 px-3 font-mono text-emerald-400 font-semibold">0.96 / 1.0</td>
                <td className="py-3 px-3 font-mono text-slate-300">320ms</td>
                <td className="py-3 px-3 text-right">
                  <Link to="/prompts/editor" className="text-brand-400 hover:text-brand-300 font-medium">
                    Edit Prompt
                  </Link>
                </td>
              </tr>
              <tr className="hover:bg-slate-800/40 transition-colors">
                <td className="py-3 px-3 font-medium text-white flex items-center gap-2">
                  <FileCode2 className="w-4 h-4 text-violet-400" />
                  <span>Customer Intent Classifier</span>
                </td>
                <td className="py-3 px-3 font-mono text-brand-400 font-semibold">v1.2.0-rc</td>
                <td className="py-3 px-3 font-mono text-slate-400">claude-3-5-sonnet</td>
                <td className="py-3 px-3">
                  <span className="px-2 py-0.5 rounded-full text-[10px] font-semibold bg-amber-500/20 text-amber-400 border border-amber-500/30">
                    STAGING
                  </span>
                </td>
                <td className="py-3 px-3 font-mono text-emerald-400 font-semibold">0.92 / 1.0</td>
                <td className="py-3 px-3 font-mono text-slate-300">410ms</td>
                <td className="py-3 px-3 text-right">
                  <Link to="/prompts/history" className="text-brand-400 hover:text-brand-300 font-medium">
                    View Diff
                  </Link>
                </td>
              </tr>
            </tbody>
          </table>
        </div>
      </div>
    </div>
  );
};
