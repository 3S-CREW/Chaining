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
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.chaining.R
import com.example.chaining.ui.component.SaveButton
import com.example.chaining.ui.component.formatDate
import com.example.chaining.ui.component.ownerProfile
import com.example.chaining.viewmodel.RecruitPostViewModel

@Composable
fun ViewPostScreen(
    postViewModel: RecruitPostViewModel = hiltViewModel(),
) {

    val post by postViewModel.post.collectAsState()
    val currentPost = post

    // post가 null이면 로딩 UI 표시
    if (currentPost == null) {
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
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp)
                    .clip(RoundedCornerShape(bottomEnd = 20.dp))
                    .background(Color(0xFF4A526A)),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = { /* TODO: 뒤로 가기 */ }) {
                    Icon(
                        painter = painterResource(id = R.drawable.back_arrow),
                        contentDescription = "뒤로 가기",
                        modifier = Modifier.size(20.dp),
                        tint = Color.White
                    )
                }

                ownerProfile(owner = currentPost.owner, where = "모집글 상세보기")

                // 제목을 완벽한 중앙에 맞추기 위한 빈 공간
                Spacer(modifier = Modifier.width(48.dp))
            }
        },
        containerColor = Color(0xFFF3F6FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(30.dp)
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            Text(
                text = currentPost.title,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF4A526A)
            )

            Spacer(modifier = Modifier.height(20.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            setInfo(
                icon = R.drawable.global,
                title = "선호 여행지 스타일",
                content = currentPost.preferredDestinations
            )

            setInfo(
                icon = R.drawable.calendar,
                title = "여행 일자",
                content = formatDate(currentPost.tourAt)
            )

            setInfo(
                icon = R.drawable.car,
                title = "자차 여부",
                content = currentPost.hasCar
            )

            setInfo(
                icon = R.drawable.timer,
                title = "모집 마감일",
                content = formatDate(currentPost.closeAt)
            )

            setInfo(
                icon = R.drawable.language,
                title = "선호하는 언어",
                content = currentPost.preferredLanguages.joinToString { it.language }
            )

            setInfo(
                icon = R.drawable.level,
                title = "선호하는 언어 수준",
                content = currentPost.preferredLanguages.joinToString { it.level.toString() }
            )

            Spacer(modifier = Modifier.height(24.dp))

            HorizontalDivider(
                thickness = 1.dp,
                color = Color.LightGray
            )

            Spacer(modifier = Modifier.height(20.dp))

            Text(
                text = currentPost.content,
                fontSize = 16.sp,
                lineHeight = 22.sp,
                color = Color(0xFF4A526A)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                SaveButton(onSave = { /*TODO*/ }, text = "신청")
                SaveButton(onSave = { /*TODO*/ }, text = "숨김")
            }

        }
    }
}

@Composable
fun setInfo(
    icon: Int,
    title: String,
    content: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(48.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "정보 아이콘",
            modifier = Modifier.size(16.dp)
        )

        Text(
            text = title,
            modifier = Modifier.weight(1f),
            color = Color(0xFF4A526A),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.width(20.dp))

        Text(
            text = content,
            modifier = Modifier.weight(1f),
            color = Color(0xFF4A526A),
            fontSize = 16.sp,
            textAlign = TextAlign.Center
        )
    }
}