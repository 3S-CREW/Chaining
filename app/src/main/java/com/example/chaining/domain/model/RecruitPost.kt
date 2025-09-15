package com.example.chaining.domain.model

import com.google.firebase.database.PropertyName

@kotlinx.serialization.Serializable
data class RecruitPost(
    val postId: String = "",
    // 제목
    val title: String = "",
    // 선호 여행지 스타일
    val preferredDestinations: String = "",
    // 선호 여행지 or 장소
    val preferredLocations: String = "",
    // 여행 일자
    val tourAt: Long = 0L,
    // 자차 여부
    val hasCar: String = "",
    // 모집 마감일
    val closeAt: Long = 0L,
    // 선호하는 언어 정보
    val preferredLanguages: Map<String, LanguagePref> = emptyMap(),
    // 모집글 내용
    val content: String = "",
    // 작성 시각
    val createdAt: Long = 0L,
    // 카톡 오픈채팅 링크
    val kakaoOpenChatUrl: String = "",
    // 작성자 프로필 (간단 정보)
    val owner: UserSummary = UserSummary(),
    // 지원자 리스트
    val applications: Map<String, Application> = emptyMap(),
    // 삭제 여부
    @get:PropertyName("isDeleted")
    val isDeleted: Boolean = false,
    // 관심을 누른 사람들의 uid
    val whoLiked: Map<String, Boolean> = emptyMap(),
)

@kotlinx.serialization.Serializable
// 간단 버전 (닉네임/사진 정도만)
data class UserSummary(
    val id: String = "",
    val nickname: String = "",
    val profileImageUrl: String = "",
    val country: String = "",
)
