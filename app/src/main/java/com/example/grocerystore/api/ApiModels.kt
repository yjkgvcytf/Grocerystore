package com.example.grocerystore.api

import com.google.gson.annotations.SerializedName

// ============ Request Models ============

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val email: String,
    val password: String,
    val fullName: String,
    val phone: String
)

data class AddToCartRequest(
    val productId: String,
    val quantity: Int = 1
)

data class UpdateCartRequest(
    val quantity: Int
)

data class CreateOrderRequest(
    val recipientName: String,
    val recipientPhone: String,
    val shippingAddress: String
)

data class UpdateProfileRequest(
    val fullName: String?,
    val phone: String?,
    val shippingAddress: String?
)

// ============ Response Models ============

data class AuthResponse(
    val token: String,
    val user: User
)

data class User(
    val id: String,
    val email: String,
    @SerializedName("fullName") val fullName: String?,
    val phone: String?,
    @SerializedName("shippingAddress") val shippingAddress: String?
)

data class Product(
    val id: String,
    val name: String,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("nameRu") val nameRu: String?,
    val description: String?,
    @SerializedName("descriptionEn") val descriptionEn: String?,
    @SerializedName("descriptionRu") val descriptionRu: String?,
    val price: Double,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("categoryId") val categoryId: String?,
    val category: String?,
    @SerializedName("categoryEn") val categoryEn: String?,
    @SerializedName("categoryRu") val categoryRu: String?,
    @SerializedName("soldCount") val soldCount: Int,
    val stock: Int,
    val featured: Boolean?
)

data class Category(
    val id: String,
    val name: String,
    @SerializedName("nameEn") val nameEn: String?,
    @SerializedName("nameRu") val nameRu: String?,
    val icon: String?,
    val products: List<Product>?
)

data class CartItem(
    val id: String,
    val product: Product,
    val quantity: Int,
    val subtotal: Double
)

data class CartResponse(
    val items: List<CartItem>,
    @SerializedName("originalPrice") val originalPrice: Double,
    val discount: Double,
    val reduction: Double,
    @SerializedName("finalTotal") val finalTotal: Double
)

data class OrderItem(
    val id: String,
    @SerializedName("productId") val productId: String,
    @SerializedName("productName") val productName: String?,
    @SerializedName("productNameEn") val productNameEn: String?,
    @SerializedName("imageUrl") val imageUrl: String?,
    @SerializedName("unitPrice") val unitPrice: Double,
    val quantity: Int,
    val subtotal: Double
)

data class Order(
    val id: String,
    @SerializedName("orderNumber") val orderNumber: String,
    @SerializedName("originalPrice") val originalPrice: Double,
    val discount: Double,
    val reduction: Double,
    @SerializedName("finalTotal") val finalTotal: Double,
    @SerializedName("shippingAddress") val shippingAddress: String,
    @SerializedName("recipientName") val recipientName: String,
    @SerializedName("recipientPhone") val recipientPhone: String,
    val status: String,
    @SerializedName("orderDate") val orderDate: String,
    val items: List<OrderItem>
)

data class PageResponse<T>(
    val content: List<T>,
    val page: Int,
    val size: Int,
    @SerializedName("totalElements") val totalElements: Long,
    @SerializedName("totalPages") val totalPages: Int,
    val last: Boolean
)

data class ErrorResponse(
    val status: Int,
    val message: String?
)
