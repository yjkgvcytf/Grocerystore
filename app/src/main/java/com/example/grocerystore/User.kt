package com.example.grocerystore

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val phone: String,
    val shippingAddress: String = "",
    val isLoggedIn: Boolean = false
)




