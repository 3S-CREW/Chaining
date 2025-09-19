package com.example.chaining.domain.model

import com.google.firebase.database.PropertyName

@kotlinx.serialization.Serializable
data class Application(
    val applicationId: String = "",
    // 어떤 모집글에 지원했는지
    val postId: String = "",
    // 모집글 작성자 정보
    val owner: UserSummary = UserSummary(),
    // 모집글 제목 (캐싱)
    val recruitPostTitle: String = "",
    // 지원자 간단 프로필
    val applicant: User = User(),
    // 자기 소개
    val introduction: String = "",
    val createdAt: Long = 0L,
    var status: String = "PENDING",
    // Soft Delete 플래그 추가
    @get:PropertyName("isDeleted")
    val isDeleted: Boolean = false,
    val closeAt: Long = 0L,
)
