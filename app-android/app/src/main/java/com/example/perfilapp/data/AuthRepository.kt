package com.example.perfilapp.data

import com.example.perfilapp.data.local.SessionManager
import com.example.perfilapp.data.remote.ErrorParser
import com.example.perfilapp.data.remote.RetrofitClient
import com.example.perfilapp.model.request.LoginRequest
import com.example.perfilapp.model.request.RegistroRequest

class AuthRepository {

    private val api = RetrofitClient.apiService

    suspend fun registrar(nombre: String, correo: String, password: String): Result<String> {
        return try {
            val response = api.registrar(RegistroRequest(nombre, correo, password))
            if (response.isSuccessful) {
                Result.success(response.body()?.mensaje ?: "Usuario registrado")
            } else {
                Result.failure(Exception(ErrorParser.mensaje(response)))
            }
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo conectar con el servidor"))
        }
    }

    suspend fun login(correo: String, password: String): Result<String> {
        return try {
            val response = api.login(LoginRequest(correo, password))
            val body = response.body()
            if (response.isSuccessful && body?.token != null) {
                SessionManager.guardarSesion(body.token, body.nombre ?: "")
                Result.success(body.nombre ?: "")
            } else {
                Result.failure(Exception(ErrorParser.mensaje(response)))
            }
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo conectar con el servidor"))
        }
    }
}
