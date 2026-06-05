package com.workwell.habitatzero.model

data class TokenDTO(
    val token: String,
    val tipo: String,
    val expiracaoMs: Long,
    val email: String,
    val nome: String
)