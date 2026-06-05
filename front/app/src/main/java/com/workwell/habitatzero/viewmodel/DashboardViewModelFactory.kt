package com.workwell.habitatzero.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.workwell.habitatzero.repository.HabitatZeroRepository

class DashboardViewModelFactory(
    private val repository: HabitatZeroRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST") // ✅ suprime o warning
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
