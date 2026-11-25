package com.example.perfilapp.ui.registro

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.perfilapp.databinding.ActivityRegistroBinding
import com.example.perfilapp.model.UiState
import com.example.perfilapp.ui.login.LoginActivity

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding
    private val viewModel: RegistroViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRegistrar.setOnClickListener {
            viewModel.registrar(
                binding.etNombre.text.toString(),
                binding.etCorreo.text.toString(),
                binding.etPassword.text.toString(),
                binding.etConfirmar.text.toString()
            )
        }

        binding.btnIrLogin.setOnClickListener { finish() }

        observarEstado()
    }

    private fun observarEstado() {
        viewModel.registroState.observe(this) { estado ->
            when (estado) {
                is UiState.Loading -> {
                    binding.progressBar.visibility = View.VISIBLE
                    binding.btnRegistrar.isEnabled = false
                    binding.tvError.visibility = View.GONE
                }
                is UiState.Success -> {
                    binding.progressBar.visibility = View.GONE
                    Toast.makeText(this, estado.data, Toast.LENGTH_LONG).show()
                    // Regresa al login con el correo ya escrito
                    val intent = Intent(this, LoginActivity::class.java).apply {
                        putExtra(LoginActivity.EXTRA_CORREO, binding.etCorreo.text.toString().trim())
                        flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                    }
                    startActivity(intent)
                    finish()
                }
                is UiState.Error -> {
                    binding.progressBar.visibility = View.GONE
                    binding.btnRegistrar.isEnabled = true
                    binding.tvError.text = estado.message
                    binding.tvError.visibility = View.VISIBLE
                }
            }
        }
    }
}
