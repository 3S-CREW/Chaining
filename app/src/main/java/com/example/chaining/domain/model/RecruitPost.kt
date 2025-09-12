package com.example.chaining.domain.model

import com.google.firebase.database.PropertyName

@kotlinx.serialization.Serializable
data class RecruitPost(
    val postId: String = "",
    val title: String = "",                                   // 제목
    val preferredDestinations: String = "",                   // 선호 여행지 스타일
    val preferredLocations: LocationPref = LocationPref(),    // 선호 여행지 or 장소
    val tourAt: Long = 0L,                                    // 여행 일자
    val hasCar: String = "",                                  // 자차 여부
    val closeAt: Long = 0L,                                   // 모집 마감일
    val preferredLanguages: Map<String, LanguagePref> = emptyMap(), // 선호하는 언어 정보
    val content: String = "",                                 // 모집글 내용
    val createdAt: Long = 0L,                                 // 작성 시각
    val kakaoOpenChatUrl: String = "",                        // 카톡 오픈채팅 링크
    val owner: UserSummary = UserSummary(),                   // 작성자 프로필 (간단 정보)
    val applications: Map<String, Application> = emptyMap(),        // 지원자 리스트
    @get:PropertyName("isDeleted")
    val isDeleted: Boolean = false,                            // 삭제 여부
    val whoLiked: Map<String, Boolean> = emptyMap()                    // 관심을 누른 사람들의 uid
)

@kotlinx.serialization.Serializable
data class UserSummary(   // 간단 버전 (닉네임/사진 정도만)
    val id: String = "",
    val nickname: String = "",
    val profileImageUrl: String = "",
    val country: String = ""
)

@kotlinx.serialization.Serializable
data class LocationPref(
    val type: String = "",
    val location: String = ""
)