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

data class UserProfile(
    val name: String,
    val level: Int,
    val levelTitle: String,
    val streak: Int,
    val wordsLearned: Int,
    val lessonsCompleted: Int,
    val daysActive: Int,
    val avatarUrl: String? = null
)

data class LearningTopic(
    val id: String,
    val title: String,
    val description: String,
    val imageUrl: String? = null,
    val difficulty: String,
    val wordCount: Int,
    val category: String
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

    val quizQuestions = listOf(
        QuizQuestion(
            "「こんにちは」 nghĩa là gì?",
            listOf("Xin chào", "Tạm biệt", "Cảm ơn", "Xin lỗi"),
            0,
            FlashcardCategory.DAILY_LIFE
        ),
        QuizQuestion(
            "「ありがとう」 nghĩa là gì?",
            listOf("Xin chào", "Tạm biệt", "Cảm ơn", "Xin lỗi"),
            2,
            FlashcardCategory.DAILY_LIFE
        ),
        QuizQuestion(
            "「犬」(いぬ) là con gì?",
            listOf("Mèo", "Chó", "Chuột", "Thỏ"),
            1,
            FlashcardCategory.ANIMALS
        ),
        QuizQuestion(
            "「寿司」(すし) là món ăn gì?",
            listOf("Mì Ramen", "Cơm", "Sushi", "Tempura"),
            2,
            FlashcardCategory.FOOD
        ),
        QuizQuestion(
            "「電車」(でんしゃ) là phương tiện gì?",
            listOf("Xe buýt", "Tàu điện", "Taxi", "Xe đạp"),
            1,
            FlashcardCategory.TRANSPORTATION
        )
    )
    
    val userProfile = UserProfile(
        name = "Minh",
        level = 3,
        levelTitle = "Người học chăm chỉ",
        streak = 7,
        wordsLearned = 120,
        lessonsCompleted = 15,
        daysActive = 14
    )
    
    val dailyProgress = 0.7f // 70% progress for today
    
    val learningProgress = LearningProgress(
        wordsLearned = 120,
        quizzesCompleted = 12,
        accuracy = 0.85f
    )
    
    val studyCategories = listOf(
        StudyCategory(
            id = "food_drinks",
            title = "Food & Drinks",
            wordCount = 42,
            emoji = "🍜"
        ),
        StudyCategory(
            id = "home",
            title = "Home",
            wordCount = 38,
            emoji = "🏠"
        ),
        StudyCategory(
            id = "travel",
            title = "Travel",
            wordCount = 56,
            emoji = "🚆"
        ),
        StudyCategory(
            id = "business",
            title = "Business",
            wordCount = 64,
            emoji = "🏢"
        )
    )
    
    val learningTopics = listOf(
        LearningTopic(
            id = "vocab_n5_1",
            title = "Từ vựng N5 - Cơ bản",
            description = "Học các từ vựng cơ bản cho người mới bắt đầu, phân loại theo chủ đề hàng ngày.",
            difficulty = "N5",
            wordCount = 100,
            category = "Từ vựng"
        ),
        LearningTopic(
            id = "vocab_n5_2",
            title = "Từ vựng N5 - Giao tiếp",
            description = "Các từ vựng thường dùng trong giao tiếp hàng ngày ở mức độ N5.",
            difficulty = "N5",
            wordCount = 80,
            category = "Từ vựng"
        ),
        LearningTopic(
            id = "grammar_n5_1",
            title = "Ngữ pháp N5 - Cơ bản",
            description = "Các cấu trúc ngữ pháp cơ bản ở trình độ N5, bao gồm thì, cách nối câu.",
            difficulty = "N5",
            wordCount = 50,
            category = "Ngữ pháp"
        ),
        LearningTopic(
            id = "grammar_n5_2",
            title = "Ngữ pháp N5 - Nâng cao",
            description = "Các cấu trúc ngữ pháp nâng cao hơn ở trình độ N5, cách diễn đạt ý kiến, cảm xúc.",
            difficulty = "N5",
            wordCount = 40,
            category = "Ngữ pháp"
        ),
        LearningTopic(
            id = "convo_n5_1",
            title = "Hội thoại N5 - Chào hỏi",
            description = "Các mẫu hội thoại thông dụng về chào hỏi, giới thiệu bản thân.",
            difficulty = "N5",
            wordCount = 30,
            category = "Hội thoại"
        ),
        LearningTopic(
            id = "convo_n5_2",
            title = "Hội thoại N5 - Mua sắm",
            description = "Các mẫu hội thoại thông dụng khi đi mua sắm, ở nhà hàng, quán ăn.",
            difficulty = "N5",
            wordCount = 35,
            category = "Hội thoại"
        ),
        LearningTopic(
            id = "reading_n5_1",
            title = "Đọc hiểu N5 - Cơ bản",
            description = "Các bài đọc ngắn với nội dung đơn giản, dễ hiểu cho người mới học.",
            difficulty = "N5",
            wordCount = 60,
            category = "Luyện đọc"
        ),
        LearningTopic(
            id = "listening_n5_1",
            title = "Luyện nghe N5 - Cơ bản",
            description = "Các bài nghe ngắn với tốc độ chậm, nội dung đơn giản cho người mới học.",
            difficulty = "N5",
            wordCount = 45,
            category = "Luyện nghe"
        )
    )
} 