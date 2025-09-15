package com.example.chaining.domain.model

import com.google.firebase.database.PropertyName

data class Notification(
    // 알림 ID
    val id: String = "",
    // 알림 종류
    val type: String = "",
    // 관련 모집글
    val postId: String? = null,
    // 지원서일 경우 지원서 ID
    val applicationId: String? = null,
    // 팔로우나 신청자
    val sender: UserSummary? = UserSummary(),
    // 지원서 승인/거절 상태
    val status: String? = null,
    // 타임 스탬프
    val createdAt: Long = 0L,
    val closeAt: Long? = 0L,
    // 읽음 여부
    @get:PropertyName("isRead")
    val isRead: Boolean = false,
    val uid: String = "",
)
