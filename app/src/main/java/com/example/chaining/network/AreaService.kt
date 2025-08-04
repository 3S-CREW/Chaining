package com.example.chaining.network

import com.example.chaining.data.model.AreaCodeResponse
import retrofit2.http.GET
import retrofit2.http.Query

// https://apis.data.go.kr/B551011/KorService2/ldongCode2?serviceKey=DATA_OPEN_API_KEY&numOfRows=1000&lDongListYn=Y&pageNo=1&lDongRegnCd=11&MobileOS=AND&MobileApp=AppTest

interface AreaService {
    @GET("ldongCode2")
    suspend fun getAreaCodes(
        @Query("serviceKey", encoded = true) serviceKey: String, // API 키
        @Query("lDongRegnCd") lDongRegnCd: Int = 11, // 지역 코드
        @Query("PageNo") PageNo: Int = 1, // 불러올 페이지 수
        @Query("numOfRows") numOfRows: Int = 1000, // 불러올 행의 수
        @Query("lDongListYn") lDongListYn: String = "Y", // 목록조회 여부
        @Query("MobileOS") mobileOS: String = "AND", // OS 종류
        @Query("MobileApp") mobileAPP: String = "Chaining",  // 앱 명
        @Query("_type") type: String = "json" // 데이터 타입
    ): AreaCodeResponse

}