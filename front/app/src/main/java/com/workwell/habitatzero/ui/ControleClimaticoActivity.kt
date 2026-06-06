package com.workwell.habitatzero.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.workwell.habitatzero.R
import com.workwell.habitatzero.model.EstufaRequest
import com.workwell.habitatzero.viewmodel.ControleClimaticoViewModel

class ControleClimaticoActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_ESTUFA_ID = "estufa_id"
        const val EXTRA_ESTUFA_NOME = "estufa_nome"
        const val EXTRA_ESTUFA_LOCALIZACAO = "estufa_localizacao"
        const val EXTRA_ESTUFA_CAPACIDADE = "estufa_capacidade"
        const val EXTRA_ESTUFA_STATUS = "estufa_status"
        const val EXTRA_THRESHOLD_O2 = "threshold_o2"
        const val EXTRA_THRESHOLD_UMIDADE = "threshold_umidade"
        const val EXTRA_THRESHOLD_RADIACAO = "threshold_radiacao"
        const val EXTRA_THRESHOLD_TEMPERATURA = "threshold_temperatura"
    }

    private val viewModel: ControleClimaticoViewModel by viewModels()

    private lateinit var sliderTemperatura: Slider
    private lateinit var sliderUmidade: Slider
    private lateinit var switchVentilacao: SwitchMaterial
    private lateinit var switchIrrigacao: SwitchMaterial
    private lateinit var btnSalvar: Button
    private lateinit var btnHistorico: Button
    private lateinit var txtTempAtual: TextView
    private lateinit var txtUmidadeAtual: TextView

    private var estufaId: Long = -1L
    private var estufaNome: String = ""
    private var estufaLocalizacao: String = ""
    private var estufaCapacidade: Double = 0.0
    private var estufaStatus: String = "ATIVA"
    private var thresholdO2: Double = 19.5
    private var thresholdUmidade: Double = 30.0
    private var thresholdRadiacao: Double = 2.0
    private var thresholdTemperatura: Double = 40.0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_controle_climatico)

        estufaId = intent.getLongExtra(EXTRA_ESTUFA_ID, -1L)
        estufaNome = intent.getStringExtra(EXTRA_ESTUFA_NOME) ?: ""
        estufaLocalizacao = intent.getStringExtra(EXTRA_ESTUFA_LOCALIZACAO) ?: ""
        estufaCapacidade = intent.getDoubleExtra(EXTRA_ESTUFA_CAPACIDADE, 0.0)
        estufaStatus = intent.getStringExtra(EXTRA_ESTUFA_STATUS) ?: "ATIVA"
        thresholdO2 = intent.getDoubleExtra(EXTRA_THRESHOLD_O2, 19.5)
        thresholdUmidade = intent.getDoubleExtra(EXTRA_THRESHOLD_UMIDADE, 30.0)
        thresholdRadiacao = intent.getDoubleExtra(EXTRA_THRESHOLD_RADIACAO, 2.0)
        thresholdTemperatura = intent.getDoubleExtra(EXTRA_THRESHOLD_TEMPERATURA, 40.0)

        if (estufaId == -1L) {
            Toast.makeText(this, "Nenhuma estufa selecionada. Selecione uma estufa primeiro.", Toast.LENGTH_LONG).show()
            finish()
            return
        }

        sliderTemperatura = findViewById(R.id.sliderTemperatura)
        sliderUmidade = findViewById(R.id.sliderUmidade)
        switchVentilacao = findViewById(R.id.switchVentilacao)
        switchIrrigacao = findViewById(R.id.switchIrrigacao)
        btnSalvar = findViewById(R.id.btnSalvar)
        btnHistorico = findViewById(R.id.btnHistorico)
        txtTempAtual = findViewById(R.id.txtTempAtual)
        txtUmidadeAtual = findViewById(R.id.txtUmidadeAtual)

        sliderTemperatura.valueFrom = 15f
        sliderTemperatura.valueTo = 80f
        sliderTemperatura.stepSize = 1f
        sliderTemperatura.value = thresholdTemperatura.toFloat().coerceIn(15f, 80f)

        sliderUmidade.valueFrom = 10f
        sliderUmidade.valueTo = 100f
        sliderUmidade.stepSize = 1f
        sliderUmidade.value = thresholdUmidade.toFloat().coerceIn(10f, 100f)

        val db = AppDatabase.getDatabase(this)
        val climaConfigDao = db.climaConfigDao()

        sliderTemperatura.addOnChangeListener { _, value, _ ->
            txtTempAtual.text = getString(R.string.label_temperatura_slider, value.toInt())
        }

        sliderUmidade.addOnChangeListener { _, value, _ ->
            txtUmidadeAtual.text = getString(R.string.label_umidade_slider, value.toInt())
        }

        btnSalvar.setOnClickListener {
            val novaTemperatura = sliderTemperatura.value.toDouble()
            val novaUmidade = sliderUmidade.value.toDouble()

            val request = EstufaRequest(
                nome = estufaNome,
                localizacao = estufaLocalizacao,
                capacidadeM2 = estufaCapacidade,
                status = estufaStatus,
                thresholdOxigenioMin = thresholdO2,
                thresholdUmidadeMin = novaUmidade,
                thresholdRadiacaoMax = thresholdRadiacao,
                thresholdTemperaturaMax = novaTemperatura
            )
            viewModel.salvarConfiguracao(estufaId, request)
        }

        btnHistorico.setOnClickListener {
            startActivity(Intent(this, HistoricoActivity::class.java))
        }

        viewModel.successLiveData.observe(this) { success ->
            if (success) {
                Toast.makeText(this, "Configuração sincronizada com a API!", Toast.LENGTH_SHORT).show()
            }
        }

        viewModel.errorLiveData.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }
}
