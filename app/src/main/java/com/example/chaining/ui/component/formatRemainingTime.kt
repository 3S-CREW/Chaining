package com.example.chaining.ui.component

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatRemainingTime(remainingMillis: Long): String {
    if (remainingMillis <= 0) return "마감됨"

    val totalMinutes = remainingMillis / 1000 / 60
    val days = totalMinutes / (60 * 24)
    val hours = (totalMinutes % (60 * 24)) / 60
    val minutes = totalMinutes % 60

    return buildString {
        if (days > 0) append("${days}일 ")
        if (hours > 0) append("${hours}시간 ")
        append("${minutes}분")
    }
}

fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    return format.format(date)
}