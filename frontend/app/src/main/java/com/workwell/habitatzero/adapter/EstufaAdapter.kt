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
        val ctx = holder.itemView.context
        val estufa = estufas[position]
        holder.tvNome.text = estufa.nome
        holder.tvLocalizacao.text = estufa.localizacao
        holder.tvPlantas.text = estufa.totalPlantas.toString()
        holder.tvAlertas.text = estufa.alertasAtivos.toString()

        val (statusText, statusColor) = when (estufa.status) {
            "ATIVA"      -> "● ATIVA"      to ctx.getColor(R.color.verde_normal)
            "MANUTENCAO" -> "⚙ MANUTENÇÃO" to ctx.getColor(R.color.amarelo_atencao)
            else         -> "○ INATIVA"    to ctx.getColor(R.color.text_secondary)
        }
        holder.tvStatus.text = statusText
        holder.tvStatus.setTextColor(statusColor)

        holder.itemView.setOnClickListener { onItemClick?.invoke(estufa) }
    }

    override fun getItemCount(): Int = estufas.size

    fun updateList(newList: List<Estufa>) {
        this.estufas = newList
        notifyDataSetChanged()
    }
}
