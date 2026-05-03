package com.example.grocerystore.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(context: Context) : Interceptor {

    private val sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE)

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val token = sharedPreferences.getString("auth_token", null)
        val language = sharedPreferences.getString("selected_language", "zh") ?: "zh"

        val requestBuilder = originalRequest.newBuilder()
            .header("Accept-Language", language)

        if (!token.isNullOrEmpty()) {
            requestBuilder.header("Authorization", "Bearer $token")
        }

        val request = requestBuilder.build()
        return chain.proceed(request)
    }
}
