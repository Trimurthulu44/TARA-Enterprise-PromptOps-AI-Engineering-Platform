import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Bot, Lock, Mail, ArrowRight, ShieldCheck } from 'lucide-react';

export const LoginPage: React.FC = () => {
  const [email, setEmail] = useState('admin@tara.ai');
  const [password, setPassword] = useState('Admin123!');
  const [error, setError] = useState('');
  const { login, isLoading } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      await login(email, password);
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.message || 'Login failed. Please check credentials.');
    }
  };

  return (
    <div className="min-h-screen bg-tara-bg flex items-center justify-center p-6 selection:bg-brand-500 selection:text-white">
      <div className="glass-card w-full max-w-md rounded-2xl p-8 border border-tara-border shadow-2xl relative">
        <div className="text-center mb-8">
          <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-brand-600 to-cyan-400 flex items-center justify-center mx-auto mb-4 shadow-glow-cyan">
            <Bot className="w-6 h-6 text-white" />
          </div>
          <h2 className="text-2xl font-bold text-white tracking-tight">Enterprise Sign In</h2>
          <p className="text-xs text-slate-400 mt-1">Authenticate into your multi-tenant TARA workspace</p>
        </div>

        {error && (
          <div className="mb-4 p-3 rounded-lg bg-rose-500/10 border border-rose-500/20 text-rose-300 text-xs">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-4">
          <div>
            <label className="text-xs font-medium text-slate-300 block mb-1.5">Enterprise Email</label>
            <div className="relative">
              <Mail className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="developer@enterprise.com"
                className="w-full bg-slate-900/90 border border-tara-border rounded-lg pl-9 pr-4 py-2 text-xs text-white placeholder-slate-400 focus:outline-none focus:border-brand-500 transition-all"
              />
            </div>
          </div>

          <div>
            <label className="text-xs font-medium text-slate-300 block mb-1.5">Password</label>
            <div className="relative">
              <Lock className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="password"
                required
                value={password}
                onChange={(e) => setPassword(e.target.value)}
                className="w-full bg-slate-900/90 border border-tara-border rounded-lg pl-9 pr-4 py-2 text-xs text-white placeholder-slate-400 focus:outline-none focus:border-brand-500 transition-all"
              />
            </div>
          </div>

          <button
            type="submit"
            disabled={isLoading}
            className="w-full py-2.5 rounded-lg bg-gradient-to-r from-brand-600 to-indigo-600 hover:from-brand-500 hover:to-indigo-500 text-white font-semibold text-xs shadow-glow-violet transition-all flex items-center justify-center gap-2 mt-6"
          >
            {isLoading ? 'Authenticating...' : 'Sign In to Workspace'}
            <ArrowRight className="w-4 h-4" />
          </button>
        </form>

        <div className="mt-6 pt-6 border-t border-slate-800 text-center text-xs text-slate-400">
          <span>Need a new organization? </span>
          <Link to="/register" className="text-brand-400 hover:text-brand-300 font-medium">
            Register Tenant
          </Link>
        </div>
      </div>
    </div>
  );
};
