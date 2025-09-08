package com.example.chaining.di

import com.example.chaining.data.repository.FeedRepository
import com.example.chaining.network.FeedApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import javax.inject.Singleton
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Module
@InstallIn(SingletonComponent::class)
object FeedModule {

    // HttpLoggingInterceptor를 제공하는 함수 추가
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        return HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
    }

    // OkHttpClient를 제공하는 함수 추가
    @Provides
    @Singleton
    fun provideOkHttpClient(interceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(interceptor)
            .build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://apis.data.go.kr/B551011/KorService2/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideTourApiService(retrofit: Retrofit): FeedApiService {
        return retrofit.create(FeedApiService::class.java)
    }

    @Provides
    @Singleton // 어노테이션을 추가하여 FeedRepository도 Singleton으로 만듭니다.
    fun provideFeedRepository(apiService: FeedApiService): FeedRepository {
        return FeedRepository(apiService)
    }
}