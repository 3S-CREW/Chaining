package com.example.chaining

import android.app.Application
import com.example.chaining.data.repository.AreaRepository
import com.google.firebase.database.FirebaseDatabase
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class ChainingApp : Application() {
    @Inject
    lateinit var areaRepository: AreaRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        // DB 인스턴스가 생성되기 전에 반드시 호출
        FirebaseDatabase.getInstance().setPersistenceEnabled(true)
        preloadData()
    }

    private fun preloadData() {
        applicationScope.launch {
            areaRepository.refreshAreasIfNeeded()
        }
    }
}
