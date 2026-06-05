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

    private val _estufasLiveData = MutableLiveData<List<Estufa>>()
    val estufasLiveData: LiveData<List<Estufa>> = _estufasLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    fun carregarSensores() {
        viewModelScope.launch {
            try {
                val response = repository.buscarSensores()
                if (response.isSuccessful && response.body() != null) {
                    val leituras = response.body()!!
                    _sensorLiveData.postValue(repository.converterParaSensorAmbiente(leituras))
                } else {
                    _errorLiveData.postValue("Erro ao carregar sensores")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Falha: ${e.message}")
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
                    _errorLiveData.postValue("Erro ao carregar estufas")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Falha: ${e.message}")
            }
        }
    }
}
