package com.example.chaining.ui.component

import android.content.Context
import com.example.chaining.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatRemainingTime(
    context: Context,
    remainingMillis: Long,
): String {
    if (remainingMillis <= 0) {
        return context.getString(R.string.time_closed)
    }
    val totalMinutes = remainingMillis / 1000 / 60
    val days = (totalMinutes / (60 * 24)).toInt()
    val hours = ((totalMinutes % (60 * 24)) / 60).toInt()
    val minutes = (totalMinutes % 60).toInt()

    val resources = context.resources
    val parts = mutableListOf<String>()

    if (days > 0) {
        parts.add(resources.getQuantityString(R.plurals.time_unit_days, days, days))
    }
    if (hours > 0) {
        parts.add(resources.getQuantityString(R.plurals.time_unit_hours, hours, hours))
    }
    if (minutes > 0 || parts.isEmpty()) {
        parts.add(resources.getQuantityString(R.plurals.time_unit_minutes, minutes, minutes))
    }

    return parts.joinToString(" ")
}

fun formatDate(timestamp: Long): String {
    val date = Date(timestamp)
    val format = SimpleDateFormat("yyyy.MM.dd", Locale.getDefault())
    return format.format(date)
}
