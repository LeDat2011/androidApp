package com.example.composeapp.models

/**
 * Model cho tiến độ học tập chi tiết
 */
data class DetailedLearningProgress(
    val streak: Int = 0,
    val wordsLearned: Int = 0,
    val lessonsCompleted: Int = 0,
    val daysActive: Int = 0,
    val lastActiveDate: Long = System.currentTimeMillis(),
    val learningProgress: Float = 0.0f,
    val readyForLevelUp: Boolean = false,
    val totalStudyTimeMinutes: Int = 0
)

/**
 * Model cho cài đặt người dùng
 */
data class UserSettings(
    val studyTimeMinutes: Int = 30,
    val notificationsEnabled: Boolean = true,
    val dailyGoalEnabled: Boolean = true,
    val soundEnabled: Boolean = true,
    val theme: String = "system"
)

/**
 * Model cho dữ liệu học tập
 */
data class LearningData(
    val vocabulary: Map<String, Boolean> = emptyMap(),
    val completedLessons: Map<String, Long> = emptyMap(),
    val quizResults: Map<String, QuizResult> = emptyMap(),
    val flashcardProgress: Map<String, FlashcardProgress> = emptyMap(),
    val studyStreak: StudyStreak = StudyStreak()
)

/**
 * Model cho kết quả quiz
 */
data class FirebaseQuizResult(
    val quizId: String = "",
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val timeSpentSeconds: Int = 0,
    val completedAt: Long = System.currentTimeMillis(),
    val category: String = "",
    val level: String = ""
)

/**
 * Model cho tiến độ flashcard
 */
data class FlashcardProgress(
    val wordId: String = "",
    val masteryLevel: String = "NEW",
    val correctCount: Int = 0,
    val incorrectCount: Int = 0,
    val lastReviewDate: Long = System.currentTimeMillis(),
    val nextReviewDate: Long = System.currentTimeMillis(),
    val difficulty: Float = 0.3f
)

/**
 * Model cho chuỗi học tập
 */
data class StudyStreak(
    val currentStreak: Int = 0,
    val longestStreak: Int = 0,
    val lastStudyDate: Long? = null
)

/**
 * Model cho thống kê học tập
 */
data class LearningStats(
    val studyTime: Map<String, Int> = emptyMap(), // date -> minutes
    val dailyProgress: Map<String, DailyProgress> = emptyMap(),
    val categoryProgress: Map<String, CategoryProgress> = emptyMap()
)

/**
 * Model cho tiến độ hàng ngày
 */
data class DailyProgress(
    val wordsLearned: Int = 0,
    val lessonsCompleted: Int = 0,
    val quizCompleted: Int = 0,
    val studyTimeMinutes: Int = 0
)

/**
 * Model cho tiến độ theo danh mục
 */
data class CategoryProgress(
    val wordsLearned: Int = 0,
    val lessonsCompleted: Int = 0,
    val accuracy: Float = 0.0f
)

/**
 * Model cho thành tích
 */
data class Achievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val type: String = "", // lesson, vocabulary, streak, quiz, level
    val target: Int = 0,
    val points: Int = 0,
    val rarity: String = "common", // common, uncommon, rare, epic, legendary
    val unlocked: Boolean = false,
    val unlockedAt: Long? = null
)

/**
 * Model cho từ vựng Firebase
 */
data class FirebaseVocabulary(
    val id: String = "",
    val japanese: String = "",
    val reading: String = "",
    val vietnamese: String = "",
    val level: String = "",
    val categories: List<String> = emptyList(),
    val exampleSentences: List<ExampleSentence> = emptyList(),
    val difficulty: Float = 0.3f,
    val masteryLevel: String = "NEW",
    val imageUrl: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Model cho bài học Firebase
 */
data class FirebaseLesson(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val level: String = "",
    val category: String = "",
    val order: Int = 0,
    val vocabularyIds: List<String> = emptyList(),
    val grammarPoints: List<GrammarPoint> = emptyList(),
    val estimatedTimeMinutes: Int = 15,
    val difficulty: Float = 0.3f,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

/**
 * Model cho danh mục Firebase
 */
data class FirebaseCategory(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconUrl: String = "",
    val color: String = "",
    val order: Int = 0,
    val vocabularyCount: Int = 0,
    val lessonCount: Int = 0
)

/**
 * Model cho cài đặt hệ thống
 */
data class SystemSettings(
    val appVersion: String = "1.0.0",
    val minSupportedVersion: String = "1.0.0",
    val maintenanceMode: Boolean = false,
    val features: SystemFeatures = SystemFeatures(),
    val contentUpdates: ContentUpdates = ContentUpdates()
)

/**
 * Model cho tính năng hệ thống
 */
data class SystemFeatures(
    val quizEnabled: Boolean = true,
    val flashcardEnabled: Boolean = true,
    val achievementsEnabled: Boolean = true,
    val socialFeaturesEnabled: Boolean = false,
    val offlineModeEnabled: Boolean = true
)

/**
 * Model cho cập nhật nội dung
 */
data class ContentUpdates(
    val lastUpdate: Long = System.currentTimeMillis(),
    val nextUpdate: Long = System.currentTimeMillis(),
    val autoUpdate: Boolean = true
)

/**
 * Model cho câu hỏi quiz
 */
data class FirebaseQuizQuestion(
    val id: String = "",
    val question: String = "",
    val type: String = "", // MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, MATCHING
    val options: Map<String, String> = emptyMap(),
    val correctAnswer: String = "",
    val explanation: String = "",
    val points: Int = 10
)

/**
 * Model cho quiz
 */
data class FirebaseQuiz(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val category: String = "",
    val level: String = "",
    val questions: Map<String, FirebaseQuizQuestion> = emptyMap(),
    val timeLimit: Int = 600, // seconds
    val icon: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
