package com.workwell.habitatzero.ui

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textfield.TextInputEditText
import com.workwell.habitatzero.R
import com.workwell.habitatzero.adapter.AlertaAdapter
import com.workwell.habitatzero.adapter.PlantaAdapter
import com.workwell.habitatzero.data.AppDatabase
import com.workwell.habitatzero.data.ClimaConfig
import com.workwell.habitatzero.data.HistoricoItem
import com.workwell.habitatzero.model.Estufa
import com.workwell.habitatzero.model.PlantaRequest
import com.workwell.habitatzero.viewmodel.EstufaDetailViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlin.math.abs
import kotlin.math.sin

class EstufaDetailActivity : AppCompatActivity() {

    private val viewModel: EstufaDetailViewModel by viewModels()

    // Estufa data from Intent
    private var estufaId = -1L
    private var estufaNome = ""
    private var estufaLocalizacao = ""
    private var estufaCapacidade = 0.0
    private var estufaStatus = "ATIVA"
    private var thresholdO2 = 19.5
    private var thresholdUmidade = 30.0
    private var thresholdRadiacao = 2.0
    private var thresholdTemperatura = 40.0

    // Views
    private lateinit var tvInfoLocalizacao: TextView
    private lateinit var tvInfoCapacidade: TextView
    private lateinit var tvThreshO2: TextView
    private lateinit var tvThreshUmid: TextView
    private lateinit var tvThreshRad: TextView
    private lateinit var tvThreshTemp: TextView

    private lateinit var tvPressao: TextView
    private lateinit var tvEnergia: TextView
    private lateinit var tvLink: TextView
    private lateinit var tvFiltroCo2: TextView

    private lateinit var switchVentilacao: SwitchMaterial
    private lateinit var switchIrrigacao: SwitchMaterial
    private lateinit var pbVentilacao: ProgressBar
    private lateinit var pbIrrigacao: ProgressBar
    private lateinit var btnAjustarThresholds: MaterialButton

    private lateinit var tvSemPlantas: TextView
    private lateinit var recyclerPlantas: RecyclerView
    private lateinit var btnAdicionarPlanta: MaterialButton
    private lateinit var plantaAdapter: PlantaAdapter

    private lateinit var tvSemAlertas: TextView
    private lateinit var recyclerAlertasDetalhe: RecyclerView

    private val systemsHandler = Handler(Looper.getMainLooper())
    private var systemsTick = 0

    private val systemsRunnable = object : Runnable {
        override fun run() {
            atualizarSistemasColonia()
            systemsHandler.postDelayed(this, 3000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estufa_detail)

        readIntentExtras()
        setupToolbar()
        bindViews()
        setupInfoCard()
        setupControleCard()
        setupPlantasCard()
        setupAlertasCard()
        setupObservers()

        viewModel.carregarPlantas(estufaId)
        viewModel.carregarAlertas(estufaId)
    }

    override fun onResume() {
        super.onResume()
        systemsHandler.post(systemsRunnable)
    }

    override fun onPause() {
        super.onPause()
        systemsHandler.removeCallbacks(systemsRunnable)
    }

    // ─── Setup ────────────────────────────────────────────────────────────────

    private fun readIntentExtras() {
        estufaId           = intent.getLongExtra(EXTRA_ESTUFA_ID, -1L)
        estufaNome         = intent.getStringExtra(EXTRA_ESTUFA_NOME) ?: ""
        estufaLocalizacao  = intent.getStringExtra(EXTRA_ESTUFA_LOCALIZACAO) ?: ""
        estufaCapacidade   = intent.getDoubleExtra(EXTRA_ESTUFA_CAPACIDADE, 0.0)
        estufaStatus       = intent.getStringExtra(EXTRA_ESTUFA_STATUS) ?: "ATIVA"
        thresholdO2        = intent.getDoubleExtra(EXTRA_THRESHOLD_O2, 19.5)
        thresholdUmidade   = intent.getDoubleExtra(EXTRA_THRESHOLD_UMIDADE, 30.0)
        thresholdRadiacao  = intent.getDoubleExtra(EXTRA_THRESHOLD_RADIACAO, 2.0)
        thresholdTemperatura = intent.getDoubleExtra(EXTRA_THRESHOLD_TEMPERATURA, 40.0)

        if (estufaId == -1L) { finish(); return }
    }

    private fun setupToolbar() {
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = estufaNome
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }
    }

    private fun bindViews() {
        tvInfoLocalizacao = findViewById(R.id.tvInfoLocalizacao)
        tvInfoCapacidade  = findViewById(R.id.tvInfoCapacidade)
        tvThreshO2        = findViewById(R.id.tvThreshO2)
        tvThreshUmid      = findViewById(R.id.tvThreshUmid)
        tvThreshRad       = findViewById(R.id.tvThreshRad)
        tvThreshTemp      = findViewById(R.id.tvThreshTemp)
        tvPressao         = findViewById(R.id.tvPressao)
        tvEnergia         = findViewById(R.id.tvEnergia)
        tvLink            = findViewById(R.id.tvLink)
        tvFiltroCo2       = findViewById(R.id.tvFiltroCo2)
        switchVentilacao  = findViewById(R.id.switchVentilacao)
        switchIrrigacao   = findViewById(R.id.switchIrrigacao)
        pbVentilacao      = findViewById(R.id.pbVentilacao)
        pbIrrigacao       = findViewById(R.id.pbIrrigacao)
        btnAjustarThresholds = findViewById(R.id.btnAjustarThresholds)
        tvSemPlantas      = findViewById(R.id.tvSemPlantas)
        recyclerPlantas   = findViewById(R.id.recyclerPlantas)
        btnAdicionarPlanta = findViewById(R.id.btnAdicionarPlanta)
        tvSemAlertas      = findViewById(R.id.tvSemAlertas)
        recyclerAlertasDetalhe = findViewById(R.id.recyclerAlertasDetalhe)
    }

    private fun setupInfoCard() {
        tvInfoLocalizacao.text = "📍 $estufaLocalizacao"
        tvInfoCapacidade.text  = "%.0f m²".format(estufaCapacidade)
        tvThreshO2.text        = "🫧 O₂ min: $thresholdO2%"
        tvThreshUmid.text      = "💧 Umid min: $thresholdUmidade%"
        tvThreshRad.text       = "☢ Rad max: $thresholdRadiacao mSv/h"
        tvThreshTemp.text      = "🌡 Temp max: $thresholdTemperatura°C"
    }

    private fun setupControleCard() {
        val db = AppDatabase.getDatabase(this)
        val climaDao = db.climaConfigDao()
        val historicoDao = db.historicoDao()

        // Load last saved state
        lifecycleScope.launch(Dispatchers.IO) {
            val last = climaDao.getHistorico().firstOrNull()
            last?.let {
                runOnUiThread {
                    switchVentilacao.isChecked = it.ventilacao
                    switchIrrigacao.isChecked = it.irrigacao
                }
            }
        }

        fun handleSwitch(switch: SwitchMaterial, pb: ProgressBar, nome: String) {
            switch.setOnCheckedChangeListener { _, isChecked ->
                switch.isEnabled = false
                pb.visibility = View.VISIBLE
                Handler(Looper.getMainLooper()).postDelayed({
                    pb.visibility = View.GONE
                    switch.isEnabled = true
                    val status = if (isChecked) "ativada" else "desativada"
                    Snackbar.make(window.decorView, "$nome $status ✓", Snackbar.LENGTH_SHORT).show()
                    val now = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault()).format(Date())
                    lifecycleScope.launch(Dispatchers.IO) {
                        climaDao.insert(ClimaConfig(
                            temperatura = 22,
                            umidade = 60,
                            ventilacao = if (nome.contains("Ventilação")) isChecked else switchVentilacao.isChecked,
                            irrigacao  = if (nome.contains("Irrigação"))  isChecked else switchIrrigacao.isChecked
                        ))
                        historicoDao.insert(HistoricoItem(
                            descricao = "$nome $status — $estufaNome",
                            data = now
                        ))
                    }
                }, 1500)
            }
        }

        handleSwitch(switchVentilacao, pbVentilacao, "Ventilação")
        handleSwitch(switchIrrigacao,  pbIrrigacao,  "Irrigação")

        btnAjustarThresholds.setOnClickListener {
            val intent = Intent(this, ControleClimaticoActivity::class.java).apply {
                putExtra(ControleClimaticoActivity.EXTRA_ESTUFA_ID,           estufaId)
                putExtra(ControleClimaticoActivity.EXTRA_ESTUFA_NOME,         estufaNome)
                putExtra(ControleClimaticoActivity.EXTRA_ESTUFA_LOCALIZACAO,  estufaLocalizacao)
                putExtra(ControleClimaticoActivity.EXTRA_ESTUFA_CAPACIDADE,   estufaCapacidade)
                putExtra(ControleClimaticoActivity.EXTRA_ESTUFA_STATUS,       estufaStatus)
                putExtra(ControleClimaticoActivity.EXTRA_THRESHOLD_O2,        thresholdO2)
                putExtra(ControleClimaticoActivity.EXTRA_THRESHOLD_UMIDADE,   thresholdUmidade)
                putExtra(ControleClimaticoActivity.EXTRA_THRESHOLD_RADIACAO,  thresholdRadiacao)
                putExtra(ControleClimaticoActivity.EXTRA_THRESHOLD_TEMPERATURA, thresholdTemperatura)
            }
            startActivity(intent)
        }
    }

    private fun setupPlantasCard() {
        plantaAdapter = PlantaAdapter(mutableListOf()) { planta ->
            viewModel.avancarFase(planta)
        }
        recyclerPlantas.layoutManager = LinearLayoutManager(this)
        recyclerPlantas.adapter = plantaAdapter

        btnAdicionarPlanta.setOnClickListener { mostrarDialogAdicionarPlanta() }
    }

    private fun setupAlertasCard() {
        recyclerAlertasDetalhe.layoutManager = LinearLayoutManager(this)
    }

    private fun setupObservers() {
        viewModel.plantasLiveData.observe(this) { plantas ->
            if (plantas.isEmpty()) {
                tvSemPlantas.visibility = View.VISIBLE
                recyclerPlantas.visibility = View.GONE
            } else {
                tvSemPlantas.visibility = View.GONE
                recyclerPlantas.visibility = View.VISIBLE
                plantaAdapter.updateList(plantas)
            }
        }

        viewModel.plantaUpdatedLiveData.observe(this) { updated ->
            plantaAdapter.updateItem(updated)
            Toast.makeText(this, "Fase avançada para ${updated.faseCrescimento} ✓", Toast.LENGTH_SHORT).show()
        }

        viewModel.plantaAddedLiveData.observe(this) { added ->
            tvSemPlantas.visibility = View.GONE
            recyclerPlantas.visibility = View.VISIBLE
            plantaAdapter.addItem(added)
            Toast.makeText(this, "${added.nomeCientifico} adicionada ✓", Toast.LENGTH_SHORT).show()
        }

        viewModel.alertasLiveData.observe(this) { alertas ->
            val naoResolvidos = alertas.filter { !it.resolvido }
            if (naoResolvidos.isEmpty()) {
                tvSemAlertas.visibility = View.VISIBLE
                recyclerAlertasDetalhe.visibility = View.GONE
            } else {
                tvSemAlertas.visibility = View.GONE
                recyclerAlertasDetalhe.visibility = View.VISIBLE
                val adapter = AlertaAdapter(naoResolvidos.toMutableList()) { id ->
                    viewModel.resolverAlerta(id)
                }
                recyclerAlertasDetalhe.adapter = adapter
            }
        }

        viewModel.alertaResolvidoId.observe(this) { id ->
            (recyclerAlertasDetalhe.adapter as? AlertaAdapter)?.removeById(id)
            Toast.makeText(this, "Alerta resolvido ✓", Toast.LENGTH_SHORT).show()
        }

        viewModel.errorLiveData.observe(this) { error ->
            Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
        }
    }

    // ─── Sistemas da Colônia ──────────────────────────────────────────────────

    private fun atualizarSistemasColonia() {
        val tick = systemsTick++.toDouble()
        val pressao = 101.3 + (Math.random() - 0.5) * 0.6
        val energia = 82.0 + abs(sin(tick * 0.3)) * 14.0 + (Math.random() - 0.5) * 2.0
        val linkAtivo = (0..9).random() > 1
        val filtro = 90.0 + (Math.random() - 0.5) * 8.0

        tvPressao.text   = "%.1f kPa".format(pressao)
        tvEnergia.text   = "%.0f%%".format(energia)
        tvFiltroCo2.text = "%.0f%%".format(filtro)

        if (linkAtivo) {
            tvLink.text      = "● ATIVO"
            tvLink.setTextColor(getColor(R.color.verde_normal))
        } else {
            tvLink.text = "◐ DEGRADADO"
            tvLink.setTextColor(getColor(R.color.amarelo_atencao))
        }
    }

    // ─── Dialog Adicionar Planta ──────────────────────────────────────────────

    private fun mostrarDialogAdicionarPlanta() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_add_planta, null)
        val etNomeCientifico = dialogView.findViewById<TextInputEditText>(R.id.etNomeCientifico)
        val etNomeComum      = dialogView.findViewById<TextInputEditText>(R.id.etNomeComum)
        val etDataPlantio    = dialogView.findViewById<TextInputEditText>(R.id.etDataPlantio)

        MaterialAlertDialogBuilder(this)
            .setTitle("🌱 Adicionar Planta")
            .setView(dialogView)
            .setPositiveButton("Adicionar") { _, _ ->
                val nomeCientifico = etNomeCientifico.text?.toString()?.trim() ?: ""
                val nomeComum      = etNomeComum.text?.toString()?.trim()?.ifBlank { null }
                val dataPlantio    = etDataPlantio.text?.toString()?.trim() ?: ""

                if (nomeCientifico.isBlank() || dataPlantio.isBlank()) {
                    Toast.makeText(this, "Preencha nome científico e data de plantio.", Toast.LENGTH_SHORT).show()
                    return@setPositiveButton
                }

                val request = PlantaRequest(
                    nomeCientifico = nomeCientifico,
                    nomeComum = nomeComum,
                    faseCrescimento = "SEMENTE",
                    dataPlantio = dataPlantio,
                    estufaId = estufaId
                )
                viewModel.adicionarPlanta(request)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }

    companion object {
        const val EXTRA_ESTUFA_ID           = "estufa_id"
        const val EXTRA_ESTUFA_NOME         = "estufa_nome"
        const val EXTRA_ESTUFA_LOCALIZACAO  = "estufa_localizacao"
        const val EXTRA_ESTUFA_CAPACIDADE   = "estufa_capacidade"
        const val EXTRA_ESTUFA_STATUS       = "estufa_status"
        const val EXTRA_THRESHOLD_O2        = "threshold_o2"
        const val EXTRA_THRESHOLD_UMIDADE   = "threshold_umidade"
        const val EXTRA_THRESHOLD_RADIACAO  = "threshold_radiacao"
        const val EXTRA_THRESHOLD_TEMPERATURA = "threshold_temperatura"
    }
}
