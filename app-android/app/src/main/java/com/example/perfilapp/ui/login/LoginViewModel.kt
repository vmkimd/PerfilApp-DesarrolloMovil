package com.example.perfilapp.ui.login

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perfilapp.data.AuthRepository
import com.example.perfilapp.model.UiState
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _loginState = MutableLiveData<UiState<String>>()
    val loginState: LiveData<UiState<String>> = _loginState

    fun login(correo: String, password: String) {
        if (correo.isBlank() || password.isBlank()) {
            _loginState.value = UiState.Error("Ingresa tu correo y contraseña")
            return
        }

        _loginState.value = UiState.Loading

        viewModelScope.launch {
            repository.login(correo.trim(), password)
                .onSuccess { _loginState.value = UiState.Success(it) }
                .onFailure { _loginState.value = UiState.Error(it.message ?: "Error al iniciar sesión") }
        }
    }
}
