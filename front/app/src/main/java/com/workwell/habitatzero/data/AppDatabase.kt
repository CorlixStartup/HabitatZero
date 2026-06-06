package com.workwell.habitatzero.data

import android.content.Context

// Room removed — persistence handled by SharedPreferences.
// This object is kept as a stub so any remaining references compile.
object AppDatabase {
    fun getDatabase(context: Context): AppDatabase = this
    fun historicoDao(): HistoricoDao = object : HistoricoDao {
        override suspend fun getAll() = emptyList<HistoricoItem>()
        override suspend fun insert(item: HistoricoItem) {}
    }
    fun climaConfigDao(): ClimaConfigDao = object : ClimaConfigDao {
        override suspend fun insert(config: ClimaConfig) {}
        override suspend fun getHistorico() = emptyList<ClimaConfig>()
    }
}

