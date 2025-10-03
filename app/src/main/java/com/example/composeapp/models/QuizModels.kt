package com.example.composeapp.models

data class Quiz(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val timeLimit: Int = 0,
    val questions: Map<String, Question> = emptyMap()
)

data class Question(
    val id: String = "",
    val type: QuestionType = QuestionType.MULTIPLE_CHOICE,
    val question: String = "",
    val options: Map<String, String>? = null,
    val correctAnswer: String = "",
    val explanation: String = "",
    val points: Int = 0,
    val audioUrl: String? = null
)

enum class QuestionType {
    MULTIPLE_CHOICE,
    TRUE_FALSE,
    FILL_BLANK,
    MATCHING,
    AUDIO_CHOICE
}

data class QuizResult(
    val score: Int = 0,
    val completedAt: String = "",
    val timeTaken: Int = 0,
    val correctAnswers: Int = 0,
    val totalQuestions: Int = 0
)

data class UserProgress(
    val completedQuizzes: Map<String, QuizResult> = emptyMap(),
    val currentLevel: String = "N5",
    val totalPoints: Int = 0,
    val achievements: Achievements = Achievements()
)

data class Achievements(
    val perfectScore: Int = 0,
    val fastCompletion: Int = 0,
    val streakDays: Int = 0
)

data class LeaderboardEntry(
    val name: String = "",
    val points: Int = 0,
    val rank: Int = 0
)

// Thêm các model mới cho cấu trúc quiz từ Firebase
data class QuizCategory(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "",
    val iconUrl: String? = null,
    val color: String? = null,
    val levels: Map<String, QuizLevel> = emptyMap()
)

data class QuizLevel(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val quizzes: List<Quiz> = emptyList(),
    val color: String = "#9C27B0"
) 