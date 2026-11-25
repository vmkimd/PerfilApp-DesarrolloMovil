package com.example.perfilapp.ui.perfil

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.example.perfilapp.R
import com.example.perfilapp.databinding.ActivityPerfilBinding
import com.example.perfilapp.model.UiState
import com.example.perfilapp.ui.login.LoginActivity
import com.example.perfilapp.util.ImagenUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding
    private val viewModel: PerfilViewModel by viewModels()

    // Cámara: devuelve la foto como Bitmap en memoria (no crea archivo en el teléfono)
    private val tomarFoto = registerForActivityResult(
        ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) viewModel.subirFoto(bitmap)
    }

    // Galería: selector de fotos del sistema
    private val elegirImagen = registerForActivityResult(
        ActivityResultContracts.PickVisualMedia()
    ) { uri ->
        if (uri != null) {
            lifecycleScope.launch {
                val bitmap = withContext(Dispatchers.IO) { ImagenUtils.uriABitmap(this@PerfilActivity, uri) }
                if (bitmap != null) viewModel.subirFoto(bitmap)
                else mostrarMensaje("No se pudo leer la imagen")
            }
        }
    }

    // Permiso de cámara en tiempo de ejecución
    private val pedirPermisoCamara = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { concedido ->
        if (concedido) abrirCamara()
        else mostrarMensaje("Se necesita el permiso de cámara para tomar la foto")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnCamara.setOnClickListener { verificarPermisoCamara() }

        binding.btnGaleria.setOnClickListener {
            elegirImagen.launch(
                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
            )
        }

        binding.btnCerrarSesion.setOnClickListener {
            viewModel.cerrarSesion()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        observarPerfil()
        observarFoto()
        viewModel.cargarPerfil()
    }

    private fun verificarPermisoCamara() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            abrirCamara()
        } else {
            pedirPermisoCamara.launch(Manifest.permission.CAMERA)
        }
    }

    private fun abrirCamara() {
        try {
            tomarFoto.launch(null)
        } catch (e: Exception) {
            mostrarMensaje("No se encontró una aplicación de cámara")
        }
    }

    private fun observarPerfil() {
        viewModel.perfil.observe(this) { estado ->
            when (estado) {
                is UiState.Loading -> binding.tvNombre.text = "Cargando..."
                is UiState.Success -> {
                    binding.tvNombre.text = estado.data.nombre
                    binding.tvCorreo.text = estado.data.correo
                    binding.tvFecha.text = estado.data.fechaRegistro
                }
                is UiState.Error -> {
                    mostrarMensaje(estado.message)
                    // Si el token venció, se regresa al login
                    if (estado.message.contains("Token")) {
                        viewModel.cerrarSesion()
                        startActivity(Intent(this, LoginActivity::class.java))
                        finish()
                    }
                }
            }
        }
    }

    private fun observarFoto() {
        viewModel.foto.observe(this) { estado ->
            when (estado) {
                is UiState.Loading -> activarControles(false)
                is UiState.Success -> {
                    activarControles(true)
                    if (estado.data != null) binding.ivFotoPerfil.setImageBitmap(estado.data)
                    else binding.ivFotoPerfil.setImageResource(R.drawable.ic_persona)
                }
                is UiState.Error -> {
                    activarControles(true)
                    mostrarMensaje(estado.message)
                }
            }
        }
    }

    private fun activarControles(activo: Boolean) {
        binding.progressFoto.visibility = if (activo) View.GONE else View.VISIBLE
        binding.btnCamara.isEnabled = activo
        binding.btnGaleria.isEnabled = activo
    }

    private fun mostrarMensaje(texto: String) {
        Toast.makeText(this, texto, Toast.LENGTH_LONG).show()
    }
}
