package com.workwell.habitatzero.model

data class Estufa(
    val id: Long,
    val nome: String,
    val localizacao: String,
    val capacidadeM2: Double,
    val status: String,
    val thresholdOxigenioMin: Double,
    val thresholdUmidadeMin: Double,
    val thresholdRadiacaoMax: Double,
    val thresholdTemperaturaMax: Double,
    val totalPlantas: Int,
    val alertasAtivos: Int
)
