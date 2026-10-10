import React, { useState } from 'react';
import { useAuth } from '../../context/AuthContext';
import { useWorkspace } from '../../context/WorkspaceContext';
import {
  Bell,
  Search,
  ChevronDown,
  LogOut,
  Building2,
  CheckCircle2,
  ShieldCheck,
  AlertTriangle
} from 'lucide-react';

export const Header: React.FC = () => {
  const { user, logout } = useAuth();
  const { activeWorkspace, setActiveWorkspace } = useWorkspace();
  const [showNotifications, setShowNotifications] = useState(false);
  const [showUserMenu, setShowUserMenu] = useState(false);

  return (
    <header className="h-16 bg-tara-surface/80 backdrop-blur-md border-b border-tara-border px-6 flex items-center justify-between sticky top-0 z-30">
      {/* Left: Workspace dropdown */}
      <div className="flex items-center gap-4">
        <div className="flex items-center gap-2 px-3 py-1.5 rounded-lg bg-slate-900/60 border border-tara-border text-xs text-slate-200">
          <Building2 className="w-4 h-4 text-brand-400" />
          <span className="font-semibold text-white">{activeWorkspace}</span>
          <ChevronDown className="w-3.5 h-3.5 text-slate-400" />
        </div>

        {/* Global Environment Indicator */}
        <div className="flex items-center gap-1.5 px-2.5 py-1 rounded-md bg-emerald-500/10 border border-emerald-500/20 text-[11px] font-semibold text-emerald-400">
          <span className="w-2 h-2 rounded-full bg-emerald-400"></span>
          <span>PRODUCTION CLUSTER</span>
        </div>
      </div>

      {/* Center: Global Search Bar */}
      <div className="flex-1 max-w-md mx-8 hidden md:block">
        <div className="relative">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
          <input
            type="text"
            placeholder="Search prompts, versions, test cases, or scans (⌘K)..."
            className="w-full bg-slate-900/80 border border-tara-border rounded-lg pl-9 pr-4 py-1.5 text-xs text-slate-200 placeholder-slate-400 focus:outline-none focus:border-brand-500 focus:ring-1 focus:ring-brand-500 transition-all"
          />
        </div>
      </div>

      {/* Right: Notifications & User Menu */}
      <div className="flex items-center gap-3">
        {/* Notifications Bell */}
        <div className="relative">
          <button
            onClick={() => setShowNotifications(!showNotifications)}
            className="p-2 rounded-lg text-slate-400 hover:text-white hover:bg-slate-800/60 relative transition-all"
            title="Notifications"
          >
            <Bell className="w-4 h-4" />
            <span className="absolute top-1.5 right-1.5 w-2 h-2 rounded-full bg-cyan-400 animate-pulse"></span>
          </button>

          {showNotifications && (
            <div className="absolute right-0 mt-2 w-80 bg-tara-card border border-tara-border rounded-xl shadow-2xl p-4 z-50 text-xs">
              <div className="flex items-center justify-between pb-3 border-b border-slate-800 font-semibold text-white">
                <span>Enterprise Alerts</span>
                <span className="text-[10px] bg-brand-500/20 text-brand-400 px-2 py-0.5 rounded">2 new</span>
              </div>
              <div className="py-2 space-y-2.5">
                <div className="flex items-start gap-2.5 p-2 rounded-lg bg-emerald-500/10 border border-emerald-500/20">
                  <CheckCircle2 className="w-4 h-4 text-emerald-400 mt-0.5 shrink-0" />
                  <div>
                    <p className="font-medium text-slate-200">Regression Suite Passed</p>
                    <p className="text-[11px] text-slate-400">v1.1.0 scored +8.2% on Support Golden Dataset</p>
                  </div>
                </div>
                <div className="flex items-start gap-2.5 p-2 rounded-lg bg-amber-500/10 border border-amber-500/20">
                  <AlertTriangle className="w-4 h-4 text-amber-400 mt-0.5 shrink-0" />
                  <div>
                    <p className="font-medium text-slate-200">Injection Pattern Blocked</p>
                    <p className="text-[11px] text-slate-400">Candidate input neutralized at security gateway</p>
                  </div>
                </div>
              </div>
            </div>
          )}
        </div>

        {/* User Profile */}
        <div className="relative">
          <button
            onClick={() => setShowUserMenu(!showUserMenu)}
            className="flex items-center gap-2.5 p-1.5 pr-2.5 rounded-lg hover:bg-slate-800/60 border border-transparent hover:border-slate-800 transition-all"
          >
            <div className="w-7 h-7 rounded-lg bg-gradient-to-tr from-brand-500 to-violet-500 flex items-center justify-center font-bold text-xs text-white shadow-sm">
              {user?.fullName ? user.fullName[0] : 'S'}
            </div>
            <div className="text-left hidden sm:block">
              <div className="text-xs font-medium text-white">{user?.fullName || 'Sarah Connor'}</div>
              <div className="text-[10px] text-brand-400 font-mono font-semibold">{user?.role || 'ADMIN'}</div>
            </div>
            <ChevronDown className="w-3.5 h-3.5 text-slate-400" />
          </button>

          {showUserMenu && (
            <div className="absolute right-0 mt-2 w-52 bg-tara-card border border-tara-border rounded-xl shadow-2xl py-2 z-50 text-xs">
              <div className="px-4 py-2 border-b border-slate-800">
                <p className="font-semibold text-white">{user?.fullName}</p>
                <p className="text-[11px] text-slate-400 truncate">{user?.email}</p>
              </div>
              <div className="py-1">
                <button
                  onClick={logout}
                  className="w-full px-4 py-2 text-left flex items-center gap-2 text-rose-400 hover:bg-rose-500/10 transition-all font-medium"
                >
                  <LogOut className="w-3.5 h-3.5" />
                  Sign Out
                </button>
              </div>
            </div>
          )}
        </div>
      </div>
    </header>
  );
};
