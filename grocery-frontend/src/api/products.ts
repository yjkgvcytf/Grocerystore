import api from './client';
import type { Product, PageResponse } from '../types';

export const productApi = {
  getAll: (page = 0, size = 20) => api.get<PageResponse<Product>>('/products', { params: { page, size } }),
  getById: (id: string) => api.get<Product>(`/products/${id}`),
  getByCategory: (categoryId: string, page = 0, size = 20) => 
    api.get<PageResponse<Product>>(`/products/category/${categoryId}`, { params: { page, size } }),
  getFeatured: (limit = 10) => api.get<Product[]>('/products/featured', { params: { limit } }),
  search: (q: string, page = 0, size = 20) => 
    api.get<PageResponse<Product>>('/products/search', { params: { q, page, size } }),
};
