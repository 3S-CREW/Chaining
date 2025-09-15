package com.example.chaining.ui.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.chaining.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("FunctionName")
@Composable
fun DatePickerFieldToModal(
    modifier: Modifier = Modifier,
    label: String,
    selectedDate: Long?,
    onDateSelected: (Long?) -> Unit,
) {
    var showModal by remember { mutableStateOf(false) }

    // 날짜 포맷 변환
    val formattedDate = selectedDate?.let { convertMillisToDate(it) } ?: ""

    Box(
        modifier =
            modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 1.dp,
                    color =
                        if (showModal) {
                            Color(0xFF4285F4)
                        } else {
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                        },
                    shape = RoundedCornerShape(16.dp),
                )
                .clickable { showModal = true }
                .padding(horizontal = 16.dp, vertical = 14.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column {
                Text(
                    text = label,
                    style =
                        MaterialTheme.typography.labelMedium.copy(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        ),
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = if (formattedDate.isEmpty()) "YYYY/MM/DD" else formattedDate,
                    style =
                        MaterialTheme.typography.bodyLarge.copy(
                            color =
                                if (formattedDate.isEmpty()) {
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                },
                        ),
                )
            }

            Icon(
                imageVector = Icons.Default.DateRange,
                contentDescription = stringResource(id = R.string.datepicker_icon_description),
                tint = Color(0xFF4285F4),
                modifier = Modifier.size(24.dp),
            )
        }
    }

    // 모달
    if (showModal) {
        DatePickerModal(
            onDateSelected = {
                onDateSelected(it)
            },
            onDismiss = { showModal = false },
        )
    }
}

@Suppress("FunctionName")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DatePickerModal(
    onDateSelected: (Long?) -> Unit,
    onDismiss: () -> Unit,
) {
    val datePickerState = rememberDatePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        colors =
            DatePickerDefaults.colors(
                containerColor = Color(0xFFFEFEFE),
            ),
        confirmButton = {
            TextButton(
                onClick = {
                    onDateSelected(datePickerState.selectedDateMillis)
                    onDismiss()
                },
            ) {
                Text(text = stringResource(id = R.string.choose), color = Color(0xFF4285F4))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(text = stringResource(id = R.string.mypage_cancel), color = Color(0xFF637387))
            }
        },
    ) {
        DatePicker(
            colors =
                DatePickerDefaults.colors(
                    containerColor = Color(0xFFFEFEFE),
                    selectedDayContainerColor = Color(0xFF4285F4),
                    selectedDayContentColor = Color.White,
                    todayDateBorderColor = Color(0xFF4285F4),
                    todayContentColor = Color(0xFF4285F4),
                    dayContentColor = Color.Black,
                    weekdayContentColor = Color.Gray,
                ),
            state = datePickerState,
        )
    }
}

fun convertMillisToDate(millis: Long): String {
    val formatter = SimpleDateFormat("yyyy/MM/dd", Locale.getDefault())
    return formatter.format(Date(millis))
}
