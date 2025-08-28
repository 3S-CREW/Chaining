package com.example.chaining.ui.screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chaining.viewmodel.QuizViewModel

@Composable
fun KRQuizScreen(
    quizViewModel: QuizViewModel = viewModel() // ViewModel 인스턴스 생성
) {
    val context = LocalContext.current

    // 이 화면이 처음 생성될 때 딱 한 번만 실행되는 코드 블록
    LaunchedEffect(Unit) {
        quizViewModel.loadQuizzes(context, "ENGLISH")
    }

    // TODO: 여기에 퀴즈 UI가 들어옵니다.
}