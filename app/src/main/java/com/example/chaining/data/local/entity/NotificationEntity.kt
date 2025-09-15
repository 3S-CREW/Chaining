package com.example.chaining.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.chaining.domain.model.UserSummary

@Entity(tableName = "notification_table")
data class NotificationEntity(
    // 알림 ID
    @PrimaryKey val id: String = "",
    // 알림 종류
    val type: String = "",
    // 관련 모집글
    val postId: String? = null,
    // 지원서일 경우 지원서 ID
    val applicationId: String? = null,
    // 팔로우나 신청자 ID
    val sender: UserSummary? = UserSummary(),
    // 지원서 승인/거절 상태
    val status: String? = null,
    // 타임 스탬프
    val createdAt: Long = 0L,
    val closeAt: Long? = 0L,
    // 읽음 여부
    val isRead: Boolean = false,
    val uid: String,
)
