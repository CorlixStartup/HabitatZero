package com.workwell.habitatzero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workwell.habitatzero.repository.HabitatZeroRepository
import kotlinx.coroutines.launch
import kotlin.math.max
import kotlin.math.roundToInt

data class ProfileUiState(
    val email: String = "",
    val initials: String = "",
    val efficiencyPct: Int = 0,
    val totalEstufas: Int = 0,
    val isLoading: Boolean = false
)

class ProfileViewModel : ViewModel() {

    private val repository = HabitatZeroRepository()

    private val _uiState = MutableLiveData(ProfileUiState(isLoading = true))
    val uiState: LiveData<ProfileUiState> = _uiState

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    // TODO: replace with real Mars cycle data source when available
    // Exposed as LiveData so the View can observe it without special logic
    val solsCount: LiveData<Int> = MutableLiveData(142)

    fun carregarPerfil(email: String) {
        val initials = email
            .split("@").first()
            .split(".", "_", "-")
            .mapNotNull { it.firstOrNull()?.uppercaseChar() }
            .take(2)
            .joinToString("")
            .ifEmpty { email.take(2).uppercase() }

        _uiState.value = ProfileUiState(
            email = email,
            initials = initials,
            isLoading = true
        )
        carregarEficiencia(email, initials)
    }

    private fun carregarEficiencia(email: String, initials: String) {
        viewModelScope.launch {
            try {
                val response = repository.listarEstufas()
                if (response.isSuccessful && response.body() != null) {
                    val estufas = response.body()!!
                    // Efficiency: inverse of alert density across all greenhouses.
                    // A greenhouse with 0 alerts and >0 plants = 100% efficient.
                    // Each active alert reduces the score proportionally.
                    val totalPlantas = estufas.sumOf { max(it.totalPlantas, 1).toDouble() }
                    val totalAlertas = estufas.sumOf { it.alertasAtivos.toDouble() }
                    val efficiencyPct = if (totalPlantas == 0.0) 98
                    else {
                        val ratio = (1.0 - (totalAlertas / totalPlantas)).coerceIn(0.0, 1.0)
                        (ratio * 100).roundToInt()
                    }
                    _uiState.postValue(ProfileUiState(
                        email = email,
                        initials = initials,
                        efficiencyPct = efficiencyPct,
                        totalEstufas = estufas.size,
                        isLoading = false
                    ))
                } else {
                    // Non-fatal — show defaults and surface error
                    _uiState.postValue(ProfileUiState(
                        email = email,
                        initials = initials,
                        efficiencyPct = 98, // fallback
                        isLoading = false
                    ))
                    _errorLiveData.postValue("Erro ao carregar eficiência: ${response.code()}")
                }
            } catch (e: Exception) {
                _uiState.postValue(ProfileUiState(
                    email = email,
                    initials = initials,
                    efficiencyPct = 98,
                    isLoading = false
                ))
                _errorLiveData.postValue("Falha de conexão: ${e.message}")
            }
        }
    }
}
