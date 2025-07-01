package com.example.composeapp.models

import java.util.Date
import java.util.UUID

data class Flashcard(
    val id: String = UUID.randomUUID().toString(),
    val japaneseWord: String = "", // Từ tiếng Nhật
    val vietnameseMeaning: String = "", // Nghĩa tiếng Việt
    val reading: String = "", // Phiên âm tiếng Nhật
    val level: JapaneseLevel = JapaneseLevel.N5, // Mức độ JLPT
    val category: FlashcardCategory = FlashcardCategory.MISC, // Chủ đề
    val examples: List<Example> = emptyList(), // Các ví dụ
    val imageUrl: String? = null, // URL hình ảnh minh họa (nếu có)
    val dateAdded: Date = Date(), // Ngày thêm vào
    val viewCount: Int = 0, // Số lần xem
    val masteryLevel: MasteryLevel = MasteryLevel.NEW, // Mức độ thành thạo
    val lastReviewDate: Date? = null, // Ngày xem lại gần nhất
    val nextReviewDate: Date? = null, // Ngày nên xem lại tiếp theo
    val difficulty: Float = 0.3f, // Độ khó (0.0 - 1.0), mặc định trung bình thấp
    val correctCount: Int = 0, // Số lần trả lời đúng
    val incorrectCount: Int = 0 // Số lần trả lời sai
)

// Enum cho chủ đề
enum class FlashcardCategory(val displayName: String) {
    ANIMALS("Animals"),
    FOOD("Food"),
    TRANSPORTATION("Transportation"),
    WEATHER("Weather"),
    FAMILY("Family"),
    COLORS("Colors"),
    NUMBERS("Numbers"),
    TIME("Time"),
    VERBS("Verbs"),
    ADJECTIVES("Adjectives"),
    PLACES("Places"),
    BODY("Body Parts"),
    CLOTHING("Clothing"),
    EMOTIONS("Emotions"),
    NATURE("Nature"),
    DAILY_LIFE("Daily Life"),
    TECHNOLOGY("Technology"),
    HOBBIES("Hobbies"),
    MISC("Miscellaneous")
}

// Enum cho mức độ thành thạo
enum class MasteryLevel(val displayName: String, val color: String) {
    NEW("New", "#2196F3"),
    LEARNING("Learning", "#FF9800"),
    REVIEWING("Reviewing", "#8BC34A"),
    MASTERED("Mastered", "#4CAF50")
}

// Class cho các ví dụ
data class Example(
    val japanese: String, // Câu ví dụ tiếng Nhật
    val vietnamese: String // Nghĩa tiếng Việt
) 