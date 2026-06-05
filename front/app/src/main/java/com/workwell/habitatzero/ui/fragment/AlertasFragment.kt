package com.workwell.habitatzero.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.workwell.habitatzero.R
import com.workwell.habitatzero.adapter.AlertaAdapter
import com.workwell.habitatzero.viewmodel.AlertasViewModel

class AlertasFragment : Fragment() {

    private val viewModel: AlertasViewModel by viewModels()
    private var adapter: AlertaAdapter? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_alertas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerAlertas)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        viewModel.alertasLiveData.observe(viewLifecycleOwner) { alertas ->
            val ativos = alertas.filter { !it.resolvido }
            adapter = AlertaAdapter(ativos.toMutableList()) { id -> viewModel.resolverAlerta(id) }
            recycler.adapter = adapter
        }

        viewModel.alertaResolvidoId.observe(viewLifecycleOwner) { id ->
            adapter?.removeById(id)
            Toast.makeText(requireContext(), "Alerta resolvido ✓", Toast.LENGTH_SHORT).show()
        }

        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregarAlertas()
    }
}
