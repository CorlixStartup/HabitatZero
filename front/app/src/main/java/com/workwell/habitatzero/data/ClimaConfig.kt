package com.workwell.habitatzero.data

data class ClimaConfig(
    val id: Int = 0,
    val temperatura: Int,
    val umidade: Int,
    val ventilacao: Boolean,
    val irrigacao: Boolean,
    val timestamp: Long = System.currentTimeMillis()
)
