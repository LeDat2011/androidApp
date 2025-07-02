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

// Sample Data
object SampleData {
    val flashcards = listOf(
        // Cập nhật để sử dụng Flashcard từ Flashcard.kt
        Flashcard(
            japaneseWord = "こんにちは",
            vietnameseMeaning = "Xin chào",
            examples = listOf(Example("こんにちは、元気ですか？", "Xin chào, bạn khỏe không?"))
        ),

    )
}