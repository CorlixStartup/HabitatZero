package com.workwell.habitatzero.api

import android.content.Context
import android.content.Intent
import android.widget.Toast
import com.workwell.habitatzero.R
import com.workwell.habitatzero.ui.LoginActivity
import okhttp3.Interceptor
import okhttp3.Response
import androidx.core.content.edit // 🔹 Import necessário para usar a extensão KTX

class AuthInterceptor(private val context: Context) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())

        if (response.code == 401) {
            // Limpa token salvo usando KTX extension
            val prefs = context.getSharedPreferences("HabitatZeroPrefs", Context.MODE_PRIVATE)
            prefs.edit {
                remove("token")
            }

            // 👉 Mensagem amigável usando strings.xml
            Toast.makeText(
                context,
                context.getString(R.string.sessao_expirada),
                Toast.LENGTH_LONG
            ).show()

            // Redireciona para LoginActivity
            val intent = Intent(context, LoginActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
        }

        return response
    }
}
