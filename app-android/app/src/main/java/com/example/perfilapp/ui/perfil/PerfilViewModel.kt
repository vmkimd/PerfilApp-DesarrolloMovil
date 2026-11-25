package com.example.perfilapp.ui.perfil

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.perfilapp.data.PerfilRepository
import com.example.perfilapp.data.local.SessionManager
import com.example.perfilapp.model.UiState
import com.example.perfilapp.model.response.Usuario
import com.example.perfilapp.util.ImagenUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilViewModel(
    private val repository: PerfilRepository = PerfilRepository()
) : ViewModel() {

    private val _perfil = MutableLiveData<UiState<Usuario>>()
    val perfil: LiveData<UiState<Usuario>> = _perfil

    private val _foto = MutableLiveData<UiState<Bitmap?>>()
    val foto: LiveData<UiState<Bitmap?>> = _foto

    fun cargarPerfil() {
        _perfil.value = UiState.Loading
        viewModelScope.launch {
            repository.obtenerPerfil()
                .onSuccess { usuario ->
                    _perfil.value = UiState.Success(usuario)
                    if (usuario.tieneFoto) cargarFoto()
                }
                .onFailure { _perfil.value = UiState.Error(it.message ?: "Error al cargar el perfil") }
        }
    }

    private fun cargarFoto() {
        _foto.value = UiState.Loading
        viewModelScope.launch {
            repository.obtenerFoto()
                .onSuccess { _foto.value = UiState.Success(it) }
                .onFailure { _foto.value = UiState.Error(it.message ?: "Error al cargar la foto") }
        }
    }

    /** Convierte la imagen a JPEG, la envía al backend y luego la vuelve a pedir. */
    fun subirFoto(bitmap: Bitmap) {
        _foto.value = UiState.Loading
        viewModelScope.launch {
            val bytes = withContext(Dispatchers.Default) { ImagenUtils.bitmapAJpeg(bitmap) }
            repository.subirFoto(bytes)
                .onSuccess { cargarFoto() }
                .onFailure { _foto.value = UiState.Error(it.message ?: "Error al subir la foto") }
        }
    }

    fun cerrarSesion() {
        SessionManager.cerrarSesion()
    }
}
