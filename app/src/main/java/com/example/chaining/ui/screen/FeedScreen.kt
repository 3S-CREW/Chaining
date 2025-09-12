package com.example.chaining.ui.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.R
import com.example.chaining.ui.component.FeedItem
import com.example.chaining.viewmodel.FeedViewModel

@Composable
fun FeedScreen(
    onBackClick: () -> Unit,
    onMainHomeClick: () -> Unit,
    onFeedClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onNotificationClick: () -> Unit,
    feedViewModel: FeedViewModel = hiltViewModel()
) {
    // ViewModel의 randomizedFeedItems 상태를 구독하여 UI에 자동 반영
    val feedItems by feedViewModel.randomizedFeedItems.collectAsState()

    // 화면이 처음 로드될 때 API를 통해 관광 정보를 가져옵니다.
    LaunchedEffect(Unit) {
        feedViewModel.fetchTourItems() // 전국 데이터 로드
    }
    BackHandler(enabled = true) {
        onBackClick()
    }
    Scaffold(
        //  topBar에 로그인 제목을 넣습니다.
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(bottomEnd = 20.dp))
                    .background(Color(0xFF4A526A)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
                Text(
                    text = "피드",
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                IconButton(onClick = { feedViewModel.randomizeFeedItems() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.reload),
                        contentDescription = "새로고침",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }
            }
        },
        bottomBar = {
            AppBottomNavigation(selectedTab = "Feed", onTestClick = { menu ->
                when (menu) {
                    "Home" -> onMainHomeClick()
                    "Community" -> onCommunityClick()
                    "Notification" -> onNotificationClick()
                    "Feed" -> onFeedClick()
                }
            })
                    },
        containerColor = Color(0xFFF3F6FF)
    ) { innerPadding ->
        // feedItems의 상태에 따라 다른 UI 표시
        if (feedItems.isEmpty()) {
            // 데이터 로딩 중이거나 데이터가 없을 때
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator() // 로딩 인디케이터
            }
        } else {
            // LazyColumn을 사용하여 랜덤 3개의 피드를 표시
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                items(feedItems) { item ->
                    // Log.d("FeedScreen", "Title: ${item.title}, Image URL: ${item.imageUrl}")
                    FeedItem(
                        region = item.address.split(" ").getOrNull(0) ?: "지역",
                        place = item.title,
                        address = item.address,
                        imageUrl = item.imageUrl
                            ?: "https://your-placeholder-image-url.com/default.jpg",
                    )
                }
            }
        }
    }
}