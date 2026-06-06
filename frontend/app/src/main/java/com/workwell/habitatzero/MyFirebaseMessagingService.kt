package com.workwell.habitatzero

import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.edit
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.workwell.habitatzero.R

class MyFirebaseMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("FCM", "Novo token: $token")
        
        // Salva o token localmente
        val prefs = getSharedPreferences("HabitatZeroPrefs", MODE_PRIVATE)
        prefs.edit {
            putString("fcm_token", token)
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        // Trata mensagens de dados
        if (remoteMessage.data.isNotEmpty()) {
            val titulo = remoteMessage.data["titulo"] ?: "Aviso HabitatZero"
            val mensagem = remoteMessage.data["mensagem"] ?: ""
            mostrarNotificacao(titulo, mensagem)
        }

        // Trata mensagens de notificação do Firebase Console
        remoteMessage.notification?.let {
            mostrarNotificacao(it.title ?: "Nova mensagem", it.body ?: "")
        }
    }

    @SuppressLint("MissingPermission")
    private fun mostrarNotificacao(titulo: String, mensagem: String) {
        val channelId = "firebase_channel"

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Notificações HabitatZero",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Canal para alertas e mensagens do sistema"
            }
            val manager = getSystemService(NotificationManager::class.java)
            manager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        with(NotificationManagerCompat.from(this)) {
            notify(System.currentTimeMillis().toInt(), builder.build())
        }
    }
}
