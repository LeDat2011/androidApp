package com.example.composeapp.models

/**
 * Models cho hệ thống level và progressive unlock
 */

/**
 * JLPT Level enum
 */
enum class JLPTLevel(val displayName: String, val order: Int, val description: String) {
    N5("N5", 1, "Beginner - 800 kanji, 1000 words"),
    N4("N4", 2, "Elementary - 300 kanji, 1500 words"), 
    N3("N3", 3, "Intermediate - 650 kanji, 3750 words"),
    N2("N2", 4, "Pre-Advanced - 1000 kanji, 6000 words"),
    N1("N1", 5, "Advanced - 2000 kanji, 10000 words")
}

/**
 * Category của nội dung học
 */
enum class LearningCategory(val displayName: String, val icon: String) {
    VOCABULARY("Từ vựng", "📚"),
    GRAMMAR("Ngữ pháp", "📝"),
    KANJI("Kanji", "🈲"),
    READING("Đọc hiểu", "📖"),
    LISTENING("Nghe hiểu", "👂"),
    GAMES("Games", "🎮")
}

/**
 * Trạng thái unlock của một level
 */
enum class LevelStatus {
    LOCKED,     // Bị khóa - chưa thể truy cập
    AVAILABLE,  // Có thể truy cập - đã unlock
    IN_PROGRESS,// Đang học
    COMPLETED   // Đã hoàn thành
}

/**
 * Thông tin progress của một level
 */
data class LevelProgress(
    val level: JLPTLevel,
    val category: LearningCategory,
    val totalLessons: Int = 0,
    val completedLessons: Int = 0,
    val totalQuestions: Int = 0,
    val correctAnswers: Int = 0,
    val timeSpent: Long = 0L, // milliseconds
    val lastAccessed: Long = System.currentTimeMillis(),
    val status: LevelStatus = LevelStatus.LOCKED,
    val completionPercentage: Float = 0f,
    val masteryScore: Float = 0f, // 0-100
    val streak: Int = 0,
    val isUnlocked: Boolean = false
) {
    val progressPercentage: Float
        get() = if (totalLessons > 0) (completedLessons.toFloat() / totalLessons) * 100f else 0f
}

/**
 * Thông tin user level tổng thể
 */
data class UserLevelInfo(
    val userId: String,
    val currentLevel: JLPTLevel = JLPTLevel.N5,
    val totalExperience: Long = 0L,
    val levelProgress: Map<String, LevelProgress> = emptyMap(), // Key: "${level}_${category}"
    val achievements: List<String> = emptyList(),
    val studyStreak: Int = 0,
    val totalStudyTime: Long = 0L,
    val lastStudyDate: Long = System.currentTimeMillis(),
    val unlockedLevels: Set<JLPTLevel> = setOf(JLPTLevel.N5), // Bắt đầu với N5
    val completedCategories: Map<JLPTLevel, Set<LearningCategory>> = emptyMap()
) {
    /**
     * Lấy progress của một level và category cụ thể
     */
    fun getLevelProgress(level: JLPTLevel, category: LearningCategory): LevelProgress {
        val key = "${level.name}_${category.name}"
        return levelProgress[key] ?: LevelProgress(
            level = level,
            category = category,
            isUnlocked = isLevelUnlocked(level)
        )
    }
    
    /**
     * Kiểm tra xem level có được unlock không
     */
    fun isLevelUnlocked(level: JLPTLevel): Boolean {
        return level in unlockedLevels
    }
    
    /**
     * Kiểm tra xem có thể unlock level tiếp theo không
     */
    fun canUnlockNextLevel(): Boolean {
        val currentLevelOrder = currentLevel.order
        val nextLevelOrder = currentLevelOrder + 1
        
        // Kiểm tra xem đã hoàn thành ít nhất 80% các category của level hiện tại chưa
        val currentLevelCategories = LearningCategory.values()
        val completedCurrentCategories = completedCategories[currentLevel]?.size ?: 0
        val completionRate = if (currentLevelCategories.isNotEmpty()) {
            completedCurrentCategories.toFloat() / currentLevelCategories.size
        } else 0f
        
        return completionRate >= 0.8f && nextLevelOrder <= JLPTLevel.values().size
    }
    
    /**
     * Lấy level tiếp theo có thể unlock
     */
    fun getNextUnlockableLevel(): JLPTLevel? {
        if (!canUnlockNextLevel()) return null
        
        val currentOrder = currentLevel.order
        return JLPTLevel.values().find { it.order == currentOrder + 1 }
    }
}

/**
 * Bài học trong một level
 */
data class Lesson(
    val id: String,
    val title: String,
    val description: String,
    val level: JLPTLevel,
    val category: LearningCategory,
    val content: LessonContent,
    val estimatedTime: Int = 15, // minutes
    val difficulty: Float = 1.0f, // 1.0 - 5.0
    val prerequisites: List<String> = emptyList(), // Lesson IDs cần hoàn thành trước
    val isUnlocked: Boolean = false,
    val isCompleted: Boolean = false,
    val completionDate: Long = 0L
)

/**
 * Nội dung bài học
 */
sealed class LessonContent {
    data class VocabularyLesson(
        val words: List<VocabularyWord>,
        val exercises: List<VocabularyExercise>
    ) : LessonContent()
    
    data class GrammarLesson(
        val grammarPoints: List<GrammarPoint>,
        val examples: List<GrammarExample>,
        val exercises: List<GrammarExercise>
    ) : LessonContent()
    
    data class KanjiLesson(
        val kanji: List<KanjiInfo>,
        val exercises: List<KanjiExercise>
    ) : LessonContent()
    
    data class ReadingLesson(
        val passages: List<ReadingPassage>,
        val questions: List<ReadingQuestion>
    ) : LessonContent()
}

/**
 * Từ vựng
 */
data class VocabularyWord(
    val id: String,
    val japanese: String,
    val reading: String,
    val english: String,
    val vietnamese: String = "",
    val level: JLPTLevel,
    val category: String = "",
    val examples: List<String> = emptyList(),
    val isLearned: Boolean = false,
    val masteryLevel: Float = 0f // 0-1
)

/**
 * Bài tập từ vựng
 */
data class VocabularyExercise(
    val id: String,
    val type: ExerciseType,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String = "",
    val difficulty: Float = 1.0f
)

/**
 * Điểm ngữ pháp
 */
data class GrammarPoint(
    val id: String,
    val title: String,
    val explanation: String,
    val structure: String,
    val usage: String,
    val level: JLPTLevel
)

/**
 * Ví dụ ngữ pháp
 */
data class GrammarExample(
    val japanese: String,
    val reading: String,
    val english: String,
    val vietnamese: String = ""
)

/**
 * Bài tập ngữ pháp
 */
data class GrammarExercise(
    val id: String,
    val type: ExerciseType,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String = ""
)

/**
 * Thông tin Kanji
 */
data class KanjiInfo(
    val id: String,
    val character: String,
    val readings: List<String>,
    val meanings: List<String>,
    val level: JLPTLevel,
    val strokeCount: Int,
    val radicals: List<String> = emptyList(),
    val examples: List<String> = emptyList()
)

/**
 * Bài tập Kanji
 */
data class KanjiExercise(
    val id: String,
    val type: ExerciseType,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String = ""
)

/**
 * Đoạn văn đọc
 */
data class ReadingPassage(
    val id: String,
    val title: String,
    val content: String,
    val level: JLPTLevel,
    val estimatedTime: Int = 5 // minutes
)

/**
 * Câu hỏi đọc hiểu
 */
data class ReadingQuestion(
    val id: String,
    val question: String,
    val options: List<String>,
    val correctAnswer: String,
    val explanation: String = ""
)

/**
 * Loại bài tập
 */
enum class ExerciseType {
    MULTIPLE_CHOICE,
    FILL_IN_BLANK,
    MATCHING,
    TRUE_FALSE,
    TRANSLATION
}

/**
 * Achievement trong hệ thống level
 */
data class LevelAchievement(
    val id: String,
    val title: String,
    val description: String,
    val icon: String,
    val requirement: AchievementRequirement,
    val reward: AchievementReward,
    val isUnlocked: Boolean = false,
    val unlockedDate: Long = 0L
)

/**
 * Điều kiện để đạt achievement
 */
sealed class AchievementRequirement {
    data class CompleteLevel(val level: JLPTLevel, val category: LearningCategory) : AchievementRequirement()
    data class CompleteAllLevels(val level: JLPTLevel) : AchievementRequirement()
    data class StudyStreak(val days: Int) : AchievementRequirement()
    data class StudyTime(val totalMinutes: Long) : AchievementRequirement()
    data class MasteryScore(val level: JLPTLevel, val minScore: Float) : AchievementRequirement()
}

/**
 * Phần thưởng achievement
 */
data class AchievementReward(
    val experience: Long = 0L,
    val unlockLevel: JLPTLevel? = null,
    val title: String = "",
    val description: String = ""
)
