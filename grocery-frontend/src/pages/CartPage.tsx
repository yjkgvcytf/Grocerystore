import { useEffect } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useCartStore } from '../stores/cartStore';
import { useAuthStore, selectIsAuthenticated } from '../stores/authStore';
import i18n from '../i18n';
import { resolveProductImageSrc, onProductImageError } from '../utils/productImage';
import type { CartItem } from '../types';

export default function CartPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { cart, fetchCart, updateQuantity, removeFromCart, isLoading } = useCartStore();
  const isAuthenticated = useAuthStore(selectIsAuthenticated);

  useEffect(() => {
    if (isAuthenticated) {
      fetchCart();
    }
  }, [isAuthenticated]);

  if (!isAuthenticated) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center p-4">
        <p className="text-gray-600 mb-4">Please login to view your cart</p>
        <Link to="/login" className="px-6 py-3 bg-red-500 text-white rounded-lg">
          {t('auth.login')}
        </Link>
      </div>
    );
  }

  if (!cart || cart.items.length === 0) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center p-4">
        <svg className="w-24 h-24 text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 3h2l.4 2M7 13h10l4-8H5.4M7 13L5.4 5M7 13l-2.293 2.293c-.63.63-.184 1.707.707 1.707H17m0 0a2 2 0 100 4 2 2 0 000-4zm-8 2a2 2 0 11-4 0 2 2 0 014 0z" />
        </svg>
        <p className="text-gray-600 mb-4">{t('cart.empty')}</p>
        <Link to="/" className="px-6 py-3 bg-red-500 text-white rounded-lg">
          {t('cart.continueShopping')}
        </Link>
      </div>
    );
  }

  const getProductName = (product: any) => {
    const lang = i18n.language;
    if (lang === 'en') return product.nameEn || product.name;
    if (lang === 'ru') return product.nameRu || product.name;
    return product.name;
  };

  /** 后端 UpdateCartRequest 要求 quantity >= 1，减到 0 会 400，应走删除接口 */
  const handleDecrease = async (item: CartItem) => {
    try {
      if (item.quantity <= 1) {
        await removeFromCart(item.id);
      } else {
        await updateQuantity(item.id, item.quantity - 1);
      }
    } catch (e) {
      console.error('Failed to update cart line:', e);
    }
  };

  const handleIncrease = async (item: CartItem) => {
    try {
      await updateQuantity(item.id, item.quantity + 1);
    } catch (e) {
      console.error('Failed to update cart line:', e);
    }
  };

  const handleRemoveLine = async (itemId: string) => {
    try {
      await removeFromCart(itemId);
    } catch (e) {
      console.error('Failed to remove cart line:', e);
    }
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">{t('cart.title')}</h1>
      
      <div className="grid md:grid-cols-3 gap-6">
        <div className="md:col-span-2 space-y-4">
          {cart.items.map((item) => (
            <div key={item.id} className="bg-white rounded-xl shadow-sm p-4 flex gap-4">
              <img
                src={resolveProductImageSrc(item.product.imageUrl)}
                alt={getProductName(item.product)}
                className="w-24 h-24 object-cover rounded-lg"
                onError={onProductImageError}
              />
              <div className="flex-1">
                <h3 className="font-medium text-gray-900">{getProductName(item.product)}</h3>
                <p className="text-red-500 font-bold mt-1">¥{item.product.price.toFixed(2)}</p>
                <div className="flex items-center justify-between mt-2">
                  <div className="flex items-center gap-2">
                    <button
                      type="button"
                      onClick={() => handleDecrease(item)}
                      disabled={isLoading}
                      className="w-8 h-8 border rounded-full flex items-center justify-center hover:bg-gray-100"
                    >
                      -
                    </button>
                    <span className="w-8 text-center">{item.quantity}</span>
                    <button
                      type="button"
                      onClick={() => handleIncrease(item)}
                      disabled={isLoading}
                      className="w-8 h-8 border rounded-full flex items-center justify-center hover:bg-gray-100"
                    >
                      +
                    </button>
                  </div>
                  <button
                    type="button"
                    onClick={() => handleRemoveLine(item.id)}
                    disabled={isLoading}
                    className="text-gray-400 hover:text-red-500"
                  >
                    <svg className="w-5 h-5" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                      <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M19 7l-.867 12.142A2 2 0 0116.138 21H7.862a2 2 0 01-1.995-1.858L5 7m5 4v6m4-6v6m1-10V4a1 1 0 00-1-1h-4a1 1 0 00-1 1v3M4 7h16" />
                    </svg>
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6 h-fit sticky top-20">
          <h2 className="text-lg font-semibold mb-4">{t('cart.finalTotal')}</h2>
          <div className="space-y-2 mb-4">
            <div className="flex justify-between text-gray-600">
              <span>{t('cart.originalPrice')}</span>
              <span>¥{cart.originalPrice.toFixed(2)}</span>
            </div>
            {cart.discount > 0 && (
              <div className="flex justify-between text-green-600">
                <span>{t('cart.discount')}</span>
                <span>-¥{cart.discount.toFixed(2)}</span>
              </div>
            )}
            {cart.reduction > 0 && (
              <div className="flex justify-between text-green-600">
                <span>{t('cart.reduction')}</span>
                <span>-¥{cart.reduction.toFixed(2)}</span>
              </div>
            )}
            <div className="flex justify-between text-gray-600">
              <span>{t('cart.delivery')}</span>
              <span>{t('cart.free')}</span>
            </div>
          </div>
          <div className="border-t pt-4">
            <div className="flex justify-between text-xl font-bold">
              <span>{t('cart.finalTotal')}</span>
              <span className="text-red-500">¥{cart.finalTotal.toFixed(2)}</span>
            </div>
          </div>
          <button
            onClick={() => navigate('/checkout')}
            disabled={isLoading}
            className="w-full mt-6 py-3 bg-red-500 text-white font-semibold rounded-lg hover:bg-red-600 disabled:opacity-50"
          >
            {t('cart.checkout')}
          </button>
        </div>
      </div>
    </div>
  );
}
