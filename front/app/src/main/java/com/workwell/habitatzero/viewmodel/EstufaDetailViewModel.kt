package com.workwell.habitatzero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workwell.habitatzero.model.Alerta
import com.workwell.habitatzero.model.Planta
import com.workwell.habitatzero.model.PlantaRequest
import com.workwell.habitatzero.repository.HabitatZeroRepository
import kotlinx.coroutines.launch

class EstufaDetailViewModel : ViewModel() {

    private val repository = HabitatZeroRepository()

    private val _plantasLiveData = MutableLiveData<List<Planta>>()
    val plantasLiveData: LiveData<List<Planta>> = _plantasLiveData

    private val _plantaUpdatedLiveData = MutableLiveData<Planta>()
    val plantaUpdatedLiveData: LiveData<Planta> = _plantaUpdatedLiveData

    private val _plantaAddedLiveData = MutableLiveData<Planta>()
    val plantaAddedLiveData: LiveData<Planta> = _plantaAddedLiveData

    private val _alertasLiveData = MutableLiveData<List<Alerta>>()
    val alertasLiveData: LiveData<List<Alerta>> = _alertasLiveData

    private val _alertaResolvidoId = MutableLiveData<Long>()
    val alertaResolvidoId: LiveData<Long> = _alertaResolvidoId

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    fun carregarPlantas(estufaId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.listarPlantas(estufaId)
                if (response.isSuccessful) {
                    _plantasLiveData.postValue(response.body() ?: emptyList())
                } else {
                    _errorLiveData.postValue("Erro ao carregar plantas: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Falha: ${e.message}")
            }
        }
    }

    fun avancarFase(planta: Planta) {
        val fases = listOf("SEMENTE", "GERMINACAO", "CRESCIMENTO", "MATURACAO", "COLHEITA")
        val nextFase = fases.getOrNull(fases.indexOf(planta.faseCrescimento) + 1) ?: return
        val request = PlantaRequest(
            nomeCientifico = planta.nomeCientifico,
            nomeComum = planta.nomeComum,
            faseCrescimento = nextFase,
            dataPlantio = planta.dataPlantio,
            estufaId = planta.estufaId
        )
        viewModelScope.launch {
            try {
                val response = repository.atualizarPlanta(planta.id, request)
                if (response.isSuccessful && response.body() != null) {
                    _plantaUpdatedLiveData.postValue(response.body())
                } else {
                    _errorLiveData.postValue("Erro ao avançar fase: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Falha: ${e.message}")
            }
        }
    }

    fun adicionarPlanta(request: PlantaRequest) {
        viewModelScope.launch {
            try {
                val response = repository.criarPlanta(request)
                if (response.isSuccessful && response.body() != null) {
                    _plantaAddedLiveData.postValue(response.body())
                } else {
                    _errorLiveData.postValue("Erro ao adicionar planta: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Falha: ${e.message}")
            }
        }
    }

    fun carregarAlertas(estufaId: Long) {
        viewModelScope.launch {
            try {
                val response = repository.listarAlertasPorEstufa(estufaId)
                if (response.isSuccessful) {
                    _alertasLiveData.postValue(response.body() ?: emptyList())
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
