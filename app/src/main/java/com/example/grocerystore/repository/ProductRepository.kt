package com.example.grocerystore.repository

import android.content.Context
import com.example.grocerystore.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class ProductRepository(private val context: Context) {

    private val apiService = ApiClient.getApiService(context)

    suspend fun getProducts(page: Int = 0, size: Int = 20): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProducts(page, size)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.content)
                } else {
                    Result.failure(Exception("Failed to load products"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getProductById(id: String): Result<Product> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProductById(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Product not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getFeaturedProducts(limit: Int = 10): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getFeaturedProducts(limit)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to load featured products"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun searchProducts(query: String, page: Int = 0, size: Int = 20): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.searchProducts(query, page, size)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.content)
                } else {
                    Result.failure(Exception("Search failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getProductsByCategory(categoryId: String, page: Int = 0, size: Int = 20): Result<List<Product>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getProductsByCategory(categoryId, page, size)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.content)
                } else {
                    Result.failure(Exception("Failed to load products"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getCategories(): Result<List<Category>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCategories()
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to load categories"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getCategoryById(id: String): Result<Category> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getCategoryById(id)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Category not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
