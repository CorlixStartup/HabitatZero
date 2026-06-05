package com.workwell.habitatzero.data

import androidx.room.Entity
import androidx.room.PrimaryKey

// Entidade que representa uma configuração climática salva
@Entity(tableName = "clima_config")
data class ClimaConfig(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val temperatura: Int,
    val umidade: Int,
    val ventilacao: Boolean,
    val irrigacao: Boolean,
    val timestamp: Long = System.currentTimeMillis() // usado para ordenar histórico
)
