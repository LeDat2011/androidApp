package com.example.composeapp.models

// Flashcard đã được định nghĩa trong Flashcard.kt

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int,
    val category: FlashcardCategory = FlashcardCategory.MISC
)

data class LearningProgress(
    val wordsLearned: Int,
    val quizzesCompleted: Int,
    val accuracy: Float
)

data class StudyCategory(
    val id: String,
    val title: String,
    val wordCount: Int,
    val emoji: String
)

// Sample Data - Đã được thay thế bằng data từ Firebase