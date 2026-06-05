package com.workwell.habitatzero.ui.fragment

import android.content.Intent
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
import com.workwell.habitatzero.adapter.EstufaAdapter
import com.workwell.habitatzero.model.Estufa
import com.workwell.habitatzero.ui.EstufaDetailActivity
import com.workwell.habitatzero.viewmodel.EstufasViewModel

class EstufasFragment : Fragment() {

    private val viewModel: EstufasViewModel by viewModels()
    private lateinit var adapter: EstufaAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return inflater.inflate(R.layout.fragment_estufas, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val recycler = view.findViewById<RecyclerView>(R.id.recyclerEstufas)
        recycler.layoutManager = LinearLayoutManager(requireContext())

        adapter = EstufaAdapter(emptyList())
        adapter.onItemClick = { estufa -> abrirDetalhe(estufa) }
        recycler.adapter = adapter

        viewModel.estufasLiveData.observe(viewLifecycleOwner) { adapter.updateList(it) }
        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        viewModel.carregarEstufas()
    }

    private fun abrirDetalhe(estufa: Estufa) {
        startActivity(Intent(requireContext(), EstufaDetailActivity::class.java).apply {
            putExtra(EstufaDetailActivity.EXTRA_ESTUFA_ID,             estufa.id)
            putExtra(EstufaDetailActivity.EXTRA_ESTUFA_NOME,           estufa.nome)
            putExtra(EstufaDetailActivity.EXTRA_ESTUFA_LOCALIZACAO,    estufa.localizacao)
            putExtra(EstufaDetailActivity.EXTRA_ESTUFA_CAPACIDADE,     estufa.capacidadeM2)
            putExtra(EstufaDetailActivity.EXTRA_ESTUFA_STATUS,         estufa.status)
            putExtra(EstufaDetailActivity.EXTRA_THRESHOLD_O2,          estufa.thresholdOxigenioMin)
            putExtra(EstufaDetailActivity.EXTRA_THRESHOLD_UMIDADE,     estufa.thresholdUmidadeMin)
            putExtra(EstufaDetailActivity.EXTRA_THRESHOLD_RADIACAO,    estufa.thresholdRadiacaoMax)
            putExtra(EstufaDetailActivity.EXTRA_THRESHOLD_TEMPERATURA, estufa.thresholdTemperaturaMax)
        })
    }
}
