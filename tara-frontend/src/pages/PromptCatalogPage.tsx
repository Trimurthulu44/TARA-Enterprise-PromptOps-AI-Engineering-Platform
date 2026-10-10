import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import { FileCode2, Plus, Search, Tag, GitBranch, ArrowRight, CheckCircle2, AlertCircle } from 'lucide-react';

export const PromptCatalogPage: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedCategory, setSelectedCategory] = useState('ALL');

  const [prompts, setPrompts] = useState([
    {
      id: '66666666-6666-6666-6666-666666666661',
      name: 'Support Ticket Resolution Agent',
      slug: 'support-ticket-resolution',
      description: 'Synthesizes enterprise customer inquiries with verified knowledge base solutions.',
      category: 'CUSTOMER_SUPPORT',
      tags: ['support', 'rag', 'tier1'],
      productionVersion: 'v1.0.0',
      stagingVersion: 'v1.1.0-rc',
      model: 'gpt-4o',
      score: 0.96,
    },
    {
      id: '66666666-6666-6666-6666-666666666662',
      name: 'SQL Query Generator & Sanitizer',
      slug: 'sql-query-generator',
      description: 'Converts natural language analytical questions into dialect-safe PostgreSQL queries.',
      category: 'DATA_EXTRACTION',
      tags: ['sql', 'analytics', 'nl2sql'],
      productionVersion: 'v2.1.0',
      stagingVersion: null,
      model: 'claude-3-5-sonnet',
      score: 0.98,
    },
    {
      id: '66666666-6666-6666-6666-666666666663',
      name: 'Code Review Security Vulnerability Scanner',
      slug: 'code-review-scanner',
      description: 'Inspects pull request diffs for OWASP Top 10 vulnerabilities, secrets, and auth bypasses.',
      category: 'SECURITY',
      tags: ['security', 'owasp', 'code-review'],
      productionVersion: 'v1.0.0',
      stagingVersion: 'v1.0.1',
      model: 'gpt-4o',
      score: 0.94,
    },
  ]);

  const filtered = prompts.filter((p) => {
    const matchesSearch = p.name.toLowerCase().includes(searchTerm.toLowerCase()) || p.slug.includes(searchTerm.toLowerCase());
    const matchesCat = selectedCategory === 'ALL' || p.category === selectedCategory;
    return matchesSearch && matchesCat;
  });

  return (
    <div className="space-y-6">
      <div className="flex flex-col sm:flex-row sm:items-center justify-between gap-4">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Enterprise Prompt Catalog</h1>
          <p className="text-xs text-slate-400 mt-1">Managed prompt library with semantic versions, tags, and deployment pointers</p>
        </div>
        <Link
          to="/prompts/editor"
          className="px-3.5 py-2 rounded-lg bg-gradient-to-r from-brand-600 to-indigo-600 hover:from-brand-500 hover:to-indigo-500 text-white font-medium text-xs shadow-glow-violet flex items-center gap-2 w-fit"
        >
          <Plus className="w-4 h-4" />
          Create Prompt
        </Link>
      </div>

      {/* Filter and Search Bar */}
      <div className="flex flex-col sm:flex-row items-center gap-3">
        <div className="relative flex-1 w-full">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search prompt catalog by name, slug, or keywords..."
            className="w-full bg-slate-900/90 border border-tara-border rounded-lg pl-9 pr-4 py-2 text-xs text-white placeholder-slate-400 focus:outline-none focus:border-brand-500"
          />
        </div>

        <div className="flex items-center gap-2 w-full sm:w-auto">
          {['ALL', 'CUSTOMER_SUPPORT', 'DATA_EXTRACTION', 'SECURITY'].map((cat) => (
            <button
              key={cat}
              onClick={() => setSelectedCategory(cat)}
              className={`px-3 py-1.5 rounded-lg text-xs font-medium transition-all ${
                selectedCategory === cat
                  ? 'bg-brand-600 text-white shadow-sm'
                  : 'bg-slate-900 text-slate-400 hover:text-white border border-tara-border'
              }`}
            >
              {cat.replace('_', ' ')}
            </button>
          ))}
        </div>
      </div>

      {/* Prompts Grid */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-5">
        {filtered.map((prompt) => (
          <div key={prompt.id} className="glass-card rounded-xl p-5 border border-tara-border flex flex-col justify-between">
            <div>
              <div className="flex items-start justify-between mb-3">
                <span className="text-[10px] font-mono px-2 py-0.5 rounded font-semibold bg-brand-500/10 text-brand-300 border border-brand-500/20">
                  {prompt.category}
                </span>
                <span className="flex items-center gap-1 text-[11px] font-mono text-emerald-400 font-bold">
                  <CheckCircle2 className="w-3.5 h-3.5" /> {prompt.score * 100}%
                </span>
              </div>

              <h3 className="font-semibold text-white text-base mb-1">{prompt.name}</h3>
              <p className="text-xs font-mono text-slate-400 mb-2">{prompt.slug}</p>
              <p className="text-xs text-slate-400 mb-4 line-clamp-2">{prompt.description}</p>

              {/* Tags */}
              <div className="flex flex-wrap gap-1.5 mb-4">
                {prompt.tags.map((t) => (
                  <span key={t} className="text-[10px] px-2 py-0.5 rounded bg-slate-900 text-slate-400 border border-slate-800">
                    #{t}
                  </span>
                ))}
              </div>
            </div>

            <div>
              {/* Deployment Version Badges */}
              <div className="p-2.5 rounded-lg bg-slate-950/60 border border-slate-900 text-xs space-y-1.5 mb-4">
                <div className="flex items-center justify-between">
                  <span className="text-slate-400">Production</span>
                  <span className="font-mono text-emerald-400 font-semibold">{prompt.productionVersion || 'None'}</span>
                </div>
                {prompt.stagingVersion && (
                  <div className="flex items-center justify-between">
                    <span className="text-slate-400">Staging</span>
                    <span className="font-mono text-amber-400 font-semibold">{prompt.stagingVersion}</span>
                  </div>
                )}
                <div className="flex items-center justify-between pt-1 border-t border-slate-900 text-[11px]">
                  <span className="text-slate-400">Engine</span>
                  <span className="font-mono text-slate-300">{prompt.model}</span>
                </div>
              </div>

              <div className="flex items-center justify-between pt-3 border-t border-slate-800 text-xs">
                <Link to="/prompts/history" className="text-slate-400 hover:text-white flex items-center gap-1">
                  <GitBranch className="w-3.5 h-3.5" /> History & Diff
                </Link>
                <Link to="/prompts/editor" className="text-cyan-400 hover:text-cyan-300 font-semibold flex items-center gap-1">
                  Open Editor <ArrowRight className="w-3.5 h-3.5" />
                </Link>
              </div>
            </div>
          </div>
        ))}
      </div>
    </div>
  );
};
