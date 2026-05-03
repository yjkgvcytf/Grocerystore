import { create } from 'zustand';
import type { Cart, CartItem } from '../types';
import { cartApi } from '../api/cart';

interface CartState {
  cart: Cart | null;
  isLoading: boolean;
  error: string | null;
  fetchCart: () => Promise<void>;
  addToCart: (productId: string, quantity?: number) => Promise<void>;
  updateQuantity: (itemId: string, quantity: number) => Promise<void>;
  removeFromCart: (itemId: string) => Promise<void>;
  clearCart: () => void;
  itemCount: () => number;
}

export const useCartStore = create<CartState>((set, get) => ({
  cart: null,
  isLoading: false,
  error: null,

  fetchCart: async () => {
    set({ isLoading: true });
    try {
      const response = await cartApi.getCart();
      set({ cart: response.data, isLoading: false });
    } catch (error: any) {
      set({ error: error.message, isLoading: false });
    }
  },

  addToCart: async (productId: string, quantity = 1) => {
    set({ isLoading: true });
    try {
      const response = await cartApi.addToCart(productId, quantity);
      set({ cart: response.data, isLoading: false });
    } catch (error: any) {
      set({ error: error.message, isLoading: false });
      throw error;
    }
  },

  updateQuantity: async (itemId: string, quantity: number) => {
    set({ isLoading: true });
    try {
      const response = await cartApi.updateCartItem(itemId, quantity);
      set({ cart: response.data, isLoading: false });
    } catch (error: any) {
      set({ error: error.message, isLoading: false });
      throw error;
    }
  },

  removeFromCart: async (itemId: string) => {
    set({ isLoading: true });
    try {
      const response = await cartApi.removeFromCart(itemId);
      set({ cart: response.data, isLoading: false });
    } catch (error: any) {
      set({ error: error.message, isLoading: false });
      throw error;
    }
  },

  clearCart: () => set({ cart: null }),

  itemCount: () => {
    const cart = get().cart;
    if (!cart) return 0;
    return cart.items.reduce((sum: number, item: CartItem) => sum + item.quantity, 0);
  },
}));
