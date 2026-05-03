package com.example.grocerystore.repository

import android.content.Context
import com.example.grocerystore.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class CartRepository(private val context: Context) {

    private val apiService = ApiClient.getApiService(context)

    suspend fun getCart(): Result<CartResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCart()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to load cart"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun addToCart(productId: String, quantity: Int = 1): Result<CartResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.addToCart(AddToCartRequest(productId, quantity))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to add to cart"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun updateCartItem(itemId: String, quantity: Int): Result<CartResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateCartItem(itemId, UpdateCartRequest(quantity))
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to update cart"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun removeFromCart(itemId: String): Result<CartResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.removeFromCart(itemId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to remove from cart"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun clearCart(): Result<Unit> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.clearCart()
                if (response.isSuccessful) {
                    Result.success(Unit)
                } else {
                    Result.failure(Exception("Failed to clear cart"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
