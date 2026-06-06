package com.workwell.habitatzero.data

interface HistoricoDao {
    suspend fun getAll(): List<HistoricoItem>
    suspend fun insert(item: HistoricoItem)
}
