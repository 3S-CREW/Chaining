package com.example.chaining.ui.screen

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
import com.example.chaining.domain.model.UserSummary
import com.example.chaining.ui.component.formatRemainingTime
import com.example.chaining.viewmodel.ApplicationViewModel
import com.example.chaining.viewmodel.RecruitPostViewModel
import com.example.chaining.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Suppress("FunctionName")
@Composable
fun ApplyScreen(
    onBackClick: () -> Unit = {},
    userViewModel: UserViewModel = hiltViewModel(),
    // My, Owner
    type: String,
    closeAt: Long,
    introduction: String?,
    applicationId: String?,
    applicationViewModel: ApplicationViewModel = hiltViewModel(),
    postViewModel: RecruitPostViewModel = hiltViewModel(),
    onNavigateHome: () -> Unit? = {},
) {
    val userState by userViewModel.user.collectAsState()
    val application by applicationViewModel.application.collectAsState()
    val post by postViewModel.post.collectAsState()
    val context = LocalContext.current
    var showResultDialog by remember { mutableStateOf(false) }

// 1. applicationId가 변경되면 application 정보를 가져오는 Effect
    LaunchedEffect(key1 = applicationId) {
        applicationId?.let { id ->
            val fetchedApplication = applicationViewModel.fetchApplication(id)
            if (fetchedApplication != null) {
                applicationViewModel.setApplication(fetchedApplication)
            }
        }
    }

// 2. application 정보가 성공적으로 로드되면(null이 아니게 되면) post 정보를 가져오는 Effect
    LaunchedEffect(key1 = application) {
        // application이 null이 아니고, 그 안의 postId도 null이 아닐 때만 실행
        application?.postId?.let { postId ->
            postViewModel.fetchPost(postId)
        }
    }

    LaunchedEffect(key1 = true) {
        userViewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }

        applicationViewModel.toastEvent.collectLatest { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
        }
    }

    // post가 null이면 로딩 UI 표시
    val isLoading = type != "My" && application == null

    if (isLoading) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    Scaffold(
        // 상단바 배경색을 직접 파란색으로 지정
        topBar = {
            Row(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        // 상단바의 기본 높이
                        .height(64.dp)
                        .background(Color(0xFF4285F4)),
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

                Text(
                    text =
                        if (type == "Owner") {
                            stringResource(id = R.string.view_application)
                        } else {
                            stringResource(id = R.string.apply_mine)
                        },
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                )
                // 제목을 완벽한 중앙에 맞추기 위한 빈 공간
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        // 전체 기본 배경은 흰색으로 둡니다.
        containerColor = Color.White,
    ) { innerPadding ->
        // Box를 사용해 파란 헤더와 흰색 콘텐츠를 겹치게 합니다.
        Box(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
        ) {
            // 곡선 효과가 있는 파란색 헤더
            Box(
                modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(bottomEndPercent = 50))
                        .background(Color(0xFF4285F4)),
            ) {
                // 타이머 텍스트를 담을 Column 추가
                Column(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            // 상단바와의 간격
                            .padding(top = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        stringResource(id = R.string.accept_or_reject),
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp,
                    )
                    val remainingMillis = closeAt.minus(System.currentTimeMillis())
                    val remainingTimeText = formatRemainingTime(context, remainingMillis)
                    Text(
                        text = stringResource(id = R.string.time_left, remainingTimeText),
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }

            // 스크롤되는 흰색 콘텐츠 영역
            Column(
                modifier =
                    Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
            ) {
                // 프로필 사진에 내용이 가려지지 않도록 공간 확보
                Spacer(modifier = Modifier.height(200.dp))

                // 상세 정보 콘텐츠 추가
                Column(
                    modifier = Modifier.padding(horizontal = 50.dp),
                    horizontalAlignment = Alignment.Start,
                ) {
                    Text(
                        text =
                            if (type == "Owner") {
                                application?.applicant?.nickname
                                    ?: stringResource(id = R.string.community_unknown)
                            } else {
                                userState?.nickname ?: stringResource(id = R.string.community_unknown)
                            },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A526A),
                    )
                    Text(
                        text =
                            if (type == "Owner") {
                                application?.applicant?.country
                                    ?: stringResource(id = R.string.community_unknown)
                            } else {
                                userState?.country ?: stringResource(id = R.string.community_unknown)
                            },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7282B4),
                        modifier = Modifier.padding(top = 4.dp),
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 언어 수준
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        // 이 부분만 왼쪽 정렬
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text =
                                if (type == "Owner") {
                                    val korean =
                                        application?.applicant?.preferredLanguages?.get("KOREAN")
                                    if (korean != null) {
                                        "${korean.language} 수준 : ${korean.level} / 10"
                                    } else {
                                        stringResource(id = R.string.unknown)
                                    }
                                } else {
                                    val korean = userState?.preferredLanguages?.get("KOREAN")
                                    if (korean != null) {
                                        "${korean.language} 수준 : ${korean.level} / 10"
                                    } else {
                                        stringResource(id = R.string.unknown)
                                    }
                                },
//                            text = stringResource(id = R.string.community_unknown),
                            color = Color(0xFF4A526A),
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text =
                                if (type == "Owner") {
                                    val english =
                                        application?.applicant?.preferredLanguages?.get("ENGLISH")
                                    if (english != null) {
                                        "${english.language} 수준 : ${english.level} / 10"
                                    } else {
                                        stringResource(id = R.string.unknown)
                                    }
                                } else {
                                    val english = userState?.preferredLanguages?.get("ENGLISH")
                                    if (english != null) {
                                        "${english.language} 수준 : ${english.level} / 10"
                                    } else {
                                        stringResource(id = R.string.unknown)
                                    }
                                },
//                            text = stringResource(id = R.string.community_unknown),
                            color = Color(0xFF4A526A),
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 자기 소개
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start,
                    ) {
                        Text(
                            text = stringResource(id = R.string.introduction_self),
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF7282B4),
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text =
                                if (type == "Owner" || (type == "My" && applicationId != null)) {
                                    application?.introduction
                                        ?: stringResource(id = R.string.community_unknown)
                                } else {
                                    introduction
                                        ?: stringResource(id = R.string.community_unknown)
                                },
                            color = Color(0xFF4A526A),
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))

                    if (type == "Owner") {
                        val hasStatus =
                            application?.status != "PENDING"
                        val isAuthor = application?.applicant?.id == userState?.id
                        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                            // 수락 버튼
                            Button(
                                onClick = {
                                    application?.let { apply ->
                                        applicationViewModel.updateStatus(
                                            application = apply,
                                            value = "APPROVED",
                                        )
                                    }
                                },
                                enabled = !hasStatus && !isAuthor,
                                modifier =
                                    Modifier
                                        .weight(1.5f)
                                        .height(50.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFF2C80FF),
                                        contentColor = Color.White,
                                    ),
                            ) {
                                Text(
                                    text = stringResource(id = R.string.application_yes),
                                    fontSize = 16.sp,
                                )
                            }

                            // 거절 버튼
                            Button(
                                onClick = {
                                    application?.let { apply ->
                                        applicationViewModel.updateStatus(
                                            application = apply,
                                            value = "REJECTED",
                                        )
                                    }
                                },
                                enabled = !hasStatus && !isAuthor,
                                modifier =
                                    Modifier
                                        .weight(1f)
                                        .height(50.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFF0F2F5),
                                        contentColor = Color.DarkGray,
                                    ),
                            ) {
                                Text(
                                    text = stringResource(id = R.string.application_no),
                                    fontSize = 16.sp,
                                )
                            }
                        }
                    } else {
                        if (applicationId != null) {
                            // 결과 버튼
                            Button(
                                onClick = { showResultDialog = true },
                                modifier =
                                    Modifier
                                        .fillMaxWidth()
                                        .height(50.dp),
                                shape = RoundedCornerShape(20.dp),
                                enabled = application?.status != "PENDING",
                                colors =
                                    ButtonDefaults.buttonColors(
                                        containerColor =
                                            if (application?.status == "PENDING") {
                                                Color(
                                                    0xFFF0F2F5,
                                                )
                                            } else {
                                                Color(0xFF2C80FF)
                                            },
                                        contentColor = Color.White,
                                    ),
                            ) {
                                Text(
                                    text = stringResource(id = R.string.myapply_filter_open),
                                    fontSize = 16.sp,
                                )
                            }
                        } else {
                            // 비어있음
                        }
                    }

                    // 하단 네비게이션 바와의 간격
                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
            // 결과 모달(Dialog)
            if (showResultDialog) {
                AlertDialog(
                    onDismissRequest = { showResultDialog = false },
                    title = {
                        Text(
                            text =
                                when (application?.status) {
                                    "APPROVED" -> stringResource(id = R.string.result_approved_title)
                                    "REJECTED" -> stringResource(id = R.string.result_rejected_title)
                                    else -> stringResource(id = R.string.result_pending_title)
                                },
                            fontWeight = FontWeight.Bold,
                        )
                    },
                    text = {
                        Text(
                            text =
                                when (application?.status) {
                                    "APPROVED" -> stringResource(id = R.string.result_approved_message)
                                    "REJECTED" -> stringResource(id = R.string.result_rejected_message)
                                    else -> stringResource(id = R.string.result_pending_message)
                                },
                        )
                    },
                    confirmButton = {
                        when (application?.status) {
                            "APPROVED" -> {
                                TextButton(
                                    onClick = {
                                        showResultDialog = false
                                        val chatUrl = post?.kakaoOpenChatUrl
                                        if (!chatUrl.isNullOrEmpty()) {
                                            val intent =
                                                Intent(Intent.ACTION_VIEW, Uri.parse(chatUrl))
                                            context.startActivity(intent)
                                        } else {
                                            Toast.makeText(
                                                context,
                                                context.getString(R.string.kakao_chat_url_not_exist),
                                                Toast.LENGTH_SHORT,
                                            ).show()
                                        }
                                    },
                                ) {
                                    Text(stringResource(id = R.string.move_to_kakao_chat))
                                }
                            }

                            "REJECTED" -> {
                                TextButton(
                                    onClick = {
                                        showResultDialog = false
                                        onNavigateHome()
                                    },
                                ) {
                                    Text(stringResource(id = R.string.go_to_other_posts))
                                }
                            }

                            else -> {
                                TextButton(onClick = { showResultDialog = false }) {
                                    Text(stringResource(id = R.string.close))
                                }
                            }
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showResultDialog = false }) {
                            Text("닫기")
                        }
                    },
                )
            }
            Row(
                modifier =
                    Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 100.dp, start = 60.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                Box(
                    modifier =
                        Modifier
                            .size(80.dp)
                            .clip(RoundedCornerShape(20.dp))
                            .background(Color.White)
                            .border(3.dp, Color.White, RoundedCornerShape(20.dp)),
                ) {
                    val imageUrl =
                        if (type == "Owner") {
                            application?.applicant?.profileImageUrl ?: ""
                        } else {
                            userState?.profileImageUrl ?: ""
                        }

                    if (imageUrl.isBlank()) {
                        Image(
                            painter = painterResource(id = R.drawable.test_profile),
                            contentDescription = "기본 프로필",
                            modifier = Modifier.size(48.dp),
                        )
                    } else {
                        AsyncImage(
                            model = imageUrl,
                            contentDescription = "프로필 사진",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                            error = painterResource(id = R.drawable.test_profile),
                        )
                    }
                }

                Spacer(modifier = Modifier.width(20.dp))

                // 친구 추가 아이콘
                if (type == "Owner") {
                    val isFollowing =
                        userState?.following?.contains(application?.applicant?.id) == true

                    Box(
                        modifier =
                            Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(if (isFollowing) Color.Gray else Color(0xFF3ECDFF))
                                .border(3.dp, Color.White, CircleShape)
                                .padding(4.dp)
                                .clickable {
                                    val currentUser = userState
                                    val currentApplication = application
                                    if (currentUser != null && currentApplication != null) {
                                        val myInfo =
                                            UserSummary(
                                                id = currentUser.id,
                                                nickname = currentUser.nickname,
                                                profileImageUrl = currentUser.profileImageUrl,
                                                country = currentUser.country,
                                            )
                                        userViewModel.toggleFollow(
                                            myInfo,
                                            UserSummary(
                                                id = currentApplication.applicant.id,
                                                nickname = currentApplication.applicant.nickname,
                                                profileImageUrl = currentApplication.applicant.profileImageUrl,
                                                country = currentApplication.applicant.country,
                                            ),
                                        )

                                        val toastText =
                                            if (isFollowing) {
                                                context.getString(R.string.toast_unfollowed)
                                            } else {
                                                context.getString(R.string.toast_followed)
                                            }
                                        Toast
                                            .makeText(context, toastText, Toast.LENGTH_SHORT)
                                            .show()
                                    }
                                },
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            painter =
                                painterResource(
                                    id = if (isFollowing) R.drawable.un_follow else R.drawable.follow,
                                ),
                            contentDescription = if (isFollowing) "팔로우 취소" else "팔로우",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp),
                        )
                    }
                }
            }
        }
    }
}
