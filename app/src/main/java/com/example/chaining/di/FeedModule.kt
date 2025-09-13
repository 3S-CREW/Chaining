package com.example.chaining.di

import com.example.chaining.data.repository.FeedRepository
import com.example.chaining.network.FeedApiService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class KoreanApiService

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class EnglishApiService

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

//    @Provides
//    @Singleton
//    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
//        return Retrofit.Builder()
//            .baseUrl("https://apis.data.go.kr/B551011/KorService2/")
//            .client(okHttpClient)
//            .addConverterFactory(GsonConverterFactory.create())
//            .build()
//    }


    @Provides
    @Singleton
    @KoreanApiService
    fun provideKoreanRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://apis.data.go.kr/B551011/KorService2/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @EnglishApiService
    fun provideEnglishRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            // 사용자가 알려준 대로, 마지막 경로만 EngService2로 변경합니다.
            .baseUrl("https://apis.data.go.kr/B551011/EngService2/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

//    @Provides
//    @Singleton
//    fun provideTourApiService(retrofit: Retrofit): FeedApiService {
//        return retrofit.create(FeedApiService::class.java)
//    }

    @Provides
    @Singleton
    @KoreanApiService
    fun provideKorApiService(@KoreanApiService retrofit: Retrofit): FeedApiService {
        return retrofit.create(FeedApiService::class.java)
    }

    @Provides
    @Singleton
    @EnglishApiService
    fun provideEngApiService(@EnglishApiService retrofit: Retrofit): FeedApiService {
        return retrofit.create(FeedApiService::class.java)
    }

    //    @Provides
//    @Singleton
//    fun provideFeedRepository(apiService: FeedApiService): FeedRepository {
//        return FeedRepository(apiService)
//    }
    @Provides
    @Singleton
    fun provideFeedRepository(
        @KoreanApiService korApiService: FeedApiService,
        @EnglishApiService engApiService: FeedApiService,
    ): FeedRepository {
        return FeedRepository(korApiService, engApiService)
    }
}