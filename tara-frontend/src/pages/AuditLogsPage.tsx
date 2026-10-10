import React, { useState } from 'react';
import { History, Search, Shield, Filter, Eye } from 'lucide-react';

export const AuditLogsPage: React.FC = () => {
  const [searchTerm, setSearchTerm] = useState('');
  const [selectedLog, setSelectedLog] = useState<any>(null);

  const logs = [
    {
      id: 'log-1',
      actorEmail: 'admin@tara.ai',
      action: 'PROMPT_PROMOTED_PRODUCTION',
      resourceType: 'PROMPT_VERSION',
      resourceId: 'v1.0.0',
      ipAddress: '192.168.1.104',
      createdAt: '2026-10-10 13:45:12 UTC',
      diffJson: '{"previousVersion": "None", "newVersion": "v1.0.0", "targetEnvironment": "PRODUCTION"}',
    },
    {
      id: 'log-2',
      actorEmail: 'developer@tara.ai',
      action: 'EVALUATION_RUN_COMPLETED',
      resourceType: 'EVALUATION_RUN',
      resourceId: 'eval-run-482',
      ipAddress: '10.0.4.18',
      createdAt: '2026-10-10 12:20:04 UTC',
      diffJson: '{"passedCases": 98, "failedCases": 2, "averageScore": 0.941}',
    },
    {
      id: 'log-3',
      actorEmail: 'admin@tara.ai',
      action: 'API_KEY_GENERATED',
      resourceType: 'API_KEY',
      resourceId: 'tara_live_8f3a9...',
      ipAddress: '192.168.1.104',
      createdAt: '2026-10-09 16:11:45 UTC',
      diffJson: '{"scopes": ["prompts:read", "evaluations:run"], "rateLimit": 600}',
    },
  ];

  const filtered = logs.filter(
    (l) =>
      l.actorEmail.toLowerCase().includes(searchTerm.toLowerCase()) ||
      l.action.toLowerCase().includes(searchTerm.toLowerCase()) ||
      l.resourceType.toLowerCase().includes(searchTerm.toLowerCase())
  );

  return (
    <div className="space-y-6">
      <div className="flex items-center justify-between">
        <div>
          <h1 className="text-2xl font-bold text-white tracking-tight">Enterprise Audit Trail</h1>
          <p className="text-xs text-slate-400 mt-1">Immutable, tenant-isolated log of all administrative and lifecycle operations</p>
        </div>
      </div>

      <div className="glass-card rounded-xl p-5 border border-tara-border">
        {/* Search & Filter */}
        <div className="relative mb-4">
          <Search className="w-4 h-4 text-slate-400 absolute left-3 top-2.5" />
          <input
            type="text"
            value={searchTerm}
            onChange={(e) => setSearchTerm(e.target.value)}
            placeholder="Search audit trail by actor email, action, or resource..."
            className="w-full bg-slate-900/90 border border-tara-border rounded-lg pl-9 pr-4 py-2 text-xs text-white placeholder-slate-400 focus:outline-none focus:border-brand-500"
          />
        </div>

        <div className="overflow-x-auto">
          <table className="w-full text-left text-xs">
            <thead className="bg-slate-900/80 text-slate-400 border-b border-slate-800 uppercase font-mono text-[10px]">
              <tr>
                <th className="py-2.5 px-3">Timestamp (UTC)</th>
                <th className="py-2.5 px-3">Actor Email</th>
                <th className="py-2.5 px-3">Action</th>
                <th className="py-2.5 px-3">Resource Type</th>
                <th className="py-2.5 px-3">IP Address</th>
                <th className="py-2.5 px-3 text-right">Details</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-slate-800/60 text-slate-200">
              {filtered.map((log) => (
                <tr key={log.id} className="hover:bg-slate-800/40 transition-colors">
                  <td className="py-3 px-3 font-mono text-slate-400 text-[11px]">{log.createdAt}</td>
                  <td className="py-3 px-3 font-medium text-white">{log.actorEmail}</td>
                  <td className="py-3 px-3">
                    <span className="font-mono text-[10px] px-2 py-0.5 rounded font-bold bg-brand-500/20 text-brand-300 border border-brand-500/30">
                      {log.action}
                    </span>
                  </td>
                  <td className="py-3 px-3 text-slate-300 font-mono">{log.resourceType}</td>
                  <td className="py-3 px-3 text-slate-400 font-mono text-[11px]">{log.ipAddress}</td>
                  <td className="py-3 px-3 text-right">
                    <button
                      onClick={() => setSelectedLog(log)}
                      className="text-cyan-400 hover:text-cyan-300 font-medium inline-flex items-center gap-1"
                    >
                      <Eye className="w-3.5 h-3.5" /> Inspect
                    </button>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>
      </div>

      {/* Audit Log Inspect Modal */}
      {selectedLog && (
        <div className="fixed inset-0 bg-black/70 backdrop-blur-sm flex items-center justify-center p-4 z-50">
          <div className="glass-card w-full max-w-lg rounded-2xl p-6 border border-slate-700 shadow-2xl">
            <h3 className="text-base font-bold text-white mb-1">Audit Record Details</h3>
            <p className="text-xs text-slate-400 mb-4">{selectedLog.action} on {selectedLog.resourceType}</p>

            <div className="space-y-3 mb-4 text-xs">
              <div className="p-3 rounded-lg bg-slate-950 border border-slate-900 font-mono text-cyan-300 whitespace-pre-wrap">
                {JSON.stringify(JSON.parse(selectedLog.diffJson), null, 2)}
              </div>
            </div>

            <div className="flex justify-end pt-3 border-t border-slate-800">
              <button
                onClick={() => setSelectedLog(null)}
                className="px-4 py-2 rounded-lg bg-slate-800 hover:bg-slate-700 text-white font-medium text-xs"
              >
                Close
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
};
