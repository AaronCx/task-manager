import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';

/**
 * Singleton Axios instance used throughout the app.
 *
 * Access token is stored in memory (XSS-safe).
 * Refresh token is stored in localStorage to survive page reloads.
 * On 401, the interceptor automatically attempts a token refresh.
 */

// ── In-memory token store ─────────────────────────────────────────────────────
let _accessToken: string | null = null;

export const setAccessToken = (token: string | null) => {
  _accessToken = token;
};

export const getAccessToken = () => _accessToken;

// ── Refresh token (persisted across reloads) ──────────────────────────────────
const REFRESH_TOKEN_KEY = 'taskflow_refresh_token';

export const setRefreshToken = (token: string | null) => {
  if (token) {
    localStorage.setItem(REFRESH_TOKEN_KEY, token);
  } else {
    localStorage.removeItem(REFRESH_TOKEN_KEY);
  }
};

export const getRefreshToken = () => localStorage.getItem(REFRESH_TOKEN_KEY);

// ── Shared interceptor setup ──────────────────────────────────────────────────
function addAuthInterceptor(instance: ReturnType<typeof axios.create>) {
  instance.interceptors.request.use(
    (config: InternalAxiosRequestConfig) => {
      if (_accessToken) {
        config.headers.Authorization = `Bearer ${_accessToken}`;
      }
      return config;
    },
    (error) => Promise.reject(error)
  );
}

// ── Task API client ───────────────────────────────────────────────────────────
const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 60_000,
});
addAuthInterceptor(axiosClient);

// ── Notifications API client ──────────────────────────────────────────────────
export const notificationsClient = axios.create({
  baseURL: import.meta.env.VITE_NOTIFICATIONS_URL || '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 60_000,
});
addAuthInterceptor(notificationsClient);

// ── Auto-refresh on 401 ──────────────────────────────────────────────────────
let isRefreshing = false;
let refreshQueue: Array<{ resolve: (token: string) => void; reject: (err: unknown) => void }> = [];

function processQueue(error: unknown, token: string | null) {
  refreshQueue.forEach(({ resolve, reject }) => {
    if (error) reject(error);
    else resolve(token!);
  });
  refreshQueue = [];
}

// Callback set by AuthContext to handle forced logout
let _onForceLogout: (() => void) | null = null;
export const setOnForceLogout = (cb: (() => void) | null) => { _onForceLogout = cb; };

function addRefreshInterceptor(instance: ReturnType<typeof axios.create>) {
  instance.interceptors.response.use(
    (response) => response,
    async (error: AxiosError) => {
      const originalRequest = error.config as InternalAxiosRequestConfig & { _retry?: boolean };

      if (error.response?.status !== 401 || originalRequest._retry) {
        return Promise.reject(error);
      }

      // Don't try to refresh on auth endpoints
      if (originalRequest.url?.includes('/auth/')) {
        return Promise.reject(error);
      }

      const refreshToken = getRefreshToken();
      if (!refreshToken) {
        _onForceLogout?.();
        return Promise.reject(error);
      }

      if (isRefreshing) {
        return new Promise<string>((resolve, reject) => {
          refreshQueue.push({ resolve, reject });
        }).then((token) => {
          originalRequest.headers.Authorization = `Bearer ${token}`;
          originalRequest._retry = true;
          return instance(originalRequest);
        });
      }

      isRefreshing = true;
      originalRequest._retry = true;

      try {
        const res = await axios.post(
          `${import.meta.env.VITE_API_URL || '/api'}/auth/refresh`,
          { refreshToken },
          { headers: { 'Content-Type': 'application/json' } }
        );

        const { token: newAccess, refreshToken: newRefresh } = res.data;
        setAccessToken(newAccess);
        setRefreshToken(newRefresh);

        processQueue(null, newAccess);

        originalRequest.headers.Authorization = `Bearer ${newAccess}`;
        return instance(originalRequest);
      } catch (refreshError) {
        processQueue(refreshError, null);
        setAccessToken(null);
        setRefreshToken(null);
        _onForceLogout?.();
        return Promise.reject(refreshError);
      } finally {
        isRefreshing = false;
      }
    }
  );
}

addRefreshInterceptor(axiosClient);
addRefreshInterceptor(notificationsClient);

export default axiosClient;
