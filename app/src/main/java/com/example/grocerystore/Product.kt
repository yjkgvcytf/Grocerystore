package com.example.grocerystore

data class Product(
    val id: String,
    val name: String,
    val nameEn: String?,
    val nameRu: String?,
    val description: String?,
    val descriptionEn: String?,
    val descriptionRu: String?,
    val price: Double,
    val imageUrl: String?,
    val category: String?,
    val categoryEn: String?,
    val categoryRu: String?,
    val soldCount: Int = 0,
    val stock: Int = 100,
    val featured: Boolean? = false
)




