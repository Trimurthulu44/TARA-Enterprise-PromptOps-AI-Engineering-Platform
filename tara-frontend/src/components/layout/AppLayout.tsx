import React from 'react';
import { Outlet } from 'react-router-dom';
import { Sidebar } from './Sidebar';
import { Header } from './Header';

export const AppLayout: React.FC = () => {
  return (
    <div className="flex h-screen bg-tara-bg overflow-hidden font-sans">
      <Sidebar />
      <div className="flex-1 flex flex-col min-w-0 overflow-hidden">
        <Header />
        <main className="flex-1 overflow-y-auto p-6 bg-gradient-to-b from-tara-bg to-slate-950">
          <Outlet />
        </main>
      </div>
    </div>
  );
};
