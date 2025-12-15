package com.example.grocerystore

import android.content.Context
import android.content.SharedPreferences

object UserManager {
    private const val PREFS_NAME = "user_prefs"
    private const val KEY_USER_ID = "user_id"
    private const val KEY_EMAIL = "email"
    private const val KEY_FULL_NAME = "full_name"
    private const val KEY_PHONE = "phone"
    private const val KEY_SHIPPING_ADDRESS = "shipping_address"
    private const val KEY_IS_LOGGED_IN = "is_logged_in"
    
    private var currentUser: User? = null
    
    fun login(context: Context, email: String, fullName: String, phone: String) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        
        val userId = prefs.getString(KEY_USER_ID, null) ?: java.util.UUID.randomUUID().toString()
        
        editor.putString(KEY_USER_ID, userId)
        editor.putString(KEY_EMAIL, email)
        editor.putString(KEY_FULL_NAME, fullName)
        editor.putString(KEY_PHONE, phone)
        editor.putBoolean(KEY_IS_LOGGED_IN, true)
        editor.apply()
        
        currentUser = User(
            id = userId,
            email = email,
            fullName = fullName,
            phone = phone,
            shippingAddress = prefs.getString(KEY_SHIPPING_ADDRESS, "") ?: "",
            isLoggedIn = true
        )
    }
    
    fun logout(context: Context) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putBoolean(KEY_IS_LOGGED_IN, false)
        editor.apply()
        
        currentUser = null
    }
    
    fun getCurrentUser(context: Context): User? {
        if (currentUser != null) {
            return currentUser
        }
        
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isLoggedIn = prefs.getBoolean(KEY_IS_LOGGED_IN, false)
        
        if (!isLoggedIn) {
            return null
        }
        
        val userId = prefs.getString(KEY_USER_ID, null) ?: return null
        val email = prefs.getString(KEY_EMAIL, null) ?: return null
        val fullName = prefs.getString(KEY_FULL_NAME, null) ?: return null
        val phone = prefs.getString(KEY_PHONE, null) ?: return null
        val shippingAddress = prefs.getString(KEY_SHIPPING_ADDRESS, "") ?: ""
        
        currentUser = User(
            id = userId,
            email = email,
            fullName = fullName,
            phone = phone,
            shippingAddress = shippingAddress,
            isLoggedIn = true
        )
        
        return currentUser
    }
    
    fun updateUser(context: Context, fullName: String?, phone: String?, shippingAddress: String?) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        
        fullName?.let { editor.putString(KEY_FULL_NAME, it) }
        phone?.let { editor.putString(KEY_PHONE, it) }
        shippingAddress?.let { editor.putString(KEY_SHIPPING_ADDRESS, it) }
        editor.apply()
        
        currentUser?.let { user ->
            currentUser = user.copy(
                fullName = fullName ?: user.fullName,
                phone = phone ?: user.phone,
                shippingAddress = shippingAddress ?: user.shippingAddress
            )
        }
    }
    
    fun isLoggedIn(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false)
    }
}




