package com.workwell.habitatzero.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.workwell.habitatzero.R
import com.workwell.habitatzero.adapter.HistoricoAdapter
import com.workwell.habitatzero.data.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HistoricoActivity : AppCompatActivity() {

    private lateinit var recyclerHistorico: RecyclerView
    private lateinit var adapter: HistoricoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        recyclerHistorico = findViewById(R.id.recyclerHistorico)
        recyclerHistorico.layoutManager = LinearLayoutManager(this)

        // Inicializa o banco de dados Room
        val db = AppDatabase.getDatabase(this)
        val historicoDao = db.historicoDao()

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                // Carregar lista do banco
                val listaHistorico = historicoDao.getAll()
                
                withContext(Dispatchers.Main) {
                    // Configurar adapter com os dados do Room
                    adapter = HistoricoAdapter(listaHistorico)
                    recyclerHistorico.adapter = adapter
                }
            }
        }
    }
}
