package com.example.perfilapp.ui.login

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.perfilapp.data.local.SessionManager
import com.example.perfilapp.databinding.ActivityLoginBinding
import com.example.perfilapp.model.UiState
import com.example.perfilapp.ui.perfil.PerfilActivity
import com.example.perfilapp.ui.registro.RegistroActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private val viewModel: LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Si ya hay sesión guardada, se entra directo al perfil
        if (SessionManager.haySesion()) {
            irAPerfil()
            return
        }

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Si viene desde el registro, se llena el correo automáticamente
        intent.getStringExtra(EXTRA_CORREO)?.let { binding.etCorreo.setText(it) }

        binding.btnLogin.setOnClickListener {
            viewModel.login(
                binding.etCorreo.text.toString(),
                binding.etPassword.text.toString()
            )
        }

        binding.btnIrRegistro.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }

        observarEstado()
    }

    private fun observarEstado() {
        viewModel.loginState.observe(this) { estado ->
            when (estado) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnLogin.isEnabled = false
                    binding.tvError.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    irAPerfil()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnLogin.isEnabled = true
                    binding.tvError.text = estado.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun irAPerfil() {
        startActivity(Intent(this, PerfilActivity::class.java))
        finish()
    }

    companion object {
        const val EXTRA_CORREO = "extra_correo"
    }
}
