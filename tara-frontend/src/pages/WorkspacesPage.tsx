import React, { useState } from 'react';
import { useWorkspace } from '../context/WorkspaceContext';
import { Layers, Plus, Building2, Check, ArrowRight, ShieldCheck, Users } from 'lucide-react';

export const WorkspacesPage: React.FC = () => {
  const { activeWorkspace, setActiveWorkspace } = useWorkspace();
  const [showModal, setShowModal] = useState(false);
  const [newWsName, setNewWsName] = useState('');
  const [newWsDesc, setNewWsDesc] = useState('');

  const [workspaces, setWorkspaces] = useState([
    { id: '1', name: 'Core AI Engineering', slug: 'core-engineering', isDefault: true, desc: 'Production workspace for enterprise customer Copilots and RAG pipelines', members: 12 },
    { id: '2', name: 'NLP Research & Evaluation', slug: 'nlp-research', isDefault: false, desc: 'Experimental testing ground for fine-tuned open-weights models and prompts', members: 6 },
    { id: '3', name: 'Financial Extraction Agents', slug: 'financial-agents', isDefault: false, desc: 'High-security zero-leakage workspace for 10-K & earnings report processing', members: 8 },
  ]);

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newWsName.trim()) return;
    const newWs = {
      id: String(Date.now()),
      name: newWsName.trim(),
      slug: newWsName.toLowerCase().replace(/\s+/g, '-'),
      isDefault: false,
      desc: newWsDesc,
      members: 1,
    };
    setWorkspaces([...workspaces, newWs]);
    setNewWsName('');
    setNewWsDesc('');
    setShowModal(false);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Enterprise Workspaces</h1>
          <p className="text-xs text-slate-400 mt-1">Isolate projects, applications, API keys, and RBAC permissions per workspace</p>
        </div>
        <button
          onClick={() => setShowModal(true)}
          className="px-3.5 py-2 rounded-lg bg-gradient-to-r from-brand-600 to-indigo-600 hover:from-brand-500 hover:to-indigo-500 text-white font-medium text-xs shadow-glow-violet flex items-center gap-2 transition-all"
        >
          <Plus className="w-4 h-4" />
          Create Workspace
        </button>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        {workspaces.map((ws) => {
          const isActive = activeWorkspace === ws.name;
          return (
            <div
              key={ws.id}
              className={`glass-card rounded-xl p-5 border transition-all duration-200 flex flex-col justify-between ${
                isActive ? 'border-brand-500/80 shadow-glow-violet' : 'border-tara-border'
              }`}
            >
              <div>
                <div className="flex items-start justify-between mb-3">
                  <div className="p-2.5 rounded-lg bg-brand-500/10 text-brand-400 border border-brand-500/20">
                    <Layers className="w-5 h-5" />
                  </div>
                  {isActive && (
                    <span className="flex items-center gap-1 text-[10px] font-bold px-2 py-0.5 rounded-full bg-brand-500/20 text-brand-300 border border-brand-500/30">
                      <Check className="w-3 h-3" /> ACTIVE
                    </span>
                  )}
                </div>

                <h3 className="font-semibold text-white text-base mb-1">{ws.name}</h3>
                <p className="text-xs text-slate-400 mb-4 line-clamp-2">{ws.desc}</p>
              </div>

              <div className="pt-4 border-t border-slate-800 flex items-center justify-between text-xs">
                <span className="flex items-center gap-1.5 text-slate-400">
                  <Users className="w-3.5 h-3.5" /> {ws.members} members
                </span>
                {!isActive ? (
                  <button
                    onClick={() => setActiveWorkspace(ws.name)}
                    className="text-brand-400 hover:text-brand-300 font-medium"
                  >
                    Switch Workspace →
                  </button>
                ) : (
                  <span className="text-slate-400 font-mono text-[11px]">Selected</span>
                )}
              </div>
            </div>
          );
        })}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="glass-card w-full max-w-md rounded-2xl p-6 border border-slate-700 shadow-2xl">
            <h3 className="text-lg font-bold text-white mb-1">Create Enterprise Workspace</h3>
            <p className="text-xs text-slate-400 mb-4">Workspaces partition team members, prompts, and evaluations.</p>

            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="text-xs font-medium text-slate-300 block mb-1">Workspace Name</label>
                <input
                  type="text"
                  required
                  value={newWsName}
                  onChange={(e) => setNewWsName(e.target.value)}
                  placeholder="e.g. Fraud Detection AI"
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-500"
                />
              </div>

              <div>
                <label className="text-xs font-medium text-slate-300 block mb-1">Description</label>
                <textarea
                  rows={3}
                  value={newWsDesc}
                  onChange={(e) => setNewWsDesc(e.target.value)}
                  placeholder="Target workload description and team scope"
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
                  Save Workspace
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
