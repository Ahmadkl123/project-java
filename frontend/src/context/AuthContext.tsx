import { createContext, useContext, useEffect, useState, ReactNode } from 'react';
import { authApi } from '../api/endpoints';
import type { User, Role } from '../types';

interface AuthState {
  user: User | null;
  loading: boolean;
  login: (email: string, password: string) => Promise<void>;
  register: (data: Parameters<typeof authApi.register>[0]) => Promise<void>;
  logout: () => void;
  hasRole: (...roles: Role[]) => boolean;
  refresh: () => Promise<void>;
}

const AuthContext = createContext<AuthState | undefined>(undefined);

export function AuthProvider({ children }: { children: ReactNode }) {
  const [user, setUser] = useState<User | null>(null);
  const [loading, setLoading] = useState(true);

  const refresh = async () => {
    if (!localStorage.getItem('accessToken')) {
      setUser(null);
      return;
    }
    try {
      const me = await authApi.me();
      setUser(me);
    } catch {
      setUser(null);
      localStorage.removeItem('accessToken');
    }
  };

  useEffect(() => {
    refresh().finally(() => setLoading(false));
  }, []);

  const login = async (email: string, password: string) => {
    const res = await authApi.login(email, password);
    localStorage.setItem('accessToken', res.accessToken);
    setUser(res.user);
  };

  const register: AuthState['register'] = async (data) => {
    const res = await authApi.register(data);
    localStorage.setItem('accessToken', res.accessToken);
    setUser(res.user);
  };

  const logout = () => {
    localStorage.removeItem('accessToken');
    setUser(null);
    window.location.href = '/login';
  };

  const hasRole = (...roles: Role[]) =>
    !!user && roles.some((r) => user.roles.includes(r));

  return (
    <AuthContext.Provider value={{ user, loading, login, register, logout, hasRole, refresh }}>
      {children}
    </AuthContext.Provider>
  );
}

export function useAuth() {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within AuthProvider');
  return ctx;
}
