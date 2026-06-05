package com.workwell.habitatzero.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.workwell.habitatzero.model.LoginDTO
import com.workwell.habitatzero.model.TokenDTO
import com.workwell.habitatzero.repository.HabitatZeroRepository
import kotlinx.coroutines.launch

class LoginViewModel : ViewModel() {

    private val repository by lazy { HabitatZeroRepository() }

    private val _tokenLiveData = MutableLiveData<TokenDTO>()
    val tokenLiveData: LiveData<TokenDTO> = _tokenLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    fun login(email: String, senha: String) {
        val loginDTO = LoginDTO(email, senha)

        viewModelScope.launch {
            try {
                val response = repository.login(loginDTO)
                if (response.isSuccessful && response.body() != null) {
                    _tokenLiveData.postValue(response.body())
                } else {
                    _errorLiveData.postValue("Falha no login: ${response.code()}")
                }
            } catch (e: Exception) {
                _errorLiveData.postValue("Erro: ${e.message}")
            }
        }
    }
}
