package com.example.chaining.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_table")
data class Notification(
    @PrimaryKey val id: String = "",            // 알림 ID
    val type: String = "",          // 알림 종류
    val postId: String? = null,     // 관련 모집글
    val applicationId: String? = null, // 지원서일 경우 지원서 ID
    val senderId: String? = null,   // 팔로우나 신청자 ID
    val status: String? = null,     // 지원서 승인/거절 상태
    val createdAt: Long = 0L,       // 타임 스탬프
    val isRead: Boolean = false,    // 읽음 여부
    val uid: String
)