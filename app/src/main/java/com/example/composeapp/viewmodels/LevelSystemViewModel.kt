package com.example.composeapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LevelSystemViewModel : ViewModel() {
    
    // State cho thông tin level của user
    private val _userLevelInfo = MutableStateFlow(UserLevelInfo(userId = "current_user"))
    val userLevelInfo: StateFlow<UserLevelInfo> = _userLevelInfo.asStateFlow()
    
    // State cho danh sách lessons hiện tại
    private val _currentLessons = MutableStateFlow<List<Lesson>>(emptyList())
    val currentLessons: StateFlow<List<Lesson>> = _currentLessons.asStateFlow()
    
    // State cho loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // State cho achievements
    private val _achievements = MutableStateFlow<List<LevelAchievement>>(emptyList())
    val achievements: StateFlow<List<LevelAchievement>> = _achievements.asStateFlow()
    
    init {
        initializeLevelSystem()
        loadAchievements()
    }
    
    /**
     * Khởi tạo hệ thống level
     */
    private fun initializeLevelSystem() {
        viewModelScope.launch {
            _isLoading.value = true
            
            // Tạo progress cho tất cả levels và categories
            val levelProgressMap = mutableMapOf<String, LevelProgress>()
            
            LearningCategory.values().forEach { category ->
                JLPTLevel.values().forEach { level ->
                    val key = "${level.name}_${category.name}"
                    val isUnlocked = level == JLPTLevel.N5 || isLevelUnlocked(level)
                    
                    levelProgressMap[key] = LevelProgress(
                        level = level,
                        category = category,
                        totalLessons = getTotalLessonsForLevel(level, category),
                        status = if (isUnlocked) LevelStatus.AVAILABLE else LevelStatus.LOCKED,
                        isUnlocked = isUnlocked
                    )
                }
            }
            
            val updatedUserInfo = _userLevelInfo.value.copy(
                levelProgress = levelProgressMap,
                unlockedLevels = setOf(JLPTLevel.N5) // Bắt đầu với N5
            )
            
            _userLevelInfo.value = updatedUserInfo
            
            // Load lessons cho level hiện tại
            loadLessonsForCurrentLevel()
            
            _isLoading.value = false
        }
    }
    
    /**
     * Load lessons cho level hiện tại của user
     */
    fun loadLessonsForCurrentLevel() {
        val currentUser = _userLevelInfo.value
        val currentLevel = currentUser.currentLevel
        
        viewModelScope.launch {
            _isLoading.value = true
            
            val lessons = mutableListOf<Lesson>()
            
            // Load lessons cho từng category của level hiện tại
            LearningCategory.values().forEach { category ->
                val categoryLessons = createLessonsForLevel(currentLevel, category)
                lessons.addAll(categoryLessons)
            }
            
            _currentLessons.value = lessons
            _isLoading.value = false
        }
    }
    
    /**
     * Tạo lessons cho một level và category cụ thể
     */
    private fun createLessonsForLevel(level: JLPTLevel, category: LearningCategory): List<Lesson> {
        return when (category) {
            LearningCategory.VOCABULARY -> createVocabularyLessons(level)
            LearningCategory.GRAMMAR -> createGrammarLessons(level)
            LearningCategory.KANJI -> createKanjiLessons(level)
            LearningCategory.READING -> createReadingLessons(level)
            LearningCategory.LISTENING -> createListeningLessons(level)
            LearningCategory.GAMES -> createGameLessons(level)
        }
    }
    
    /**
     * Tạo vocabulary lessons
     */
    private fun createVocabularyLessons(level: JLPTLevel): List<Lesson> {
        return when (level) {
            JLPTLevel.N5 -> listOf(
                Lesson(
                    id = "vocab_n5_1",
                    title = "Từ vựng cơ bản N5 - Phần 1",
                    description = "Học 50 từ vựng cơ bản nhất",
                    level = level,
                    category = LearningCategory.VOCABULARY,
                    content = LessonContent.VocabularyLesson(
                        words = createN5VocabularyWords(1),
                        exercises = createN5VocabularyExercises(1)
                    ),
                    estimatedTime = 20
                ),
                Lesson(
                    id = "vocab_n5_2", 
                    title = "Từ vựng cơ bản N5 - Phần 2",
                    description = "Học 50 từ vựng cơ bản (tiếp theo)",
                    level = level,
                    category = LearningCategory.VOCABULARY,
                    content = LessonContent.VocabularyLesson(
                        words = createN5VocabularyWords(2),
                        exercises = createN5VocabularyExercises(2)
                    ),
                    estimatedTime = 20,
                    prerequisites = listOf("vocab_n5_1")
                )
            )
            JLPTLevel.N4 -> listOf(
                Lesson(
                    id = "vocab_n4_1",
                    title = "Từ vựng N4 - Phần 1", 
                    description = "Học từ vựng N4 cơ bản",
                    level = level,
                    category = LearningCategory.VOCABULARY,
                    content = LessonContent.VocabularyLesson(
                        words = createN4VocabularyWords(1),
                        exercises = createN4VocabularyExercises(1)
                    ),
                    estimatedTime = 25
                )
            )
            else -> emptyList()
        }
    }
    
    /**
     * Tạo grammar lessons
     */
    private fun createGrammarLessons(level: JLPTLevel): List<Lesson> {
        return when (level) {
            JLPTLevel.N5 -> listOf(
                Lesson(
                    id = "grammar_n5_1",
                    title = "Ngữ pháp cơ bản N5",
                    description = "Học các cấu trúc ngữ pháp cơ bản",
                    level = level,
                    category = LearningCategory.GRAMMAR,
                    content = LessonContent.GrammarLesson(
                        grammarPoints = createN5GrammarPoints(),
                        examples = createN5GrammarExamples(),
                        exercises = createN5GrammarExercises()
                    ),
                    estimatedTime = 30
                )
            )
            else -> emptyList()
        }
    }
    
    /**
     * Tạo các loại lessons khác (placeholder)
     */
    private fun createKanjiLessons(level: JLPTLevel): List<Lesson> = emptyList()
    private fun createReadingLessons(level: JLPTLevel): List<Lesson> = emptyList()
    private fun createListeningLessons(level: JLPTLevel): List<Lesson> = emptyList()
    private fun createGameLessons(level: JLPTLevel): List<Lesson> = emptyList()
    
    /**
     * Hoàn thành một lesson
     */
    fun completeLesson(lessonId: String, score: Float, timeSpent: Long) {
        viewModelScope.launch {
            val currentUser = _userLevelInfo.value
            val lesson = _currentLessons.value.find { it.id == lessonId } ?: return@launch
            
            // Cập nhật lesson completion
            val updatedLessons = _currentLessons.value.map { l ->
                if (l.id == lessonId) {
                    l.copy(isCompleted = true, completionDate = System.currentTimeMillis())
                } else l
            }
            _currentLessons.value = updatedLessons
            
            // Cập nhật level progress
            val key = "${lesson.level.name}_${lesson.category.name}"
            val currentProgress = currentUser.levelProgress[key] ?: LevelProgress(
                level = lesson.level,
                category = lesson.category
            )
            
            val updatedProgress = currentProgress.copy(
                completedLessons = currentProgress.completedLessons + 1,
                correctAnswers = currentProgress.correctAnswers + (score * 10).toInt(),
                timeSpent = currentProgress.timeSpent + timeSpent,
                lastAccessed = System.currentTimeMillis(),
                masteryScore = (currentProgress.masteryScore + score) / 2f,
                completionPercentage = if (currentProgress.totalLessons > 0) {
                    ((currentProgress.completedLessons + 1).toFloat() / currentProgress.totalLessons) * 100f
                } else 0f
            )
            
            val updatedProgressMap = currentUser.levelProgress.toMutableMap()
            updatedProgressMap[key] = updatedProgress
            
            // Cập nhật user info
            val updatedUser = currentUser.copy(
                levelProgress = updatedProgressMap,
                totalExperience = currentUser.totalExperience + (score * 100).toLong(),
                totalStudyTime = currentUser.totalStudyTime + timeSpent
            )
            
            _userLevelInfo.value = updatedUser
            
            // Kiểm tra xem có thể unlock level tiếp theo không
            checkAndUnlockNextLevel()
            
            // Kiểm tra achievements
            checkAchievements()
        }
    }
    
    /**
     * Kiểm tra và unlock level tiếp theo
     */
    private fun checkAndUnlockNextLevel() {
        val currentUser = _userLevelInfo.value
        
        if (currentUser.canUnlockNextLevel()) {
            val nextLevel = currentUser.getNextUnlockableLevel()
            if (nextLevel != null) {
                val updatedUnlockedLevels = currentUser.unlockedLevels + nextLevel
                val updatedUser = currentUser.copy(
                    unlockedLevels = updatedUnlockedLevels,
                    currentLevel = nextLevel
                )
                _userLevelInfo.value = updatedUser
                
                // Load lessons cho level mới
                loadLessonsForCurrentLevel()
            }
        }
    }
    
    /**
     * Load achievements
     */
    private fun loadAchievements() {
        val defaultAchievements = listOf(
            LevelAchievement(
                id = "first_lesson",
                title = "Bài học đầu tiên",
                description = "Hoàn thành bài học đầu tiên",
                icon = "🎓",
                requirement = AchievementRequirement.CompleteLevel(JLPTLevel.N5, LearningCategory.VOCABULARY),
                reward = AchievementReward(experience = 100L)
            ),
            LevelAchievement(
                id = "n5_master",
                title = "Bậc thầy N5",
                description = "Hoàn thành tất cả bài học N5",
                icon = "🏆",
                requirement = AchievementRequirement.CompleteAllLevels(JLPTLevel.N5),
                reward = AchievementReward(
                    experience = 1000L,
                    unlockLevel = JLPTLevel.N4
                )
            ),
            LevelAchievement(
                id = "study_streak_7",
                title = "Tuần học tập",
                description = "Học liên tục 7 ngày",
                icon = "🔥",
                requirement = AchievementRequirement.StudyStreak(7),
                reward = AchievementReward(experience = 500L)
            )
        )
        
        _achievements.value = defaultAchievements
    }
    
    /**
     * Kiểm tra achievements
     */
    private fun checkAchievements() {
        val currentUser = _userLevelInfo.value
        val updatedAchievements = _achievements.value.map { achievement ->
            if (!achievement.isUnlocked) {
                val isUnlocked = when (achievement.requirement) {
                    is AchievementRequirement.CompleteLevel -> {
                        val key = "${achievement.requirement.level.name}_${achievement.requirement.category.name}"
                        val progress = currentUser.levelProgress[key]
                        progress?.completionPercentage ?: 0f >= 100f
                    }
                    is AchievementRequirement.CompleteAllLevels -> {
                        val levelCategories = LearningCategory.values()
                        levelCategories.all { category ->
                            val key = "${achievement.requirement.level.name}_${category.name}"
                            val progress = currentUser.levelProgress[key]
                            progress?.completionPercentage ?: 0f >= 100f
                        }
                    }
                    is AchievementRequirement.StudyStreak -> {
                        currentUser.studyStreak >= achievement.requirement.days
                    }
                    else -> false
                }
                
                if (isUnlocked) {
                    achievement.copy(
                        isUnlocked = true,
                        unlockedDate = System.currentTimeMillis()
                    )
                } else achievement
            } else achievement
        }
        
        _achievements.value = updatedAchievements
    }
    
    /**
     * Lấy lessons cho một level và category cụ thể
     */
    fun getLessonsForLevel(level: JLPTLevel, category: LearningCategory): List<Lesson> {
        return _currentLessons.value.filter { it.level == level && it.category == category }
    }
    
    /**
     * Kiểm tra xem level có được unlock không
     */
    private fun isLevelUnlocked(level: JLPTLevel): Boolean {
        return _userLevelInfo.value.isLevelUnlocked(level)
    }
    
    /**
     * Lấy tổng số lessons cho một level và category
     */
    private fun getTotalLessonsForLevel(level: JLPTLevel, category: LearningCategory): Int {
        return when (level) {
            JLPTLevel.N5 -> when (category) {
                LearningCategory.VOCABULARY -> 10
                LearningCategory.GRAMMAR -> 8
                LearningCategory.KANJI -> 12
                LearningCategory.READING -> 6
                LearningCategory.LISTENING -> 5
                LearningCategory.GAMES -> 15
            }
            JLPTLevel.N4 -> when (category) {
                LearningCategory.VOCABULARY -> 15
                LearningCategory.GRAMMAR -> 12
                LearningCategory.KANJI -> 18
                LearningCategory.READING -> 10
                LearningCategory.LISTENING -> 8
                LearningCategory.GAMES -> 20
            }
            else -> 0
        }
    }
    
    // Placeholder functions để tạo dữ liệu mẫu
    private fun createN5VocabularyWords(part: Int): List<VocabularyWord> = emptyList()
    private fun createN5VocabularyExercises(part: Int): List<VocabularyExercise> = emptyList()
    private fun createN4VocabularyWords(part: Int): List<VocabularyWord> = emptyList()
    private fun createN4VocabularyExercises(part: Int): List<VocabularyExercise> = emptyList()
    private fun createN5GrammarPoints(): List<GrammarPoint> = emptyList()
    private fun createN5GrammarExamples(): List<GrammarExample> = emptyList()
    private fun createN5GrammarExercises(): List<GrammarExercise> = emptyList()
}