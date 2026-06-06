package com.workwell.habitatzero.model

data class Alerta(
    val id: Long,
    val estufaId: Long,
    val nomeEstufa: String,
    val severidade: String,
    val mensagem: String,
    val tipoSensor: String?,
    val valorRegistrado: Double?,
    val criadoEm: String,
    val resolvidoEm: String?,
    val resolvido: Boolean
)
