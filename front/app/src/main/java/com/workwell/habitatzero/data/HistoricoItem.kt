package com.workwell.habitatzero.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidade que representa um item do histórico
@Entity(tableName = "historico")
data class HistoricoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val descricao: String,   // Texto descritivo da ação/configuração
    val data: String         // Data em formato string (ex: "02/06/2026")
)
