import React, { createContext, useCallback, useContext, useEffect, useState } from 'react';
import { authApi } from '../api/auth';
import {
  setAccessToken,
  setRefreshToken,
  getRefreshToken,
  setOnForceLogout,
} from '../api/axiosClient';
import { AuthUser, LoginRequest, RegisterRequest } from '../types';

// ── Context shape ─────────────────────────────────────────────────────────────

interface AuthContextValue {
  user: AuthUser | null;
  isAuthenticated: boolean;
  isLoading: boolean;
  login: (data: LoginRequest)       => Promise<void>;
  register: (data: RegisterRequest) => Promise<void>;
  logout: ()                        => void;
}

const AuthContext = createContext<AuthContextValue | null>(null);

// ── User persistence (for display only — not security-sensitive) ──────────────
const USER_KEY = 'taskflow_user';

function persistUser(user: AuthUser | null) {
  if (user) localStorage.setItem(USER_KEY, JSON.stringify(user));
  else localStorage.removeItem(USER_KEY);
}

function loadPersistedUser(): AuthUser | null {
  try {
    const raw = localStorage.getItem(USER_KEY);
    return raw ? JSON.parse(raw) : null;
  } catch {
    return null;
  }
}

// ── Provider ──────────────────────────────────────────────────────────────────

export function AuthProvider({ children }: { children: React.ReactNode }) {
  const [user, setUser] = useState<AuthUser | null>(loadPersistedUser);
  const [isLoading, setIsLoading] = useState(() => !!getRefreshToken());

  const clearAuth = useCallback(() => {
    setAccessToken(null);
    setRefreshToken(null);
    persistUser(null);
    setUser(null);
  }, []);

  // Register the force-logout callback so the axios interceptor can trigger it
  useEffect(() => {
    setOnForceLogout(clearAuth);
    return () => setOnForceLogout(null);
  }, [clearAuth]);

  // On mount: attempt to restore session using the persisted refresh token
  useEffect(() => {
    const refreshToken = getRefreshToken();
    if (!refreshToken) {
      setIsLoading(false);
      return;
    }

    authApi.refresh(refreshToken)
      .then((res) => {
        const { token, refreshToken: newRefresh, userId, email, firstName, lastName } = res.data;
        setAccessToken(token);
        setRefreshToken(newRefresh);
        const u = { userId, email, firstName, lastName };
        persistUser(u);
        setUser(u);
      })
      .catch(() => {
        clearAuth();
      })
      .finally(() => setIsLoading(false));
  }, []); // eslint-disable-line react-hooks/exhaustive-deps

  const login = useCallback(async (data: LoginRequest) => {
    const res = await authApi.login(data);
    const { token, refreshToken, userId, email, firstName, lastName } = res.data;

    setAccessToken(token);
    setRefreshToken(refreshToken);
    const u = { userId, email, firstName, lastName };
    persistUser(u);
    setUser(u);
  }, []);

  const register = useCallback(async (data: RegisterRequest) => {
    const res = await authApi.register(data);
    const { token, refreshToken, userId, email, firstName, lastName } = res.data;

    setAccessToken(token);
    setRefreshToken(refreshToken);
    const u = { userId, email, firstName, lastName };
    persistUser(u);
    setUser(u);
  }, []);

  const logout = useCallback(async () => {
    try { await authApi.logout(); } catch { /* ignore */ }
    clearAuth();
  }, [clearAuth]);

  return (
    <AuthContext.Provider value={{
      user,
      isAuthenticated: user !== null,
      isLoading,
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
