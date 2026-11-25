package com.example.perfilapp.ui.registro

import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perfilapp.data.AuthRepository
import com.example.perfilapp.model.UiState
import kotlinx.coroutines.launch

class RegistroViewModel(
    private val repository: AuthRepository = AuthRepository()
) : ViewModel() {

    private val _registroState = MutableLiveData<UiState<String>>()
    val registroState: LiveData<UiState<String>> = _registroState

    fun registrar(nombre: String, correo: String, password: String, confirmar: String) {
        val error = when {
            nombre.isBlank() || correo.isBlank() || password.isBlank() -> "Todos los campos son obligatorios"
            !Patterns.EMAIL_ADDRESS.matcher(correo.trim()).matches() -> "El correo no es válido"
            password.length < 6 -> "La contraseña debe tener al menos 6 caracteres"
            password != confirmar -> "Las contraseñas no coinciden"
            else -> null
        }
        if (error != null) {
            _registroState.value = UiState.Error(error)
            return
        }

        _registroState.value = UiState.Loading

        viewModelScope.launch {
            repository.registrar(nombre.trim(), correo.trim(), password)
                .onSuccess { _registroState.value = UiState.Success(it) }
                .onFailure { _registroState.value = UiState.Error(it.message ?: "Error al registrar") }
        }
    }
}
