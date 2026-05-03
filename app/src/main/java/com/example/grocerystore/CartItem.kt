package com.example.grocerystore

data class CartItem(
    val product: Product,
    var quantity: Int = 1,
    var id: String? = null  // API cart item ID for update/delete
) {
    fun getTotalPrice(): Double {
        return product.price * quantity
    }
}




