package com.workwell.habitatzero.model

data class PlantaRequest(
    val nomeCientifico: String,
    val nomeComum: String?,
    val faseCrescimento: String,
    val dataPlantio: String,
    val estufaId: Long
)
