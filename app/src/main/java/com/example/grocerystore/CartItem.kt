package com.example.grocerystore

data class CartItem(
    val product: Product,
    var quantity: Int = 1
) {
    fun getTotalPrice(): Double {
        return product.price * quantity
    }
}




