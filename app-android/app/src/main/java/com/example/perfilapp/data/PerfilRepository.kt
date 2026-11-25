package com.example.perfilapp.data

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.perfilapp.data.remote.ErrorParser
import com.example.perfilapp.data.remote.RetrofitClient
import com.example.perfilapp.model.response.Usuario
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

class PerfilRepository {

    private val api = RetrofitClient.apiService

    suspend fun obtenerPerfil(): Result<Usuario> {
        return try {
            val response = api.obtenerPerfil()
            val usuario = response.body()?.usuario
            if (response.isSuccessful && usuario != null) {
                Result.success(usuario)
            } else {
                Result.failure(Exception(ErrorParser.mensaje(response)))
            }
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo conectar con el servidor"))
        }
    }

    /** Envía los bytes JPEG al backend. No se crea ningún archivo en el teléfono. */
    suspend fun subirFoto(bytes: ByteArray): Result<String> {
        return try {
            val cuerpo = bytes.toRequestBody("image/jpeg".toMediaType())
            val parte = MultipartBody.Part.createFormData("foto", "perfil.jpg", cuerpo)
            val response = api.subirFoto(parte)
            if (response.isSuccessful) {
                Result.success(response.body()?.mensaje ?: "Foto actualizada")
            } else {
                Result.failure(Exception(ErrorParser.mensaje(response)))
            }
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo subir la foto"))
        }
    }

    /** Descarga la foto desde el backend y la convierte en Bitmap (solo en memoria). */
    suspend fun obtenerFoto(): Result<Bitmap?> {
        return try {
            val response = api.obtenerFoto()
            if (response.isSuccessful) {
                val bytes = response.body()?.bytes()
                val bitmap = bytes?.let { BitmapFactory.decodeByteArray(it, 0, it.size) }
                Result.success(bitmap)
            } else if (response.code() == 404) {
                Result.success(null)
            } else {
                Result.failure(Exception(ErrorParser.mensaje(response)))
            }
        } catch (e: Exception) {
            Result.failure(Exception("No se pudo cargar la foto"))
        }
    }
}
