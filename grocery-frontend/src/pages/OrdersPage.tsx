import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { orderApi } from '../api/orders';
import type { Order } from '../types';
import i18n from '../i18n';
import { resolveProductImageSrc, onProductImageError } from '../utils/productImage';

export default function OrdersPage() {
  const { t } = useTranslation();
  const [orders, setOrders] = useState<Order[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    loadOrders();
  }, []);

  const loadOrders = async () => {
    try {
      setLoading(true);
      const response = await orderApi.getOrders(0, 20);
      setOrders(response.data.content);
    } catch (error) {
      console.error('Failed to load orders:', error);
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

  if (orders.length === 0) {
    return (
      <div className="min-h-screen flex flex-col items-center justify-center p-4">
        <svg className="w-24 h-24 text-gray-300 mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5H7a2 2 0 00-2 2v12a2 2 0 002 2h10a2 2 0 002-2V7a2 2 0 00-2-2h-2M9 5a2 2 0 002 2h2a2 2 0 002-2M9 5a2 2 0 012-2h2a2 2 0 012 2" />
        </svg>
        <p className="text-gray-600 mb-4">{t('order.empty')}</p>
        <Link to="/" className="px-6 py-3 bg-red-500 text-white rounded-lg">
          {t('cart.continueShopping')}
        </Link>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-6">
      <h1 className="text-2xl font-bold text-gray-900 mb-6">{t('order.title')}</h1>
      
      <div className="space-y-4">
        {orders.map((order) => (
          <Link key={order.id} to={`/order/${order.id}`} className="block bg-white rounded-xl shadow-sm p-4 hover:shadow-md transition-shadow">
            <div className="flex items-center justify-between mb-4">
              <div>
                <p className="text-sm text-gray-500">{t('order.orderNumber')}: {order.orderNumber}</p>
                <p className="text-sm text-gray-400">{new Date(order.orderDate).toLocaleDateString()}</p>
              </div>
              <span className={`px-3 py-1 rounded-full text-sm ${getStatusColor(order.status)}`}>
                {getStatusText(order.status)}
              </span>
            </div>
            
            <div className="space-y-2 mb-4">
              {order.items.slice(0, 3).map((item) => (
                <div key={item.id} className="flex gap-3">
                  <img
                    src={resolveProductImageSrc(item.imageUrl)}
                    alt={item.productName ?? ''}
                    className="w-12 h-12 object-cover rounded"
                    onError={onProductImageError}
                  />
                  <div className="flex-1">
                    <p className="text-sm font-medium text-gray-900 line-clamp-1">{item.productName}</p>
                    <p className="text-xs text-gray-500">x{item.quantity}</p>
                  </div>
                  <p className="text-sm font-medium">¥{item.subtotal.toFixed(2)}</p>
                </div>
              ))}
              {order.items.length > 3 && (
                <p className="text-sm text-gray-500">+{order.items.length - 3} more items</p>
              )}
            </div>
            
            <div className="border-t pt-4 flex justify-between items-center">
              <span className="text-lg font-bold text-red-500">¥{order.finalTotal.toFixed(2)}</span>
              <span className="text-sm text-gray-500">{t('order.viewDetails')} →</span>
            </div>
          </Link>
        ))}
      </div>
    </div>
  );
}
