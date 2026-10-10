import React from 'react';
import { NavLink } from 'react-router-dom';
import {
  LayoutDashboard,
  Layers,
  FolderKanban,
  FileCode2,
  GitBranch,
  SearchCode,
  Sparkles,
  ClipboardCheck,
  Database,
  GitCompare,
  ShieldAlert,
  BarChart3,
  Sliders,
  History,
  KeyRound,
  Settings,
  Bot
} from 'lucide-react';

const NAV_ITEMS = [
  { section: 'CORE PLATFORM' },
  { path: '/dashboard', label: 'Dashboard', icon: LayoutDashboard },
  { path: '/workspaces', label: 'Workspaces', icon: Layers },
  { path: '/projects', label: 'Projects & Apps', icon: FolderKanban },

  { section: 'PROMPTOPS LIFECYCLE' },
  { path: '/prompts', label: 'Prompt Catalog', icon: FileCode2 },
  { path: '/prompts/editor', label: 'Monaco Editor', icon: Bot, highlight: true },
  { path: '/prompts/history', label: 'Version Diff & History', icon: GitBranch },
  { path: '/analyzer', label: 'Prompt Analyzer', icon: SearchCode },
  { path: '/optimizer', label: 'Prompt Optimizer', icon: Sparkles },

  { section: 'TESTING & QUALITY GATES' },
  { path: '/evaluations', label: 'Batch Evaluations', icon: ClipboardCheck },
  { path: '/datasets', label: 'Golden Datasets', icon: Database },
  { path: '/regression', label: 'Regression Suite', icon: GitCompare },
  { path: '/quality-gates', label: 'Quality Gates', icon: Sliders },

  { section: 'SECURITY & OBSERVABILITY' },
  { path: '/security', label: 'Security Guardrails', icon: ShieldAlert },
  { path: '/analytics', label: 'Analytics & Costs', icon: BarChart3 },
  { path: '/audit-logs', label: 'Audit Trail', icon: History },
  { path: '/api-keys', label: 'API Keys', icon: KeyRound },
  { path: '/settings', label: 'Settings & Admin', icon: Settings },
];

export const Sidebar: React.FC = () => {
  return (
    <aside className="w-64 bg-tara-surface border-r border-tara-border flex flex-col h-screen select-none shrink-0">
      {/* Brand Logo Header */}
      <div className="h-16 flex items-center px-6 border-b border-tara-border gap-3">
        <div className="w-9 h-9 rounded-xl bg-gradient-to-tr from-brand-600 via-brand-500 to-cyan-400 flex items-center justify-center shadow-glow-cyan">
          <Bot className="w-5 h-5 text-white" />
        </div>
        <div>
          <div className="flex items-center gap-1.5">
            <span className="font-bold tracking-tight text-lg text-white font-mono">TARA</span>
            <span className="text-[10px] font-semibold bg-brand-500/20 text-brand-400 px-1.5 py-0.5 rounded border border-brand-500/30">v1.0</span>
          </div>
          <p className="text-[11px] text-slate-400 tracking-wide">Enterprise PromptOps</p>
        </div>
      </div>

      {/* Nav Link Tree */}
      <nav className="flex-1 overflow-y-auto px-3 py-4 space-y-1">
        {NAV_ITEMS.map((item, idx) => {
          if (item.section) {
            return (
              <div key={idx} className="pt-4 pb-1 px-3 text-[10px] font-bold text-slate-400 tracking-wider">
                {item.section}
              </div>
            );
          }
          const Icon = item.icon!;
          return (
            <NavLink
              key={item.path}
              to={item.path!}
              className={({ isActive }) => `
                flex items-center justify-between px-3 py-2 rounded-lg text-xs font-medium transition-all duration-150
                ${isActive
                  ? 'bg-brand-600/20 text-brand-300 border border-brand-500/40 shadow-sm'
                  : 'text-slate-400 hover:text-slate-200 hover:bg-slate-800/50'}
              `}
            >
              <div className="flex items-center gap-3">
                <Icon className="w-4 h-4 shrink-0" />
                <span>{item.label}</span>
              </div>
              {item.highlight && (
                <span className="w-2 h-2 rounded-full bg-cyan-400 shadow-glow-cyan animate-pulse"></span>
              )}
            </NavLink>
          );
        })}
      </nav>

      {/* Bottom Status Card */}
      <div className="p-3 border-t border-tara-border">
        <div className="p-3 rounded-lg bg-slate-900/60 border border-slate-800/80">
          <div className="flex items-center justify-between mb-1.5">
            <span className="text-[11px] font-medium text-slate-300">Cluster Status</span>
            <span className="flex items-center gap-1 text-[10px] text-emerald-400 font-semibold">
              <span className="w-1.5 h-1.5 rounded-full bg-emerald-400"></span> Healthy
            </span>
          </div>
          <div className="text-[10px] text-slate-500">PostgreSQL + pgvector • Redis 7</div>
        </div>
      </div>
    </aside>
  );
};
