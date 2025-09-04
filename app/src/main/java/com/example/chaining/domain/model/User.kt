package com.example.chaining.domain.model

import com.google.firebase.database.PropertyName

@kotlinx.serialization.Serializable
data class User(
    val id: String = "",                          // DB key (uid)
    val nickname: String = "",
    val profileImageUrl: String = "",
    val country: String = "",                     // 출신국
    val residence: String = "",                   // 거주지
    val preferredDestinations: String = "",       // 선호 여행지
    val preferredLanguages: List<LanguagePref> = emptyList(), // 선호 언어 + 수준
    @get:PropertyName("isPublic")
    val isPublic: Boolean = true,                 // 모집/지원 현황 공개 여부
    val recruitPosts: Map<String, RecruitPost> = emptyMap(), // 내가 모집한 글
    val applications: Map<String, Application> = emptyMap(), // 내가 지원한 글
    val createdAt: Long = 0L,                     // 서버 타임스탬프
    @get:PropertyName("isDeleted")
    val isDeleted: Boolean = false,                // Soft Delete 플래그 추가
    val likedPosts: Map<String, Boolean> = emptyMap()     // 관심글 postId
)

@kotlinx.serialization.Serializable
data class LanguagePref(
    val language: String = "",
    val level: Int = 0   // 0 ~ 10
)