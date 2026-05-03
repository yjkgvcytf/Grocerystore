package com.example.grocerystore

object ProductData {
    fun getSampleProducts(): List<Product> {
        return listOf(
            Product(
                id = "1",
                name = "竹纤维毛巾",
                nameEn = "Bamboo Towel",
                nameRu = "Полотенце из бамбуса",
                description = "天然抗菌，柔软舒适，快速吸水",
                descriptionEn = "Natural antibacterial, soft and comfortable, quickly absorbs water",
                descriptionRu = "Натуральный антибактериальный, мягкий и комфортный, быстро впитывает воду",
                price = 39.90,
                imageUrl = "竹纤维毛巾",
                category = "家居用品",
                categoryEn = "Home Goods",
                categoryRu = "Товары для дома",
                soldCount = 1200,
                stock = 50
            ),
            Product(
                id = "2",
                name = "电动牙刷",
                nameEn = "Electric Toothbrush",
                nameRu = "Электрическая зубная щетка",
                description = "高效清洁，多种模式，长续航",
                descriptionEn = "Efficient cleaning, multiple modes, long battery life",
                descriptionRu = "Эффективная очистка, несколько режимов, долгая работа от батареи",
                price = 129.00,
                imageUrl = "电动牙刷",
                category = "个人护理",
                categoryEn = "Personal Care",
                categoryRu = "Личная гигиена",
                soldCount = 850,
                stock = 30
            ),
            Product(
                id = "3",
                name = "洗发水",
                nameEn = "Shampoo",
                nameRu = "Шампунь",
                description = "温和配方，深层清洁，适合所有发质",
                descriptionEn = "Gentle formula, deep cleaning, suitable for all hair types",
                descriptionRu = "Мягкая формула, глубокое очищение, подходит для всех типов волос",
                price = 49.90,
                imageUrl = "洗发水",
                category = "个人护理",
                categoryEn = "Personal Care",
                categoryRu = "Личная гигиена",
                soldCount = 2100,
                stock = 100
            ),
            Product(
                id = "4",
                name = "液体肥皂",
                nameEn = "Liquid Soap",
                nameRu = "Жидкое мыло",
                description = "天然成分，温和不刺激，香味清新",
                descriptionEn = "Natural ingredients, gentle and non-irritating, fresh fragrance",
                descriptionRu = "Натуральные ингредиенты, мягкое и не раздражающее, свежий аромат",
                price = 24.90,
                imageUrl = "液体肥皂",
                category = "个人护理",
                categoryEn = "Personal Care",
                categoryRu = "Личная гигиена",
                soldCount = 1500,
                stock = 80
            ),
            Product(
                id = "5",
                name = "保温杯",
                nameEn = "Thermos",
                nameRu = "Термос",
                description = "304不锈钢，24小时保温，便携设计",
                descriptionEn = "304 stainless steel, 24-hour heat retention, portable design",
                descriptionRu = "Нержавеющая сталь 304, сохранение тепла 24 часа, портативный дизайн",
                price = 99.00,
                imageUrl = "保温杯",
                category = "厨房用品",
                categoryEn = "Kitchen",
                categoryRu = "Кухонные принадлежности",
                soldCount = 680,
                stock = 40
            ),
            Product(
                id = "6",
                name = "有机大米",
                nameEn = "Organic Rice",
                nameRu = "Органический рис",
                description = "有机认证，营养丰富，口感香糯",
                descriptionEn = "Organic certified, nutritious, fragrant and sticky",
                descriptionRu = "Органический сертификат, питательный, ароматный и липкий",
                price = 69.90,
                imageUrl = "有机大米",
                category = "食品",
                categoryEn = "Food",
                categoryRu = "Еда",
                soldCount = 3200,
                stock = 200
            ),
            Product(
                id = "7",
                name = "新鲜苹果",
                nameEn = "Fresh Apples",
                nameRu = "Свежие яблоки",
                description = "脆甜多汁，营养丰富，每日新鲜",
                descriptionEn = "Crisp, sweet and juicy, nutritious, fresh daily",
                descriptionRu = "Хрустящие, сладкие и сочные, питательные, свежие ежедневно",
                price = 24.90,
                imageUrl = "新鲜苹果",
                category = "食品",
                categoryEn = "Food",
                categoryRu = "Еда",
                soldCount = 4500,
                stock = 150
            ),
            Product(
                id = "8",
                name = "洗衣液",
                nameEn = "Laundry Detergent",
                nameRu = "Стиральный порошок",
                description = "强效去污，护色护衣，温和不伤手",
                descriptionEn = "Strong stain removal, color protection, gentle on hands",
                descriptionRu = "Сильное удаление пятен, защита цвета, мягкое для рук",
                price = 69.90,
                imageUrl = "洗衣液",
                category = "清洁用品",
                categoryEn = "Cleaning",
                categoryRu = "Чистящие средства",
                soldCount = 1800,
                stock = 120
            )
        )
    }
    
    fun getBannerProducts(): List<Product> {
        return getSampleProducts().take(3)
    }
    
    fun getPopularProducts(): List<Product> {
        return getSampleProducts().sortedByDescending { it.soldCount }.take(6)
    }
    
    fun getProductsByCategory(category: String): List<Product> {
        return getSampleProducts().filter {
            it.category == category || it.categoryEn == category || it.categoryRu == category
        }
    }

    fun getProductsByCategoryId(categoryId: String): List<Product> {
        val category = CategoryData.getCategoryById(categoryId) ?: return emptyList()
        return getSampleProducts().filter { product ->
            product.category == category.name ||
            product.categoryEn == category.nameEn ||
            product.categoryRu == category.nameRu
        }
    }
}




