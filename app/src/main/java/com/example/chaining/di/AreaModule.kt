package com.example.chaining.di

import com.example.chaining.data.local.dao.AreaDao
import com.example.chaining.data.repository.AreaRepository
import com.example.chaining.network.AreaService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KoreanArea

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EnglishArea

@Module
@InstallIn(SingletonComponent::class)
object AreaModule {
    @Provides
    @Singleton
    @KoreanArea
    fun provideKoreanAreaApi(): AreaService {
        return Retrofit.Builder()
            .baseUrl("https://apis.data.go.kr/B551011/KorService2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AreaService::class.java)
    }

    @Provides
    @Singleton
    @EnglishArea
    fun provideEnglishAreaApi(): AreaService {
        return Retrofit.Builder()
            .baseUrl("https://apis.data.go.kr/B551011/EngService2/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(AreaService::class.java)
    }

    @Provides
    @Singleton
    fun provideAreaRepository(
        @KoreanArea korAreaService: AreaService,
        @EnglishArea engAreaService: AreaService,
        areaDao: AreaDao
    ): AreaRepository {
        return AreaRepository(korAreaService, engAreaService, areaDao)
    }
}
