package com.example.chaining.domain.model

import com.google.firebase.database.PropertyName

data class Notification(
    val id: String = "",            // 알림 ID
    val type: String = "",          // 알림 종류
    val postId: String? = null,     // 관련 모집글
    val applicationId: String? = null, // 지원서일 경우 지원서 ID
    val sender: UserSummary? = UserSummary(),   // 팔로우나 신청자
    val status: String? = null,     // 지원서 승인/거절 상태
    val createdAt: Long = 0L,       // 타임 스탬프
    val closeAt: Long? = 0L,
    @get:PropertyName("isRead")
    val isRead: Boolean = false,    // 읽음 여부
    val uid: String = ""
)