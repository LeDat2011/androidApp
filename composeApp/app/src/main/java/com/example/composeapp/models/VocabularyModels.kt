package com.example.composeapp.models

/**
 * Mô hình dữ liệu cho từ vựng tiếng Nhật
 */
data class VocabularyItem(
    val id: String = "",
    val japanese: String = "",
    val reading: String = "",
    val vietnamese: String = "",
    val level: String = "",
    val categories: List<String> = emptyList(),
    val exampleSentences: List<ExampleSentence> = emptyList()
)

/**
 * Mô hình dữ liệu cho câu ví dụ
 */
data class ExampleSentence(
    val japanese: String = "",
    val vietnamese: String = ""
)

/**
 * Mô hình dữ liệu cho kết quả học từ vựng
 */
data class VocabularyProgress(
    val wordId: String = "",
    val reviewCount: Int = 0,
    val correctCount: Int = 0,
    val lastReviewDate: Long = 0,
    val nextReviewDate: Long = 0,
    val strength: Float = 0f // 0-1, độ mạnh của trí nhớ
) 