package com.example.chaining.network

import com.example.chaining.data.model.FeedApiResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface FeedApiService {
    // 지역기반 관광정보 조회 API 엔드포인트
    @GET("areaBasedList1")
    suspend fun getAreaBasedList(
        @Query("serviceKey") serviceKey: String,
        @Query("numOfRows") numOfRows: Int = 10,  // 한 페이지 결과 수
        @Query("pageNo") pageNo: Int = 1,        // 페이지 번호
        @Query("MobileOS") mobileOS: String = "AND", // OS 구분 (안드로이드)
        @Query("MobileApp") mobileApp: String = "Chaining", // 앱 이름
        @Query("_type") type: String = "json",       // 응답 타입 (JSON)
        @Query("listYN") listYN: String = "Y",       // 목록 구분 (Y=목록)
        @Query("arrange") arrange: String = "O",     // 정렬 기준 (A=제목순, C=수정일순, D=생성일순)
        @Query("contentTypeId") contentTypeId: Int = 12, // 관광지 타입 (12=관광지)
        @Query("areaCode") areaCode: Int? = null     // 지역 코드 (생략 시 전국)
        // @Query("sigunguCode") sigunguCode: Int? = null, // 시군구 코드 (선택적)
        // @Query("cat1") cat1: String? = null, // 대분류 (선택적)
    ): FeedApiResponse
}