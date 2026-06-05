package com.workwell.habitatzero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workwell.habitatzero.model.Alerta
import com.workwell.habitatzero.repository.HabitatZeroRepository
import kotlinx.coroutines.launch

class AlertasViewModel : ViewModel() {

    private val repository = HabitatZeroRepository()

    private val _alertasLiveData = MutableLiveData<List<Alerta>>()
    val alertasLiveData: LiveData<List<Alerta>> = _alertasLiveData

    private val _alertaResolvidoId = MutableLiveData<Long>()
    val alertaResolvidoId: LiveData<Long> = _alertaResolvidoId

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    fun carregarAlertas() {
        viewModelScope.launch {
            try {
                val response = repository.listarAlertas()
                if (response.isSuccessful && response.body() != null) {
                    _alertasLiveData.postValue(response.body())
                } else {
                    _errorLiveData.postValue("Erro ao carregar alertas: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Falha: ${e.message}")
            }
        }
    }

    fun resolverAlerta(alertaId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.resolverAlerta(alertaId)
                if (response.isSuccessful) {
                    _alertaResolvidoId.postValue(alertaId)
                } else {
                    _errorLiveData.postValue("Erro ao resolver alerta: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Falha: ${e.message}")
            }
        }
    }
}
