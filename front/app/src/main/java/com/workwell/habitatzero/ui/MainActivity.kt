package com.workwell.habitatzero.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.workwell.habitatzero.R

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNavigation: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNavigation = findViewById(R.id.bottomNavigation)

        bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> {
                    startActivity(Intent(this, DashboardActivity::class.java))
                    true
                }
                R.id.nav_alertas -> {
                    startActivity(Intent(this, AlertasActivity::class.java))
                    true
                }
                R.id.nav_controle -> {
                    startActivity(Intent(this, ControleClimaticoActivity::class.java))
                    true
                }
                R.id.nav_estufas -> {
                    startActivity(Intent(this, EstufasActivity::class.java))
                    true
                }
                else -> false
            }
        }
        
        // Default selection
        if (savedInstanceState == null) {
            bottomNavigation.selectedItemId = R.id.nav_dashboard
        }
    }
}
