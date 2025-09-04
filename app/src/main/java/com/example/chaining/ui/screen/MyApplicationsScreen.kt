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
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.chaining.ui.component.CardItem
import com.example.chaining.viewmodel.UserViewModel

@Composable
fun MyApplicationsScreen(
    onBackClick: () -> Unit = {},
    userViewModel: UserViewModel = hiltViewModel(),
) {
    val userState by userViewModel.user.collectAsState()

    val myApplications = userState?.applications.orEmpty()

    var showOnlyFinishedApplications by remember { mutableStateOf(false) }

    val filteredApplications = if (showOnlyFinishedApplications) {
        myApplications.filter { application -> application.value.status != "PENDING" }
    } else {
        myApplications
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
                    text = "내 모집글 보기",
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.width(48.dp))
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
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 새로 만든 CommunityActionButton 호출
                ActionButton(
                    modifier = Modifier.weight(1f),
                    iconRes = R.drawable.post,
                    text = if (showOnlyFinishedApplications) "전체 지원서 보기" else "지원서 결과 보기",
                    onClick = {
                        showOnlyFinishedApplications = !showOnlyFinishedApplications
                    }
                )
            }
            if (filteredApplications.isEmpty()) {
                // 데이터가 없을 때
                Text(
                    text = "등록된 지원서가 없습니다.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp),
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            } else {
                // 모집글 목록 표시
                filteredApplications.forEach { application ->
                    CardItem(
                        onClick = { },
                        type = "지원서",
                        application = application.value,
                    )
                }
            }
        }
    }
}