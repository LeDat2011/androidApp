package com.example.composeapp

import android.app.Application
import com.google.firebase.FirebaseApp

class ComposeApp : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseApp.initializeApp(this)
    }
} 