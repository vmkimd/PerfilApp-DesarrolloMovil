package com.example.perfilapp

import android.app.Application
import com.example.perfilapp.data.local.SessionManager

class PerfilApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        SessionManager.init(this)
    }
}
