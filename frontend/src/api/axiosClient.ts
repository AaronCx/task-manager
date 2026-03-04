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

// ── Axios instance ────────────────────────────────────────────────────────────
const axiosClient = axios.create({
  baseURL: '/api',          // proxied to http://localhost:8080/api in dev
  headers: { 'Content-Type': 'application/json' },
  timeout: 10_000,
});

// Request interceptor — attach Bearer token if available
axiosClient.interceptors.request.use(
  (config: InternalAxiosRequestConfig) => {
    if (_accessToken) {
      config.headers.Authorization = `Bearer ${_accessToken}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

// Response interceptor — surface friendly error messages
axiosClient.interceptors.response.use(
  (response) => response,
  (error: AxiosError) => {
    // Pass the error through; callers decide how to handle it
    return Promise.reject(error);
  }
);

export default axiosClient;
