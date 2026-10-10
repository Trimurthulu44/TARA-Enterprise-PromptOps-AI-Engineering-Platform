import React, { useState } from 'react';
import { useWorkspace } from '../context/WorkspaceContext';
import { FolderKanban, Plus, Cpu, ArrowRight, AppWindow } from 'lucide-react';
import { Link } from 'react-router-dom';

export const ProjectsPage: React.FC = () => {
  const { activeWorkspace, activeProject, setActiveProject } = useWorkspace();
  const [showModal, setShowModal] = useState(false);
  const [name, setName] = useState('');
  const [desc, setDesc] = useState('');

  const [projects, setProjects] = useState([
    {
      id: '1',
      name: 'Customer Intelligence Suite',
      slug: 'customer-intelligence',
      description: 'Enterprise support Copilots and ticket sentiment extraction pipelines.',
      appsCount: 3,
      promptsCount: 14,
    },
    {
      id: '2',
      name: 'Code Review Copilot Engine',
      slug: 'code-review-engine',
      description: 'Static analysis and LLM-assisted pull request code reviews and security audits.',
      appsCount: 2,
      promptsCount: 9,
    },
    {
      id: '3',
      name: 'Document Extraction & RAG',
      slug: 'document-rag',
      description: 'Financial 10-K parsing, invoice extraction, and knowledge graph querying.',
      appsCount: 4,
      promptsCount: 25,
    },
  ]);

  const handleCreate = (e: React.FormEvent) => {
    e.preventDefault();
    if (!name.trim()) return;
    setProjects([
      ...projects,
      {
        id: String(Date.now()),
        name: name.trim(),
        slug: name.toLowerCase().replace(/\s+/g, '-'),
        description: desc,
        appsCount: 0,
        promptsCount: 0,
      },
    ]);
    setName('');
    setDesc('');
    setShowModal(false);
  };

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">AI Projects & Applications</h1>
          <p className="text-xs text-slate-400 mt-1">
            Projects in <span className="text-brand-400 font-semibold">{activeWorkspace}</span>
          </p>
        </div>
        <div className="flex items-center gap-3">
          <Link
            to="/applications"
            className="px-3.5 py-2 rounded-lg bg-slate-900 hover:bg-slate-800 text-xs text-slate-200 border border-tara-border flex items-center gap-2"
          >
            <AppWindow className="w-3.5 h-3.5 text-cyan-400" />
            Manage Applications
          </Link>
          <button
            onClick={() => setShowModal(true)}
            className="px-3.5 py-2 rounded-lg bg-gradient-to-r from-brand-600 to-indigo-600 hover:from-brand-500 hover:to-indigo-500 text-white font-medium text-xs shadow-glow-violet flex items-center gap-2"
          >
            <Plus className="w-4 h-4" />
            New Project
          </button>
        </div>
      </div>

      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        {projects.map((proj) => {
          const isSelected = activeProject === proj.name;
          return (
            <div
              key={proj.id}
              className={`glass-card rounded-xl p-5 border transition-all duration-200 flex flex-col justify-between ${
                isSelected ? 'border-cyan-500/80 shadow-glow-cyan' : 'border-tara-border'
              }`}
            >
              <div>
                <div className="flex items-center justify-between mb-3">
                  <div className="p-2.5 rounded-lg bg-cyan-500/10 text-cyan-400 border border-cyan-500/20">
                    <FolderKanban className="w-5 h-5" />
                  </div>
                  {isSelected && (
                    <span className="text-[10px] font-bold px-2 py-0.5 rounded-full bg-cyan-500/20 text-cyan-300 border border-cyan-500/30">
                      CURRENT
                    </span>
                  )}
                </div>

                <h3 className="font-semibold text-white text-base mb-1">{proj.name}</h3>
                <p className="text-xs text-slate-400 mb-4 line-clamp-2">{proj.description}</p>
              </div>

              <div>
                <div className="grid grid-cols-2 gap-2 mb-4 p-2.5 rounded-lg bg-slate-950/60 border border-slate-900 text-xs">
                  <div>
                    <span className="text-slate-400 text-[11px] block">Applications</span>
                    <span className="font-mono text-white font-semibold">{proj.appsCount} Apps</span>
                  </div>
                  <div>
                    <span className="text-slate-400 text-[11px] block">Prompts</span>
                    <span className="font-mono text-cyan-400 font-semibold">{proj.promptsCount} Prompts</span>
                  </div>
                </div>

                <div className="flex items-center justify-between pt-3 border-t border-slate-800 text-xs">
                  <button
                    onClick={() => setActiveProject(proj.name)}
                    className="text-brand-400 hover:text-brand-300 font-medium"
                  >
                    Select Project
                  </button>
                  <Link to="/prompts" className="text-slate-400 hover:text-white flex items-center gap-1 font-medium">
                    View Prompts <ArrowRight className="w-3 h-3" />
                  </Link>
                </div>
              </div>
            </div>
          );
        })}
      </div>

      {showModal && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="glass-card w-full max-w-md rounded-2xl p-6 border border-slate-700 shadow-2xl">
            <h3 className="text-lg font-bold text-white mb-1">Create AI Project</h3>
            <p className="text-xs text-slate-400 mb-4">Projects group applications, prompt templates, and evaluation datasets.</p>

            <form onSubmit={handleCreate} className="space-y-4">
              <div>
                <label className="text-xs font-medium text-slate-300 block mb-1">Project Name</label>
                <input
                  type="text"
                  required
                  value={name}
                  onChange={(e) => setName(e.target.value)}
                  placeholder="e.g. Healthcare Diagnostics AI"
                  className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-500"
                />
              </div>

              <div>
                <label className="text-xs font-medium text-slate-300 block mb-1">Description</label>
                <textarea
                  rows={3}
                  value={desc}
                  onChange={(e) => setDesc(e.target.value)}
                  placeholder="Purpose of this AI project and expected capabilities"
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
                  Save Project
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
};
