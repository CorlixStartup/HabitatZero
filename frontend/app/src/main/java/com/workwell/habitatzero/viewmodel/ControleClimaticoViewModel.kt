package com.workwell.habitatzero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workwell.habitatzero.model.EstufaRequest
import com.workwell.habitatzero.repository.HabitatZeroRepository
import kotlinx.coroutines.launch

class ControleClimaticoViewModel : ViewModel() {

    private val repository = HabitatZeroRepository()

    private val _successLiveData = MutableLiveData<Boolean>()
    val successLiveData: LiveData<Boolean> = _successLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    fun salvarConfiguracao(estufaId: Long, request: EstufaRequest) {
        viewModelScope.launch {
            try {
                val response = repository.atualizarEstufa(estufaId, request)
                if (response.isSuccessful) {
                    _successLiveData.postValue(true)
                } else {
                    _errorLiveData.postValue("Erro ao salvar: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Falha: ${e.message}")
            }
        }
    }
}
