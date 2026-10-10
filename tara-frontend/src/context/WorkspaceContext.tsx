import React, { createContext, useContext, useState } from 'react';

interface WorkspaceContextType {
  activeWorkspace: string;
  setActiveWorkspace: (ws: string) => void;
  activeProject: string;
  setActiveProject: (p: string) => void;
}

const WorkspaceContext = createContext<WorkspaceContextType | undefined>(undefined);

export const WorkspaceProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [activeWorkspace, setActiveWorkspace] = useState('Core AI Engineering');
  const [activeProject, setActiveProject] = useState('Customer Intelligence Suite');

  return (
    <WorkspaceContext.Provider value={{ activeWorkspace, setActiveWorkspace, activeProject, setActiveProject }}>
      {children}
    </WorkspaceContext.Provider>
  );
};

export const useWorkspace = () => {
  const context = useContext(WorkspaceContext);
  if (!context) throw new Error('useWorkspace must be used within a WorkspaceProvider');
  return context;
};
