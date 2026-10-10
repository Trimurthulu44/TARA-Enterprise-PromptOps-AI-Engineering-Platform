import React, { useState } from 'react';
import { AppWindow, Plus, Activity, DollarSign, Clock, ShieldCheck } from 'lucide-react';

export const ApplicationsPage: React.FC = () => {
  const [showModal, setShowModal] = useState(false);
  const [name, setName] = useState('');
  const [appType, setAppType] = useState('RAG');
  const [env, setEnv] = useState('PRODUCTION');
  const [sla, setSla] = useState('1500');

  const [apps, setApps] = useState([
    {
      id: '1',
      name: 'Customer Support RAG Copilot',
      slug: 'support-rag-copilot',
      appType: 'RAG',
      environment: 'PRODUCTION',
      latencySlaMs: 1500,
      costBudgetMonthly: 500,
      status: 'HEALTHY',
      promptsAttached: 8,
    },
    {
      id: '2',
      name: 'Executive Email Drafter',
      slug: 'exec-email-drafter',
      appType: 'AGENT',
      environment: 'STAGING',
      latencySlaMs: 2000,
      costBudgetMonthly: 250,
      status: 'HEALTHY',
      promptsAttached: 4,
    },
    {
      id: '3',
      name: 'Document Analysis Pipeline',
      slug: 'doc-analysis-pipeline',
      appType: 'EXTRACTION',
      environment: 'DEVELOPMENT',
      latencySlaMs: 3000,
      costBudgetMonthly: 1000,
      status: 'HEALTHY',
      promptsAttached: 6,
    },
  ]);

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    setApps([
      ...apps,
      {
        id: String(Date.now()),
        name: name.trim(),
        slug: name.toLowerCase().replace(/\s+/g, '-'),
        appType,
        environment: env,
        latencySlaMs: Number(sla) || 2000,
        costBudgetMonthly: 400,
        status: 'HEALTHY',
        promptsAttached: 1,
      },
    ]);
    setName('');
    setShowModal(false);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">AI Applications Catalog</h1>
          <p className="text-xs text-slate-400 mt-1">Target AI applications that consume governed prompts and report telemetry</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="px-3.5 py-2 rounded-lg bg-gradient-to-r from-brand-600 to-indigo-600 hover:from-brand-500 hover:to-indigo-500 text-white font-medium text-xs shadow-glow-violet flex items-center gap-2"
        >
          <Plus className="w-4 h-4" />
          Register Application
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        {apps.map((app) => (
          <div key={app.id} className="glass-card rounded-xl p-5 border border-tara-border flex flex-col justify-between">
            <div>
              <div className="flex items-center justify-between mb-3">
                <span className="text-[10px] font-mono px-2 py-0.5 rounded font-bold bg-brand-500/20 text-brand-300 border border-brand-500/30">
                  {app.appType}
                </span>
                <span className={`text-[10px] font-mono px-2 py-0.5 rounded-full font-bold ${
                  app.environment === 'PRODUCTION'
                    ? 'bg-emerald-500/20 text-emerald-400 border border-emerald-500/30'
                    : 'bg-amber-500/20 text-amber-400 border border-amber-500/30'
                }`}>
                  {app.environment}
                </span>
              </div>

              <h3 className="font-semibold text-white text-base mb-1">{app.name}</h3>
              <p className="text-xs font-mono text-slate-400 mb-4">{app.slug}</p>
            </div>

            <div>
              <div className="space-y-2 p-3 rounded-lg bg-slate-950/60 border border-slate-900 text-xs mb-4">
                <div className="flex items-center justify-between">
                  <span className="text-slate-400 flex items-center gap-1.5">
                    <Clock className="w-3.5 h-3.5 text-cyan-400" /> Latency SLA
                  </span>
                  <span className="font-mono text-white font-semibold">&lt; {app.latencySlaMs}ms</span>
                </div>
                <div className="flex items-center justify-between">
                  <span className="text-slate-400 flex items-center gap-1.5">
                    <DollarSign className="w-3.5 h-3.5 text-emerald-400" /> Monthly Budget
                  </span>
                  <span className="font-mono text-white font-semibold">${app.costBudgetMonthly}/mo</span>
                </div>
              </div>

              <div className="flex items-center justify-between pt-3 border-t border-slate-800 text-xs">
                <span className="text-slate-400">{app.promptsAttached} prompts mapped</span>
                <span className="flex items-center gap-1 text-emerald-400 font-semibold text-[11px]">
                  <Activity className="w-3 h-3" /> Online
                </span>
              </div>
            </div>
          </div>
        ))}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="glass-card w-full max-w-md rounded-2xl p-6 border border-slate-700 shadow-2xl">
            <h3 className="text-lg font-bold text-white mb-1">Register Target Application</h3>
            <p className="text-xs text-slate-400 mb-4">Configure application metadata and operational SLA boundaries.</p>

            <form onSubmit={handleCreate} className="space-y-3.5">
              <div>
                <label className="text-xs font-medium text-slate-300 block mb-1">Application Name</label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g. Sales Outreach Bot"
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-500"
                />
              </div>

              <div className="grid grid-cols-2 gap-3">
                <div>
                  <label className="text-xs font-medium text-slate-300 block mb-1">Architecture Type</label>
                  <select
                    value={appType}
                    onChange={(e) => setAppType(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-500"
                  >
                    <option value="RAG">RAG</option>
                    <option value="AGENT">Autonomous Agent</option>
                    <option value="CHATBOT">Conversational Chatbot</option>
                    <option value="EXTRACTION">Extraction / ETL</option>
                  </select>
                </div>

                <div>
                  <label className="text-xs font-medium text-slate-300 block mb-1">Environment</label>
                  <select
                    value={env}
                    onChange={(e) => setEnv(e.target.value)}
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-500"
                  >
                    <option value="PRODUCTION">PRODUCTION</option>
                    <option value="STAGING">STAGING</option>
                    <option value="DEVELOPMENT">DEVELOPMENT</option>
                  </select>
                </div>
              </div>

              <div>
                <label className="text-xs font-medium text-slate-300 block mb-1">Latency SLA (ms)</label>
                <input
                  type="number"
                  value={sla}
                  onChange={(e) => setSla(e.target.value)}
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-500"
                />
              </div>

              <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-800">
                <button
                  type="button"
                  onClick={() => setShowModal(false)}
                  className="px-4 py-2 rounded-lg text-xs font-medium text-slate-400 hover:text-white"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  className="px-4 py-2 rounded-lg bg-brand-600 hover:bg-brand-500 text-white font-semibold text-xs"
                >
                  Register App
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
