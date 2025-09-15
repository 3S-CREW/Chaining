package com.example.chaining.data.model

import com.google.gson.annotations.SerializedName

// API 전체 응답 구조
data class FeedApiResponse(
    val response: FeedResponse,
)

// 응답의 헤더와 본문
data class FeedResponse(
    val header: FeedResponseHeader,
    val body: FeedResponseBody,
)

// 응답 결과 코드와 메시지
data class FeedResponseHeader(
    val resultCode: String,
    val resultMsg: String,
)

// 실제 데이터 목록과 페이지 정보
data class FeedResponseBody(
    val items: FeedApiItems,
    val numOfRows: Int,
    val pageNo: Int,
    val totalCount: Int,
)

// 관광지 정보 리스트
data class FeedApiItems(
    val item: List<TourItem>,
)

// 피드 UI에 필요한 관광지 개별 정보
data class TourItem(
    // 주소
    @SerializedName("addr1") val address: String,
    // 관광지명
    @SerializedName("title") val title: String,
    // 대표 이미지 URL
    @SerializedName("firstimage") val imageUrl: String?,
    // 콘텐츠 ID
    @SerializedName("contentid") val contentId: String,
)
