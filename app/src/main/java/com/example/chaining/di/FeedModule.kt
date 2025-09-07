package com.example.chaining.di

import com.example.chaining.network.FeedApiService
import dagger.Provides
import jakarta.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object FeedModule {
    @Provides
    @Singleton
    fun provideRetrofit(): Retrofit {
        return Retrofit.Builder()
            .baseUrl("http://apis.data.go.kr/B551011/KorService2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTourApiService(retrofit: Retrofit): FeedApiService {
        return retrofit.create(FeedApiService::class.java)
    }
}