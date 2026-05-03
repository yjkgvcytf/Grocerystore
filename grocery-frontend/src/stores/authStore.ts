import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import type { User } from '../types';
import { authApi } from '../api/auth';

function normalizeAuthError(message: string): string {
  const m = message.trim();
  if (/invalid email or password/i.test(m) || /bad credentials/i.test(m)) {
    return '邮箱或密码错误';
  }
  if (/email already exists/i.test(m)) {
    return '该邮箱已被注册';
  }
  return m;
}

interface AuthState {
  user: User | null;
  token: string | null;
  isLoading: boolean;
  error: string | null;
  login: (email: string, password: string) => Promise<void>;
  register: (email: string, password: string, fullName: string, phone: string) => Promise<void>;
  logout: () => void;
  clearError: () => void;
}

export const useAuthStore = create<AuthState>()(
  persist(
    (set) => ({
      user: null,
      token: null,
      isLoading: false,
      error: null,

      login: async (email: string, password: string) => {
        set({ isLoading: true, error: null });
        const trimmedEmail = email.trim();
        try {
          const response = await authApi.login({ email: trimmedEmail, password });
          const { token, user } = response.data;
          set({ user, token, isLoading: false });
        } catch (error: any) {
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          const raw =
            error.response?.data?.message
            ?? (typeof error.response?.data === 'string' ? error.response.data : null)
            ?? '登录失败';
          set({
            user: null,
            token: null,
            error: normalizeAuthError(String(raw)),
            isLoading: false,
          });
          throw error;
        }
      },

      register: async (email: string, password: string, fullName: string, phone: string) => {
        set({ isLoading: true, error: null });
        const trimmedEmail = email.trim();
        const trimmedName = fullName.trim();
        const trimmedPhone = phone.trim();
        try {
          const response = await authApi.register({
            email: trimmedEmail,
            password,
            fullName: trimmedName,
            phone: trimmedPhone,
          });
          const { token, user } = response.data;
          set({ user, token, isLoading: false });
        } catch (error: any) {
          localStorage.removeItem('token');
          localStorage.removeItem('user');
          const raw =
            error.response?.data?.message
            ?? (typeof error.response?.data === 'string' ? error.response.data : null)
            ?? '注册失败';
          set({
            user: null,
            token: null,
            error: normalizeAuthError(String(raw)),
            isLoading: false,
          });
          throw error;
        }
      },

      logout: () => {
        localStorage.removeItem('token');
        localStorage.removeItem('user');
        set({ user: null, token: null });
      },

      clearError: () => set({ error: null }),
    }),
    {
      name: 'auth-storage',
      // Only persist user and token; isAuthenticated is derived from token
      partialize: (state) => ({
        user: state.user,
        token: state.token,
      }),
    }
  )
);

// Selector: derived auth state
export const selectIsAuthenticated = (state: AuthState) => !!state.token;
