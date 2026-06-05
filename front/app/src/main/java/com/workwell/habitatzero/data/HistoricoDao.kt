package com.workwell.habitatzero.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface HistoricoDao {
    @Query("SELECT * FROM historico ORDER BY id DESC")
    suspend fun getAll(): List<HistoricoItem>

    @Insert
    suspend fun insert(item: HistoricoItem)
}
