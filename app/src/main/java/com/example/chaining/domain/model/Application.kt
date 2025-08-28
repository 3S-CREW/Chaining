package com.example.chaining.domain.model

@kotlinx.serialization.Serializable
data class Application(
    val applicationId: String = "",
    val postId: String = "",           // 어떤 모집글에 지원했는지
    val recruitPostTitle: String = "",        // 모집글 제목 (캐싱)
    val applicant: UserSummary = UserSummary(), // 지원자 간단 프로필
    val introduction: String = "",            // 자기 소개
    val createdAt: Long = 0L,
    var status: String = "PENDING",
    val isDeleted: Boolean = false                // Soft Delete 플래그 추가
)