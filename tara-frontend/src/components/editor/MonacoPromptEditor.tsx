import React, { useRef } from 'react';
import Editor, { OnMount } from '@monaco-editor/react';

interface MonacoPromptEditorProps {
  value: string;
  onChange: (value: string) => void;
  language?: string;
  height?: string;
  readOnly?: boolean;
}

export const MonacoPromptEditor: React.FC<MonacoPromptEditorProps> = ({
  value,
  onChange,
  language = 'markdown',
  height = '450px',
  readOnly = false,
}) => {
  const editorRef = useRef<any>(null);

  const handleEditorDidMount: OnMount = (editor, monaco) => {
    editorRef.current = editor;

    // Define subtle custom dark theme rules for prompt engineering
    monaco.editor.defineTheme('taraDark', {
      base: 'vs-dark',
      inherit: true,
      rules: [
        { token: 'comment', foreground: '64748b', fontStyle: 'italic' },
        { token: 'keyword', foreground: '818cf8', fontStyle: 'bold' },
        { token: 'string', foreground: '38bdf8' },
        { token: 'variable', foreground: '06b6d4', fontStyle: 'bold' },
      ],
      colors: {
        'editor.background': '#0f172a',
        'editor.foreground': '#e2e8f0',
        'editor.lineHighlightBackground': '#1e293b50',
        'editorLineNumber.foreground': '#475569',
        'editorLineNumber.activeForeground': '#94a3b8',
        'editor.selectionBackground': '#3730a370',
        'editorCursor.foreground': '#38bdf8',
      },
    });

    monaco.editor.setTheme('taraDark');
  };

  return (
    <div className="rounded-xl overflow-hidden border border-tara-border bg-[#0f172a] shadow-2xl relative">
      <Editor
        height={height}
        language={language}
        value={value}
        onChange={(val) => onChange(val || '')}
        onMount={handleEditorDidMount}
        options={{
          theme: 'vs-dark',
          minimap: { enabled: false },
          fontSize: 13,
          fontFamily: "'JetBrains Mono', 'Fira Code', monospace",
          lineNumbers: 'on',
          wordWrap: 'on',
          scrollBeyondLastLine: false,
          automaticLayout: true,
          readOnly,
          padding: { top: 12, bottom: 12 },
          renderLineHighlight: 'all',
          cursorBlinking: 'smooth',
          bracketPairColorization: { enabled: true },
        }}
      />
    </div>
  );
};
