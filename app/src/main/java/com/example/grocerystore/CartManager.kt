package com.example.grocerystore

object CartManager {
    private val cartItems = mutableListOf<CartItem>()
    
    fun addToCart(product: Product, quantity: Int = 1) {
        val existingItem = cartItems.find { it.product.id == product.id }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            cartItems.add(CartItem(product, quantity))
        }
    }
    
    fun removeFromCart(productId: String) {
        cartItems.removeAll { it.product.id == productId }
    }
    
    fun updateQuantity(productId: String, quantity: Int) {
        val item = cartItems.find { it.product.id == productId }
        if (item != null) {
            if (quantity <= 0) {
                removeFromCart(productId)
            } else {
                item.quantity = quantity
            }
        }
    }
    
    fun getCartItems(): List<CartItem> {
        return cartItems.toList()
    }
    
    fun getTotalCount(): Int {
        return cartItems.sumOf { it.quantity }
    }
    
    fun getTotalPrice(): Double {
        return cartItems.sumOf { it.getTotalPrice() }
    }
    
    fun clearCart() {
        cartItems.clear()
    }
    
    fun getItemCount(productId: String): Int {
        return cartItems.find { it.product.id == productId }?.quantity ?: 0
    }
    
    // Price calculation methods
    fun getOriginalTotalPrice(): Double {
        return cartItems.sumOf { it.getTotalPrice() }
    }
    
    fun getDiscount(): Double {
        // Example: 10% discount if total > 100
        val originalTotal = getOriginalTotalPrice()
        return if (originalTotal > 100) {
            originalTotal * 0.10
        } else {
            0.0
        }
    }
    
    fun getReduction(): Double {
        // Example: ¥20 reduction if total > 200
        val originalTotal = getOriginalTotalPrice()
        return if (originalTotal > 200) {
            20.0
        } else {
            0.0
        }
    }
    
    fun getFinalTotal(): Double {
        val originalTotal = getOriginalTotalPrice()
        val discount = getDiscount()
        val reduction = getReduction()
        val finalTotal = originalTotal - discount - reduction
        return if (finalTotal < 0) 0.0 else finalTotal
    }
    
    fun getTotalSavings(): Double {
        return getDiscount() + getReduction()
    }
}

