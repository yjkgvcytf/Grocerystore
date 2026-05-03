/** Key must match zustand persist `name` in authStore */
const AUTH_PERSIST_KEY = 'auth-storage';

/**
 * Returns JWT for API calls. Token lives in zustand-persisted `auth-storage`, not `localStorage.token`.
 */
export function getStoredAccessToken(): string | null {
  try {
    const legacy = localStorage.getItem('token');
    if (legacy) return legacy;
    const raw = localStorage.getItem(AUTH_PERSIST_KEY);
    if (!raw) return null;
    const parsed = JSON.parse(raw) as { state?: { token?: string | null } };
    const token = parsed?.state?.token;
    return typeof token === 'string' && token.length > 0 ? token : null;
  } catch {
    return null;
  }
}

export function clearPersistedAuthCredentials(): void {
  localStorage.removeItem('token');
  localStorage.removeItem('user');
  localStorage.removeItem(AUTH_PERSIST_KEY);
}
