package com.example.grocerystore

object CategoryData {
    fun getCategories(): List<Category> {
        return listOf(
            Category(
                id = "1",
                name = "家居用品",
                nameEn = "Home Goods",
                nameRu = "Товары для дома",
                icon = android.R.drawable.ic_menu_myplaces
            ),
            Category(
                id = "2",
                name = "个人护理",
                nameEn = "Personal Care",
                nameRu = "Личная гигиена",
                icon = android.R.drawable.ic_menu_view
            ),
            Category(
                id = "3",
                name = "厨房用品",
                nameEn = "Kitchen",
                nameRu = "Кухонные принадлежности",
                icon = android.R.drawable.ic_menu_agenda
            ),
            Category(
                id = "4",
                name = "食品",
                nameEn = "Food",
                nameRu = "Еда",
                icon = android.R.drawable.ic_menu_gallery
            ),
            Category(
                id = "5",
                name = "清洁用品",
                nameEn = "Cleaning",
                nameRu = "Чистящие средства",
                icon = android.R.drawable.ic_menu_delete
            ),
            Category(
                id = "6",
                name = "电子产品",
                nameEn = "Electronics",
                nameRu = "Электроника",
                icon = android.R.drawable.ic_menu_compass
            ),
            Category(
                id = "7",
                name = "服装配饰",
                nameEn = "Clothing & Accessories",
                nameRu = "Одежда и аксессуары",
                icon = android.R.drawable.ic_menu_recent_history
            ),
            Category(
                id = "8",
                name = "运动健身",
                nameEn = "Sports & Fitness",
                nameRu = "Спорт и фитнес",
                icon = android.R.drawable.ic_menu_share
            )
        )
    }
    
    fun getCategoryById(id: String): Category? {
        return getCategories().find { it.id == id }
    }
    
    fun getProductsByCategoryId(categoryId: String): List<Product> {
        val category = getCategoryById(categoryId)
        return if (category != null) {
            ProductData.getSampleProducts().filter { product ->
                product.category == category.name || 
                product.categoryEn == category.nameEn || 
                product.categoryRu == category.nameRu
            }
        } else {
            emptyList()
        }
    }
}




