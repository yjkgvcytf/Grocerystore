import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { orderApi } from '../api/orders';
import type { Order } from '../types';
import i18n from '../i18n';
import { resolveProductImageSrc, onProductImageError } from '../utils/productImage';

export default function OrderDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [order, setOrder] = useState<Order | null>(null);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) {
      loadOrder(id);
    }
  }, [id]);

  const loadOrder = async (orderId: string) => {
    try {
      setLoading(true);
      const response = await orderApi.getOrderById(orderId);
      setOrder(response.data);
    } catch (error) {
      console.error('Failed to load order:', error);
    } finally {
      setLoading(false);
    }
  };

  const getStatusText = (status: string) => {
    const lang = i18n.language;
    const statuses: Record<string, any> = {
      PENDING: { zh: '待处理', en: 'Pending', ru: 'Ожидает' },
      PROCESSING: { zh: '处理中', en: 'Processing', ru: 'Обрабатывается' },
      SHIPPED: { zh: '已发货', en: 'Shipped', ru: 'Отправлен' },
      DELIVERED: { zh: '已送达', en: 'Delivered', ru: 'Доставлен' },
      CANCELLED: { zh: '已取消', en: 'Cancelled', ru: 'Отменен' },
    };
    return statuses[status]?.[lang === 'ru' ? 'ru' : lang === 'en' ? 'en' : 'zh'] || status;
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'DELIVERED': return 'bg-green-100 text-green-800';
      case 'SHIPPED': return 'bg-blue-100 text-blue-800';
      case 'PROCESSING': return 'bg-yellow-100 text-yellow-800';
      case 'CANCELLED': return 'bg-gray-100 text-gray-800';
      default: return 'bg-orange-100 text-orange-800';
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-500"></div>
      </div>
    );
  }

  if (!order) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center p-4">
        <p className="text-gray-600 mb-4">Order not found</p>
        <button onClick={() => navigate('/orders')} className="px-6 py-3 bg-red-500 text-white rounded-lg">
          Back to Orders
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-6">
      <button onClick={() => navigate('/orders')} className="flex items-center text-gray-600 mb-6">
        <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
        </svg>
        Back
      </button>

      <div className="bg-white rounded-xl shadow-sm p-6 mb-6">
        <div className="flex items-center justify-between mb-4">
          <div>
            <h1 className="text-xl font-bold text-gray-900">{t('order.orderNumber')}: {order.orderNumber}</h1>
            <p className="text-sm text-gray-500">{new Date(order.orderDate).toLocaleString()}</p>
          </div>
          <span className={`px-4 py-2 rounded-full ${getStatusColor(order.status)}`}>
            {getStatusText(order.status)}
          </span>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm p-6 mb-6">
        <h2 className="text-lg font-semibold mb-4">Shipping Information</h2>
        <div className="space-y-2">
          <p><span className="text-gray-500">Recipient:</span> {order.recipientName}</p>
          <p><span className="text-gray-500">Phone:</span> {order.recipientPhone}</p>
          <p><span className="text-gray-500">Address:</span> {order.shippingAddress}</p>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm p-6 mb-6">
        <h2 className="text-lg font-semibold mb-4">Items</h2>
        <div className="space-y-4">
          {order.items.map((item) => (
            <div key={item.id} className="flex gap-4">
              <img
                src={resolveProductImageSrc(item.imageUrl)}
                alt={item.productName ?? ''}
                className="w-20 h-20 object-cover rounded-lg"
                onError={onProductImageError}
              />
              <div className="flex-1">
                <h3 className="font-medium text-gray-900">{item.productName}</h3>
                <p className="text-sm text-gray-500">Unit: ¥{item.unitPrice.toFixed(2)} x {item.quantity}</p>
              </div>
              <p className="font-medium">¥{item.subtotal.toFixed(2)}</p>
            </div>
          ))}
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm p-6">
        <h2 className="text-lg font-semibold mb-4">Payment Summary</h2>
        <div className="space-y-2">
          <div className="flex justify-between text-gray-600">
            <span>{t('cart.originalPrice')}</span>
            <span>¥{order.originalPrice.toFixed(2)}</span>
          </div>
          {order.discount > 0 && (
            <div className="flex justify-between text-green-600">
              <span>{t('cart.discount')}</span>
              <span>-¥{order.discount.toFixed(2)}</span>
            </div>
          )}
          {order.reduction > 0 && (
            <div className="flex justify-between text-green-600">
              <span>{t('cart.reduction')}</span>
              <span>-¥{order.reduction.toFixed(2)}</span>
            </div>
          )}
          <div className="flex justify-between text-gray-600">
            <span>{t('cart.delivery')}</span>
            <span>{t('cart.free')}</span>
          </div>
        </div>
        <div className="border-t pt-4 mt-4">
          <div className="flex justify-between text-xl font-bold">
            <span>{t('cart.finalTotal')}</span>
            <span className="text-red-500">¥{order.finalTotal.toFixed(2)}</span>
          </div>
        </div>
      </div>
    </div>
  );
}
