package com.workwell.habitatzero.model

data class Planta(
    val id: Long,
    val nomeCientifico: String,
    val nomeComum: String?,
    val faseCrescimento: String,
    val dataPlantio: String,
    val estufaId: Long,
    val nomeEstufa: String
)
