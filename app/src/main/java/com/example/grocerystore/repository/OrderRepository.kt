package com.example.grocerystore.repository

import android.content.Context
import com.example.grocerystore.api.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class OrderRepository(private val context: Context) {

    private val apiService = ApiClient.getApiService(context)

    suspend fun getOrders(page: Int = 0, size: Int = 10): Result<List<Order>> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getOrders(page, size)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!.content)
                } else {
                    Result.failure(Exception("Failed to load orders"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun getOrderById(orderId: String): Result<Order> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.getOrderById(orderId)
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Order not found"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }

    suspend fun createOrder(recipientName: String, recipientPhone: String, shippingAddress: String): Result<Order> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.createOrder(
                    CreateOrderRequest(recipientName, recipientPhone, shippingAddress)
                )
                if (response.isSuccessful && response.body() != null) {
                    Result.success(response.body()!!)
                } else {
                    Result.failure(Exception("Failed to create order"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
