import api from './client';
import type { Cart } from '../types';

export const cartApi = {
  getCart: () => api.get<Cart>('/cart'),
  addToCart: (productId: string, quantity = 1) => 
    api.post<Cart>('/cart/items', { productId, quantity }),
  updateCartItem: (itemId: string, quantity: number) => 
    api.put<Cart>(`/cart/items/${itemId}`, { quantity }),
  removeFromCart: (itemId: string) => api.delete<Cart>(`/cart/items/${itemId}`),
  clearCart: () => api.delete('/cart'),
};
