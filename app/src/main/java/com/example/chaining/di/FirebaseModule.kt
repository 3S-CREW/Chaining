package com.example.chaining.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object FirebaseModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth = FirebaseAuth.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseDatabase(): FirebaseDatabase {
        // 특정 URL을 쓰고 싶다면:
        // return FirebaseDatabase.getInstance("https://<your-db>.firebasedatabase.app")
        return FirebaseDatabase.getInstance("https://chaining-88dbd-default-rtdb.firebaseio.com/")
    }

    @Provides
    @Singleton
    fun provideRootRef(db: FirebaseDatabase): DatabaseReference = db.reference
}