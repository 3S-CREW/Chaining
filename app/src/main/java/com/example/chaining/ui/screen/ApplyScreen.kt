package com.example.chaining.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.chaining.R
import com.example.chaining.domain.model.Application
import com.example.chaining.viewmodel.ApplicationViewModel
import com.example.chaining.viewmodel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ApplyScreen(
    onBackClick: () -> Unit = {},
    type: String, // My, Other
    application: Application? = null,
    userViewModel: UserViewModel = hiltViewModel(),
    applicationViewModel: ApplicationViewModel = hiltViewModel()
) {
    val userState by userViewModel.user.collectAsState()

    // post가 null이면 로딩 UI 표시
    if (type == "Other" && application == null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text("Loading...", fontSize = 18.sp)
        }
        return
    }

    Scaffold(
        // 상단바 배경색을 직접 파란색으로 지정
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp) // 상단바의 기본 높이
                    .background(Color(0xFF4285F4)),
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
                    text = if (type == "Other") {
                        "지원서 보기"
                    } else {
                        "내 지원서 보기"
                    },
                    fontSize = 20.sp,
                    color = Color.White,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center
                )
                // 제목을 완벽한 중앙에 맞추기 위한 빈 공간
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        bottomBar = {
            AppBottomNavigation(selectedTab = "NONE", onCommunityClick = {})
        },
        // 전체 기본 배경은 흰색으로 둡니다.
        containerColor = Color.White
    ) { innerPadding ->
        // Box를 사용해 파란 헤더와 흰색 콘텐츠를 겹치게 합니다.
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // 곡선 효과가 있는 파란색 헤더
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
                    .clip(RoundedCornerShape(bottomEndPercent = 50))
                    .background(Color(0xFF4285F4))
            ) {
                // 타이머 텍스트를 담을 Column 추가
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp), // 상단바와의 간격
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "수락/거절까지",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 14.sp
                    )
                    Text(
                        text = "12시간 30분 남음",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // 스크롤되는 흰색 콘텐츠 영역
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // 프로필 사진에 내용이 가려지지 않도록 공간 확보
                Spacer(modifier = Modifier.height(200.dp))

                // 상세 정보 콘텐츠 추가
                Column(
                    modifier = Modifier.padding(horizontal = 50.dp),
                    horizontalAlignment = Alignment.Start
                ) {
                    Text(
                        text = if (type == "Other") {
                            application?.applicant?.nickname ?: "알 수 없음"
                        } else {
                            userState?.nickname ?: "알 수 없음"
                        },
                        fontSize = 24.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF4A526A)
                    )
                    Text(
                        text = if (type == "Other") {
                            application?.applicant?.country ?: "알 수 없음"
                        } else {
                            userState?.country ?: "알 수 없음"
                        },
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7282B4),
                        modifier = Modifier.padding(top = 4.dp)
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 언어 수준
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start // 이 부분만 왼쪽 정렬
                    ) {
                        Text(
                            text = if (type == "Other") {
                                "${application?.applicationId ?: "알 수 없음"} 수정 필요"
                            } else {
                                "${userState?.preferredLanguages?.get(0)?.language ?: "알 수 없음"} 수준 : ${
                                    userState?.preferredLanguages?.get(
                                        0
                                    )?.level ?: "알 수 없음"
                                } / 10"
                            },
                            color = Color(0xFF4A526A)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (type == "Other") {
                                "${application?.applicationId ?: "알 수 없음"} 수정 필요"
                            } else {
                                "${userState?.preferredLanguages?.get(0)?.language ?: "알 수 없음"} 수준 : ${
                                    userState?.preferredLanguages?.get(
                                        0
                                    )?.level ?: "알 수 없음"
                                } / 10"
                            },
                            color = Color(0xFF4A526A)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // 자기 소개
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.Start
                    ) {
                        Text(
                            text = "자기소개:",
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF7282B4)
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (type == "Other") {
                                application?.introduction ?: "알 수 없음"
                            } else {
                                "이전 페이지에서 넘어와야함"
                            },
                            color = Color(0xFF4A526A)
                        )
                    }

                    Spacer(modifier = Modifier.height(100.dp))

                    if (type == "Other") {
                        Row {
                            // 수락 버튼
                            Button(
                                onClick = {
                                    if (application != null) {
                                        applicationViewModel.updateStatus(
                                            application = application,
                                            value = "승인"
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .width(200.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF2C80FF),
                                    contentColor = Color.White
                                )
                            ) {
                                Text("수락", fontSize = 16.sp)
                            }

                            // 거절 버튼
                            Button(
                                onClick = {
                                    if (application != null) {
                                        applicationViewModel.updateStatus(
                                            application = application,
                                            value = "거절"
                                        )
                                    }
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp)
                                    .width(120.dp),
                                shape = RoundedCornerShape(20.dp),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFF0F2F5),
                                    contentColor = Color.DarkGray
                                )
                            ) {
                                Text("거절", fontSize = 16.sp)
                            }
                        }


                    } else {
                        // 닫기 버튼
                        Button(
                            onClick = { /* TODO: 창 닫기 */ },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp),
                            shape = RoundedCornerShape(20.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFF0F2F5), // 연한 회색 배경
                                contentColor = Color.DarkGray
                            )
                        ) {
                            Text("닫기", fontSize = 16.sp)
                        }

                    }

                    Spacer(modifier = Modifier.height(24.dp)) // 하단 네비게이션 바와의 간격
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 100.dp, start = 60.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // 프로필 사진
                AsyncImage(
                    model = "https://newsimg-hams.hankookilbo.com/2023/03/24/4531dada-e9cf-4775-951c-902e3558ca41.jpg",
                    contentDescription = "프로필 사진",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(80.dp)
                        .clip(RoundedCornerShape(20.dp))
                        .border(3.dp, Color.White, RoundedCornerShape(20.dp))
                )

                Spacer(modifier = Modifier.width(20.dp))

                // 친구 추가 아이콘
                Box(
                    modifier = Modifier
                        .size(60.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF3ECDFF))
                        .border(3.dp, Color.White, CircleShape)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.follow),
                        contentDescription = "친구 추가",
                        tint = Color.White,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}