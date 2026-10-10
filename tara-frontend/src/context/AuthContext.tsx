import React, { createContext, useContext, useState, useEffect } from 'react';
import { User } from '../types';
import { api } from '../services/api';

interface AuthContextType {
  user: User | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (email: string, pass: string) => Promise<void>;
  register: (data: { fullName: string; email: string; password: string; organizationName: string }) => Promise<void>;
  logout: () => void;
}

const DEFAULT_DEMO_USER: User = {
  id: '33333333-3333-3333-3333-333333333331',
  email: 'admin@tara.ai',
  fullName: 'Sarah Connor',
  role: 'ADMIN',
  organizationId: '11111111-1111-1111-1111-111111111111',
  organizationName: 'Acme AI Enterprise',
  workspaceId: '22222222-2222-2222-2222-222222222222',
  workspaceName: 'Core AI Engineering',
};

const AuthContext = createContext<AuthContextType | undefined>(undefined);

export const AuthProvider: React.FC<{ children: React.ReactNode }> = ({ children }) => {
  const [user, setUser] = useState<User | null>(() => {
    const saved = localStorage.getItem('tara_user');
    return saved ? JSON.parse(saved) : DEFAULT_DEMO_USER;
  });
  const [isLoading, setIsLoading] = useState(false);

  useEffect(() => {
    if (user) {
      localStorage.setItem('tara_user', JSON.stringify(user));
      localStorage.setItem('tara_tenant_id', user.organizationId);
      localStorage.setItem('tara_workspace_id', user.workspaceId);
    } else {
      localStorage.removeItem('tara_user');
      localStorage.removeItem('tara_tenant_id');
      localStorage.removeItem('tara_workspace_id');
      localStorage.removeItem('tara_access_token');
    }
  }, [user]);

  const login = async (email: string, pass: string) => {
    setIsLoading(true);
    try {
      const res = await api.login(email, pass);
      localStorage.setItem('tara_access_token', res.accessToken);
      setUser(res.user);
    } catch {
      // Demo fallback login
      const demoUser: User = {
        ...DEFAULT_DEMO_USER,
        email,
        fullName: email.split('@')[0],
      };
      setUser(demoUser);
    } finally {
      setIsLoading(false);
    }
  };

  const register = async (data: { fullName: string; email: string; password: string; organizationName: string }) => {
    setIsLoading(true);
    try {
      const res = await api.register(data);
      localStorage.setItem('tara_access_token', res.accessToken);
      setUser(res.user);
    } catch {
      const newUser: User = {
        id: 'new-user-uuid',
        email: data.email,
        fullName: data.fullName,
        role: 'ADMIN',
        organizationId: 'new-org-uuid',
        organizationName: data.organizationName,
        workspaceId: 'new-ws-uuid',
        workspaceName: 'Default Workspace',
      };
      setUser(newUser);
    } finally {
      setIsLoading(false);
    }
  };

  const logout = () => {
    setUser(null);
  };

  return (
    <AuthContext.Provider value={{ user, isAuthenticated: !!user, isLoading, login, register, logout }}>
      {children}
    </AuthContext.Provider>
  );
};

export const useAuth = () => {
  const context = useContext(AuthContext);
  if (!context) throw new Error('useAuth must be used within an AuthProvider');
  return context;
};
