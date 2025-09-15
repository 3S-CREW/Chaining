package com.example.chaining.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.R
import com.example.chaining.domain.model.RecruitPost
import com.example.chaining.ui.component.OwnerProfile
import com.example.chaining.ui.component.SaveButton
import com.example.chaining.ui.component.formatDate
import com.example.chaining.viewmodel.RecruitPostViewModel
import com.example.chaining.viewmodel.UserViewModel

@Suppress("FunctionName")
@Composable
fun ViewPostScreen(
    userViewModel: UserViewModel = hiltViewModel(),
    postViewModel: RecruitPostViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onJoinPostClick: (post: RecruitPost) -> Unit = {},
    onEditClick: (postId: String) -> Unit = {},
    onApplicationListClick: (postId: String) -> Unit = {},
    onMainHomeClick: () -> Unit,
    onCommunityClick: () -> Unit,
    onFeedClick: () -> Unit,
    onNotificationClick: () -> Unit,
) {
    val userState by userViewModel.user.collectAsState()
    val post by postViewModel.post.collectAsState()
    val currentPost = post

    // post가 null이면 로딩 UI 표시
    if (currentPost == null) {
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text("Loading...", fontSize = 18.sp)
        }
        return
    }

    Scaffold(
        topBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(64.dp)
                        .clip(RoundedCornerShape(bottomEnd = 20.dp))
                        .background(Color(0xFF4A526A)),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White,
                    )
                }

                OwnerProfile(owner = currentPost.owner, where = "모집글 상세보기", type = "상세 보기")

                // 제목을 완벽한 중앙에 맞추기 위한 빈 공간
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        bottomBar = {
            AppBottomNavigation(selectedTab = "Community", onTestClick = { menu ->
                when (menu) {
                    "Home" -> onMainHomeClick()
                    "Community" -> onCommunityClick()
                    "Notification" -> onNotificationClick()
                    "Feed" -> onFeedClick()
                }
            })
        },
        containerColor = Color(0xFFF3F6FF),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(start = 10.dp, end = 10.dp),
        ) {
            // 스크롤이 필요한 콘텐츠 영역 (Card)
            Card(
                // weight(1f)를 주어 남는 공간을 모두 차지하게 함
                modifier =
                    Modifier
                        .padding(12.dp)
                        .weight(1f),
                colors = CardDefaults.cardColors(containerColor = Color(0xFFF3F6FF)),
            ) {
                // 카드 내부는 이전과 동일하게 스크롤 가능
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .verticalScroll(rememberScrollState()),
                ) {
                    Spacer(modifier = Modifier.height(30.dp))

                    Text(
                        text = currentPost.title,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A526A),
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    SetInfo(
                        icon = R.drawable.global,
                        title = stringResource(id = R.string.post_style),
                        content = currentPost.preferredDestinations,
                    )

                    SetInfo(
                        icon = R.drawable.calendar,
                        title = stringResource(id = R.string.post_date),
                        content = formatDate(currentPost.tourAt),
                    )

                    SetInfo(
                        icon = R.drawable.car,
                        title = stringResource(id = R.string.post_car),
                        content = currentPost.hasCar,
                    )

                    SetInfo(
                        icon = R.drawable.timer,
                        title = stringResource(id = R.string.post_finish),
                        content = formatDate(currentPost.closeAt),
                    )

                    SetInfo(
                        icon = R.drawable.language,
                        title = stringResource(id = R.string.post_lang),
                        content = currentPost.preferredLanguages.values.joinToString { it.language },
                    )

                    SetInfo(
                        icon = R.drawable.level,
                        title = stringResource(id = R.string.post_lang_level),
                        content = currentPost.preferredLanguages.values.joinToString { it.level.toString() },
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    HorizontalDivider(
                        thickness = 1.dp,
                        color = Color.LightGray,
                    )

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = currentPost.content,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        color = Color(0xFF4A526A),
                    )
                }
            }

            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .padding(bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                if (userState?.id == currentPost.owner.id) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                    ) {
                        SaveButton(
                            onSave = { onEditClick(currentPost.postId) },
                            text = stringResource(id = R.string.post_edit),
                            modifier = Modifier.weight(1f),
                        )
                        SaveButton(
                            onSave = { onApplicationListClick(currentPost.postId) },
                            text = stringResource(id = R.string.post_look),
                            modifier = Modifier.weight(1f),
                        )
                    }

                    //                SaveButton(onSave = { /*TODO*/ }, text = "삭제")
                } else {
                    SaveButton(
                        onSave = { onJoinPostClick(currentPost) },
                        text = stringResource(id = R.string.post_button),
                    )
                    //                SaveButton(onSave = { /*TODO*/ }, text = "숨김")
                }
            }
        }
    }
}

@Suppress("FunctionName")
@Composable
fun SetInfo(
    icon: Int,
    title: String,
    content: String,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "정보 아이콘",
            modifier = Modifier.size(25.dp),
            tint = Color(0xFF4A526A),
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = title,
            modifier = Modifier.weight(2f),
            color = Color(0xFF4A526A),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = content,
            modifier = Modifier.weight(1f),
            color = Color(0xFF4A526A),
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}
