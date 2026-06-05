package com.workwell.habitatzero.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.workwell.habitatzero.R
import com.workwell.habitatzero.model.Estufa

class EstufaAdapter(private var estufas: List<Estufa>) :
    RecyclerView.Adapter<EstufaAdapter.EstufaViewHolder>() {

    var onItemClick: ((Estufa) -> Unit)? = null

    class EstufaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvNome: TextView = itemView.findViewById(R.id.tvNomeEstufa)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatusEstufa)
        val tvLocalizacao: TextView = itemView.findViewById(R.id.tvLocalizacaoEstufa)
        val tvPlantas: TextView = itemView.findViewById(R.id.tvPlantasEstufa)
        val tvAlertas: TextView = itemView.findViewById(R.id.tvAlertasEstufa)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EstufaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_estufa, parent, false)
        return EstufaViewHolder(view)
    }

    override fun onBindViewHolder(holder: EstufaViewHolder, position: Int) {
        val estufa = estufas[position]
        holder.tvNome.text = estufa.nome
        holder.tvStatus.text = estufa.status
        holder.tvLocalizacao.text = estufa.localizacao
        holder.tvPlantas.text = holder.itemView.context.getString(R.string.label_plantas, estufa.totalPlantas)
        holder.tvAlertas.text = holder.itemView.context.getString(R.string.label_alertas_ativos, estufa.alertasAtivos)
        holder.itemView.setOnClickListener { onItemClick?.invoke(estufa) }
    }

    override fun getItemCount(): Int = estufas.size

    fun updateList(newList: List<Estufa>) {
        this.estufas = newList
        notifyDataSetChanged()
    }
}
