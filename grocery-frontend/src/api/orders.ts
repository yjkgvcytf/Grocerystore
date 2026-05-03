import api from './client';
import type { Order, PageResponse, CreateOrderRequest } from '../types';

export const orderApi = {
  getOrders: (page = 0, size = 10) => 
    api.get<PageResponse<Order>>('/orders', { params: { page, size } }),
  getOrderById: (id: string) => api.get<Order>(`/orders/${id}`),
  createOrder: (data: CreateOrderRequest) => api.post<Order>('/orders', data),
};
