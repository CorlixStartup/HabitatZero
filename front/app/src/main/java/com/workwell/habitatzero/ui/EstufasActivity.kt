package com.workwell.habitatzero.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.workwell.habitatzero.R
import com.workwell.habitatzero.adapter.EstufaAdapter
import com.workwell.habitatzero.model.Estufa
import com.workwell.habitatzero.viewmodel.EstufasViewModel

class EstufasActivity : AppCompatActivity() {

    private val estufasViewModel: EstufasViewModel by viewModels()
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: EstufaAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_estufas)

        recyclerView = findViewById(R.id.recyclerEstufas)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = EstufaAdapter(emptyList())
        adapter.onItemClick = { estufa -> abrirDetalheEstufa(estufa) }
        recyclerView.adapter = adapter

        estufasViewModel.estufasLiveData.observe(this) { lista ->
            adapter.updateList(lista)
        }

        estufasViewModel.errorLiveData.observe(this) { erro ->
            Toast.makeText(this, erro, Toast.LENGTH_SHORT).show()
        }

        estufasViewModel.carregarEstufas()
    }

    private fun abrirDetalheEstufa(estufa: Estufa) {
        val intent = Intent(this, EstufaDetailActivity::class.java).apply {
            putExtra(EstufaDetailActivity.EXTRA_ESTUFA_ID,             estufa.id)
            putExtra(EstufaDetailActivity.EXTRA_ESTUFA_NOME,           estufa.nome)
            putExtra(EstufaDetailActivity.EXTRA_ESTUFA_LOCALIZACAO,    estufa.localizacao)
            putExtra(EstufaDetailActivity.EXTRA_ESTUFA_CAPACIDADE,     estufa.capacidadeM2)
            putExtra(EstufaDetailActivity.EXTRA_ESTUFA_STATUS,         estufa.status)
            putExtra(EstufaDetailActivity.EXTRA_THRESHOLD_O2,          estufa.thresholdOxigenioMin)
            putExtra(EstufaDetailActivity.EXTRA_THRESHOLD_UMIDADE,     estufa.thresholdUmidadeMin)
            putExtra(EstufaDetailActivity.EXTRA_THRESHOLD_RADIACAO,    estufa.thresholdRadiacaoMax)
            putExtra(EstufaDetailActivity.EXTRA_THRESHOLD_TEMPERATURA, estufa.thresholdTemperaturaMax)
        }
        startActivity(intent)
    }
}
