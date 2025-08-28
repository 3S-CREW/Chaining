package com.example.chaining.domain.model

data class QuizItem(
    val id: String = "",                         // 고유 ID
    val language: String = "",                   // 언어 종류 ("KOREAN" 또는 "ENGLISH")
    val level: Int = 0,                          // 난이도 (1-5)
    val type: String = "",                       // 퀴즈 유형 (SENTENCE_ORDER, MULTIPLE_CHOICE, FILL_IN_THE_BLANK)
    val problem: String = "",                    // 문제 내용
    val translation: String = "",                // 문제 번역
    val options: List<String> = emptyList(),     // 객관식 보기 목록
    val answer: String = ""                      // 정답
)

enum class QuizType {
    SENTENCE_ORDER,   // 문장 순서 맞추기
    MULTIPLE_CHOICE,  // 단어 의미 맞추기
    FILL_IN_THE_BLANK // 문장 빈칸 채우기
}