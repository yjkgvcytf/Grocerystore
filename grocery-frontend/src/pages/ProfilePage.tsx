import { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useAuthStore } from '../stores/authStore';

export default function ProfilePage() {
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { user, logout } = useAuthStore();
  const [showEditModal, setShowEditModal] = useState(false);

  const handleLogout = () => {
    logout();
    navigate('/');
  };

  return (
    <div className="max-w-7xl mx-auto px-4 py-6">
      <div className="bg-white rounded-xl shadow-sm p-6 mb-6">
        <div className="flex items-center gap-4">
          <div className="w-20 h-20 bg-gray-200 rounded-full flex items-center justify-center">
            <span className="text-3xl font-bold text-gray-600">
              {user?.fullName?.charAt(0).toUpperCase() || 'U'}
            </span>
          </div>
          <div>
            <h1 className="text-xl font-bold text-gray-900">{user?.fullName || 'User'}</h1>
            <p className="text-gray-500">{user?.email}</p>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm mb-6">
        <div className="p-4 border-b">
          <h2 className="font-semibold text-gray-900">Contact Information</h2>
        </div>
        <div className="p-4 space-y-4">
          <div>
            <label className="text-sm text-gray-500">{t('auth.email')}</label>
            <p className="text-gray-900">{user?.email}</p>
          </div>
          <div>
            <label className="text-sm text-gray-500">{t('auth.phone')}</label>
            <p className="text-gray-900">{user?.phone || 'Not set'}</p>
          </div>
          <div>
            <label className="text-sm text-gray-500">{t('order.shippingAddress')}</label>
            <p className="text-gray-900">{user?.shippingAddress || 'Not set'}</p>
          </div>
        </div>
      </div>

      <div className="bg-white rounded-xl shadow-sm mb-6">
        <Link to="/orders" className="p-4 border-b flex items-center justify-between hover:bg-gray-50">
          <span className="font-semibold text-gray-900">{t('profile.recentOrders')}</span>
          <svg className="w-5 h-5 text-gray-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
            <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 5l7 7-7 7" />
          </svg>
        </Link>
      </div>

      <button
        onClick={handleLogout}
        className="w-full py-3 bg-white border border-red-500 text-red-500 font-semibold rounded-lg hover:bg-red-50 transition-colors"
      >
        {t('auth.logout')}
      </button>
    </div>
  );
}
