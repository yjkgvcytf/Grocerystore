package com.example.grocerystore.repository

import android.content.Context
import com.example.grocerystore.api.*
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AuthRepository(private val context: Context) {

    private val apiService = ApiClient.getApiService(context)
    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    private fun parseServerMessage(raw: String?, httpCode: Int): String {
        if (raw.isNullOrBlank()) {
            return when (httpCode) {
                401 -> "邮箱或密码错误"
                409 -> "该邮箱已被注册"
                403 -> "没有权限"
                404 -> "资源不存在"
                else -> "请求失败 ($httpCode)"
            }
        }
        val trimmed = raw.trim()
        if (trimmed.startsWith("{")) {
            try {
                val err = gson.fromJson(trimmed, ErrorResponse::class.java)
                if (!err.message.isNullOrBlank()) {
                    return when {
                        err.message!!.contains("Invalid email or password", ignoreCase = true) ||
                        err.message!!.contains("Bad credentials", ignoreCase = true) -> "邮箱或密码错误"
                        err.message!!.contains("already", ignoreCase = true) &&
                        err.message!!.contains("email", ignoreCase = true) -> "该邮箱已被注册"
                        else -> err.message!!
                    }
                }
            } catch (_: Exception) {}
        }
        return trimmed
    }

    suspend fun login(email: String, password: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.login(LoginRequest(email, password))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    saveAuthData(authResponse)
                    Result.success(authResponse)
                } else {
                    val errorMsg = try {
                        parseServerMessage(response.errorBody()?.string(), response.code())
                    } catch (e: Exception) {
                        "登录失败 (错误代码: ${response.code()})"
                    }
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true ->
                        "无法连接到服务器，请检查网络连接"
                    e.message?.contains("timeout") == true ->
                        "连接超时，请稍后重试"
                    e.message?.contains("connection") == true ->
                        "连接失败，请检查服务器是否运行"
                    else -> e.message ?: "登录失败"
                }
                Result.failure(Exception(errorMessage))
            }
        }
    }

    suspend fun register(email: String, password: String, fullName: String, phone: String): Result<AuthResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.register(RegisterRequest(email, password, fullName, phone))
                if (response.isSuccessful && response.body() != null) {
                    val authResponse = response.body()!!
                    saveAuthData(authResponse)
                    Result.success(authResponse)
                } else {
                    val errorMsg = try {
                        parseServerMessage(response.errorBody()?.string(), response.code())
                    } catch (e: Exception) {
                        "注册失败 (错误代码: ${response.code()})"
                    }
                    Result.failure(Exception(errorMsg))
                }
            } catch (e: Exception) {
                val errorMessage = when {
                    e.message?.contains("Unable to resolve host") == true ->
                        "无法连接到服务器，请检查网络连接"
                    e.message?.contains("timeout") == true ->
                        "连接超时，请稍后重试"
                    e.message?.contains("connection") == true ->
                        "连接失败，请检查服务器是否运行"
                    else -> e.message ?: "注册失败"
                }
                Result.failure(Exception(errorMessage))
            }
        }
    }

    private fun saveAuthData(authResponse: AuthResponse) {
        sharedPreferences.edit().apply {
            putString("auth_token", authResponse.token)
            putString("user_id", authResponse.user.id)
            putString("user_email", authResponse.user.email)
            putString("user_name", authResponse.user.fullName)
            putString("user_phone", authResponse.user.phone)
            putString("user_address", authResponse.user.shippingAddress)
            putBoolean("is_logged_in", true)
            apply()
        }
    }

    fun logout() {
        sharedPreferences.edit().apply {
            remove("auth_token")
            remove("user_id")
            remove("user_email")
            remove("user_name")
            remove("user_phone")
            remove("user_address")
            putBoolean("is_logged_in", false)
            apply()
        }
    }

    /**
     * Clear all authentication data - useful for debugging login issues
     */
    fun clearAuthData() {
        sharedPreferences.edit().apply {
            remove("auth_token")
            remove("user_id")
            remove("user_email")
            remove("user_name")
            remove("user_phone")
            remove("user_address")
            remove("is_logged_in")
            apply()
        }
    }

    /**
     * Check if auth token exists (even if isLoggedIn might have issues)
     */
    fun hasAuthToken(): Boolean {
        val token = sharedPreferences.getString("auth_token", null)
        return !token.isNullOrEmpty()
    }

    fun isLoggedIn(): Boolean {
        return sharedPreferences.getBoolean("is_logged_in", false)
    }

    /**
     * Debug method to check all stored auth data
     */
    fun debugAuthState(): Map<String, *> {
        return sharedPreferences.all.filter { it.key.startsWith("auth_") || it.key == "is_logged_in" }
    }

    fun getCurrentUser(): User? {
        if (!isLoggedIn()) return null
        
        return User(
            id = sharedPreferences.getString("user_id", "") ?: "",
            email = sharedPreferences.getString("user_email", "") ?: "",
            fullName = sharedPreferences.getString("user_name", null),
            phone = sharedPreferences.getString("user_phone", null),
            shippingAddress = sharedPreferences.getString("user_address", null)
        )
    }

    suspend fun updateProfile(fullName: String?, phone: String?, address: String?): Result<User> {
        return withContext(Dispatchers.IO) {
            try {
                val response = apiService.updateProfile(UpdateProfileRequest(fullName, phone, address))
                if (response.isSuccessful && response.body() != null) {
                    val user = response.body()!!
                    // Update local storage
                    sharedPreferences.edit().apply {
                        putString("user_name", user.fullName)
                        putString("user_phone", user.phone)
                        putString("user_address", user.shippingAddress)
                        apply()
                    }
                    Result.success(user)
                } else {
                    Result.failure(Exception("Update failed"))
                }
            } catch (e: Exception) {
                Result.failure(e)
            }
        }
    }
}
