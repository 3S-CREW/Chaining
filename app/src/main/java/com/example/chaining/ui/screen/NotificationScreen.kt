package com.example.chaining.ui.notification

import android.widget.Toast
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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.R
import com.example.chaining.domain.model.Notification
import com.example.chaining.ui.component.CardItem
import com.example.chaining.ui.component.FollowNotificationItem
import com.example.chaining.ui.component.formatRemainingTime
import com.example.chaining.ui.screen.LightGrayBackground
import com.example.chaining.ui.screen.PrimaryBlue
import com.example.chaining.viewmodel.ApplicationViewModel
import com.example.chaining.viewmodel.NotificationEvent
import com.example.chaining.viewmodel.NotificationViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Suppress("FunctionName")
@Composable
fun NotificationScreen(
    viewModel: NotificationViewModel = hiltViewModel(),
    onViewApplyClick: (String) -> Unit,
) {
    val notifications by viewModel.notifications.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val errorMessage by viewModel.errorMessage.collectAsState()

    val eventFlow = viewModel.event
    val context = LocalContext.current

    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabTitles =
        listOf(
            stringResource(id = R.string.alarm_follow),
            stringResource(id = R.string.alarm_apply),
        )

    LaunchedEffect(Unit) {
        eventFlow.collect { event ->
            when (event) {
                is NotificationEvent.NavigateToApplication -> {
                    onViewApplyClick(event.applicationId)
                }

                is NotificationEvent.ShowToast -> {
                    Toast.makeText(context, event.message, Toast.LENGTH_SHORT).show()
                }

                NotificationEvent.Refresh -> {
                    // 필요하면 새로고침 처리
                }
            }
        }
    }

    // 알림 타입별 필터링
    val filteredNotifications =
        when (selectedTabIndex) {
            0 -> notifications.filter { it.type.equals("follow", ignoreCase = true) }
            1 -> notifications.filter { it.type.equals("application", ignoreCase = true) }
            else -> emptyList()
        }

    Scaffold(
        containerColor = LightGrayBackground,
        topBar = {
            Column(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .background(LightGrayBackground),
            ) {
                Text(
                    text = stringResource(id = R.string.alarm_title),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                    textAlign = TextAlign.Center,
                )

                TabRow(
                    selectedTabIndex = selectedTabIndex,
                    containerColor = LightGrayBackground,
                    contentColor = PrimaryBlue,
                    modifier = Modifier.fillMaxWidth(),
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                            color = PrimaryBlue,
                        )
                    },
                ) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index },
                            text = { Text(title, fontSize = 14.sp) },
                            selectedContentColor = PrimaryBlue,
                            unselectedContentColor = Color.Gray,
                        )
                    }
                }
            }
        },
    ) { innerPadding ->
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .background(LightGrayBackground),
        ) {
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                errorMessage != null -> {
                    Text(
                        text = "오류 발생: $errorMessage",
                        color = Color.Red,
                        modifier = Modifier.align(Alignment.Center),
                    )
                }

                filteredNotifications.isEmpty() -> {
                    Text(
                        text = stringResource(id = R.string.alarm_apply_text_two),
                        modifier = Modifier.align(Alignment.Center),
                        fontSize = 16.sp,
                    )
                }

                else -> {
                    LazyColumn(
                        modifier =
                            Modifier
                                .fillMaxSize()
                                .padding(horizontal = 16.dp, vertical = 8.dp),
                    ) {
                        items(filteredNotifications) { notification ->
                            NotificationItem(
                                notification = notification,
                            )
                        }
                    }
                }
            }
        }
    }
}

@Suppress("FunctionName")
@Composable
fun NotificationItem(
    notification: Notification,
    viewModel: NotificationViewModel = hiltViewModel(),
    applicationViewModel: ApplicationViewModel = hiltViewModel(),
) {
    val context = LocalContext.current
    val formattedDate =
        remember(notification.createdAt) {
            val date = Date(notification.createdAt)
            SimpleDateFormat("yyyy.MM.dd HH:mm", Locale.getDefault()).format(date)
        }

    when (notification.type) {
        "follow" -> {
            FollowNotificationItem(
                name =
                    notification.sender?.nickname
                        ?: stringResource(id = R.string.community_unknown),
                timestamp = formattedDate,
                imageUrl = notification.sender?.profileImageUrl?.takeIf { it.isNotEmpty() } ?: "",
            )
        }

        "application" -> {
            // Application 데이터를 StateFlow로 구독
            val application by applicationViewModel.application.collectAsState()

            // notification.applicationId로 데이터 로드
            LaunchedEffect(notification.applicationId) {
                notification.applicationId?.let { applicationViewModel.fetchApplication(it) }
            }

            CardItem(
                onClick = {
                    notification.applicationId?.let { id ->
                        viewModel.onApplicationClick(id)
                    }
                },
                type = "지원서",
                // Notification -> Application 매핑 필요
                application = application,
                remainingTime =
                    formatRemainingTime(
                        context,
                        notification.closeAt?.minus(System.currentTimeMillis()) ?: 0L,
                    ),
                onLeftButtonClick = {
                    application?.let { apply ->
                        applicationViewModel.updateStatus(
                            application = apply,
                            value = "APPROVED",
                        )
                    }
                },
                onRightButtonClick = {
                    application?.let { apply ->
                        applicationViewModel.updateStatus(
                            application = apply,
                            value = "REJECTED",
                        )
                    }
                },
            )
        }

        else -> {
            // 기타 알림 처리
            Card(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp),
                shape = RoundedCornerShape(12.dp),
                colors =
                    CardDefaults.cardColors(
                        containerColor =
                            if (notification.isRead) {
                                MaterialTheme.colorScheme.surface
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            },
                    ),
                elevation = CardDefaults.cardElevation(2.dp),
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "알림",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "알림을 확인하세요.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            text = "${stringResource(id = R.string.post_writer)}: ${
                                notification.sender ?: stringResource(
                                    id = R.string.community_unknown,
                                )
                            }",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Text(
                            text = formattedDate,
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
    }
}
