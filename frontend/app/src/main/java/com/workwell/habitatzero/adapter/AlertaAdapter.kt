package com.workwell.habitatzero.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.workwell.habitatzero.R
import com.workwell.habitatzero.model.Alerta

class AlertaAdapter(
    private val alertas: MutableList<Alerta>,
    private val onResolver: ((Long) -> Unit)? = null
) : RecyclerView.Adapter<AlertaAdapter.AlertaViewHolder>() {

    class AlertaViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvMensagem: TextView   = itemView.findViewById(R.id.tvMensagem)
        val tvCriadoEm: TextView   = itemView.findViewById(R.id.tvCriadoEm)
        val tvSeveridade: TextView = itemView.findViewById(R.id.tvSeveridade)
        val btnResolver: Button    = itemView.findViewById(R.id.btnResolver)
        val card: MaterialCardView = itemView as MaterialCardView
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlertaViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_alerta, parent, false)
        return AlertaViewHolder(view)
    }

    override fun onBindViewHolder(holder: AlertaViewHolder, position: Int) {
        val ctx = holder.itemView.context
        val alerta = alertas[position]

        holder.tvMensagem.text   = alerta.mensagem
        holder.tvCriadoEm.text   = alerta.criadoEm.take(16).replace("T", "  ")
        holder.tvSeveridade.text = alerta.severidade

        val (badgeColor, strokeColor) = when (alerta.severidade.uppercase()) {
            "EMERGENCIA" -> ctx.getColor(R.color.vermelho_critico) to ctx.getColor(R.color.vermelho_critico)
            "CRITICO"    -> ctx.getColor(R.color.vermelho_critico) to ctx.getColor(R.color.vermelho_critico)
            "ATENCAO"    -> ctx.getColor(R.color.amarelo_atencao)  to ctx.getColor(R.color.amarelo_atencao)
            else         -> ctx.getColor(R.color.verde_normal)      to ctx.getColor(R.color.verde_normal)
        }
        holder.tvSeveridade.setTextColor(badgeColor)
        holder.card.strokeColor = strokeColor
        holder.card.strokeWidth = 2

        if (onResolver != null) {
            holder.btnResolver.visibility = View.VISIBLE
            holder.btnResolver.setOnClickListener { onResolver.invoke(alerta.id) }
        } else {
            holder.btnResolver.visibility = View.GONE
        }
    }

    override fun getItemCount(): Int = alertas.size

    fun removeById(id: Long) {
        val idx = alertas.indexOfFirst { it.id == id }
        if (idx >= 0) {
            alertas.removeAt(idx)
            notifyItemRemoved(idx)
        }
    }
}
