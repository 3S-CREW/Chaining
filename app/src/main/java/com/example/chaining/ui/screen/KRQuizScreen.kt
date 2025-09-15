package com.example.chaining.ui.screen

import android.widget.Toast
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chaining.domain.model.QuizType
import com.example.chaining.viewmodel.QuizViewModel

@OptIn(ExperimentalLayoutApi::class)
@Suppress("FunctionName")
@Composable
fun KRQuizScreen(
    quizViewModel: QuizViewModel = viewModel(),
    onNavigateToResult: () -> Unit,
) {
    val context = LocalContext.current
    val isQuizFinished = quizViewModel.isQuizFinished.value
    val toastMessage by quizViewModel.toastMessage.collectAsState()

    // toastMessage 상태가 변경될 때마다 실행
    LaunchedEffect(toastMessage) {
        toastMessage?.let { message ->
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            // Toast를 보여준 후에는 ViewModel의 상태를 다시 null로 초기화
            quizViewModel.clearToastMessage()
        }
    }

    // 화면이 처음 생성될 때 한국어 퀴즈를 불러옵니다.
    LaunchedEffect(Unit) {
        // "KOREAN"으로 변경된 부분
        quizViewModel.loadQuizzes(context, "KOREAN")
    }

    LaunchedEffect(isQuizFinished) {
        if (isQuizFinished) {
            onNavigateToResult()
        }
    }

    val currentQuiz = quizViewModel.currentQuestion.value
    val userAnswerSentence = quizViewModel.userAnswerSentence.value
    val selectedOption = quizViewModel.selectedOption.value
    val selectedBlankWord = quizViewModel.selectedBlankWord.value
    val currentIndex = quizViewModel.currentQuestionIndex.value
    val totalQuestions = quizViewModel.quizSet.value.size
    val isAnswerSubmitted = quizViewModel.isAnswerSubmitted.value

    Scaffold(
        containerColor = Color(0xFFF3F6FF),
    ) { innerPadding ->
        Column(
            modifier =
                Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .padding(24.dp),
        ) {
            QuizProgressIndicator(
                currentQuestionIndex = currentIndex,
                totalQuestions = totalQuestions,
            )

            if (currentQuiz != null) {
                val questionTextToShow =
                    when (currentQuiz.type) {
                        QuizType.MULTIPLE_CHOICE.name -> currentQuiz.problem
                        else -> currentQuiz.translation
                    }

                Spacer(modifier = Modifier.height(60.dp))

                Text(
                    text = questionTextToShow,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                )

                Spacer(modifier = Modifier.weight(1f))

                when (currentQuiz.type) {
                    QuizType.SENTENCE_ORDER.name -> {
                        SentenceOrderAnswerArea(
                            remainingWords = quizViewModel.remainingWordChips.value,
                            userAnswer = userAnswerSentence,
                            onWordChipClicked = quizViewModel::onWordChipClicked,
                            onAnswerWordClicked = quizViewModel::onAnswerWordClicked,
                        )
                    }

                    QuizType.MULTIPLE_CHOICE.name -> {
                        MultipleChoiceAnswerArea(
                            options = currentQuiz.options,
                            selectedOption = selectedOption,
                            onOptionSelected = quizViewModel::onOptionSelected,
                        )
                    }

                    QuizType.FILL_IN_THE_BLANK.name -> {
                        FillInTheBlankAnswerArea(
                            problem = currentQuiz.problem,
                            options = currentQuiz.options,
                            selectedWord = selectedBlankWord,
                            onWordSelected = quizViewModel::onBlankWordSelected,
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                // '다음' 버튼
                Button(
                    // 항상 다음 문제로 이동
                    onClick = { quizViewModel.submitAndGoToNext() },
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .height(50.dp),
                    // 사용자가 답을 제출했을 때만 활성화
                    enabled = isAnswerSubmitted,
                    shape = RoundedCornerShape(30.dp),
                    colors =
                        ButtonDefaults.buttonColors(
                            // 버튼의 배경색
                            containerColor = Color(0xFF4285F4),
                            // 버튼 안의 텍스트 색상
                            contentColor = Color.White,
                        ),
                ) {
                    Text(
                        // 텍스트를 '다음'으로 고정
                        text = "다음",
                        fontSize = 16.sp,
                    )
                }
            }
        }
    }
}
