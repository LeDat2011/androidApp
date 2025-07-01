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
        Flashcard(
            japaneseWord = "ありがとう",
            vietnameseMeaning = "Cảm ơn",
            examples = listOf(Example("ありがとうございます。", "Cảm ơn rất nhiều."))
        ),
        Flashcard(
            japaneseWord = "さようなら",
            vietnameseMeaning = "Tạm biệt",
            examples = listOf(Example("さようなら、また明日。", "Tạm biệt, hẹn gặp lại ngày mai."))
        ),
        Flashcard(
            japaneseWord = "食べ物",
            vietnameseMeaning = "Thức ăn",
            examples = listOf(Example("私は日本の食べ物が好きです。", "Tôi thích đồ ăn Nhật Bản."))
        ),
        Flashcard(
            japaneseWord = "飲み物",
            vietnameseMeaning = "Đồ uống",
            examples = listOf(Example("何か飲み物はいかがですか？", "Bạn muốn uống gì không?"))
        ),
        Flashcard(
            japaneseWord = "水",
            vietnameseMeaning = "Nước",
            examples = listOf(Example("水をください。", "Vui lòng cho tôi nước."))
        )
    )
} 