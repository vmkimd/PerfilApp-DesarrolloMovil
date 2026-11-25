package com.example.perfilapp.model.response

data class Usuario(
    val id: Int,
    val nombre: String,
    val correo: String,
    val fechaRegistro: String,
    val tieneFoto: Boolean
)
