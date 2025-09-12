package com.example.chaining.data.repository

import android.util.Log
import com.example.chaining.BuildConfig
import com.example.chaining.data.local.dao.AreaDao
import com.example.chaining.data.local.entity.AreaEntity
import com.example.chaining.data.model.AreaCodeResponse
import com.example.chaining.network.AreaService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AreaRepository @Inject constructor(
    private val api: AreaService,
    private val areaDao: AreaDao
) {
    // 대한민국 주요 시/도 지역 코드 목록
    private val majorRegionCodes = listOf(
//        11, 26, 27, 28, 29, 30, 31, 32, 33, 34, 35, 36, 37, 38, 39, 50, 51
        11, 26, 27, 28, 29, 30, 31, 36, 41, 43, 44, 46, 47, 48, 50, 51, 52
    )

    val allAreas: Flow<List<AreaEntity>> = areaDao.getAll()

    suspend fun refreshAreasIfNeeded() {
        val isDbEmpty = allAreas.first().isEmpty()
        if (isDbEmpty) {
            Log.d("AreaRepository", "Database is empty. Fetching from network...")
            try {
                val apiItems = fetchAllMajorAreaCodes()
                val entities = apiItems.map { it.toEntity() }
                areaDao.clearAndInsert(entities)
                Log.d("AreaRepository", "Successfully fetched and saved to DB.")
            } catch (e: Exception) {
                Log.e("AreaRepository", "Failed to refresh areas", e)
            }
        } else {
            Log.d("AreaRepository", "Database is already populated. No need to fetch.")
        }
    }

    suspend fun fetchAreaCodes(): List<AreaCodeResponse.AreaCodeResponse.AreaCodeBody.AreaCodeItems.AreaCodeItem> {
        val response = api.getAreaCodes(
            serviceKey = BuildConfig.DATA_OPEN_API_KEY
        )
        return response.response.body.items.item
    }

    suspend fun fetchAllMajorAreaCodes(): List<AreaCodeResponse.AreaCodeResponse.AreaCodeBody.AreaCodeItems.AreaCodeItem> {
        return withContext(Dispatchers.IO) {
            majorRegionCodes.map { code ->
                async {
                    try {
                        val response = api.getAreaCodes(
                            serviceKey = BuildConfig.DATA_OPEN_API_KEY,
                            lDongRegnCd = code
                        )
                        response.response.body.items.item.firstOrNull()
                    } catch (e: Exception) {
                        Log.e("AreaRepository", "Failed to fetch area for code $code", e)
                        null
                    }
                }
            }
                .awaitAll()
                .filterNotNull()
        }
    }

    private fun AreaCodeResponse.AreaCodeResponse.AreaCodeBody.AreaCodeItems.AreaCodeItem.toEntity(): AreaEntity {
        return AreaEntity(
            regionCode = this.lDongRegnCd,
            regionName = this.lDongRegnNm,
            subRegionCode = this.lDongSignguCd,
            subRegionName = this.lDongSignguNm,
            rowNum = this.rnum
        )
    }

}


