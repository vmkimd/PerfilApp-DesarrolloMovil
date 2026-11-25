package com.example.perfilapp.model.response

data class LoginResponse(
    val ok: Boolean,
    val mensaje: String?,
    val token: String?,
    val nombre: String?
)
