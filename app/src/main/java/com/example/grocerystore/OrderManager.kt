package com.example.grocerystore

import java.util.Date
import java.util.UUID

object OrderManager {
    private val orders = mutableListOf<Order>()
    
    fun createOrder(
        items: List<CartItem>,
        shippingAddress: String,
        recipientName: String,
        recipientPhone: String
    ): Order {
        val originalPrice = items.sumOf { it.getTotalPrice() }
        val discount = calculateDiscount(originalPrice)
        val reduction = calculateReduction(originalPrice)
        val finalTotal = originalPrice - discount - reduction
        
        val order = Order(
            id = UUID.randomUUID().toString(),
            orderNumber = generateOrderNumber(),
            items = items.toList(),
            originalPrice = originalPrice,
            discount = discount,
            reduction = reduction,
            finalTotal = if (finalTotal < 0) 0.0 else finalTotal,
            shippingAddress = shippingAddress,
            recipientName = recipientName,
            recipientPhone = recipientPhone,
            orderDate = Date(),
            status = OrderStatus.PENDING
        )
        
        orders.add(0, order) // Add to beginning
        return order
    }
    
    fun getOrders(): List<Order> {
        return orders.toList()
    }
    
    fun getOrderById(orderId: String): Order? {
        return orders.find { it.id == orderId }
    }
    
    private fun calculateDiscount(total: Double): Double {
        return if (total > 100) {
            total * 0.10
        } else {
            0.0
        }
    }
    
    private fun calculateReduction(total: Double): Double {
        return if (total > 200) {
            20.0
        } else {
            0.0
        }
    }
    
    private fun generateOrderNumber(): String {
        return "ORD${System.currentTimeMillis()}"
    }
}




