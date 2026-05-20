import axios, { AxiosError } from 'axios';
import toast from 'react-hot-toast';

const baseURL = import.meta.env.VITE_API_URL || '/api';

export const api = axios.create({
  baseURL,
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use((config) => {
  const token = localStorage.getItem('accessToken');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

api.interceptors.response.use(
  (res) => res,
  (err: AxiosError<{ message?: string; data?: Record<string, string> }>) => {
    const status = err.response?.status;
    const message = err.response?.data?.message || err.message || 'Network error';

    if (status === 401) {
      localStorage.removeItem('accessToken');
      if (window.location.pathname !== '/login' && window.location.pathname !== '/register') {
        window.location.href = '/login';
      }
    } else if (status && status >= 500) {
      toast.error('Server error — please retry');
    } else if (message && status !== 401) {
      toast.error(message);
    }
    return Promise.reject(err);
  }
);

export function unwrap<T>(promise: Promise<{ data: { data: T } }>): Promise<T> {
  return promise.then((r) => r.data.data);
}
