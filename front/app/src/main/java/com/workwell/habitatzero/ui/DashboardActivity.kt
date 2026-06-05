package com.workwell.habitatzero.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.github.mikephil.charting.components.XAxis
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
    private lateinit var tvStatusTemperatura: TextView
    private lateinit var tvStatusUmidade: TextView
    private lateinit var tvStatusOxigenio: TextView
    private lateinit var tvStatusRadiacao: TextView

    private lateinit var cardTemperatura: MaterialCardView
    private lateinit var cardUmidade: MaterialCardView
    private lateinit var cardOxigenio: MaterialCardView
    private lateinit var cardRadiacao: MaterialCardView

    private lateinit var progressBar: ProgressBar
    private lateinit var tvSemDados: TextView

    private lateinit var chartTemperatura: LineChart
    private lateinit var chartUmidade: LineChart
    private lateinit var chartOxigenio: LineChart
    private lateinit var chartRadiacao: LineChart

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

        tvTemperatura       = findViewById(R.id.tvTemperatura)
        tvUmidade           = findViewById(R.id.tvUmidade)
        tvOxigenio          = findViewById(R.id.tvOxigenio)
        tvRadiacao          = findViewById(R.id.tvRadiacao)
        tvStatusTemperatura = findViewById(R.id.tvStatusTemperatura)
        tvStatusUmidade     = findViewById(R.id.tvStatusUmidade)
        tvStatusOxigenio    = findViewById(R.id.tvStatusOxigenio)
        tvStatusRadiacao    = findViewById(R.id.tvStatusRadiacao)
        cardTemperatura     = findViewById(R.id.cardTemperatura)
        cardUmidade         = findViewById(R.id.cardUmidade)
        cardOxigenio        = findViewById(R.id.cardOxigenio)
        cardRadiacao        = findViewById(R.id.cardRadiacao)
        progressBar         = findViewById(R.id.progressBar)
        tvSemDados          = findViewById(R.id.tvSemDados)
        chartTemperatura    = findViewById(R.id.chartTemperatura)
        chartUmidade        = findViewById(R.id.chartUmidade)
        chartOxigenio       = findViewById(R.id.chartOxigenio)
        chartRadiacao       = findViewById(R.id.chartRadiacao)

        listOf(chartTemperatura, chartUmidade, chartOxigenio, chartRadiacao).forEach { setupChart(it) }

        setupObservers()
        requestNotificationPermission()
        createNotificationChannel()
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

    private fun setupChart(chart: LineChart) {
        chart.apply {
            description.isEnabled = false
            setTouchEnabled(false)
            isDragEnabled = false
            setScaleEnabled(false)
            setDrawGridBackground(false)
            setNoDataText("")
            legend.isEnabled = false
            axisRight.isEnabled = false
            axisLeft.apply {
                textColor = 0xFFAAAAAA.toInt()
                gridColor = 0x22FFFFFF.toInt()
                axisLineColor = 0x44FFFFFF.toInt()
                setDrawAxisLine(true)
            }
            xAxis.apply {
                position = XAxis.XAxisPosition.BOTTOM
                textColor = 0xFFAAAAAA.toInt()
                gridColor = 0x22FFFFFF.toInt()
                setDrawLabels(false)
            }
            setBackgroundColor(0xFF1A1A2E.toInt())
        }
    }

    private fun setupObservers() {
        viewModel.sensorLiveData.observe(this) { sensor ->
            progressBar.visibility = View.GONE
            tvSemDados.visibility = View.GONE

            tvTemperatura.text = getString(R.string.valor_temperatura, sensor.temperatura)
            tvUmidade.text = getString(R.string.valor_umidade, sensor.umidade)
            tvOxigenio.text = getString(R.string.valor_oxigenio, sensor.oxigenio)
            tvRadiacao.text = getString(R.string.valor_radiacao, sensor.radiacao)

            aplicarStatus(cardTemperatura, tvStatusTemperatura, sensor.temperatura, 18.0, 26.0,
                getString(R.string.titulo_temperatura))
            aplicarStatus(cardUmidade, tvStatusUmidade, sensor.umidade, 40.0, 70.0,
                getString(R.string.titulo_umidade))
            aplicarStatus(cardOxigenio, tvStatusOxigenio, sensor.oxigenio, 19.5, 23.0,
                getString(R.string.titulo_oxigenio))
            aplicarStatus(cardRadiacao, tvStatusRadiacao, sensor.radiacao, 0.0, 2.0,
                getString(R.string.titulo_radiacao), invertido = true)
        }

        viewModel.historyLiveData.observe(this) { history ->
            if (history.size >= 2) {
                atualizarGraficos(history)
            }
        }

        viewModel.errorLiveData.observe(this) { error ->
            progressBar.visibility = View.GONE
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    private fun atualizarGraficos(history: List<SensorAmbiente>) {
        fun buildSet(values: List<Float>, label: String, color: Int): LineDataSet {
            val entries = values.mapIndexed { i, v -> Entry(i.toFloat(), v) }
            return LineDataSet(entries, label).apply {
                this.color = color
                setDrawCircles(false)
                setDrawValues(false)
                lineWidth = 2f
                mode = LineDataSet.Mode.CUBIC_BEZIER
                fillAlpha = 60
                setDrawFilled(true)
                fillColor = color
            }
        }

        val tempColor  = getColor(R.color.sensor_temperatura)
        val umidColor  = getColor(R.color.sensor_umidade)
        val o2Color    = getColor(R.color.sensor_oxigenio)
        val radColor   = getColor(R.color.sensor_radiacao)

        chartTemperatura.data = LineData(buildSet(history.map { it.temperatura.toFloat() }, "°C", tempColor))
        chartUmidade.data     = LineData(buildSet(history.map { it.umidade.toFloat() }, "%", umidColor))
        chartOxigenio.data    = LineData(buildSet(history.map { it.oxigenio.toFloat() }, "%", o2Color))
        chartRadiacao.data    = LineData(buildSet(history.map { it.radiacao.toFloat() }, "mSv/h", radColor))

        listOf(chartTemperatura, chartUmidade, chartOxigenio, chartRadiacao).forEach {
            it.notifyDataSetChanged()
            it.invalidate()
        }
    }

    private fun aplicarStatus(
        card: MaterialCardView,
        tvStatus: TextView,
        valor: Double,
        min: Double,
        max: Double,
        nome: String,
        invertido: Boolean = false
    ) {
        val normal = if (!invertido) valor in min..max else valor <= max
        if (normal) {
            card.setCardBackgroundColor(getColor(R.color.card_normal))
            card.strokeColor = getColor(R.color.sensor_normal_border)
            tvStatus.text = "● NORMAL"
            tvStatus.setTextColor(getColor(R.color.verde_normal))
        } else {
            card.setCardBackgroundColor(getColor(R.color.card_alerta))
            card.strokeColor = getColor(R.color.vermelho_critico)
            tvStatus.text = "⚠ ALERTA"
            tvStatus.setTextColor(getColor(R.color.vermelho_critico))
            mostrarNotificacao("Alerta de $nome", "$nome fora do limite: $valor")
        }
    }

    private fun requestNotificationPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                this, arrayOf(android.Manifest.permission.POST_NOTIFICATIONS), 100)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "Alertas de Sensores",
                NotificationManager.IMPORTANCE_HIGH).apply { enableVibration(true) }
            (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
                .createNotificationChannel(channel)
        }
    }

    private fun mostrarNotificacao(titulo: String, mensagem: String) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
            ContextCompat.checkSelfPermission(this, android.Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) return

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(titulo)
            .setContentText(mensagem)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .build()

        (getSystemService(NOTIFICATION_SERVICE) as NotificationManager)
            .notify(System.currentTimeMillis().toInt(), notification)
    }

    companion object {
        private const val CHANNEL_ID = "alertas_sensores"
    }
}
