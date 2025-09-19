package com.example.chaining.ui.screen

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.R
import com.example.chaining.data.model.FilterState
import com.example.chaining.domain.model.RecruitPost
import com.example.chaining.ui.component.CardItem
import com.example.chaining.ui.component.FilterOptionsSheet
import com.example.chaining.ui.component.formatRemainingTime
import com.example.chaining.viewmodel.RecruitPostViewModel
import com.example.chaining.viewmodel.UserViewModel
import kotlinx.coroutines.launch

@Suppress("FunctionName")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    postViewModel: RecruitPostViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onViewPostClick: (postId: String) -> Unit = {},
    userViewModel: UserViewModel = hiltViewModel(),
    onCreatePostClick: () -> Unit,
    onJoinPostClick: (post: RecruitPost) -> Unit,
) {
    val context = LocalContext.current
    // 1. ViewModel로부터 필터링된 posts와 현재 filterState를 직접 구독
    val posts by postViewModel.posts.collectAsState()
    val filterState by postViewModel.filterState.collectAsState()
    val userState by userViewModel.user.collectAsState()

    // 1. Bottom Sheet의 상태를 제어하기 위한 변수들
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    // 2. '필터 적용' 함수는 ViewModel의 함수를 호출하는 역할만 함
    val applyFilters: (FilterState) -> Unit = { newFilterState ->
        postViewModel.applyFilters(newFilterState)
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) {
                showBottomSheet = false
            }
        }
    }
    val systemBarHorizontalPadding = WindowInsets.systemBars.asPaddingValues()
    val horizontalPaddingValue =
        systemBarHorizontalPadding.calculateStartPadding(LocalLayoutDirection.current) + 16.dp

    // 3. Bottom Sheet UI 구현
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false },
            sheetState = sheetState,
        ) {
            FilterOptionsSheet(
                currentFilterState = filterState,
                onApplyFilters = applyFilters,
                onClose = {
                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                        if (!sheetState.isVisible) {
                            showBottomSheet = false
                        }
                    }
                },
            )
        }
    }
    BackHandler(enabled = true) {
        onBackClick()
    }
    Scaffold(
        contentWindowInsets = WindowInsets.systemBars,
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
                // 뒤로가기 버튼
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White,
                    )
                }

                // 제목
                Text(
                    text = stringResource(id = R.string.community_title),
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center,
                )

                IconButton(onClick = { showBottomSheet = true }) {
                    Icon(
                        painter = painterResource(id = R.drawable.filter),
                        contentDescription = "필터",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White,
                    )
                }
            }
        },
        containerColor = Color(0xFFF3F6FF),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(top = innerPadding.calculateTopPadding())
                    .padding(bottom = 0.dp)
                    .padding(horizontal = horizontalPaddingValue)
                    .verticalScroll(rememberScrollState()),
        ) {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                // 새로 만든 CommunityActionButton 호출
                CommunityActionButton(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.post,
                    text = stringResource(id = R.string.community_write_post),
                    onClick = onCreatePostClick,
                )

                // 새로 만든 CommunityActionButton 호출
                CommunityActionButton(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.reload,
                    text = stringResource(id = R.string.community_refresh),
                    onClick = { postViewModel.refreshPosts() },
                )
            }
            if (posts.isEmpty()) {
                // 데이터가 없을 때
                Text(
                    text = stringResource(id = R.string.community_no_posts),
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .padding(top = 50.dp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center,
                )
            } else {
                posts.forEach { post ->
                    val isLiked = userState?.likedPosts?.get(post.postId) == true
                    val hasApplied =
                        userState?.applications?.values?.any { it.postId == post.postId } == true
                    Log.d("CardItemCheck", "게시글 ID: ${post.postId}, 작성자 정보: ${post.owner}")
                    CardItem(
                        onClick = { onViewPostClick(post.postId) },
                        type = "모집글",
                        recruitPost = post,
                        isLiked = isLiked,
                        remainingTime =
                            formatRemainingTime(
                                context,
                                post.closeAt - System.currentTimeMillis(),
                            ),
                        onLeftButtonClick = { onJoinPostClick(post) },
                        onRightButtonClick = { userViewModel.toggleLike(post.postId) },
                        currentUserId = userState?.id,
                        hasApplied = hasApplied,
                    )
                }
            }
        }
    }
}

@Suppress("FunctionName")
@Composable
fun CommunityActionButton(
    modifier: Modifier = Modifier,
    iconRes: Int,
    text: String,
    onClick: () -> Unit,
) {
    Button(
        onClick = onClick,
        modifier = modifier,
        shape = RoundedCornerShape(30.dp),
        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF7282B4)),
        // 버튼 내부 컨텐츠의 좌우 여백을 조절
        contentPadding = PaddingValues(horizontal = 8.dp, vertical = 6.dp),
    ) {
        Icon(
            painter = painterResource(id = iconRes),
            contentDescription = text,
            tint = Color.White,
            modifier = Modifier.size(22.dp),
        )
        Spacer(modifier = Modifier.width(10.dp))
        Text(
            text = text,
            color = Color.White,
            fontWeight = FontWeight.SemiBold,
            fontSize = 16.sp,
        )
    }
}
