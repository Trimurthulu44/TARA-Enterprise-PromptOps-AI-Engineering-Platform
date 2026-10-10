import React from 'react';
import { DiffEditor } from '@monaco-editor/react';

interface MonacoDiffViewerProps {
  original: string; // Baseline
  modified: string; // Candidate
  height?: string;
  originalTitle?: string;
  modifiedTitle?: string;
}

export const MonacoDiffViewer: React.FC<MonacoDiffViewerProps> = ({
  original,
  modified,
  height = '500px',
  originalTitle = 'Baseline (Production)',
  modifiedTitle = 'Candidate (v1.1.0)',
}) => {
  return (
    <div className="rounded-xl overflow-hidden border border-tara-border bg-[#0f172a] shadow-2xl flex flex-col">
      {/* Diff Header Bar */}
      <div className="flex items-center justify-between px-4 py-2 bg-slate-900 border-b border-tara-border text-xs">
        <div className="flex items-center gap-2">
          <span className="w-2.5 h-2.5 rounded-full bg-rose-500/80"></span>
          <span className="font-semibold text-rose-300 font-mono">{originalTitle}</span>
        </div>
        <div className="flex items-center gap-2">
          <span className="w-2.5 h-2.5 rounded-full bg-emerald-500/80"></span>
          <span className="font-semibold text-emerald-300 font-mono">{modifiedTitle}</span>
        </div>
      </div>

      <DiffEditor
        height={height}
        theme="vs-dark"
        language="markdown"
        original={original}
        modified={modified}
        options={{
          readOnly: true,
          minimap: { enabled: false },
          fontSize: 13,
          fontFamily: "'JetBrains Mono', 'Fira Code', monospace",
          lineNumbers: 'on',
          automaticLayout: true,
          renderSideBySide: true,
          scrollBeyondLastLine: false,
        }}
      />
    </div>
  );
};
