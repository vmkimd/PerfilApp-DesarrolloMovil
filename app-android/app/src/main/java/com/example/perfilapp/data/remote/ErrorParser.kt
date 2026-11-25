package com.example.perfilapp.data.remote

import com.example.perfilapp.model.response.StandardResponse
import com.google.gson.Gson
import retrofit2.Response

/** Lee el mensaje de error que devuelve el backend en formato { ok, mensaje }. */
object ErrorParser {
    fun mensaje(response: Response<*>): String {
        return try {
            val json = response.errorBody()?.string()
            Gson().fromJson(json, StandardResponse::class.java)?.mensaje
                ?: "Error ${response.code()}"
        } catch (e: Exception) {
            "Error ${response.code()}"
        }
    }
}
