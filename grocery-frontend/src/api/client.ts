import axios from 'axios';
import { clearPersistedAuthCredentials, getStoredAccessToken } from '../utils/authToken';

const API_BASE_URL = import.meta.env.VITE_API_URL || '/api';

const api = axios.create({
  baseURL: API_BASE_URL,
  headers: {
    'Content-Type': 'application/json',
  },
});

api.interceptors.request.use(
  (config) => {
    const token = getStoredAccessToken();
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    const lang = localStorage.getItem('language') || 'zh';
    config.headers['Accept-Language'] = lang;
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  (error) => {
    const status = error.response?.status;
    const sentAuth = Boolean(error.config?.headers?.Authorization);
    // 401: 未认证或已失效；403 且已带 Token：常见于匿名访问受保护资源或 JWT 无效时 Spring 仍返回 403
    if (status === 401 || (status === 403 && sentAuth)) {
      clearPersistedAuthCredentials();
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default api;
