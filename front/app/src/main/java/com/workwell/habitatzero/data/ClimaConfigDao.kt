package com.workwell.habitatzero.data

interface ClimaConfigDao {
    suspend fun insert(config: ClimaConfig)
    suspend fun getHistorico(): List<ClimaConfig>
}
