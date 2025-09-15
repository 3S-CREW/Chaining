package com.example.chaining.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.example.chaining.data.local.entity.AreaEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AreaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(areas: List<AreaEntity>)

    @Query("SELECT * FROM area_codes ORDER BY region_name ASC")
    fun getAll(): Flow<List<AreaEntity>>

    @Query("DELETE FROM area_codes")
    suspend fun deleteAll()

    @Transaction
    suspend fun clearAndInsert(areas: List<AreaEntity>) {
        deleteAll()
        insertAll(areas)
    }
}
