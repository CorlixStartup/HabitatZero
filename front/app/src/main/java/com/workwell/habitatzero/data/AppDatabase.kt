package com.workwell.habitatzero.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(
    entities = [HistoricoItem::class, ClimaConfig::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun historicoDao(): HistoricoDao
    abstract fun climaConfigDao(): ClimaConfigDao   // ✅ adicionado

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "habitatzero_db"
                )
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
