package com.workwell.habitatzero.api

import android.content.Context
import okhttp3.Interceptor
import okhttp3.Response

class JWTInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val prefs = context.getSharedPreferences("HabitatZeroPrefs", Context.MODE_PRIVATE)
        val token = prefs.getString("token", null)

        val requestBuilder = chain.request().newBuilder()
        if (!token.isNullOrEmpty()) {
            requestBuilder.addHeader("Authorization", "Bearer $token")
        }

        return chain.proceed(requestBuilder.build())
    }
}

