package com.example.composeapp.models

/**
 * Mô hình dữ liệu cho bài học
 */
data class Lesson(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val level: String = "",
    val category: String = "",
    val order: Int = 0,
    val vocabularyIds: List<String> = emptyList(),
    val grammarPoints: List<GrammarPoint> = emptyList()
)

/**
 * Mô hình dữ liệu cho điểm ngữ pháp
 */
data class GrammarPoint(
    val rule: String = "",
    val explanation: String = "",
    val examples: List<String> = emptyList()
)

/**
 * Mô hình dữ liệu cho danh mục học tập
 */
data class Category(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconUrl: String = ""
)

/**
 * Mô hình dữ liệu cho tiến độ bài học
 */
data class LessonProgress(
    val lessonId: String = "",
    val completed: Boolean = false,
    val completedAt: Long = 0,
    val score: Int = 0, // 0-100
    val timeSpentMinutes: Int = 0
) 