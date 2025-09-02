package com.example.chaining.ui.screen

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
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
    val language by quizViewModel.currentLanguage
    val isKoreanQuiz = language == "KOREAN"
    val topBarTitle = if (isKoreanQuiz) "Quiz Result" else "퀴즈 결과"
    val mainTitle = if (isKoreanQuiz) "Nickname's Korean Level" else "닉네임님의 영어 레벨"
    val scoreLabel = if (isKoreanQuiz) "Total Score" else "총점"
    val scoreUnit = if (isKoreanQuiz) "pts" else "점"
    val descriptionText = if (isKoreanQuiz) "The quiz consists of 3 types from LV 1 to 5." else "LV 1~5, 3가지 유형으로 출제되었습니다."
    val confirmButtonText = if (isKoreanQuiz) "Confirm" else "확인"

    // 프로그레스 바 애니메이션을 위한 상태
    var animationPlayed by remember { mutableStateOf(false) }
    val progress by animateFloatAsState(
        targetValue = if (animationPlayed) (finalLevel / 10f) else 0f,
        animationSpec = tween(
            durationMillis = 2000,
            delayMillis = 500,
            easing = FastOutSlowInEasing
        )
    )

    LaunchedEffect(Unit) {
        animationPlayed = true
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
            ){
                Text(
                    text = "$topBarTitle",
                    modifier = Modifier.weight(1f),
                    color = Color.White,
                    fontSize = 20.sp,
                    textAlign = TextAlign.Center
                )
            }
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
            Spacer(modifier = Modifier.height(24.dp))

            Text(mainTitle, fontSize = 20.sp, fontWeight = FontWeight.SemiBold)
            Spacer(modifier = Modifier.weight(1f))


            // 레벨 표시
            Text("LV. $finalLevel", fontSize = 40.sp, fontWeight = FontWeight.Bold, color = Color(0xFF4285F4))

            Spacer(modifier = Modifier.height(24.dp))

            // 프로그레스 바
            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(30.dp)
                    .clip(RoundedCornerShape(15.dp)),
                color = Color(0xFF4285F4),
                trackColor = Color.LightGray
            )

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                "$scoreLabel: $totalScore / 45 $scoreUnit",
                fontSize = 20.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f))

            Text(
                descriptionText,
                fontSize = 12.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.weight(1f))

            Button(
                onClick = onNavigateToMyPage,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
                    .height(50.dp),
                shape = RoundedCornerShape(30.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4285F4))
            ) {
                Text(confirmButtonText, fontSize = 16.sp)
            }
        }
    }
}