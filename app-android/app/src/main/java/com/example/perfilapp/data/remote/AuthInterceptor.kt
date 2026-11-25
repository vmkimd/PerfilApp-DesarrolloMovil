package com.example.perfilapp.data.remote

import com.example.perfilapp.data.local.SessionManager
import okhttp3.Interceptor
import okhttp3.Response

/** Agrega el token JWT a cada petición cuando hay sesión iniciada. */
class AuthInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val token = SessionManager.getToken()
        val request = if (token != null) {
            chain.request().newBuilder()
                .addHeader("Authorization", "Bearer $token")
                .build()
        } else {
            chain.request()
        }
        return chain.proceed(request)
    }
}
