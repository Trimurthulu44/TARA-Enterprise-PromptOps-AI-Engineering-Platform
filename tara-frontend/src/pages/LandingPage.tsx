import React, { useState } from 'react';
import { Link } from 'react-router-dom';
import {
  Bot,
  ShieldCheck,
  GitBranch,
  Gauge,
  Sparkles,
  ArrowRight,
  Database,
  Terminal,
  Lock,
  Layers,
  CheckCircle2
} from 'lucide-react';

export const LandingPage: React.FC = () => {
  const [demoInput, setDemoInput] = useState('How do I configure pgvector HNSW indexing?');
  const [demoOutput, setDemoOutput] = useState(
    'To configure pgvector HNSW indexing in PostgreSQL 16:\n1. CREATE EXTENSION IF NOT EXISTS vector;\n2. CREATE INDEX idx_embeddings_hnsw ON prompt_embeddings USING hnsw (embedding vector_cosine_ops) WITH (m = 16, ef_construction = 64);'
  );

  return (
    <div className="min-h-screen bg-tara-bg text-slate-100 flex flex-col font-sans selection:bg-brand-500 selection:text-white">
      {/* Top Navbar */}
      <header className="border-b border-tara-border bg-tara-surface/60 backdrop-blur-md sticky top-0 z-50 px-8 py-4 flex items-center justify-between">
        <div className="flex items-center gap-3">
          <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-brand-600 via-brand-500 to-cyan-400 flex items-center justify-center shadow-glow-cyan">
            <Bot className="w-5 h-5 text-white" />
          </div>
          <div>
            <span className="font-bold text-xl font-mono tracking-tight text-white">TARA</span>
            <span className="ml-2 text-xs text-brand-400 font-semibold uppercase tracking-wider">PromptOps</span>
          </div>
        </div>

        <div className="flex items-center gap-4">
          <Link
            to="/login"
            className="text-xs font-medium text-slate-300 hover:text-white px-3 py-1.5 rounded-lg hover:bg-slate-800/60 transition-all"
          >
            Sign In
          </Link>
          <Link
            to="/dashboard"
            className="text-xs font-semibold px-4 py-2 rounded-lg bg-gradient-to-r from-brand-600 to-indigo-600 hover:from-brand-500 hover:to-indigo-500 text-white shadow-glow-violet transition-all flex items-center gap-2"
          >
            Launch Console <ArrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>
      </header>

      {/* Hero Section */}
      <section className="pt-24 pb-20 px-8 text-center max-w-5xl mx-auto flex-1">
        <div className="inline-flex items-center gap-2 px-3 py-1.5 rounded-full bg-brand-500/10 border border-brand-500/30 text-xs font-semibold text-brand-300 mb-6 shadow-sm">
          <Sparkles className="w-3.5 h-3.5 text-cyan-400" />
          <span>Enterprise AI Engineering & Production Prompt Governance</span>
        </div>

        <h1 className="text-5xl md:text-6xl font-extrabold tracking-tight text-white mb-6 leading-tight">
          The Operating System for <br />
          <span className="gradient-text">Enterprise PromptOps</span>
        </h1>

        <p className="text-lg text-slate-300 max-w-2xl mx-auto mb-10 leading-relaxed">
          TARA sits beneath production AI applications to govern the full prompt lifecycle:
          semantic version control, automated golden dataset testing, LLM-as-a-judge scoring, and prompt injection defense.
        </p>

        <div className="flex items-center justify-center gap-4 mb-16">
          <Link
            to="/dashboard"
            className="px-6 py-3 rounded-xl bg-gradient-to-r from-brand-600 to-cyan-600 hover:from-brand-500 hover:to-cyan-500 text-white font-semibold text-sm shadow-glow-cyan transition-all flex items-center gap-2"
          >
            Explore Live Dashboard <ArrowRight className="w-4 h-4" />
          </Link>
          <Link
            to="/prompts/editor"
            className="px-6 py-3 rounded-xl bg-slate-900 hover:bg-slate-800 text-slate-200 border border-tara-border font-medium text-sm transition-all flex items-center gap-2"
          >
            Open Monaco Editor
          </Link>
        </div>

        {/* Live Interactive Prompt Demo Card */}
        <div className="glass-card rounded-2xl p-6 text-left border border-slate-700/80 shadow-2xl max-w-3xl mx-auto">
          <div className="flex items-center justify-between border-b border-slate-800 pb-3 mb-4">
            <div className="flex items-center gap-2">
              <Terminal className="w-4 h-4 text-cyan-400" />
              <span className="text-xs font-mono font-semibold text-slate-200">Interactive Prompt Runner & Guardrail</span>
            </div>
            <span className="text-[10px] font-mono px-2 py-0.5 rounded bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
              Active: v1.0.0 (Production)
            </span>
          </div>

          <div className="space-y-4">
            <div>
              <label className="text-[11px] font-medium text-slate-400 block mb-1">Input Variable: {'{{customer_query}}'}</label>
              <input
                type="text"
                value={demoInput}
                onChange={(e) => setDemoInput(e.target.value)}
                className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-cyan-500"
              />
            </div>

            <div>
              <label className="text-[11px] font-medium text-slate-400 block mb-1">Target Model Output (LLM Judge Verified)</label>
              <div className="bg-slate-950/80 border border-slate-800 rounded-lg p-3 font-mono text-xs text-slate-300 whitespace-pre-line">
                {demoOutput}
              </div>
            </div>

            <div className="flex items-center justify-between pt-2 border-t border-slate-900 text-xs text-slate-400">
              <span className="flex items-center gap-1.5 text-emerald-400">
                <CheckCircle2 className="w-4 h-4" /> Judge Score: 0.98 / 1.0 (Passed)
              </span>
              <span className="font-mono text-slate-400">P95 Latency: 320ms • Cost: $0.0004</span>
            </div>
          </div>
        </div>
      </section>

      {/* Feature Pillar Highlights */}
      <section className="py-16 bg-slate-950/60 border-t border-tara-border px-8">
        <div className="max-w-6xl mx-auto grid grid-cols-1 md:grid-cols-4 gap-6">
          <div className="glass-card p-5 rounded-xl border border-slate-800">
            <div className="p-2.5 rounded-lg bg-brand-500/10 text-brand-400 w-fit mb-3">
              <GitBranch className="w-5 h-5" />
            </div>
            <h3 className="font-semibold text-white text-sm mb-1.5">Semantic Versioning</h3>
            <p className="text-xs text-slate-400">
              Immutable version history, git-like diffing, staging/production pointers, and instant one-click rollback.
            </p>
          </div>

          <div className="glass-card p-5 rounded-xl border border-slate-800">
            <div className="p-2.5 rounded-lg bg-cyan-500/10 text-cyan-400 w-fit mb-3">
              <Gauge className="w-5 h-5" />
            </div>
            <h3 className="font-semibold text-white text-sm mb-1.5">LLM-as-a-Judge Testing</h3>
            <p className="text-xs text-slate-400">
              Automate regression checks on golden datasets with rubric-based faithfulness, relevance, and tone evaluations.
            </p>
          </div>

          <div className="glass-card p-5 rounded-xl border border-slate-800">
            <div className="p-2.5 rounded-lg bg-rose-500/10 text-rose-400 w-fit mb-3">
              <ShieldCheck className="w-5 h-5" />
            </div>
            <h3 className="font-semibold text-white text-sm mb-1.5">Security Guardrails</h3>
            <p className="text-xs text-slate-400">
              Real-time prompt injection detection, PII sanitization, canary token leakage prevention, and policy gates.
            </p>
          </div>

          <div className="glass-card p-5 rounded-xl border border-slate-800">
            <div className="p-2.5 rounded-lg bg-violet-500/10 text-violet-400 w-fit mb-3">
              <Database className="w-5 h-5" />
            </div>
            <h3 className="font-semibold text-white text-sm mb-1.5">pgvector Semantic Search</h3>
            <p className="text-xs text-slate-400">
              High-dimensional vector embeddings, HNSW approximate nearest neighbors, prompt deduplication, and clustering.
            </p>
          </div>
        </div>
      </section>

      {/* Footer */}
      <footer className="py-6 border-t border-tara-border text-center text-xs text-slate-400">
        <p>© 2026 TARA Platform Inc. Enterprise PromptOps & AI Engineering. All rights reserved.</p>
      </footer>
    </div>
  );
};
