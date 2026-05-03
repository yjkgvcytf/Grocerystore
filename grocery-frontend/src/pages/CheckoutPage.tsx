import { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useCartStore } from '../stores/cartStore';
import { orderApi } from '../api/orders';
import { useAuthStore } from '../stores/authStore';
import i18n from '../i18n';
import { resolveProductImageSrc, onProductImageError } from '../utils/productImage';

export default function CheckoutPage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { cart, fetchCart, isLoading: cartLoading } = useCartStore();
  const { user } = useAuthStore();
  const [isSubmitting, setIsSubmitting] = useState(false);

  const [formData, setFormData] = useState({
    recipientName: user?.fullName || '',
    recipientPhone: user?.phone || '',
    shippingAddress: user?.shippingAddress || '',
  });

  useEffect(() => {
    fetchCart();
  }, []);

  const handleChange = (e: React.ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
    setFormData({ ...formData, [e.target.name]: e.target.value });
  };

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!formData.recipientName || !formData.recipientPhone || !formData.shippingAddress) {
      alert('Please fill in all fields');
      return;
    }

    try {
      setIsSubmitting(true);
      await orderApi.createOrder(formData);
      navigate('/orders');
    } catch (error) {
      console.error('Failed to create order:', error);
      alert('Failed to create order');
    } finally {
      setIsSubmitting(false);
    }
  };

  const getProductName = (product: any) => {
    const lang = i18n.language;
    if (lang === 'en') return product.nameEn || product.name;
    if (lang === 'ru') return product.nameRu || product.name;
    return product.name;
  };

  if (!cart || cart.items.length === 0) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center p-4">
        <p className="text-gray-600 mb-4">Your cart is empty</p>
        <button onClick={() => navigate('/')} className="px-6 py-3 bg-red-500 text-white rounded-lg">
          Continue Shopping
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">{t('order.createOrder')}</h1>

      <div className="grid md:grid-cols-2 gap-6">
        <div className="space-y-6">
          <div className="bg-white rounded-xl shadow-sm p-6">
            <h2 className="text-lg font-semibold mb-4">Shipping Information</h2>
            <form onSubmit={handleSubmit} className="space-y-4">
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  {t('order.recipientName')}
                </label>
                <input
                  type="text"
                  name="recipientName"
                  value={formData.recipientName}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  {t('order.recipientPhone')}
                </label>
                <input
                  type="tel"
                  name="recipientPhone"
                  value={formData.recipientPhone}
                  onChange={handleChange}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                  required
                />
              </div>
              <div>
                <label className="block text-sm font-medium text-gray-700 mb-1">
                  {t('order.shippingAddress')}
                </label>
                <textarea
                  name="shippingAddress"
                  value={formData.shippingAddress}
                  onChange={handleChange}
                  rows={3}
                  className="w-full px-4 py-3 border border-gray-300 rounded-lg focus:ring-2 focus:ring-red-500 focus:border-transparent"
                  required
                />
              </div>
            </form>
          </div>

          <div className="bg-white rounded-xl shadow-sm p-6">
            <h2 className="text-lg font-semibold mb-4">Order Summary</h2>
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
          </div>
        </div>

        <div className="bg-white rounded-xl shadow-sm p-6">
          <h2 className="text-lg font-semibold mb-4">Items</h2>
          <div className="space-y-4">
            {cart.items.map((item) => (
              <div key={item.id} className="flex gap-4">
                <img
                  src={resolveProductImageSrc(item.product.imageUrl)}
                  alt={getProductName(item.product)}
                  className="w-16 h-16 object-cover rounded-lg"
                  onError={onProductImageError}
                />
                <div className="flex-1">
                  <h3 className="font-medium text-gray-900">{getProductName(item.product)}</h3>
                  <p className="text-sm text-gray-500">x{item.quantity}</p>
                </div>
                <p className="font-medium">¥{item.subtotal.toFixed(2)}</p>
              </div>
            ))}
          </div>

          <button
            onClick={handleSubmit}
            disabled={isSubmitting || cartLoading}
            className="w-full mt-6 py-3 bg-red-500 text-white font-semibold rounded-lg hover:bg-red-600 disabled:opacity-50"
          >
            {isSubmitting ? t('common.loading') : t('order.createOrder')}
          </button>
        </div>
      </div>
    </div>
  );
}
