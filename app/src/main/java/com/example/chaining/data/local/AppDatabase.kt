package com.example.chaining.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.chaining.data.local.dao.NotificationDao
import com.example.chaining.data.local.dao.UserDao
import com.example.chaining.data.local.entity.NotificationEntity
import com.example.chaining.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, NotificationEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun notificationDao(): NotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

    }
}
