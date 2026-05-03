package com.example.grocerystore.api

import retrofit2.Response
import retrofit2.http.*

interface ApiService {

    // ============ Auth ============
    
    @POST("auth/login")
    suspend fun login(@Body request: LoginRequest): Response<AuthResponse>

    @POST("auth/register")
    suspend fun register(@Body request: RegisterRequest): Response<AuthResponse>

    // ============ Products ============

    @GET("products")
    suspend fun getProducts(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<Product>>

    @GET("products/{id}")
    suspend fun getProductById(@Path("id") id: String): Response<Product>

    @GET("products/featured")
    suspend fun getFeaturedProducts(
        @Query("limit") limit: Int = 10
    ): Response<List<Product>>

    @GET("products/search")
    suspend fun searchProducts(
        @Query("q") query: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<Product>>

    @GET("products/category/{categoryId}")
    suspend fun getProductsByCategory(
        @Path("categoryId") categoryId: String,
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 20
    ): Response<PageResponse<Product>>

    // ============ Categories ============

    @GET("categories")
    suspend fun getCategories(): Response<List<Category>>

    @GET("categories/{id}")
    suspend fun getCategoryById(@Path("id") id: String): Response<Category>

    // ============ Cart ============

    @GET("cart")
    suspend fun getCart(): Response<CartResponse>

    @POST("cart/items")
    suspend fun addToCart(@Body request: AddToCartRequest): Response<CartResponse>

    @PUT("cart/items/{itemId}")
    suspend fun updateCartItem(
        @Path("itemId") itemId: String,
        @Body request: UpdateCartRequest
    ): Response<CartResponse>

    @DELETE("cart/items/{itemId}")
    suspend fun removeFromCart(@Path("itemId") itemId: String): Response<CartResponse>

    @DELETE("cart")
    suspend fun clearCart(): Response<Unit>

    // ============ Orders ============

    @GET("orders")
    suspend fun getOrders(
        @Query("page") page: Int = 0,
        @Query("size") size: Int = 10
    ): Response<PageResponse<Order>>

    @GET("orders/{id}")
    suspend fun getOrderById(@Path("id") id: String): Response<Order>

    @POST("orders")
    suspend fun createOrder(@Body request: CreateOrderRequest): Response<Order>

    // ============ User ============

    @GET("users/profile")
    suspend fun getProfile(): Response<User>

    @PUT("users/profile")
    suspend fun updateProfile(@Body request: UpdateProfileRequest): Response<User>
}
