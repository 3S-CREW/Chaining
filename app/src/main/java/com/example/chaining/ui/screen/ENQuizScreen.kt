package com.example.chaining.ui.screen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.chaining.domain.model.QuizType
import com.example.chaining.viewmodel.QuizViewModel

@Composable
fun ENQuizScreen(
    quizViewModel: QuizViewModel = viewModel(),
    onNavigateToResult: () -> Unit
) {
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        quizViewModel.loadQuizzes(context, "ENGLISH")
    }

    // ViewModel에서 현재 퀴즈 데이터 가져오기
    val currentQuiz = quizViewModel.currentQuestion.value
    val wordChips = quizViewModel.wordChips.value
    val userAnswer = quizViewModel.userAnswerSentence.value
    val currentIndex = quizViewModel.currentQuestionIndex.value
    val totalQuestions = quizViewModel.quizSet.value.size
    val selectedOption = quizViewModel.selectedOption.value
    val selectedBlankWord = quizViewModel.selectedBlankWord.value
    val isAnswerSubmitted = quizViewModel.isAnswerSubmitted.value
    val isQuizFinished = quizViewModel.isQuizFinished.value

    // isQuizFinished 상태가 true로 바뀌면 onNavigateToResult 함수를 호출
    LaunchedEffect(isQuizFinished) {
        if (isQuizFinished) {
            onNavigateToResult()
        }
    }

    Scaffold(
        containerColor = Color(0xFFF3F6FF) // 배경색
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp)
        ) {
            // 진행률 표시줄 추가
            QuizProgressIndicator(
                currentQuestionIndex = quizViewModel.currentQuestionIndex.value,
                totalQuestions = quizViewModel.quizSet.value.size
            )

            // 문제 표시 영역 추가
            // 현재 퀴즈 데이터가 있을 경우에만 UI를 표시
            if (currentQuiz != null) {
                // 퀴즈 유형에 따라 보여줄 텍스트를 결정하는 변수
                val questionTextToShow = when (currentQuiz.type) {
                    QuizType.MULTIPLE_CHOICE.name -> currentQuiz.problem
                    else -> currentQuiz.translation
                }
                // 진행률 표시줄과의 간격
                Spacer(modifier = Modifier.height(80.dp))

                // 문제(번역문) 텍스트
                Text(
                    text = questionTextToShow,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    lineHeight = 32.sp
                )
                // 정답 입력 영역을 화면 중앙에 배치하기 위한 Spacer
                Spacer(modifier = Modifier.weight(1f))

                // 퀴즈 유형에 따라 다른 UI를 보여주는 when 블록
                when (currentQuiz.type) {
                    QuizType.SENTENCE_ORDER.name -> {
                        SentenceOrderAnswerArea(
                            remainingWords = quizViewModel.remainingWordChips.value,
                            userAnswer = userAnswer,
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

                // 정답 입력 영역을 화면 중앙에 배치하기 위한 Spacer
                Spacer(modifier = Modifier.weight(1f))

                // '다음' 버튼
                Button(
                    onClick = { quizViewModel.submitAndGoToNext() }, // 항상 다음 문제로 이동
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(50.dp),
                    enabled = isAnswerSubmitted, // 사용자가 답을 제출했을 때만 활성화
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4), // 버튼의 배경색
                        contentColor = Color.White        // 버튼 안의 텍스트 색상
                    )
                ) {
                    Text(
                        text = "다음", // 텍스트를 '다음'으로 고정
                        fontSize = 16.sp
                    )
                }
            }
        }
    }
}

/**
 * 퀴즈 진행률을 보여주는 인디케이터
 */
@Composable
fun QuizProgressIndicator(currentQuestionIndex: Int, totalQuestions: Int) {
    if (totalQuestions > 0) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            // 전체 문제 개수만큼 인디케이터를 만듭니다.
            for (i in 0 until totalQuestions) {
                val color = if (i <= currentQuestionIndex) Color(0xFF4285F4) else Color.LightGray
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(color)
                )
            }
        }
    }
}

// '문장 순서 맞추기' UI를 위한 별도 Composable 함수
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SentenceOrderAnswerArea(
    remainingWords: List<String>,
    userAnswer: List<String>,
    onWordChipClicked: (String) -> Unit,
    onAnswerWordClicked: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // TODO: 사용자가 선택한 단어를 표시할 영역

        Spacer(modifier = Modifier.height(40.dp))

        // 단어 칩들을 표시할 영역
        FlowRow(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .heightIn(min = 50.dp) // 최소 높이 지정
                .background(Color.White, RoundedCornerShape(16.dp))
                .padding(8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            userAnswer.forEach { word ->
                Button(
                    onClick = { onAnswerWordClicked(word) }, // 클릭 시 선택 해제
                    shape = CircleShape,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4285F4),
                        contentColor = Color.White
                    )
                ) {
                    Text(text = word)
                }
            }
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 2. 선택 가능한 단어 칩들을 표시할 영역
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            remainingWords.forEach { word ->
                key(word) {
                    Button(
                        onClick = { onWordChipClicked(word) },
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White,
                            contentColor = Color.Black
                        )
                    ) {
                        Text(text = word)
                    }
                }
            }
        }
    }
}

// '객관식' UI를 위한 별도 Composable 함수 추가
@Composable
fun MultipleChoiceAnswerArea(
    options: List<String>,
    selectedOption: String?,
    onOptionSelected: (String) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        options.forEach { option ->
            val isSelected = option == selectedOption
            OutlinedButton(
                onClick = { onOptionSelected(option) },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (isSelected) Color(0xFF4285F4).copy(alpha = 0.1f) else Color.White,
                    contentColor = Color.Black
                ),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) Color(0xFF4285F4) else Color.LightGray
                )
            ) {
                Text(text = option)
            }
        }
    }
}

// '빈칸 채우기' UI를 위한 별도 Composable 함수 추가
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun FillInTheBlankAnswerArea(
    problem: String,
    options: List<String>,
    selectedWord: String?,
    onWordSelected: (String) -> Unit
) {
    // 문제 문장을 빈칸("______") 기준으로 나눔
    val sentenceParts = problem.split("______")

    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // 1. 빈칸이 채워지는 문장 UI
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.CenterHorizontally)
        ) {
            // 빈칸 앞부분
            Text(text = sentenceParts.getOrNull(0) ?: "",
                fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterVertically),
                lineHeight = 20.sp
            )

            // 빈칸 부분
            Box(
                modifier = Modifier
                    .width(100.dp)
                    .height(40.dp)
                    .background(Color.White, RoundedCornerShape(8.dp))
                    .border(1.dp, Color.LightGray, RoundedCornerShape(8.dp))
                    .align(Alignment.CenterVertically),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedWord ?: "",
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF4285F4)
                )
            }

            // 빈칸 뒷부분
            Text(text = sentenceParts.getOrNull(1) ?: "", fontSize = 18.sp,
                modifier = Modifier.align(Alignment.CenterVertically), lineHeight = 20.sp)
        }

        Spacer(modifier = Modifier.height(40.dp))

        // 2. 선택지 단어 칩 UI
        FlowRow(
            modifier = Modifier.width(300.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterHorizontally),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            options.forEach { option ->
                Button(
                    onClick = { onWordSelected(option) },
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.width(140.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.White,
                        contentColor = Color.Black
                    )
                ) {
                    Text(text = option)
                }
            }
        }
    }
}