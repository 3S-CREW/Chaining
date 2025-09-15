package com.example.chaining.data.model

data class FilterState(
    // 여행지 스타일 (예: 액티비티, 힐링, 문화)
    val travelStyle: String? = null,
    // 여행지 (예: 서울, 제주)
    val travelLocation: String? = null,
    // 언어 (예: 영어, 중국어)
    val language: String? = null,
    // 언어 레벨 (예: 1, 2, 3) - null은 '상관 없음'
    val languageLevel: Int? = null,
    // 정렬 방식 ("latest", "interest", "deadline")
    val sortBy: String = "latest",
)
