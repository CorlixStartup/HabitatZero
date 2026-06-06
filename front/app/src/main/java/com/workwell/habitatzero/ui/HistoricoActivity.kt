package com.workwell.habitatzero.ui

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.workwell.habitatzero.R
import com.workwell.habitatzero.adapter.HistoricoAdapter
import com.workwell.habitatzero.data.HistoricoItem

class HistoricoActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_historico)

        val recyclerHistorico = findViewById<RecyclerView>(R.id.recyclerHistorico)
        recyclerHistorico.layoutManager = LinearLayoutManager(this)

        val prefs = getSharedPreferences(EstufaDetailActivity.PREFS_HISTORICO, Context.MODE_PRIVATE)
        val raw = prefs.getString(EstufaDetailActivity.KEY_HISTORICO, "") ?: ""
        val items = if (raw.isBlank()) emptyList()
        else raw.split("\n").filter { it.isNotBlank() }.mapIndexed { idx, line ->
            val parts = line.split(" — ")
            HistoricoItem(
                id = idx,
                descricao = parts.getOrElse(0) { line } + if (parts.size > 1) " — ${parts[1]}" else "",
                data = parts.getOrElse(2) { "" }
            )
        }

        recyclerHistorico.adapter = HistoricoAdapter(items)
    }
}
