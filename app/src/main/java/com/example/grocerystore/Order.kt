package com.example.grocerystore

import java.util.Date

data class Order(
    val id: String,
    val orderNumber: String,
    val items: List<CartItem>,
    val originalPrice: Double,
    val discount: Double,
    val reduction: Double,
    val finalTotal: Double,
    val shippingAddress: String,
    val recipientName: String,
    val recipientPhone: String,
    val orderDate: Date,
    val status: OrderStatus = OrderStatus.PENDING
)

enum class OrderStatus {
    PENDING,    // 待处理
    PROCESSING, // 处理中
    SHIPPED,    // 已发货
    DELIVERED,  // 已送达
    CANCELLED   // 已取消
}




