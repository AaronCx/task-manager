import React, { createContext, useCallback, useContext, useState } from 'react';
import { authApi } from '../api/auth';
import { setAccessToken } from '../api/axiosClient';
import { AuthUser, LoginRequest, RegisterRequest } from '../types';

// ── Context shape ─────────────────────────────────────────────────────────────

interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  login: (data: LoginRequest)       => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: ()                        => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

// ── Provider ──────────────────────────────────────────────────────────────────

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(null);

  const login = useCallback(async (data: LoginRequest) => {
    const res = await authApi.login(data);
    const { token, userId, email, firstName, lastName } = res.data;

    // Store token in memory — never localStorage
    setAccessToken(token);
    setUser({ userId, email, firstName, lastName });
  }, []);

  const register = useCallback(async (data: RegisterRequest) => {
    const res = await authApi.register(data);
    const { token, userId, email, firstName, lastName } = res.data;

    setAccessToken(token);
    setUser({ userId, email, firstName, lastName });
  }, []);

  const logout = useCallback(() => {
    setAccessToken(null);
    setUser(null);
  }, []);

  return (
    <AuthContext.Provider value={{
      user,
      isAuthenticated: user !== null,
      login,
      register,
      logout,
    }}>
      {children}
    </AuthContext.Provider>
  );
}

// ── Hook ──────────────────────────────────────────────────────────────────────

export function useAuth(): AuthContextValue {
  const ctx = useContext(AuthContext);
  if (!ctx) throw new Error('useAuth must be used within <AuthProvider>');
  return ctx;
}
