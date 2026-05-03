export interface User {
  id: string;
  email: string;
  fullName: string;
  phone: string;
  shippingAddress: string;
}

export interface Product {
  id: string;
  name: string;
  nameEn: string;
  nameRu: string;
  description: string;
  descriptionEn: string;
  descriptionRu: string;
  price: number;
  imageUrl: string;
  categoryId: string;
  category: string;
  categoryEn: string;
  categoryRu: string;
  soldCount: number;
  stock: number;
  featured: boolean;
}

export interface Category {
  id: string;
  name: string;
  nameEn: string;
  nameRu: string;
  icon: string;
  products?: Product[];
}

export interface CartItem {
  id: string;
  product: Product;
  quantity: number;
  subtotal: number;
}

export interface Cart {
  items: CartItem[];
  originalPrice: number;
  discount: number;
  reduction: number;
  finalTotal: number;
}

export interface OrderItem {
  id: string;
  productId: string;
  productName: string;
  productNameEn: string;
  imageUrl: string;
  unitPrice: number;
  quantity: number;
  subtotal: number;
}

export type OrderStatus = 'PENDING' | 'PROCESSING' | 'SHIPPED' | 'DELIVERED' | 'CANCELLED';

export interface Order {
  id: string;
  orderNumber: string;
  originalPrice: number;
  discount: number;
  reduction: number;
  finalTotal: number;
  shippingAddress: string;
  recipientName: string;
  recipientPhone: string;
  status: OrderStatus;
  orderDate: string;
  items: OrderItem[];
}

export interface AuthResponse {
  token: string;
  user: User;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
}

export interface LoginRequest {
  email: string;
  password: string;
}

export interface RegisterRequest {
  email: string;
  password: string;
  fullName: string;
  phone: string;
}

export interface CreateOrderRequest {
  recipientName: string;
  recipientPhone: string;
  shippingAddress: string;
}
