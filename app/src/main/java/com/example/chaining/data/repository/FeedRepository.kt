package com.example.chaining.data.repository

import com.example.chaining.BuildConfig
import com.example.chaining.data.model.TourItem
import com.example.chaining.di.EnglishApiService
import com.example.chaining.di.KoreanApiService
import com.example.chaining.network.FeedApiService
import java.util.Locale
import javax.inject.Inject

class FeedRepository @Inject constructor(
    @KoreanApiService private val korApiService: FeedApiService,
    @EnglishApiService private val engApiService: FeedApiService,
) {
    suspend fun getTourItems(areaCode: Int?): List<TourItem> {
        val currentLanguage = Locale.getDefault().language
        val apiService = if (currentLanguage == "ko") {
            korApiService
        } else {
            engApiService
        }

        val response = if (currentLanguage == "ko") {
            apiService.getAreaBasedList(
                serviceKey = BuildConfig.DATA_OPEN_API_KEY,
                mobileApp = "Chaining",
                areaCode = areaCode,
                contentTypeId = 12
            )
        } else {
            apiService.getAreaBasedList(
                serviceKey = BuildConfig.DATA_OPEN_API_KEY,
                mobileApp = "Chaining",
                areaCode = areaCode,
                contentTypeId = 76
            )
        }
        if (response.response.header.resultCode == "0000") {
            return response.response.body.items.item
        } else {
            // 에러 처리
            throw Exception(response.response.header.resultMsg)
        }
    }
}