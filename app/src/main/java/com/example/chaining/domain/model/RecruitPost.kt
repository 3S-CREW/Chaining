package com.example.chaining.domain.model

@kotlinx.serialization.Serializable
data class RecruitPost(
    val id: String = "",
    val title: String = "",
    val travelStyle: String = "",             // 선호 여행지 스타일
    val travelDate: String = "",              // 여행 일자
    val withCar: Boolean = false,             // 자차 여부
    val deadline: String = "",                // 모집 마감일
    val preferredLanguages: List<LanguagePref> = emptyList(), // 선호하는 언어 정보
    val content: String = "",                 // 모집글 내용
    val createdAt: Long = 0L,                 // 작성 시각
    val owner: UserSummary = UserSummary(),   // 작성자 프로필 (간단 정보)
    val applicants: List<Application> = emptyList() // 지원자 리스트
)

@kotlinx.serialization.Serializable
data class UserSummary(   // 간단 버전 (닉네임/사진 정도만)
    val id: String = "",
    val nickname: String = "",
    val profileImageUrl: String = ""
)