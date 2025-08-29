package com.example.chaining.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MyApplyScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("지원서 보기") },
                navigationIcon = {
                    IconButton(onClick = { /* TODO: 뒤로 가기 */ }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "뒤로 가기")
                    }
                },
                // 상단바 배경을 투명하게 만들어 뒷배경이 보이게 함
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = Color.Transparent,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            AppBottomNavigation() // 기존 하단 네비게이션 바 재사용
        }
    ) { innerPadding ->
        // ✅ 1. 전체 화면을 덮는 Box 레이아웃
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // ✅ 2. 상단 파란색 배경 Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp) // 파란 배경의 높이
                    .background(Color(0xFF4285F4))
            )

            // ✅ 3. 흰색 콘텐츠 영역 Column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 150.dp) // 파란 배경 아래에서 시작하도록 상단 여백
                    .clip(RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp))
                    .background(Color.White)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // TODO: 여기에 이름, 자기소개 등 텍스트 정보가 들어옵니다.
            }
        }
    }
}