package com.workwell.habitatzero.api

import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.widget.Toast
import androidx.core.content.edit
import com.workwell.habitatzero.R
import com.workwell.habitatzero.ui.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == 401) {
            context.getSharedPreferences("HabitatZeroPrefs", Context.MODE_PRIVATE).edit {
                remove("token")
            }
            // Must run UI operations on the main thread
            Handler(Looper.getMainLooper()).post {
                Toast.makeText(context, context.getString(R.string.sessao_expirada), Toast.LENGTH_LONG).show()
                context.startActivity(Intent(context, LoginActivity::class.java).apply {
                    flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                })
            }
        }

        return response
    }
}
