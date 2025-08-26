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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.R
import com.example.chaining.viewmodel.RecruitPostViewModel

@Composable
fun CommunityScreen(
    postViewModel: RecruitPostViewModel = hiltViewModel(),
    onBackClick: () -> Unit = {}
) {
    val posts by postViewModel.posts.collectAsState()

    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(bottomEnd = 20.dp))
                    .background(Color(0xFF4A526A)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // 뒤로가기 버튼
                IconButton(onClick = onBackClick) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }

                // 제목
                Text(
                    text = "커뮤니티",
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )

                // 새로고침 버튼
                IconButton(onClick = { postViewModel.refreshPosts() }) {
                    Icon(
                        painter = painterResource(id = R.drawable.change), // Todo: 새로고침 아이콘 필요
                        contentDescription = "새로고침",
                        modifier = Modifier.size(22.dp),
                        tint = Color.White
                    )
                }
            }
        },
        containerColor = Color(0xFFF3F6FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
            if (posts.isEmpty()) {
                // 데이터가 없을 때
                Text(
                    text = "등록된 게시글이 없습니다.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            } else {
                // 게시글 목록 표시
                posts.forEach { post ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.White
                        ),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                    ) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(text = post.title, fontSize = 18.sp, color = Color.Black)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = post.content, fontSize = 14.sp, color = Color.Gray)
                        }
                    }
                }
            }
        }
    }
}
