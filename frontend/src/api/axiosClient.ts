import axios, { AxiosError, InternalAxiosRequestConfig } from 'axios';

/**
 * Singleton Axios instance used throughout the app.
 *
 * The JWT is stored as a module-level variable (in-memory) so it is:
 *  - Never persisted to localStorage / sessionStorage (XSS-safe)
 *  - Automatically cleared on page refresh (forces re-login — acceptable for a portfolio demo)
 *
 * In production you might use httpOnly cookies + a refresh token flow instead.
 */

// ── In-memory token store ─────────────────────────────────────────────────────
let _accessToken: string | null = null;

export const setAccessToken = (token: string | null) => {
  _accessToken = token;
};

export const getAccessToken = () => _accessToken;

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

  instance.interceptors.response.use(
    (response) => response,
    (error: AxiosError) => Promise.reject(error)
  );
}

// ── Task API client ───────────────────────────────────────────────────────────
const axiosClient = axios.create({
  baseURL: import.meta.env.VITE_API_URL || '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 10_000,
});
addAuthInterceptor(axiosClient);

// ── Notifications API client ──────────────────────────────────────────────────
export const notificationsClient = axios.create({
  baseURL: import.meta.env.VITE_NOTIFICATIONS_URL || '/api',
  headers: { 'Content-Type': 'application/json' },
  timeout: 10_000,
});
addAuthInterceptor(notificationsClient);

export default axiosClient;
