package com.example.chaining.ui.screen

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.chaining.R
import com.example.chaining.domain.model.Application
import com.example.chaining.domain.model.RecruitPost
import com.example.chaining.ui.component.OwnerProfile
import com.example.chaining.ui.component.SaveButton
import com.example.chaining.viewmodel.ApplicationViewModel
import com.example.chaining.viewmodel.UserViewModel
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

private const val MAX_CONTENT_LENGTH = 300

@Suppress("FunctionName")
@Composable
fun JoinPostScreen(
    applicationViewModel: ApplicationViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {},
    onSubmitSuccess: () -> Unit,
    userViewModel: UserViewModel = hiltViewModel(),
    post: RecruitPost,
    onViewMyApplyClick: (String) -> Unit,
    navController: NavController,
) {
    val savedStateHandle = navController.currentBackStackEntry?.savedStateHandle

    var introduction by rememberSaveable { mutableStateOf("") }
    val context = LocalContext.current
    val userState by userViewModel.user.collectAsState()

    val isSubmitSuccess by applicationViewModel.isSubmitSuccess.collectAsState()

    LaunchedEffect(Unit) {
        // 신청 완료 이벤트 처리
        launch {
            applicationViewModel.isSubmitSuccess.collectLatest { success ->
                if (success) {
                    // 성공 시 콜백 호출 (화면 전환)
                    onSubmitSuccess()
                    // 상태 초기화
                    applicationViewModel.resetSubmitStatus()
                }
            }
        }
        // 토스트 메시지 이벤트 처리
        launch {
            applicationViewModel.toastEvent.collectLatest { message ->
                Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(savedStateHandle) {
        savedStateHandle?.getStateFlow("introduction", "")
            ?.collect { restored ->
                if (restored.isNotEmpty()) {
                    introduction = restored
                    savedStateHandle.remove<String>("introduction")
                }
            }
    }


    val decodedTitle = post.title.replace("+", " ")

    Scaffold(
        topBar = {
            Row(
                modifier =
                Modifier
                    .fillMaxWidth()
                    // 원하는 높이로 직접 설정
                    .height(64.dp)
                    .clip(RoundedCornerShape(bottomEnd = 20.dp))
                    .background(Color(0xFF4A526A)),
                // 내부 요소들을 세로 중앙에 정렬
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // 뒤로가기 아이콘 버튼
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White,
                    )
                }

                // 제목 텍스트
                Text(
                    text = stringResource(id = R.string.apply_title),
                    // 남는 공간을 모두 차지
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    fontSize = 20.sp,
                    // 텍스트를 가운데 정렬
                    textAlign = TextAlign.Center,
                )

                // 제목을 완벽한 중앙에 맞추기 위한 빈 공간
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        // 신청서 화면 내 네비게이션 바 보류
//        bottomBar = {
//            // 이전에 만든 하단 네비게이션 바 재사용
//            AppBottomNavigation()
//        },
        // 전체 배경색
        containerColor = Color(0xFFF3F6FF),
    ) { innerPadding ->
        // 스크롤 영역과 하단 고정 영역을 나누기 위한 부모 Column
        Column(
            modifier =
            Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(30.dp),
        ) {
            // 게시글 정보 섹션 추가
            // 상단바와의 간격
            Spacer(modifier = Modifier.height(30.dp))

            // 게시글 제목
            Text(
                text = decodedTitle,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A526A),
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 작성자 정보
            OwnerProfile(owner = post.owner, where = "지원서")
            // 정보와 구분선 사이 간격
            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray,
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                // 너비를 꽉 채워 왼쪽 정렬 효과
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(
                    text = stringResource(id = R.string.apply_intro),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4A526A),
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = introduction,
                    onValueChange = { if (it.length <= MAX_CONTENT_LENGTH) introduction = it },
                    modifier =
                    Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = {
                        Text(
                            stringResource(id = R.string.apply_write),
                            fontSize = 13.sp,
                            color = Color.Gray,
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    supportingText = {
                        Text(
                            text = "${introduction.length} / $MAX_CONTENT_LENGTH",
                            modifier = Modifier.fillMaxWidth(),
                            textAlign = TextAlign.End,
                        )
                    },
                    colors =
                    TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.LightGray,
                        unfocusedIndicatorColor = Color.LightGray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black,
                    ),
                )
                Text(
                    text = stringResource(id = R.string.apply_text_one),
                    modifier =
                    Modifier
                        .padding(top = 8.dp)
                        // 오른쪽 정렬
                        .align(Alignment.End),
                    fontSize = 10.sp,
                    color = Color.Gray,
                )
            }
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = stringResource(id = R.string.apply_text_two),
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                color = Color.Gray,
            )

            Spacer(modifier = Modifier.height(8.dp))

            // '내 지원서 보기' 버튼 (보조 버튼)
            Button(
                onClick = { onViewMyApplyClick(introduction) },
                modifier =
                Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(30.dp),
                colors =
                ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD9DDE9),
                    contentColor = Color(0xFF7282B4),
                ),
            ) {
                Text(stringResource(id = R.string.apply_mine), fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // '신청 완료' 버튼 (주요 버튼)
            SaveButton(onSave = {
                if (introduction.isBlank()) {
                    Toast.makeText(
                        context,
                        context.getString(R.string.application_intro_blank),
                        Toast.LENGTH_SHORT,
                    ).show()
                } else {
                    val newApplication =
                        userState?.let {
                            Application(
                                applicationId = "",
                                postId = post.postId,
                                owner = post.owner,
                                recruitPostTitle = post.title,
                                introduction = introduction,
                                applicant = it,
                            )
                        }
                    if (newApplication != null) {
                        applicationViewModel.submitApplication(newApplication)
                    }
                }
            }, text = stringResource(id = R.string.apply_button))

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
