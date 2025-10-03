package com.example.composeapp.models

/**
 * Models cho game học tiếng Nhật
 */

/**
 * Loại mini-game
 */
enum class GameType {
    WORD_PUZZLE,    // Ghép từ thành câu
    MEMORY_GAME,    // Ghép từ với nghĩa
    SPEED_QUIZ,     // Trả lời nhanh
    PRONUNCIATION   // Phát âm
}

/**
 * Độ khó game
 */
enum class GameDifficulty(val jlptLevels: List<JLPTLevel>) {
    EASY(listOf(JLPTLevel.N5)),                    // Dễ - N5
    MEDIUM(listOf(JLPTLevel.N4, JLPTLevel.N3)),    // Trung bình - N4-N3
    HARD(listOf(JLPTLevel.N2, JLPTLevel.N1))       // Khó - N2-N1
}

/**
 * Câu hỏi game
 */
data class GameQuestion(
    val id: String = "",
    val type: GameType = GameType.WORD_PUZZLE,
    val difficulty: GameDifficulty = GameDifficulty.EASY,
    val question: String = "",
    val correctAnswer: String = "",
    val options: List<String> = emptyList(), // Cho multiple choice
    val japaneseText: String = "", // Văn bản tiếng Nhật
    val englishText: String = "", // Nghĩa tiếng Anh
    val explanation: String = "", // Giải thích
    val points: Int = 10, // Điểm khi trả lời đúng
    val timeLimit: Int = 30 // Thời gian giới hạn (giây)
)

/**
 * Kết quả game
 */
data class GameResult(
    val gameId: String = "",
    val gameType: GameType = GameType.WORD_PUZZLE,
    val difficulty: GameDifficulty = GameDifficulty.EASY,
    val score: Int = 0,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val timeSpent: Long = 0, // Thời gian chơi (ms)
    val completedAt: Long = System.currentTimeMillis(),
    val streak: Int = 0, // Chuỗi trả lời đúng liên tiếp
    val maxStreak: Int = 0, // Chuỗi dài nhất
    val accuracy: Float = 0f // Độ chính xác (0.0 - 1.0)
)

/**
 * Thống kê người chơi
 */
data class PlayerStats(
    val userId: String = "",
    val totalGamesPlayed: Int = 0,
    val totalScore: Long = 0,
    val averageAccuracy: Float = 0f,
    val bestStreak: Int = 0,
    val gamesByType: Map<GameType, Int> = emptyMap(),
    val gamesByDifficulty: Map<GameDifficulty, Int> = emptyMap(),
    val lastPlayedAt: Long = System.currentTimeMillis(),
    val level: Int = 1,
    val experience: Int = 0,
    val achievements: List<String> = emptyList()
)

/**
 * Game Achievement (Thành tích game)
 */
data class GameAchievement(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val icon: String = "", // Emoji hoặc icon name
    val requirement: String = "", // Điều kiện để đạt được
    val points: Int = 0,
    val isUnlocked: Boolean = false,
    val unlockedAt: Long = 0L
)

/**
 * Game session hiện tại
 */
data class GameSession(
    val sessionId: String = "",
    val gameType: GameType = GameType.WORD_PUZZLE,
    val difficulty: GameDifficulty = GameDifficulty.EASY,
    val questions: List<GameQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val answers: MutableList<String> = mutableListOf(),
    val startTime: Long = System.currentTimeMillis(),
    val isCompleted: Boolean = false,
    val isPaused: Boolean = false
)

/**
 * Word cho Memory Game
 */
data class GameWord(
    val id: String = "",
    val japanese: String = "",
    val english: String = "",
    val hiragana: String = "",
    val difficulty: GameDifficulty = GameDifficulty.EASY,
    val category: String = "", // Thể loại từ vựng
    val isVisible: Boolean = false // Cho memory game
)

/**
 * Puzzle piece cho Word Puzzle
 */
data class PuzzlePiece(
    val id: String = "",
    val text: String = "",
    val isCorrect: Boolean = false,
    val isSelected: Boolean = false,
    val position: Int = 0 // Vị trí trong câu đúng
)

/**
 * Game Leaderboard entry
 */
data class GameLeaderboardEntry(
    val userId: String = "",
    val username: String = "",
    val score: Int = 0,
    val rank: Int = 0,
    val avatar: String = "",
    val lastPlayedAt: Long = System.currentTimeMillis()
)

/**
 * Extension functions cho GameDifficulty
 */
fun GameDifficulty.isUnlockedForUser(userLevelInfo: UserLevelInfo): Boolean {
    return jlptLevels.any { level -> userLevelInfo.isLevelUnlocked(level) }
}

fun GameDifficulty.getRequiredLevel(): JLPTLevel {
    return jlptLevels.minByOrNull { it.order } ?: JLPTLevel.N5
}

fun GameDifficulty.canPlayAtLevel(userLevel: JLPTLevel): Boolean {
    return jlptLevels.any { it.order <= userLevel.order }
}
