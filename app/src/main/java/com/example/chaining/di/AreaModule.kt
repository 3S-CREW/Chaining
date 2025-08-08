package com.example.chaining.di

import com.example.chaining.network.AreaService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object AreaModule {
    @Provides
    fun provideAreaApi(): AreaService {
        return Retrofit.Builder()
            .baseUrl("https://apis.data.go.kr/B551011/KorService2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AreaService::class.java)
    }
}
