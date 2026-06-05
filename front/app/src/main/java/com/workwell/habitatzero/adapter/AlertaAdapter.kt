package com.workwell.habitatzero.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.workwell.habitatzero.R
import com.workwell.habitatzero.model.Alerta

class AlertaAdapter(private val alertas: List<Alerta>) :
    RecyclerView.Adapter<AlertaAdapter.AlertaViewHolder>() {

    class AlertaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMensagem: TextView = itemView.findViewById(R.id.tvMensagem)
        val tvCriadoEm: TextView = itemView.findViewById(R.id.tvCriadoEm)
        val tvSeveridade: TextView = itemView.findViewById(R.id.tvSeveridade)
        val card: MaterialCardView = itemView as MaterialCardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alerta, parent, false)
        return AlertaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertaViewHolder, position: Int) {
        val alerta = alertas[position]
        holder.tvMensagem.text = alerta.mensagem
        holder.tvCriadoEm.text = alerta.criadoEm
        holder.tvSeveridade.text = alerta.severidade

        val cor = when (alerta.severidade.uppercase()) {
            "ATENCAO" -> holder.itemView.context.getColor(R.color.amarelo_atencao)
            "CRITICO" -> holder.itemView.context.getColor(R.color.vermelho_critico)
            "EMERGENCIA" -> holder.itemView.context.getColor(R.color.vermelho_critico)
            else -> holder.itemView.context.getColor(R.color.verde_normal)
        }
        holder.card.setCardBackgroundColor(cor)
    }

    override fun getItemCount(): Int = alertas.size
}
