package com.example.chaining.viewmodel

import android.content.Context
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.chaining.data.repository.UserRepository
import com.example.chaining.domain.model.QuizItem // 이전에 만든 QuizItem 데이터 클래스 import
import com.example.chaining.domain.model.QuizType
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QuizViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    // 전체 퀴즈 목록 (비공개)
    private var allQuizzes: List<QuizItem> = emptyList()

    // UI가 구독할 최종 15문제 퀴즈 리스트 (공개)
    private val _quizSet = mutableStateOf<List<QuizItem>>(emptyList())
    val quizSet: State<List<QuizItem>> = _quizSet

    // 현재 몇 번째 문제를 풀고 있는지 추적
    private val _currentQuestionIndex = mutableStateOf(0)
    val currentQuestionIndex: State<Int> = _currentQuestionIndex

    // 현재 문제 State (읽기 전용)
    val currentQuestion: State<QuizItem?> = derivedStateOf {
        _quizSet.value.getOrNull(_currentQuestionIndex.value)
    }

    // '순서 맞추기' 유형을 위한 단어 묶음 (shuffled)
    val wordChips = derivedStateOf {
        currentQuestion.value?.takeIf { it.type == QuizType.SENTENCE_ORDER.name }
            ?.answer?.split(" ")?.shuffled() ?: emptyList()
    }
    // ✅ '순서 맞추기' 유형을 위한 '한 번만 섞인' 단어 목록 (상태로 관리)
    private val _shuffledWordChips = mutableStateOf<List<String>>(emptyList())
    // ✅ '선택하고 남은 단어 칩'은 이제 _shuffledWordChips를 기준으로 계산
    val remainingWordChips: State<List<String>> = derivedStateOf {
        _shuffledWordChips.value - _userAnswerSentence.value.toSet()
    }

    // 사용자가 구성한 정답 문장을 저장하는 State
    private val _userAnswerSentence = mutableStateOf<List<String>>(emptyList())
    val userAnswerSentence: State<List<String>> = _userAnswerSentence


    // '객관식' 유형을 위한 사용자 선택 답안 저장 State
    private val _selectedOption = mutableStateOf<String?>(null)
    val selectedOption: State<String?> = _selectedOption

    // '빈칸 채우기' 유형을 위한 사용자 선택 답안 저장 State
    private val _selectedBlankWord = mutableStateOf<String?>(null)
    val selectedBlankWord: State<String?> = _selectedBlankWord

    // 사용자의 답변을 기록할 Map (Key: 문제 ID, Value: 사용자 답변)
    private val _userAnswersMap = mutableStateOf<Map<String, String>>(emptyMap())
    val userAnswersMap: State<Map<String, String>> = _userAnswersMap

    // 사용자가 답을 제출했는지 확인 (버튼 활성화용)
    val isAnswerSubmitted = derivedStateOf {
        when (currentQuestion.value?.type) {
            QuizType.SENTENCE_ORDER.name -> userAnswerSentence.value.isNotEmpty()
            QuizType.MULTIPLE_CHOICE.name -> selectedOption.value != null
            QuizType.FILL_IN_THE_BLANK.name -> selectedBlankWord.value != null
            else -> false
        }
    }

    // 퀴즈가 종료되었는지 여부를 저장
    private val _isQuizFinished = mutableStateOf(false)
    val isQuizFinished: State<Boolean> = _isQuizFinished

    // UI에 Toast 메시지를 전달하기 위한 상태 변수
    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage = _toastMessage.asStateFlow()

    private val _currentLanguage = mutableStateOf("ENGLISH")
    val currentLanguage: State<String> = _currentLanguage

    // 채점 결과를 저장할 상태 변수 추가
    private val _totalScore = mutableStateOf(0)
    val totalScore: State<Int> = _totalScore

    private val _finalLevel = mutableStateOf(0)
    val finalLevel: State<Int> = _finalLevel

    /**
     * Assets 폴더에서 언어에 맞는 퀴즈 JSON 파일을 읽어오는 함수
     */
    fun loadQuizzes(context: Context, language: String) {
        _currentLanguage.value = language
        val fileName = if (language == "KOREAN") {
            "korean_quizzes.json"
        } else {
            "english_quizzes.json"
        }

        try {
            // 1. Assets에서 파일 스트림 열기
            val inputStream = context.assets.open(fileName)
            // 2. 텍스트 읽기
            val jsonString = inputStream.bufferedReader().use { it.readText() }
            // 3. Gson을 사용해 JSON 텍스트를 List<QuizItem>으로 변환
            val listType = object : TypeToken<List<QuizItem>>() {}.type
            allQuizzes = Gson().fromJson(jsonString, listType)

            // 퀴즈를 모두 불러온 후, 15문제를 선택하는 함수 호출
            selectQuizSet()
            // ✅ 퀴즈 로드 후 첫 문제의 단어 칩을 미리 섞어둠
            prepareSentenceOrderChips()

        } catch (e: Exception) {
            // 파일을 읽지 못했을 경우 예외 처리
            allQuizzes = emptyList()
        }
    }

    private fun selectQuizSet() {
        val finalQuizList = mutableListOf<QuizItem>()
        // LV1 부터 LV5 까지 반복
        (1..5).forEach { level ->
            // 3가지 유형(SO, MC, FB)에 대해 반복
            QuizType.values().forEach { type ->
                // 해당 레벨과 유형에 맞는 문제들을 필터링
                val filteredQuizzes =
                    allQuizzes.filter { it.level == level && it.type == type.name }
                // 필터링된 문제들 중 하나를 랜덤으로 선택 (문제가 있을 경우에만)
                filteredQuizzes.randomOrNull()?.let {
                    finalQuizList.add(it)
                }
            }
        }

        _quizSet.value = finalQuizList
    }
    // ✅ '문장 순서 맞추기' 단어 칩을 준비하는 함수 추가
    private fun prepareSentenceOrderChips() {
        val quiz = currentQuestion.value
        if (quiz != null && quiz.type == QuizType.SENTENCE_ORDER.name) {
            _shuffledWordChips.value = quiz.answer.split(" ").shuffled()
        } else {
            _shuffledWordChips.value = emptyList()
        }
    }

    // 단어 칩을 클릭했을 때 호출될 함수
    fun onWordChipClicked(word: String) {
        _userAnswerSentence.value = _userAnswerSentence.value + word
    }

    // '내가 만든 문장'의 단어를 클릭했을 때 (선택 해제)
    fun onAnswerWordClicked(word: String) {
        _userAnswerSentence.value = _userAnswerSentence.value - word
    }

    // '객관식' 선택지를 클릭했을 때
    fun onOptionSelected(option: String) {
        _selectedOption.value = option
    }

    // '빈칸 채우기' 선택지를 클릭했을 때
    fun onBlankWordSelected(word: String) {
        _selectedBlankWord.value = word
    }

    // '다음' 버튼을 눌렀을 때 호출될 함수
    fun submitAndGoToNext() {
        // 현재 문제 가져오기 (ID를 얻기 위함)
        val quiz = currentQuestion.value ?: return

        // '문장 순서 맞추기' 유형일 때만 유효성 검사
        if (quiz.type == QuizType.SENTENCE_ORDER.name) {
            if (remainingWordChips.value.isNotEmpty()) {
                val message = if (_currentLanguage.value == "KOREAN") {
                    "Please complete the sentence"
                } else {
                    "문장을 완성해주세요"
                }
                viewModelScope.launch {
                    _toastMessage.value = message
                }
                return
            }
        }
        // 현재 문제 유형에 맞는 사용자 답변 가져오기
        val userAnswer = when (quiz.type) {
            QuizType.SENTENCE_ORDER.name -> _userAnswerSentence.value.joinToString(" ")
            QuizType.MULTIPLE_CHOICE.name -> _selectedOption.value
            QuizType.FILL_IN_THE_BLANK.name -> _selectedBlankWord.value
            else -> null
        }

        // 답변이 있을 경우, Map에 기록
        if (userAnswer != null) {
            val newAnswers = _userAnswersMap.value.toMutableMap()
            newAnswers[quiz.id] = userAnswer
            _userAnswersMap.value = newAnswers
        }

        // 다음 문제로 이동 또는 결과 화면으로 전환
        if (_currentQuestionIndex.value < (_quizSet.value.size - 1)) {
            _currentQuestionIndex.value++
            clearUserAnswer()
            // ✅ 다음 문제로 넘어갈 때 새로운 문제의 단어 칩을 섞어서 준비
            prepareSentenceOrderChips()
        } else {
            // 모든 퀴즈를 다 푼 경우
            calculateScore()
            _isQuizFinished.value = true
        }
    }

    // 다음 문제로 넘어갈 때 사용자가 선택한 답을 초기화하는 함수
    fun clearUserAnswer() {
        _userAnswerSentence.value = emptyList()
        _selectedOption.value = null
        _selectedBlankWord.value = null
    }

    // Toast 메시지를 보여준 후 호출할 함수
    fun clearToastMessage() {
        _toastMessage.value = null
    }

    private fun calculateScore() {
        var score = 0
        _quizSet.value.forEach { quizItem ->
            val userAnswer = _userAnswersMap.value[quizItem.id]
            if (userAnswer == quizItem.answer) {
                score += quizItem.level // 정답이면 레벨만큼 점수 추가
            }
        }
        _totalScore.value = score
        _finalLevel.value = mapScoreToLevel(score)
    }

    private fun mapScoreToLevel(score: Int): Int {
        // 총점 45점을 11개 레벨(0~10)로 분배 (45 / 11 ≒ 4.1)
        return when (score) {
            in 0..4 -> 0
            in 5..8 -> 1
            in 9..12 -> 2
            in 13..16 -> 3
            in 17..20 -> 4
            in 21..25 -> 5
            in 26..30 -> 6
            in 31..35 -> 7
            in 36..40 -> 8
            in 41..44 -> 9
            45 -> 10
            else -> 0
        }
    }

    fun saveTestResult() {
        val languagePref = com.example.chaining.domain.model.LanguagePref(
            language = _currentLanguage.value,
            level = _finalLevel.value
        )

        viewModelScope.launch {
            try {
                userRepository.updateTestResult(languagePref)
                println("퀴즈 결과 저장 성공: $languagePref")
            } catch (e: Exception) {
                println("퀴즈 결과 저장 실패: ${e.message}")
            }
        }
    }

}