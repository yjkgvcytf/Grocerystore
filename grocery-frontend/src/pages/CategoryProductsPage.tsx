import { useEffect, useState } from 'react';
import { useParams, useNavigate } from 'react-router-dom';
import { useTranslation } from 'react-i18next';
import { categoryApi } from '../api/categories';
import { productApi } from '../api/products';
import ProductCard from '../components/product/ProductCard';
import type { Category, Product } from '../types';
import i18n from '../i18n';

export default function CategoryProductsPage() {
  const { id } = useParams<{ id: string }>();
  const { t } = useTranslation();
  const navigate = useNavigate();
  const [category, setCategory] = useState<Category | null>(null);
  const [products, setProducts] = useState<Product[]>([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    if (id) {
      loadCategory(id);
      loadProducts(id);
    }
  }, [id]);

  const loadCategory = async (categoryId: string) => {
    try {
      const response = await categoryApi.getById(categoryId);
      setCategory(response.data);
    } catch (error) {
      console.error('Failed to load category:', error);
    }
  };

  const loadProducts = async (categoryId: string) => {
    try {
      setLoading(true);
      const response = await productApi.getByCategory(categoryId, 0, 50);
      setProducts(response.data.content);
    } catch (error) {
      console.error('Failed to load products:', error);
    } finally {
      setLoading(false);
    }
  };

  const getName = () => {
    if (!category) return '';
    const lang = i18n.language;
    if (lang === 'en') return category.nameEn || category.name;
    if (lang === 'ru') return category.nameRu || category.name;
    return category.name;
  };

  if (loading) {
    return (
      <div className="flex items-center justify-center min-h-screen">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-red-500"></div>
      </div>
    );
  }

  return (
    <div className="max-w-7xl mx-auto px-4 py-6">
      <button onClick={() => navigate('/categories')} className="flex items-center text-gray-600 mb-6">
        <svg className="w-5 h-5 mr-2" fill="none" stroke="currentColor" viewBox="0 0 24 24">
          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
        </svg>
        {t('category.title')}
      </button>

      <h1 className="text-2xl font-bold text-gray-900 mb-6">{getName()}</h1>

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
    </div>
  );
}
