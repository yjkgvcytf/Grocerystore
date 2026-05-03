import { useState, useEffect } from 'react';
import { useTranslation } from 'react-i18next';
import { useSearchParams } from 'react-router-dom';
import ProductCard from '../components/product/ProductCard';
import { productApi } from '../api/products';
import type { Product } from '../types';
import i18n from '../i18n';

export default function HomePage() {
  const { t } = useTranslation();
  const [searchParams] = useSearchParams();
  const searchQuery = searchParams.get('search') || '';
  
  const [products, setProducts] = useState<Product[]>([]);
  const [featuredProducts, setFeaturedProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);

  useEffect(() => {
    loadProducts();
    loadFeaturedProducts();
  }, []);

  useEffect(() => {
    if (searchQuery) {
      searchProducts(searchQuery);
    }
  }, [searchQuery]);

  const loadProducts = async () => {
    try {
      setLoading(true);
      const response = await productApi.getAll(0, 20);
      setProducts(response.data.content);
      setError(null);
    } catch (err) {
      setError('Failed to load products');
    } finally {
      setLoading(false);
    }
  };

  const loadFeaturedProducts = async () => {
    try {
      const response = await productApi.getFeatured(6);
      setFeaturedProducts(response.data);
    } catch (err) {
      console.error('Failed to load featured products:', err);
    }
  };

  const searchProducts = async (keyword: string) => {
    try {
      setLoading(true);
      const response = await productApi.search(keyword);
      setProducts(response.data.content);
      setError(null);
    } catch (err) {
      setError('Search failed');
    } finally {
      setLoading(false);
    }
  };

  const getName = (product: Product) => {
    const lang = i18n.language;
    if (lang === 'en') return product.nameEn || product.name;
    if (lang === 'ru') return product.nameRu || product.name;
    return product.name;
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-500"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="flex flex-col items-center justify-center min-h-screen p-4">
        <p className="text-gray-600 mb-4">{error}</p>
        <button onClick={loadProducts} className="px-4 py-2 bg-red-500 text-white rounded-lg">
          {t('common.retry')}
        </button>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-6">
      {searchQuery && (
        <div className="mb-6">
          <h2 className="text-xl font-semibold text-gray-900">
            Search results for "{searchQuery}"
          </h2>
          <p className="text-gray-500 mt-1">{products.length} products found</p>
        </div>
      )}

      {!searchQuery && featuredProducts.length > 0 && (
        <section className="mb-8">
          <h2 className="text-xl font-semibold text-gray-900 mb-4">{t('product.featured')}</h2>
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-6 gap-4">
            {featuredProducts.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        </section>
      )}

      <section>
        <h2 className="text-xl font-semibold text-gray-900 mb-4">
          {searchQuery ? 'Search Results' : t('product.allProducts')}
        </h2>
        {products.length === 0 ? (
          <div className="text-center py-12">
            <p className="text-gray-500">{t('product.noProducts')}</p>
          </div>
        ) : (
          <div className="grid grid-cols-2 sm:grid-cols-3 md:grid-cols-4 lg:grid-cols-5 gap-4">
            {products.map((product) => (
              <ProductCard key={product.id} product={product} />
            ))}
          </div>
        )}
      </section>
    </div>
  );
}
