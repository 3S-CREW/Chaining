package com.example.chaining.ui.screen

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.chaining.viewmodel.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizResultScreen(
    quizViewModel: QuizViewModel, // 공유되는 ViewModel
    onNavigateToMyPage: () -> Unit
) {
    val finalLevel by quizViewModel.finalLevel
    val totalScore by quizViewModel.totalScore

    // 프로그레스 바 애니메이션을 위한 상태
    var animationPlayed by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animationPlayed) (finalLevel / 10f) else 0f,
        animationSpec = tween(durationMillis = 1000)
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("퀴즈 결과 (Quiz Result)") })
        },
        containerColor = Color(0xFFF3F6FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.weight(1f))

            Text("닉네임님의 영어 레벨", fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Text("Korean Level", fontSize = 16.sp, color = Color.Gray)

            Spacer(modifier = Modifier.height(24.dp))

            // 레벨 표시
            Text("LV. $finalLevel", fontSize = 48.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))

            Spacer(modifier = Modifier.height(24.dp))

            // 프로그레스 바
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(12.dp)
                    .clip(RoundedCornerShape(6.dp)),
                color = Color(0xFF4285F4),
                trackColor = Color.LightGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "총점: $totalScore / 45점",
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(40.dp))

            Text(
                "LV 1~5, 3가지 유형으로 출제되었습니다.",
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNavigateToMyPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text("확인", fontSize = 16.sp)
            }
        }
    }
}