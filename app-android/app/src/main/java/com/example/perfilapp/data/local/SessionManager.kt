package com.example.perfilapp.data.local

import android.content.Context
import android.content.SharedPreferences

/**
 * Guarda solo el token JWT y el nombre del usuario.
 * La foto de perfil NO se guarda en el teléfono: siempre se pide al backend.
 */
object SessionManager {

    private const val PREFS = "perfilapp_prefs"
    private const val KEY_TOKEN = "token"
    private const val KEY_NOMBRE = "nombre"

    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.applicationContext.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
    }

    fun guardarSesion(token: String, nombre: String) {
        prefs.edit()
            .putString(KEY_TOKEN, token)
            .putString(KEY_NOMBRE, nombre)
            .apply()
    }

    fun getToken(): String? = prefs.getString(KEY_TOKEN, null)

    fun haySesion(): Boolean = !getToken().isNullOrEmpty()

    fun cerrarSesion() {
        prefs.edit().clear().apply()
    }
}
