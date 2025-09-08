package com.example.chaining.data.repository

import com.example.chaining.BuildConfig
import com.example.chaining.data.model.TourItem
import com.example.chaining.network.FeedApiService

class FeedRepository (
    private val apiService: FeedApiService
) {
    suspend fun getTourItems(areaCode: Int?): List<TourItem> {
        val response = apiService.getAreaBasedList(
            serviceKey = BuildConfig.DATA_OPEN_API_KEY,
            mobileApp = "Chaining",
            areaCode = areaCode
        )
        if (response.response.header.resultCode == "0000") {
            return response.response.body.items.item
        } else {
            // 에러 처리
            throw Exception(response.response.header.resultMsg)
        }
    }
}