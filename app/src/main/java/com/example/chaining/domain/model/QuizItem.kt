package com.example.chaining.domain.model

import java.util.UUID

data class QuizItem(
    // 고유 ID
    val id: String = "",
    // 언어 종류 ("KOREAN" 또는 "ENGLISH")
    val language: String = "",
    // 난이도 (1-5)
    val level: Int = 0,
    // 퀴즈 유형 (SENTENCE_ORDER, MULTIPLE_CHOICE, FILL_IN_THE_BLANK)
    val type: String = "",
    // 문제 내용
    val problem: String = "",
    // 문제 번역
    val translation: String = "",
    // 객관식 보기 목록
    val options: List<String> = emptyList(),
    // 정답
    val answer: String = "",
)

enum class QuizType {
    // 문장 순서 맞추기
    SENTENCE_ORDER,

    // 단어 의미 맞추기
    MULTIPLE_CHOICE,

    // 문장 빈칸 채우기
    FILL_IN_THE_BLANK,
}

data class WordChip(
    val text: String,
    // 각 단어에 고유한 ID를 자동으로 부여
    val id: UUID = UUID.randomUUID(),
)
