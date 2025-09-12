package com.example.chaining.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chaining.domain.model.Application
import com.example.chaining.domain.model.LanguagePref
import com.example.chaining.domain.model.RecruitPost
import com.example.chaining.domain.model.UserSummary

@Entity(tableName = "user_table")
data class UserEntity(
    @PrimaryKey val id: String = "",              // DB key (uid)
    val nickname: String = "",
    val profileImageUrl: String = "",
    val country: String = "",                     // 출신국
    val residence: String = "",                   // 거주지
    val preferredDestinations: String = "",       // 선호 여행지
    val preferredLanguages: Map<String, LanguagePref> = emptyMap(),
    val isPublic: Boolean = true,                 // 모집/지원 현황 공개 여부
    val recruitPosts: Map<String, RecruitPost> = emptyMap(), // 내가 모집한 글 (Post ID만 저장)
    val applications: Map<String, Application> = emptyMap(), // 내가 지원한 글 (Application ID만 저장)
    val createdAt: Long = 0L,                     // 서버 타임스탬프
    val isDeleted: Boolean = false,                // Soft Delete 플래그 추가
    val likedPosts: Map<String, Boolean> = emptyMap(),     // 관심글 postId
    val following: Map<String, UserSummary> = emptyMap(),
    val follower: Map<String, UserSummary> = emptyMap()
)