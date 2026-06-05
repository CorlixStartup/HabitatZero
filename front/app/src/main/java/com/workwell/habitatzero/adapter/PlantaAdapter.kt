package com.workwell.habitatzero.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.workwell.habitatzero.R
import com.workwell.habitatzero.model.Planta

class PlantaAdapter(
    private var plantas: MutableList<Planta>,
    private val onAvancarFase: (Planta) -> Unit
) : RecyclerView.Adapter<PlantaAdapter.PlantaViewHolder>() {

    class PlantaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNomeCientifico: TextView = itemView.findViewById(R.id.tvNomeCientifico)
        val tvNomeComum: TextView      = itemView.findViewById(R.id.tvNomeComum)
        val tvFase: TextView           = itemView.findViewById(R.id.tvFase)
        val tvDataPlantio: TextView    = itemView.findViewById(R.id.tvDataPlantio)
        val btnAvancar: Button         = itemView.findViewById(R.id.btnAvancarFase)
    }

    private val fases = listOf("SEMENTE", "GERMINACAO", "CRESCIMENTO", "MATURACAO", "COLHEITA")

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlantaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_planta, parent, false)
        return PlantaViewHolder(view)
    }

    override fun onBindViewHolder(holder: PlantaViewHolder, position: Int) {
        val ctx = holder.itemView.context
        val planta = plantas[position]

        holder.tvNomeCientifico.text = planta.nomeCientifico
        holder.tvNomeComum.text = planta.nomeComum ?: "—"
        holder.tvDataPlantio.text = planta.dataPlantio

        holder.tvFase.text = planta.faseCrescimento
        val faseColor = when (planta.faseCrescimento) {
            "SEMENTE"     -> 0xFF9E9E9E.toInt()
            "GERMINACAO"  -> 0xFFFFD600.toInt()
            "CRESCIMENTO" -> 0xFF00C853.toInt()
            "MATURACAO"   -> 0xFF2962FF.toInt()
            "COLHEITA"    -> 0xFFFF6D00.toInt()
            else          -> 0xFF9E9E9E.toInt()
        }
        holder.tvFase.setTextColor(faseColor)

        val isLast = planta.faseCrescimento == "COLHEITA"
        holder.btnAvancar.isEnabled = !isLast
        holder.btnAvancar.alpha = if (isLast) 0.4f else 1f
        holder.btnAvancar.setOnClickListener { onAvancarFase(planta) }
    }

    override fun getItemCount(): Int = plantas.size

    fun updateList(newList: List<Planta>) {
        plantas.clear()
        plantas.addAll(newList)
        notifyDataSetChanged()
    }

    fun updateItem(updated: Planta) {
        val idx = plantas.indexOfFirst { it.id == updated.id }
        if (idx >= 0) {
            plantas[idx] = updated
            notifyItemChanged(idx)
        }
    }

    fun addItem(planta: Planta) {
        plantas.add(planta)
        notifyItemInserted(plantas.size - 1)
    }
}
