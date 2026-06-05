package com.workwell.habitatzero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workwell.habitatzero.model.Estufa
import com.workwell.habitatzero.repository.HabitatZeroRepository
import kotlinx.coroutines.launch

class EstufasViewModel : ViewModel() {

    private val repository = HabitatZeroRepository()

    private val _estufasLiveData = MutableLiveData<List<Estufa>>()
    val estufasLiveData: LiveData<List<Estufa>> = _estufasLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

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
}
