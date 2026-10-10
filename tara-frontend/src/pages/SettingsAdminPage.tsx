import React, { useState } from 'react';
import { Settings, KeyRound, Users, ShieldCheck, Plus, Copy, Check, Trash2, AlertTriangle } from 'lucide-react';

export const SettingsAdminPage: React.FC = () => {
  const [activeTab, setActiveTab] = useState<'members' | 'apikeys' | 'quotas'>('apikeys');
  const [showKeyModal, setShowKeyModal] = useState(false);
  const [newKeyName, setNewKeyName] = useState('');
  const [generatedRawKey, setGeneratedRawKey] = useState<string | null>(null);
  const [copied, setCopied] = useState(false);

  const [apiKeys, setApiKeys] = useState([
    {
      id: '1',
      name: 'Production AI Gateway Key',
      keyPrefix: 'tara_live_8f3a...',
      scopes: 'prompts:read, evaluations:run',
      rateLimit: 600,
      createdAt: '2026-10-08',
      status: 'ACTIVE',
    },
    {
      id: '2',
      name: 'CI/CD Regression Runner',
      keyPrefix: 'tara_live_12c9...',
      scopes: 'prompts:read, evaluations:run, security:scan',
      rateLimit: 1200,
      createdAt: '2026-10-09',
      status: 'ACTIVE',
    },
  ]);

  const [members, setMembers] = useState([
    { id: '1', name: 'Sarah Connor', email: 'admin@tara.ai', role: 'ADMIN', status: 'ACTIVE' },
    { id: '2', name: 'Alex Murphy', email: 'developer@tara.ai', role: 'DEVELOPER', status: 'ACTIVE' },
    { id: '3', name: 'Marcus Wright', email: 'viewer@tara.ai', role: 'VIEWER', status: 'ACTIVE' },
  ]);

  const handleGenerateKey = (e: React.FormEvent) => {
    e.preventDefault();
    if (!newKeyName.trim()) return;
    const randomHex = Array.from({ length: 48 }, () => Math.floor(Math.random() * 16).toString(16)).join('');
    const raw = `tara_live_${randomHex}`;
    setGeneratedRawKey(raw);
    setApiKeys([
      ...apiKeys,
      {
        id: String(Date.now()),
        name: newKeyName.trim(),
        keyPrefix: `tara_live_${randomHex.substring(0, 4)}...`,
        scopes: 'prompts:read, evaluations:run',
        rateLimit: 600,
        createdAt: '2026-10-10',
        status: 'ACTIVE',
      },
    ]);
  };

  const copyToClipboard = () => {
    if (generatedRawKey) {
      navigator.clipboard.writeText(generatedRawKey);
      setCopied(true);
      setTimeout(() => setCopied(false), 2000);
    }
  };

  return (
    <div className="space-y-6">
      <div>
        <h1 className="text-2xl font-bold text-white tracking-tight">Organization Settings & Administration</h1>
        <p className="text-xs text-slate-400 mt-1">Manage team members, programmatic API keys, quotas, and security policies</p>
      </div>

      {/* Tabs */}
      <div className="flex items-center gap-2 border-b border-tara-border pb-2">
        <button
          onClick={() => setActiveTab('apikeys')}
          className={`px-4 py-2 rounded-lg text-xs font-semibold transition-all flex items-center gap-2 ${
            activeTab === 'apikeys'
              ? 'bg-brand-600 text-white shadow-sm'
              : 'text-slate-400 hover:text-white hover:bg-slate-900'
          }`}
        >
          <KeyRound className="w-3.5 h-3.5" /> API Keys & Access
        </button>
        <button
          onClick={() => setActiveTab('members')}
          className={`px-4 py-2 rounded-lg text-xs font-semibold transition-all flex items-center gap-2 ${
            activeTab === 'members'
              ? 'bg-brand-600 text-white shadow-sm'
              : 'text-slate-400 hover:text-white hover:bg-slate-900'
          }`}
        >
          <Users className="w-3.5 h-3.5" /> Team Members & RBAC
        </button>
        <button
          onClick={() => setActiveTab('quotas')}
          className={`px-4 py-2 rounded-lg text-xs font-semibold transition-all flex items-center gap-2 ${
            activeTab === 'quotas'
              ? 'bg-brand-600 text-white shadow-sm'
              : 'text-slate-400 hover:text-white hover:bg-slate-900'
          }`}
        >
          <ShieldCheck className="w-3.5 h-3.5" /> Plan & Enterprise Quotas
        </button>
      </div>

      {/* Tab: API Keys */}
      {activeTab === 'apikeys' && (
        <div className="glass-card rounded-xl p-5 border border-tara-border space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-semibold text-white">Programmatic API Keys</h3>
              <p className="text-xs text-slate-400">Used by Python, TypeScript, and Java SDKs to fetch and render prompts</p>
            </div>
            <button
              onClick={() => {
                setGeneratedRawKey(null);
                setNewKeyName('');
                setShowKeyModal(true);
              }}
              className="px-3.5 py-2 rounded-lg bg-gradient-to-r from-brand-600 to-indigo-600 hover:from-brand-500 hover:to-indigo-500 text-white font-medium text-xs shadow-glow-violet flex items-center gap-2"
            >
              <Plus className="w-4 h-4" /> Generate API Key
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-900/80 text-slate-400 border-b border-slate-800 uppercase font-mono text-[10px]">
                <tr>
                  <th className="py-2.5 px-3">Key Name</th>
                  <th className="py-2.5 px-3">Key Prefix</th>
                  <th className="py-2.5 px-3">Scopes</th>
                  <th className="py-2.5 px-3">Rate Limit</th>
                  <th className="py-2.5 px-3">Status</th>
                  <th className="py-2.5 px-3 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60 text-slate-200">
                {apiKeys.map((k) => (
                  <tr key={k.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="py-3 px-3 font-medium text-white">{k.name}</td>
                    <td className="py-3 px-3 font-mono text-cyan-300 font-semibold">{k.keyPrefix}</td>
                    <td className="py-3 px-3 text-slate-400 font-mono text-[11px]">{k.scopes}</td>
                    <td className="py-3 px-3 font-mono text-slate-300">{k.rateLimit} req/min</td>
                    <td className="py-3 px-3">
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500/20 text-emerald-400 border border-emerald-500/30">
                        {k.status}
                      </span>
                    </td>
                    <td className="py-3 px-3 text-right">
                      <button
                        onClick={() => setApiKeys(apiKeys.filter((item) => item.id !== k.id))}
                        className="text-rose-400 hover:text-rose-300"
                        title="Revoke Key"
                      >
                        <Trash2 className="w-3.5 h-3.5" />
                      </button>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Tab: Members */}
      {activeTab === 'members' && (
        <div className="glass-card rounded-xl p-5 border border-tara-border space-y-4">
          <div className="flex items-center justify-between">
            <div>
              <h3 className="text-sm font-semibold text-white">Workspace Members & RBAC</h3>
              <p className="text-xs text-slate-400">Manage user roles: Admin, Developer, and Viewer</p>
            </div>
            <button
              onClick={() => alert('Invite member dialog')}
              className="px-3.5 py-2 rounded-lg bg-brand-600 hover:bg-brand-500 text-white font-medium text-xs flex items-center gap-2"
            >
              <Plus className="w-4 h-4" /> Invite Member
            </button>
          </div>

          <div className="overflow-x-auto">
            <table className="w-full text-left text-xs">
              <thead className="bg-slate-900/80 text-slate-400 border-b border-slate-800 uppercase font-mono text-[10px]">
                <tr>
                  <th className="py-2.5 px-3">Name</th>
                  <th className="py-2.5 px-3">Email</th>
                  <th className="py-2.5 px-3">Role</th>
                  <th className="py-2.5 px-3">Status</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-slate-800/60 text-slate-200">
                {members.map((m) => (
                  <tr key={m.id} className="hover:bg-slate-800/40 transition-colors">
                    <td className="py-3 px-3 font-medium text-white">{m.name}</td>
                    <td className="py-3 px-3 text-slate-300">{m.email}</td>
                    <td className="py-3 px-3 font-mono font-bold text-cyan-400">{m.role}</td>
                    <td className="py-3 px-3">
                      <span className="px-2 py-0.5 rounded-full text-[10px] font-bold bg-emerald-500/20 text-emerald-400">
                        {m.status}
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </div>
      )}

      {/* Tab: Quotas */}
      {activeTab === 'quotas' && (
        <div className="glass-card rounded-xl p-5 border border-tara-border space-y-4">
          <h3 className="text-sm font-semibold text-white">Enterprise Tier & Quota Limits</h3>
          <p className="text-xs text-slate-400">Acme AI Enterprise Plan (Annual License)</p>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-4 pt-2">
            <div className="p-4 rounded-xl bg-slate-950/70 border border-slate-900">
              <p className="text-xs text-slate-400 mb-1">Max Managed Prompts</p>
              <p className="text-2xl font-bold font-mono text-white">48 / 10,000</p>
              <div className="w-full bg-slate-800 h-1.5 rounded-full mt-3 overflow-hidden">
                <div className="bg-cyan-400 h-full w-[1%]"></div>
              </div>
            </div>

            <div className="p-4 rounded-xl bg-slate-950/70 border border-slate-900">
              <p className="text-xs text-slate-400 mb-1">Monthly Evaluations</p>
              <p className="text-2xl font-bold font-mono text-white">1,420 / 100,000</p>
              <div className="w-full bg-slate-800 h-1.5 rounded-full mt-3 overflow-hidden">
                <div className="bg-emerald-400 h-full w-[2%]"></div>
              </div>
            </div>

            <div className="p-4 rounded-xl bg-slate-950/70 border border-slate-900">
              <p className="text-xs text-slate-400 mb-1">Workspace Members</p>
              <p className="text-2xl font-bold font-mono text-white">3 / 100</p>
              <div className="w-full bg-slate-800 h-1.5 rounded-full mt-3 overflow-hidden">
                <div className="bg-brand-400 h-full w-[3%]"></div>
              </div>
            </div>
          </div>
        </div>
      )}

      {/* API Key Modal */}
      {showKeyModal && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="glass-card w-full max-w-md rounded-2xl p-6 border border-slate-700 shadow-2xl">
            <h3 className="text-lg font-bold text-white mb-1">Generate Programmatic API Key</h3>
            <p className="text-xs text-slate-400 mb-4">Keys allow applications and SDKs to render prompts securely.</p>

            {!generatedRawKey ? (
              <form onSubmit={handleGenerateKey} className="space-y-4">
                <div>
                  <label className="text-xs font-medium text-slate-300 block mb-1">Key Name</label>
                  <input
                    type="text"
                    required
                    value={newKeyName}
                    onChange={(e) => setNewKeyName(e.target.value)}
                    placeholder="e.g. Production RAG Service"
                    className="w-full bg-slate-950 border border-slate-800 rounded-lg px-3 py-2 text-xs text-white focus:outline-none focus:border-brand-500"
                  />
                </div>

                <div className="flex items-center justify-end gap-3 pt-3 border-t border-slate-800">
                  <button
                    type="button"
                    onClick={() => setShowKeyModal(false)}
                    className="px-4 py-2 rounded-lg text-xs font-medium text-slate-400 hover:text-white"
                  >
                    Cancel
                  </button>
                  <button
                    type="submit"
                    className="px-4 py-2 rounded-lg bg-brand-600 hover:bg-brand-500 text-white font-semibold text-xs"
                  >
                    Generate Key
                  </button>
                </div>
              </form>
            ) : (
              <div className="space-y-4">
                <div className="p-3 rounded-lg bg-amber-500/10 border border-amber-500/30 text-xs text-amber-300 flex items-start gap-2">
                  <AlertTriangle className="w-4 h-4 shrink-0 mt-0.5" />
                  <span>
                    Copy this API key now. It is hashed with SHA-256 and will never be shown again!
                  </span>
                </div>

                <div className="p-3 rounded-lg bg-slate-950 border border-slate-800 font-mono text-xs text-cyan-300 break-all select-all flex items-center justify-between">
                  <span>{generatedRawKey}</span>
                  <button
                    onClick={copyToClipboard}
                    className="p-1.5 rounded hover:bg-slate-800 text-slate-400 hover:text-white shrink-0 ml-2"
                  >
                    {copied ? <Check className="w-4 h-4 text-emerald-400" /> : <Copy className="w-4 h-4" />}
                  </button>
                </div>

                <div className="flex justify-end pt-3 border-t border-slate-800">
                  <button
                    onClick={() => setShowKeyModal(false)}
                    className="px-4 py-2 rounded-lg bg-brand-600 hover:bg-brand-500 text-white font-semibold text-xs"
                  >
                    I Have Stored the Key
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      )}
    </div>
  );
};
