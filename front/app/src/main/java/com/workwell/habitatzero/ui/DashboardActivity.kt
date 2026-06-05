package com.workwell.habitatzero.ui

import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.media.RingtoneManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.android.material.card.MaterialCardView
import com.workwell.habitatzero.R
import com.workwell.habitatzero.model.SensorAmbiente
import com.workwell.habitatzero.repository.HabitatZeroRepository
import com.workwell.habitatzero.viewmodel.DashboardViewModel
import com.workwell.habitatzero.viewmodel.DashboardViewModelFactory

class DashboardActivity : AppCompatActivity() {

    private lateinit var tvTemperatura: TextView
    private lateinit var tvUmidade: TextView
    private lateinit var tvOxigenio: TextView
    private lateinit var tvRadiacao: TextView

    private lateinit var cardTemperatura: MaterialCardView
    private lateinit var cardUmidade: MaterialCardView
    private lateinit var cardOxigenio: MaterialCardView
    private lateinit var cardRadiacao: MaterialCardView

    private lateinit var progressBar: ProgressBar

    private lateinit var chartTemperatura: LineChart
    private lateinit var chartUmidade: LineChart
    private lateinit var chartOxigenio: LineChart

    private val viewModel: DashboardViewModel by viewModels {
        DashboardViewModelFactory(HabitatZeroRepository())
    }

    private val handler = Handler(Looper.getMainLooper())
    private val updateRunnable = object : Runnable {
        override fun run() {
            viewModel.carregarSensores()
            handler.postDelayed(this, 5000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        tvTemperatura = findViewById(R.id.tvTemperatura)
        tvUmidade = findViewById(R.id.tvUmidade)
        tvOxigenio = findViewById(R.id.tvOxigenio)
        tvRadiacao = findViewById(R.id.tvRadiacao)

        cardTemperatura = findViewById(R.id.cardTemperatura)
        cardUmidade = findViewById(R.id.cardUmidade)
        cardOxigenio = findViewById(R.id.cardOxigenio)
        cardRadiacao = findViewById(R.id.cardRadiacao)

        progressBar = findViewById(R.id.progressBar)

        chartTemperatura = findViewById(R.id.chartTemperatura)
        chartUmidade = findViewById(R.id.chartUmidade)
        chartOxigenio = findViewById(R.id.chartOxigenio)

        setupObservers()
        viewModel.carregarSensores()
    }

    private fun setupObservers() {
        viewModel.sensorLiveData.observe(this) { sensor ->
            progressBar.visibility = View.GONE
            tvTemperatura.text = getString(R.string.label_temperatura, sensor.temperatura)
            tvUmidade.text = getString(R.string.label_umidade, sensor.umidade)
            tvOxigenio.text = getString(R.string.label_oxigenio, sensor.oxigenio)
            tvRadiacao.text = getString(R.string.label_radiacao, sensor.radiacao)

            animarTexto(tvTemperatura)
            animarTexto(tvUmidade)
            animarTexto(tvOxigenio)
            animarTexto(tvRadiacao)

            aplicarCor(cardTemperatura, sensor.temperatura, 18.0, 26.0, getString(R.string.titulo_temperatura))
            aplicarCor(cardUmidade, sensor.umidade, 40.0, 70.0, getString(R.string.titulo_umidade))
            aplicarCor(cardOxigenio, sensor.oxigenio, 19.0, 23.0, getString(R.string.titulo_oxigenio))
            aplicarCor(cardRadiacao, sensor.radiacao, 0.0, 1.0, getString(R.string.titulo_radiacao))

            atualizarGraficos(sensor)
        }

        viewModel.errorLiveData.observe(this) { error ->
            progressBar.visibility = View.GONE
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        handler.post(updateRunnable)
    }

    override fun onPause() {
        super.onPause()
        handler.removeCallbacks(updateRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
    }

    private fun atualizarGraficos(sensor: SensorAmbiente) {
        val temperaturaEntries: List<Entry> = listOf(Entry(0f, sensor.temperatura.toFloat()))
        val umidadeEntries: List<Entry> = listOf(Entry(0f, sensor.umidade.toFloat()))
        val oxigenioEntries: List<Entry> = listOf(Entry(0f, sensor.oxigenio.toFloat()))

        val temperaturaDataSet = LineDataSet(temperaturaEntries, "Temperatura (°C)")
        temperaturaDataSet.color = getColor(R.color.azul_neon)

        val umidadeDataSet = LineDataSet(umidadeEntries, "Umidade (%)")
        umidadeDataSet.color = getColor(R.color.verde_tecnologico)

        val oxigenioDataSet = LineDataSet(oxigenioEntries, "Oxigênio (%)")
        oxigenioDataSet.color = getColor(R.color.amarelo_atencao)

        chartTemperatura.data = LineData(temperaturaDataSet)
        chartUmidade.data = LineData(umidadeDataSet)
        chartOxigenio.data = LineData(oxigenioDataSet)

        chartTemperatura.invalidate()
        chartUmidade.invalidate()
        chartOxigenio.invalidate()

        chartTemperatura.animateX(1000)
        chartUmidade.animateY(1000)
        chartOxigenio.animateXY(1000, 1000)
    }

    private fun aplicarCor(card: MaterialCardView, valor: Double, min: Double, max: Double, nome: String) {
        val cor = when {
            valor in min..max -> getColor(R.color.verde_normal)
            valor < min -> {
                mostrarNotificacao("Alerta de $nome", "$nome abaixo do limite: $valor")
                tocarSomCritico()
                vibrarCritico()
                animarCard(card)
                getColor(R.color.vermelho_critico)
            }
            else -> {
                mostrarNotificacao("Alerta de $nome", "$nome acima do limite: $valor")
                tocarSomCritico()
                vibrarCritico()
                animarCard(card)
                getColor(R.color.vermelho_critico)
            }
        }
        card.setCardBackgroundColor(cor)
    }

    private fun animarCard(card: MaterialCardView) {
        val animator = ObjectAnimator.ofFloat(card, "alpha", 0.5f, 1f)
        animator.duration = 800
        animator.repeatMode = ValueAnimator.REVERSE
        animator.repeatCount = 2
        animator.start()
    }

    private fun animarTexto(view: TextView) {
        view.alpha = 0f
        view.animate().alpha(1f).setDuration(600).start()
    }

    private fun mostrarNotificacao(titulo: String, mensagem: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
                return
            }
        }

        val channelId = "alertas_sensores"
        val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                channelId,
                "Alertas de Sensores",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                enableVibration(true)
                vibrationPattern = longArrayOf(100, 200, 300, 400, 500, 400, 300, 200, 400)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        notificationManager.notify(System.currentTimeMillis().toInt(), builder.build())
    }

    private fun tocarSomNormal() {
        try {
            val notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val r = RingtoneManager.getRingtone(applicationContext, notification)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun tocarSomCritico() {
        try {
            val alert = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM)
            val r = RingtoneManager.getRingtone(applicationContext, alert)
            r.play()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun vibrarNormal() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
            vibratorManager.defaultVibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(VibrationEffect.createOneShot(200, VibrationEffect.DEFAULT_AMPLITUDE))
        }
    }

    private fun vibrarCritico() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as android.os.VibratorManager
            vibratorManager.defaultVibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1))
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(VibrationEffect.createWaveform(longArrayOf(0, 500, 200, 500), -1))
        }
    }
}
