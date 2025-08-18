package com.example.chaining.data.repository

import com.example.chaining.BuildConfig
import com.example.chaining.data.model.AreaCodeResponse
import com.example.chaining.network.AreaService
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AreaRepository @Inject constructor(
    private val api: AreaService
) {
    suspend fun fetchAreaCodes(): List<AreaCodeResponse.AreaCodeResponse.AreaCodeBody.AreaCodeItems.AreaCodeItem> {
        val response = api.getAreaCodes(
            serviceKey = BuildConfig.DATA_OPEN_API_KEY
        )
        return response.response.body.items.item
    }
}
