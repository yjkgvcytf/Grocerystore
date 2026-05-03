package com.example.grocerystore

import android.content.Context
import com.example.grocerystore.api.User
import com.example.grocerystore.repository.AuthRepository

object UserManager {
    private lateinit var context: Context

    fun init(context: Context) {
        this.context = context.applicationContext
    }

    private val authRepository: AuthRepository by lazy {
        AuthRepository(context)
    }

    fun login(email: String, fullName: String, phone: String) {
        // UserManager no longer manages its own storage
        // This method is kept for backward compatibility but is a no-op
        // Login is handled via AuthRepository.login()
    }

    fun logout() {
        authRepository.logout()
    }

    fun getCurrentUser(): User? {
        return authRepository.getCurrentUser()
    }

    fun updateUser(fullName: String?, phone: String?, shippingAddress: String?) {
        // Update locally cached data
        // Note: For persistent update, use AuthRepository.updateProfile() with coroutines
    }

    fun isLoggedIn(): Boolean {
        return authRepository.isLoggedIn()
    }
}




