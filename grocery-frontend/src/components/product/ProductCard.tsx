import { Link } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { useCartStore } from '../../stores/cartStore';
import { useAuthStore, selectIsAuthenticated } from '../../stores/authStore';
import type { Product } from '../../types';
import i18n from '../../i18n';
import { resolveProductImageSrc, onProductImageError } from '../../utils/productImage';

interface ProductCardProps {
  product: Product;
  onAddToCart?: (product: Product) => void;
}

export default function ProductCard({ product, onAddToCart }: ProductCardProps) {
  const { t } = useTranslation();
  const { addToCart } = useCartStore();
  const isAuthenticated = useAuthStore(selectIsAuthenticated);

  const getName = () => {
    const lang = i18n.language;
    if (lang === 'en') return product.nameEn || product.name;
    if (lang === 'ru') return product.nameRu || product.name;
    return product.name;
  };

  const getCategory = () => {
    const lang = i18n.language;
    if (lang === 'en') return product.categoryEn || product.category;
    if (lang === 'ru') return product.categoryRu || product.category;
    return product.category;
  };

  const handleAddToCart = async (e: React.MouseEvent) => {
    e.preventDefault();
    e.stopPropagation();
    if (!isAuthenticated) {
      window.location.href = '/login';
      return;
    }
    try {
      await addToCart(product.id, 1);
    } catch (error) {
      console.error('Failed to add to cart:', error);
    }
  };

  return (
    <Link to={`/product/${product.id}`} className="block bg-white rounded-lg shadow-sm overflow-hidden hover:shadow-md transition-shadow">
      <div className="aspect-square bg-gray-100 relative overflow-hidden">
        <img
          src={resolveProductImageSrc(product.imageUrl)}
          alt={getName()}
          className="w-full h-full object-cover"
          onError={onProductImageError}
        />
        {product.featured && (
          <span className="absolute top-2 left-2 px-2 py-1 bg-red-500 text-white text-xs rounded">
            {t('product.featured')}
          </span>
        )}
      </div>
      <div className="p-3">
        <h3 className="text-sm font-medium text-gray-900 line-clamp-2 h-10">{getName()}</h3>
        <p className="text-xs text-gray-500 mt-1">{getCategory()}</p>
        <div className="flex items-center justify-between mt-2">
          <div>
            <span className="text-lg font-bold text-red-500">¥{product.price.toFixed(2)}</span>
          </div>
          <button
            onClick={handleAddToCart}
            className="p-2 bg-red-500 text-white rounded-full hover:bg-red-600 transition-colors"
          >
            <svg className="w-4 h-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M12 4v16m8-8H4" />
            </svg>
          </button>
        </div>
        <p className="text-xs text-gray-400 mt-2">
          {t('product.soldCount')}: {product.soldCount}
        </p>
      </div>
    </Link>
  );
}
