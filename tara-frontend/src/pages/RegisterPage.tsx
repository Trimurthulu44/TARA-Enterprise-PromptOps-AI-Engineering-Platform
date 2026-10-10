import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import { Bot, Lock, Mail, Building2, User as UserIcon, ArrowRight } from 'lucide-react';

export const RegisterPage: React.FC = () => {
  const [fullName, setFullName] = useState('Alex Mercer');
  const [email, setEmail] = useState('alex@enterprise.ai');
  const [password, setPassword] = useState('SecurePass2026!');
  const [organizationName, setOrganizationName] = useState('Mercer Labs AI');
  const [error, setError] = useState('');
  const { register, isLoading } = useAuth();
  const navigate = useNavigate();

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    setError('');
    try {
      await register({ fullName, email, password, organizationName });
      navigate('/dashboard');
    } catch (err: any) {
      setError(err.message || 'Registration failed.');
    }
  };

  return (
    <div className="min-h-screen bg-tara-bg flex items-center justify-center p-6 selection:bg-brand-500 selection:text-white">
      <div className="glass-card w-full max-w-md rounded-2xl p-8 border border-tara-border shadow-2xl relative">
        <div className="text-center mb-6">
          <div className="w-12 h-12 rounded-2xl bg-gradient-to-tr from-brand-600 to-cyan-400 flex items-center justify-center mx-auto mb-4 shadow-glow-cyan">
            <Bot className="w-6 h-6 text-white" />
          </div>
          <h2 className="text-2xl font-bold text-white tracking-tight">Register Organization</h2>
          <p className="text-xs text-slate-400 mt-1">Create an enterprise tenant and administrator account</p>
        </div>

        {error && (
          <div className="mb-4 p-3 rounded-lg bg-rose-500/10 border border-rose-500/20 text-rose-300 text-xs">
            {error}
          </div>
        )}

        <form onSubmit={handleSubmit} className="space-y-3.5">
          <div>
            <label className="text-xs font-medium text-slate-300 block mb-1">Organization Name</label>
            <div className="relative">
              <Building2 className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="text"
                required
                value={organizationName}
                onChange={(e) => setOrganizationName(e.target.value)}
                placeholder="Acme Global Inc"
                className="w-full bg-slate-900/90 border border-tara-border rounded-lg pl-9 pr-4 py-2 text-xs text-white placeholder-slate-400 focus:outline-none focus:border-brand-500 transition-all"
              />
            </div>
          </div>

          <div>
            <label className="text-xs font-medium text-slate-300 block mb-1">Full Name</label>
            <div className="relative">
              <UserIcon className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="text"
                required
                value={fullName}
                onChange={(e) => setFullName(e.target.value)}
                placeholder="John Doe"
                className="w-full bg-slate-900/90 border border-tara-border rounded-lg pl-9 pr-4 py-2 text-xs text-white placeholder-slate-400 focus:outline-none focus:border-brand-500 transition-all"
              />
            </div>
          </div>

          <div>
            <label className="text-xs font-medium text-slate-300 block mb-1">Enterprise Email</label>
            <div className="relative">
              <Mail className="w-4 h-4 text-slate-400 absolute left-3 top-3" />
              <input
                type="email"
                required
                value={email}
                onChange={(e) => setEmail(e.target.value)}
                placeholder="admin@enterprise.com"
                className="w-full bg-slate-900/90 border border-tara-border rounded-lg pl-9 pr-4 py-2 text-xs text-white placeholder-slate-400 focus:outline-none focus:border-brand-500 transition-all"
              />
            </div>
          </div>

          <div>
            <label className="text-xs font-medium text-slate-300 block mb-1">Password</label>
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
            className="w-full py-2.5 rounded-lg bg-gradient-to-r from-brand-600 to-indigo-600 hover:from-brand-500 hover:to-indigo-500 text-white font-semibold text-xs shadow-glow-violet transition-all flex items-center justify-center gap-2 mt-5"
          >
            {isLoading ? 'Creating Workspace...' : 'Create Organization Workspace'}
            <ArrowRight className="w-4 h-4" />
          </button>
        </form>

        <div className="mt-5 pt-5 border-t border-slate-800 text-center text-xs text-slate-400">
          <span>Already registered? </span>
          <Link to="/login" className="text-brand-400 hover:text-brand-300 font-medium">
            Sign In
          </Link>
        </div>
      </div>
    </div>
  );
};
