package com.workwell.habitatzero.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ClimaConfigDao {

    // Inserir nova configuração
    @Insert
    suspend fun insert(config: ClimaConfig)

    // Listar histórico ordenado pela data mais recente
    @Query("SELECT * FROM clima_config ORDER BY timestamp DESC")
    suspend fun getHistorico(): List<ClimaConfig>
}
