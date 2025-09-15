package com.example.chaining.domain.model

import com.google.firebase.database.PropertyName

@kotlinx.serialization.Serializable
data class User(
    // DB key (uid)
    val id: String = "",
    val nickname: String = "",
    val profileImageUrl: String = "",
    // 출신국
    val country: String = "",
    // 거주지
    val residence: String = "",
    // 선호 여행지
    val preferredDestinations: String = "",
    val preferredLanguages: Map<String, LanguagePref> = emptyMap(),
    // 모집/지원 현황 공개 여부
    @get:PropertyName("isPublic")
    val isPublic: Boolean = true,
    // 내가 모집한 글
    val recruitPosts: Map<String, RecruitPost> = emptyMap(),
    // 내가 지원한 글
    val applications: Map<String, Application> = emptyMap(),
    // 서버 타임스탬프
    val createdAt: Long = 0L,
    // Soft Delete 플래그 추가
    @get:PropertyName("isDeleted")
    val isDeleted: Boolean = false,
    // 관심글 postId
    val likedPosts: Map<String, Boolean> = emptyMap(),
    // 팔로잉
    val following: Map<String, UserSummary> = emptyMap(),
    // 팔로워
    val follower: Map<String, UserSummary> = emptyMap(),
)

@kotlinx.serialization.Serializable
data class LanguagePref(
    val language: String = "",
    // 0 ~ 10
    val level: Int = 0,
)
