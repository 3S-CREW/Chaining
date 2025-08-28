package com.example.chaining.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
@Composable
fun KRQuizScreen(
    quizViewModel: QuizViewModel = viewModel(),
    onNavigateToResult: () -> Unit
) {
    val context = LocalContext.current
    val isQuizFinished = quizViewModel.isQuizFinished.value

    // 화면이 처음 생성될 때 한국어 퀴즈를 불러옵니다.
    LaunchedEffect(Unit) {
        // ✅ "KOREAN"으로 변경된 부분
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
        containerColor = Color(0xFFF3F6FF)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            QuizProgressIndicator(
                currentQuestionIndex = currentIndex,
                totalQuestions = totalQuestions
            )

            if (currentQuiz != null) {
                val questionTextToShow = when (currentQuiz.type) {
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
                    color = Color.Black
                )

                Spacer(modifier = Modifier.weight(1f))

                when (currentQuiz.type) {
                    QuizType.SENTENCE_ORDER.name -> {
                        SentenceOrderAnswerArea(
                            remainingWords = quizViewModel.remainingWordChips.value,
                            userAnswer = userAnswerSentence,
                            onWordChipClicked = quizViewModel::onWordChipClicked,
                            onAnswerWordClicked = quizViewModel::onAnswerWordClicked
                        )
                    }
                    QuizType.MULTIPLE_CHOICE.name -> {
                        MultipleChoiceAnswerArea(
                            options = currentQuiz.options,
                            selectedOption = selectedOption,
                            onOptionSelected = quizViewModel::onOptionSelected
                        )
                    }
                    QuizType.FILL_IN_THE_BLANK.name -> {
                        FillInTheBlankAnswerArea(
                            problem = currentQuiz.problem,
                            options = currentQuiz.options,
                            selectedWord = selectedBlankWord,
                            onWordSelected = quizViewModel::onBlankWordSelected
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { quizViewModel.submitAndGoToNext() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isAnswerSubmitted,
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Text(
                        text = "다음",
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}