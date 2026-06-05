package com.workwell.habitatzero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workwell.habitatzero.model.Estufa
import com.workwell.habitatzero.model.SensorAmbiente
import com.workwell.habitatzero.repository.HabitatZeroRepository
import kotlinx.coroutines.launch

class DashboardViewModel(private val repository: HabitatZeroRepository) : ViewModel() {

    private val _sensorLiveData = MutableLiveData<SensorAmbiente>()
    val sensorLiveData: LiveData<SensorAmbiente> = _sensorLiveData

    private val _historyLiveData = MutableLiveData<List<SensorAmbiente>>()
    val historyLiveData: LiveData<List<SensorAmbiente>> = _historyLiveData

    private val _estufasLiveData = MutableLiveData<List<Estufa>>()
    val estufasLiveData: LiveData<List<Estufa>> = _estufasLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    private val history = ArrayDeque<SensorAmbiente>(HISTORY_SIZE)

    fun carregarSensores() {
        viewModelScope.launch {
            try {
                val response = repository.buscarSensores()
                if (response.isSuccessful) {
                    val body = response.body()
                    if (!body.isNullOrEmpty()) {
                        val ambient = repository.converterParaSensorAmbiente(body)
                        if (history.size >= HISTORY_SIZE) history.removeFirst()
                        history.addLast(ambient)
                        _sensorLiveData.postValue(ambient)
                        _historyLiveData.postValue(history.toList())
                    }
                    // empty list is OK — IoT simulator may not have sent data yet, stay silent
                } else {
                    _errorLiveData.postValue("Erro ao carregar sensores: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Falha de conexão: ${e.message}")
            }
        }
    }

    fun carregarEstufas() {
        viewModelScope.launch {
            try {
                val response = repository.listarEstufas()
                if (response.isSuccessful && response.body() != null) {
                    _estufasLiveData.postValue(response.body())
                } else {
                    _errorLiveData.postValue("Erro ao carregar estufas: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Falha: ${e.message}")
            }
        }
    }

    companion object {
        const val HISTORY_SIZE = 20
    }
}
