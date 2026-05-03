import api from './client';
import type { Category } from '../types';

export const categoryApi = {
  getAll: () => api.get<Category[]>('/categories'),
  getById: (id: string) => api.get<Category>(`/categories/${id}`),
};
