package com.workwell.habitatzero.model

data class SensorLeituraResponse(
    val id: Long,
    val estufaId: Long,
    val nomeEstufa: String,
    val tipoSensor: String,
    val valorLeitura: Double,
    val unidade: String,
    val timestamp: String,
    val alertaDisparado: Boolean
)
