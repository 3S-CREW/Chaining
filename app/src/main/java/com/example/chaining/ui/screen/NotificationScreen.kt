package com.example.chaining.ui.notification

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.domain.model.Notification
import com.example.chaining.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel()
) {
    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabTitles = listOf("전체", "팔로우", "지원서")

    // 알림 타입별 필터링
    val filteredNotifications = when (selectedTabIndex) {
        1 -> notifications.filter { it.type == "FOLLOW" }
        2 -> notifications.filter { it.type == "APPLICATION" }
        else -> notifications
    }

    Scaffold(
        topBar = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                Text(
                    text = "알림",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontSize = 14.sp) }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                errorMessage != null -> {
                    Text(
                        text = "오류 발생: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }

                filteredNotifications.isEmpty() -> {
                    Text(
                        text = "알림이 없습니다.",
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 16.sp
                    )
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    ) {
                        items(filteredNotifications) { notification ->
                            NotificationItem(notification = notification)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun NotificationItem(notification: Notification) {
    val formattedDate = remember(notification.createdAt) {
        val date = Date(notification.createdAt)
        SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(date)
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (notification.isRead)
                MaterialTheme.colorScheme.surface
            else
                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
        ),
        elevation = CardDefaults.cardElevation(2.dp)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // 알림 제목
            Text(
                text = when (notification.type) {
                    "FOLLOW" -> "새 팔로우 알림"
                    "APPLICATION" -> "지원서 알림"
                    else -> "기타 알림"
                },
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Spacer(modifier = Modifier.height(4.dp))

            // 알림 상세 내용
            Text(
                text = when (notification.type) {
                    "FOLLOW" -> "${notification.senderId ?: "알 수 없음"}님이 회원님을 팔로우하기 시작했습니다."
                    "APPLICATION" -> when (notification.status) {
                        "APPROVED" -> "회원님의 모집글에 지원서가 승인되었습니다."
                        "REJECTED" -> "회원님의 모집글에 지원서가 거절되었습니다."
                        else -> "새로운 지원서가 접수되었습니다."
                    }

                    else -> "알림을 확인하세요."
                },
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )

            Spacer(modifier = Modifier.height(6.dp))

            // 하단 정보 (작성자 / 날짜)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "작성자: ${notification.senderId ?: "알 수 없음"}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(
                    text = formattedDate,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
