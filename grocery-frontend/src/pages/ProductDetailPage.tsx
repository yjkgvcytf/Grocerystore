import { useState, useEffect } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { productApi } from '../api/products';
import { useCartStore } from '../stores/cartStore';
import { useAuthStore, selectIsAuthenticated } from '../stores/authStore';
import type { Product } from '../types';
import i18n from '../i18n';
import { resolveProductImageSrc, onProductImageError } from '../utils/productImage';

export default function ProductDetailPage() {
  const { id } = useParams<{ id: string }>();
  const { t } = useTranslation();
  const navigate = useNavigate();
  const { addToCart, isLoading: cartLoading } = useCartStore();
  const isAuthenticated = useAuthStore(selectIsAuthenticated);
  
  const [product, setProduct] = useState<Product | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    if (id) {
      loadProduct(id);
    }
  }, [id]);

  const loadProduct = async (productId: string) => {
    try {
      setLoading(true);
      const response = await productApi.getById(productId);
      setProduct(response.data);
      setError(null);
    } catch (err) {
      setError('Failed to load product');
    } finally {
      setLoading(false);
    }
  };

  const getName = () => {
    if (!product) return '';
    const lang = i18n.language;
    if (lang === 'en') return product.nameEn || product.name;
    if (lang === 'ru') return product.nameRu || product.name;
    return product.name;
  };

  const getDescription = () => {
    if (!product) return '';
    const lang = i18n.language;
    if (lang === 'en') return product.descriptionEn || product.description;
    if (lang === 'ru') return product.descriptionRu || product.description;
    return product.description;
  };

  const handleAddToCart = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    if (!product) return;
    try {
      await addToCart(product.id, 1);
      navigate('/cart');
    } catch (err) {
      console.error('Failed to add to cart:', err);
    }
  };

  const handleBuyNow = async () => {
    if (!isAuthenticated) {
      navigate('/login');
      return;
    }
    if (!product) return;
    try {
      await addToCart(product.id, 1);
      navigate('/checkout');
    } catch (err) {
      console.error('Failed to add to cart:', err);
    }
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-500"></div>
      </div>
    );
  }

  if (error || !product) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen p-4">
        <p className="text-gray-600 mb-4">{error || 'Product not found'}</p>
        <button onClick={() => navigate('/')} className="px-4 py-2 bg-red-500 text-white rounded-lg">
          Go Home
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-6">
      <div className="bg-white rounded-xl shadow-sm overflow-hidden">
        <div className="md:flex">
          <div className="md:w-1/2">
            <div className="aspect-square bg-gray-100">
              <img
                src={resolveProductImageSrc(product.imageUrl)}
                alt={getName()}
                className="w-full h-full object-cover"
                onError={onProductImageError}
              />
            </div>
          </div>
          
          <div className="md:w-1/2 p-6">
            {product.featured && (
              <span className="inline-block px-3 py-1 bg-red-500 text-white text-sm rounded-full mb-3">
                {t('product.featured')}
              </span>
            )}
            
            <h1 className="text-2xl font-bold text-gray-900 mb-2">{getName()}</h1>
            
            <p className="text-gray-500 mb-4">{product.category}</p>
            
            <div className="text-3xl font-bold text-red-500 mb-6">
              ¥{product.price.toFixed(2)}
            </div>
            
            <div className="space-y-3 mb-6">
              <p className="text-gray-600">
                <span className="font-medium">{t('product.soldCount')}:</span> {product.soldCount}
              </p>
              <p className="text-gray-600">
                <span className="font-medium">{t('product.stock')}:</span> {product.stock}
              </p>
            </div>
            
            <div className="mb-6">
              <h3 className="font-medium text-gray-900 mb-2">{t('product.details')}</h3>
              <p className="text-gray-600">{getDescription()}</p>
            </div>
            
            <div className="flex gap-4">
              <button
                onClick={handleAddToCart}
                disabled={cartLoading}
                className="flex-1 py-3 border-2 border-red-500 text-red-500 font-semibold rounded-lg hover:bg-red-50 transition-colors disabled:opacity-50"
              >
                {t('product.addToCart')}
              </button>
              <button
                onClick={handleBuyNow}
                disabled={cartLoading}
                className="flex-1 py-3 bg-red-500 text-white font-semibold rounded-lg hover:bg-red-600 transition-colors disabled:opacity-50"
              >
                {t('product.buyNow')}
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
