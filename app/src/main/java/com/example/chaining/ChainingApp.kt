package com.example.chaining

import android.app.Application
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class ChainingApp : Application() {
    override fun onCreate() {
        super.onCreate()
        // DB 인스턴스가 생성되기 전에 반드시 호출
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
    }
}