package com.example.chaining.ui.screen

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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import coil.compose.AsyncImage
import com.example.chaining.R

@Composable
fun JoinPostScreen() {
    var introduction by remember { mutableStateOf("") }

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp) // 원하는 높이로 직접 설정
                    .clip(RoundedCornerShape(bottomEnd = 20.dp))
                    .background(Color(0xFF4A526A)),
                // 내부 요소들을 세로 중앙에 정렬
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 뒤로가기 아이콘 버튼
                IconButton(onClick = { /* TODO: 뒤로 가기 */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }

                // 제목 텍스트
                Text(
                    text = "신청서 작성",
                    modifier = Modifier.weight(1f), // 남는 공간을 모두 차지
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center // 텍스트를 가운데 정렬
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
        containerColor = Color(0xFFF3F6FF) // 전체 배경색
    ) { innerPadding ->
        // 스크롤 영역과 하단 고정 영역을 나누기 위한 부모 Column
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(30.dp)
        ) {
            // 게시글 정보 섹션 추가
            Spacer(modifier = Modifier.height(30.dp)) // 상단바와의 간격

            // 게시글 제목
            Text(
                text = "제주도 하이킹 함께 하실 분!",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A526A)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // 작성자 정보
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = "https://newsimg-hams.hankookilbo.com/2023/03/24/4531dada-e9cf-4775-951c-902e3558ca41.jpg",
                    contentDescription = "작성자 프로필 사진",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(16.dp))
                )

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = "차무식 (1975)",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 14.sp,
                        color = Color(0xFF4A526A)
                    )
                    Text(
                        text = "필리핀",
                        fontSize = 12.sp,
                        color = Color(0xFF4A526A)
                    )
                }

            }
            Spacer(modifier = Modifier.height(24.dp)) // 정보와 구분선 사이 간격

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Column(
                modifier = Modifier.fillMaxWidth() // 너비를 꽉 채워 왼쪽 정렬 효과
            ) {
                Text(
                    text = "자기 소개",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = Color(0xFF4A526A)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = introduction,
                    onValueChange = { introduction = it },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp),
                    placeholder = { Text(
                        "내용을 입력하세요. (300자 이내)",
                        fontSize = 13.sp,
                        color = Color.Gray
                        )
                    },
                    shape = RoundedCornerShape(16.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedIndicatorColor = Color.LightGray,
                        unfocusedIndicatorColor = Color.LightGray,
                        focusedTextColor = Color.Black,
                        unfocusedTextColor = Color.Black
                    )
                )
                Text(
                    text = "모집자가 수락하면 카카오톡 오픈 채팅 링크가 전달됩니다.",
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .align(Alignment.End), // 오른쪽 정렬
                    fontSize = 10.sp,
                    color = Color.Gray
                )
            }
            Spacer(modifier = Modifier.height(60.dp))

            Text(
                text = "[내 지원서 보기]를 통해 최신 프로필로 업데이트 되어있는지 확인하세요.",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 10.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // '내 지원서 보기' 버튼 (보조 버튼)
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFD9DDE9),
                    contentColor = Color(0xFF7282B4)
                )
            ) {
                Text("내 지원서 보기", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // '신청 완료' 버튼 (주요 버튼)
            Button(
                onClick = { /* TODO */ },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4285F4),
                    contentColor = Color.White
                )
            ) {
                Text("신청 완료", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

        }
    }
}