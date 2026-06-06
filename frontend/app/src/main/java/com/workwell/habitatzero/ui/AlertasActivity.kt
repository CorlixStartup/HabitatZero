package com.workwell.habitatzero.ui

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.workwell.habitatzero.R
import com.workwell.habitatzero.adapter.AlertaAdapter
import com.workwell.habitatzero.viewmodel.AlertasViewModel

class AlertasActivity : AppCompatActivity() {

    private val viewModel: AlertasViewModel by viewModels()
    private lateinit var recyclerAlertas: RecyclerView
    private var adapter: AlertaAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alertas)

        recyclerAlertas = findViewById(R.id.recyclerAlertas)
        recyclerAlertas.layoutManager = LinearLayoutManager(this)

        viewModel.alertasLiveData.observe(this) { alertas ->
            val ativos = alertas.filter { !it.resolvido }
            adapter = AlertaAdapter(ativos.toMutableList()) { id -> viewModel.resolverAlerta(id) }
            recyclerAlertas.adapter = adapter
        }

        viewModel.alertaResolvidoId.observe(this) { id ->
            adapter?.removeById(id)
            Toast.makeText(this, "Alerta resolvido ✓", Toast.LENGTH_SHORT).show()
        }

        viewModel.errorLiveData.observe(this) { erro ->
            Toast.makeText(this, erro, Toast.LENGTH_SHORT).show()
        }

        viewModel.carregarAlertas()
    }
}
