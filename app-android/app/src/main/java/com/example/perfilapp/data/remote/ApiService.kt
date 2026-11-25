package com.example.perfilapp.data.remote

import com.example.perfilapp.model.request.LoginRequest
import com.example.perfilapp.model.request.RegistroRequest
import com.example.perfilapp.model.response.LoginResponse
import com.example.perfilapp.model.response.PerfilResponse
import com.example.perfilapp.model.response.StandardResponse
import okhttp3.MultipartBody
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Part

interface ApiService {

    @POST("api/auth/registro")
    suspend fun registrar(@Body request: RegistroRequest): Response<StandardResponse>

    @POST("api/auth/login")
    suspend fun login(@Body request: LoginRequest): Response<LoginResponse>

    @GET("api/perfil")
    suspend fun obtenerPerfil(): Response<PerfilResponse>

    @Multipart
    @PUT("api/perfil/foto")
    suspend fun subirFoto(@Part foto: MultipartBody.Part): Response<StandardResponse>

    @GET("api/perfil/foto")
    suspend fun obtenerFoto(): Response<ResponseBody>
}
