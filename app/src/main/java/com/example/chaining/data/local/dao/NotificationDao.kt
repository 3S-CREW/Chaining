package com.example.chaining.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.chaining.data.local.entity.NotificationEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationDao {
    @Query("SELECT * FROM notification_table WHERE uid = :uid ORDER BY createdAt DESC")
    fun getNotifications(uid: String): Flow<List<NotificationEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDao(notifications: List<NotificationEntity>)

    @Query("DELETE FROM notification_table WHERE uid = :uid")
    suspend fun deleteAll(uid: String)
}
