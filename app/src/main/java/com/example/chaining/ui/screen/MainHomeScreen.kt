package com.example.chaining.ui.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.chaining.R
import com.example.chaining.domain.model.Notification
import com.example.chaining.ui.notification.NotificationItem
import com.example.chaining.viewmodel.NotificationEvent
import com.example.chaining.viewmodel.NotificationViewModel
import com.example.chaining.viewmodel.UserViewModel

// OptIn annotation for using experimental Material 3 APIs
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainHomeScreen(
    onBackClick: () -> Unit,
    onMainHomeClick: () -> Unit,
    notificationViewModel: NotificationViewModel = hiltViewModel(),
    onMyPageClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onFeedClick: () -> Unit,
    onNotificationClick: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
    onViewApplyClick: (String) -> Unit

) {

    val eventFlow = notificationViewModel.event
    val context = LocalContext.current


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
    val userState by userViewModel.user.collectAsState()
    BackHandler(enabled = true) {
        onBackClick()
    }
    val notifications by notificationViewModel.notifications.collectAsState()
    val isLoading by notificationViewModel.isLoading.collectAsState()

    val recentApplication: Notification? = notifications
        .filter { it.type == "application" }
        .sortedByDescending { it.createdAt }
        .firstOrNull()

    val recentFollows = notifications
        .filter { it.type == "follow" }
        .sortedByDescending { it.createdAt }
        .take(3)

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(60.dp)
                    .background(Color(0xFFF3F6FF))
                    .padding(top = 4.dp)
                    .padding(horizontal = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Spacer(modifier = Modifier.width(40.dp))
                Text(
                    text = "Chaining",
                    modifier = Modifier.weight(1f),
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                ProfileImageWithStatus(
                    model = userState?.profileImageUrl,
                    isOnline = true,
                    onMyPageClick = onMyPageClick
                )
            }
        },
        bottomBar = {
            AppBottomNavigation(selectedTab = "Home", onTestClick = { menu ->
                when (menu) {
                    "Home" -> onMainHomeClick()
                    "Community" -> onCommunityClick()
                    "Notification" -> onNotificationClick()
                    "Feed" -> onFeedClick()
                }
            })
        },
    ) { innerPadding ->
        // 중앙 콘텐츠 구현 (환영 메시지, 매칭 카드, 팔로우 목록 등)
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .background(Color(0xFFF3F6FF))
        ) {
            Text(
                text = stringResource(id = R.string.welcome_message, userState?.nickname ?: "쳉"),
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier
                    .padding(top = 16.dp)
                    .padding(horizontal = 32.dp)
            )
            Text(
                text = stringResource(id = R.string.recent_apply),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 32.dp)
            )

            if (isLoading) {
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (recentApplication == null) {
                Text(
                    text = stringResource(id = R.string.no_recent_apply),
                    fontSize = 14.sp,
                    color = Color.Gray,
                    modifier = Modifier
                        .padding(horizontal = 32.dp, vertical = 16.dp)
                )
            } else {
                Row(
                    modifier = Modifier
                        .padding(horizontal = 24.dp)
                ) {
                    NotificationItem(notification = recentApplication)
                }

            }
            Text(
                text = stringResource(id = R.string.recent_follow),
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color.Gray,
                modifier = Modifier
                    .padding(top = 24.dp)
                    .padding(horizontal = 32.dp)
            )

            recentFollows.forEach { notification ->
                NotificationItem(notification = notification)
            }
        }
    }
}

@Composable
fun AppBottomNavigation(
    selectedTab: String,
    onTestClick: (String) -> Unit,
) { // "selectedTab" 파라미터 추가

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        shadowElevation = 12.dp,
        color = Color(0xFFF3F6FF)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // if문을 사용해 선택된 탭에 따라 다른 아이콘을 표시
            val homeIcon = if (selectedTab == "Home") R.drawable.selected_home else R.drawable.home
            val peopleIcon =
                if (selectedTab == "Community") R.drawable.selected_people else R.drawable.people
            val searchIcon =
                if (selectedTab == "Feed") R.drawable.selected_search else R.drawable.search
            val alarmIcon =
                if (selectedTab == "Notification") R.drawable.selected_alarm else R.drawable.alarm

            CustomIconButton(
                onClick = { onTestClick("Home") },
                iconRes = homeIcon,
                description = "메인 홈"
            )
            CustomIconButton(
                onClick = { onTestClick("Community") },
                iconRes = peopleIcon,
                description = "매칭"
            )
            CustomIconButton(
                onClick = { onTestClick("Feed") },
                iconRes = searchIcon,
                description = "피드"
            )
            CustomIconButton(
                onClick = { onTestClick("Notification") },
                iconRes = alarmIcon,
                description = "알림"
            )
        }
    }
}

@Composable
private fun CustomIconButton(
    onClick: () -> Unit,
    iconRes: Int,
    description: String
) {
    // 1. 버튼의 상호작용 상태를 추적하기 위한 interactionSource
    val interactionSource = remember { MutableInteractionSource() }

    // 2. interactionSource를 통해 현재 '눌려있는지' 여부를 Boolean 값으로 가져옴
    val isPressed by interactionSource.collectIsPressedAsState()

    // 3. isPressed 값에 따라 scale 값을 0.9f 또는 1f로 애니메이션
    val scale by animateFloatAsState(targetValue = if (isPressed) 0.9f else 1f)
    Box(
        modifier = Modifier
            .clickable(
                onClick = onClick,
                // 리플 효과를 없애기 위한 핵심 코드
                indication = null,
                interactionSource = interactionSource
            )
            // 버튼의 터치 영역을 적절히 확보하기 위한 패딩
            .padding(10.dp),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = description,
            modifier = Modifier
                .size(30.dp)
                // 4. 애니메이션으로 변경되는 scale 값을 아이콘에 적용
                .graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                }
        )
    }
}

@Composable
fun ProfileImageWithStatus(
    model: Any? = null,
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    onMyPageClick: () -> Unit
) {
    // Box를 사용해 이미지와 상태 점을 겹치게 만듭니다.
    Box(
        modifier = modifier
            .size(40.dp)
            .clickable {
                onMyPageClick()
            }
    ) {
        AsyncImage(
            model = model,
            contentDescription = "마이페이지로 이동",
            contentScale = ContentScale.Crop,
            placeholder = painterResource(R.drawable.test_profile),
            error = painterResource(R.drawable.test_profile),
            modifier = Modifier
                .matchParentSize() // 부모(Box) 크기에 맞춤
                .clip(RoundedCornerShape(15.dp)) // 이미지를 원형으로 자름
        )

        // 온라인 상태를 표시하는 점
        if (isOnline) {
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .align(Alignment.BottomEnd) // 오른쪽 아래에 배치
                    .background(Color(0xFF00C853), CircleShape) // 초록색 배경
                    .border(width = 1.5.dp, color = Color.White, shape = CircleShape) // 흰색 테두리
            )
        }
    }
}