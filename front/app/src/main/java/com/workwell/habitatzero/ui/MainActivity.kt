package com.workwell.habitatzero.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.workwell.habitatzero.R
import com.workwell.habitatzero.ui.fragment.AlertasFragment
import com.workwell.habitatzero.ui.fragment.DashboardFragment
import com.workwell.habitatzero.ui.fragment.EstufasFragment

class MainActivity : AppCompatActivity() {

    private lateinit var bottomNav: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        bottomNav = findViewById(R.id.bottomNavigation)

        if (savedInstanceState == null) {
            loadFragment(DashboardFragment(), NAV_DASHBOARD)
            bottomNav.selectedItemId = R.id.nav_dashboard
        }

        bottomNav.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_dashboard -> { loadFragment(DashboardFragment(), NAV_DASHBOARD); true }
                R.id.nav_estufas   -> { loadFragment(EstufasFragment(),   NAV_ESTUFAS);   true }
                R.id.nav_alertas   -> { loadFragment(AlertasFragment(),   NAV_ALERTAS);   true }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment, tag: String) {
        val existing = supportFragmentManager.findFragmentByTag(tag)
        supportFragmentManager.beginTransaction().apply {
            supportFragmentManager.fragments.forEach { hide(it) }
            if (existing == null) {
                add(R.id.fragmentContainer, fragment, tag)
            } else {
                show(existing)
            }
        }.commit()
    }

    companion object {
        const val NAV_DASHBOARD = "dashboard"
        const val NAV_ESTUFAS   = "estufas"
        const val NAV_ALERTAS   = "alertas"
    }
}
