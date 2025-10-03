package com.example.composeapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay
import java.util.*

class GameViewModel : ViewModel() {
    
    // State cho danh sách câu hỏi
    private val _questions = MutableStateFlow<List<GameQuestion>>(emptyList())
    val questions: StateFlow<List<GameQuestion>> = _questions.asStateFlow()
    
    // State cho session game hiện tại
    private val _currentSession = MutableStateFlow<GameSession?>(null)
    val currentSession: StateFlow<GameSession?> = _currentSession.asStateFlow()
    
    // State cho kết quả game
    private val _gameResult = MutableStateFlow<GameResult?>(null)
    val gameResult: StateFlow<GameResult?> = _gameResult.asStateFlow()
    
    // State cho thống kê người chơi
    private val _playerStats = MutableStateFlow(PlayerStats())
    val playerStats: StateFlow<PlayerStats> = _playerStats.asStateFlow()
    
    // State cho loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // State cho timer
    private val _timeRemaining = MutableStateFlow(30)
    val timeRemaining: StateFlow<Int> = _timeRemaining.asStateFlow()
    
    // State cho score hiện tại
    private val _currentScore = MutableStateFlow(0)
    val currentScore: StateFlow<Int> = _currentScore.asStateFlow()
    
    // State cho streak
    private val _currentStreak = MutableStateFlow(0)
    val currentStreak: StateFlow<Int> = _currentStreak.asStateFlow()
    
    // State cho achievements
    private val _achievements = MutableStateFlow<List<GameAchievement>>(emptyList())
    val achievements: StateFlow<List<GameAchievement>> = _achievements.asStateFlow()
    
    // State cho thông tin level của user
    private val _userLevelInfo = MutableStateFlow(UserLevelInfo(userId = "current_user"))
    val userLevelInfo: StateFlow<UserLevelInfo> = _userLevelInfo.asStateFlow()
    
    init {
        loadDefaultQuestions()
        loadDefaultAchievements()
    }
    
    /**
     * Tải câu hỏi mặc định
     */
    private fun loadDefaultQuestions() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val defaultQuestions = listOf(
                // Word Puzzle - Easy
                GameQuestion(
                    id = "wp_1",
                    type = GameType.WORD_PUZZLE,
                    difficulty = GameDifficulty.EASY,
                    question = "Sắp xếp các từ sau thành câu có nghĩa:",
                    japaneseText = "わたし は がっこう に いきます",
                    englishText = "I go to school",
                    correctAnswer = "わたしはがっこうにいきます",
                    options = listOf("わたし", "は", "がっこう", "に", "いきます"),
                    explanation = "Đây là câu đơn giản về việc đi học",
                    points = 10,
                    timeLimit = 45
                ),
                GameQuestion(
                    id = "wp_2",
                    type = GameType.WORD_PUZZLE,
                    difficulty = GameDifficulty.EASY,
                    question = "Sắp xếp các từ sau thành câu có nghĩa:",
                    japaneseText = "にほんご を べんきょう します",
                    englishText = "I study Japanese",
                    correctAnswer = "にほんごをべんきょうします",
                    options = listOf("にほんご", "を", "べんきょう", "します"),
                    explanation = "Câu về việc học tiếng Nhật",
                    points = 10,
                    timeLimit = 45
                ),
                GameQuestion(
                    id = "wp_3",
                    type = GameType.WORD_PUZZLE,
                    difficulty = GameDifficulty.EASY,
                    question = "Sắp xếp các từ sau thành câu có nghĩa:",
                    japaneseText = "あさごはん を たべます",
                    englishText = "I eat breakfast",
                    correctAnswer = "あさごはんをたべます",
                    options = listOf("あさごはん", "を", "たべます"),
                    explanation = "Câu về việc ăn sáng",
                    points = 10,
                    timeLimit = 45
                ),
                
                // Memory Game - Easy
                GameQuestion(
                    id = "mg_1",
                    type = GameType.MEMORY_GAME,
                    difficulty = GameDifficulty.EASY,
                    question = "Chọn nghĩa đúng của từ:",
                    japaneseText = "みず",
                    correctAnswer = "water",
                    options = listOf("water", "fire", "earth", "air"),
                    explanation = "みず có nghĩa là nước",
                    points = 15,
                    timeLimit = 25
                ),
                GameQuestion(
                    id = "mg_2",
                    type = GameType.MEMORY_GAME,
                    difficulty = GameDifficulty.EASY,
                    question = "Chọn nghĩa đúng của từ:",
                    japaneseText = "ほん",
                    correctAnswer = "book",
                    options = listOf("book", "pen", "paper", "bag"),
                    explanation = "ほん có nghĩa là sách",
                    points = 15,
                    timeLimit = 25
                ),
                GameQuestion(
                    id = "mg_3",
                    type = GameType.MEMORY_GAME,
                    difficulty = GameDifficulty.EASY,
                    question = "Chọn nghĩa đúng của từ:",
                    japaneseText = "ねこ",
                    correctAnswer = "cat",
                    options = listOf("cat", "dog", "bird", "fish"),
                    explanation = "ねこ có nghĩa là mèo",
                    points = 15,
                    timeLimit = 25
                ),
                GameQuestion(
                    id = "mg_4",
                    type = GameType.MEMORY_GAME,
                    difficulty = GameDifficulty.EASY,
                    question = "Chọn nghĩa đúng của từ:",
                    japaneseText = "いえ",
                    correctAnswer = "house",
                    options = listOf("house", "car", "tree", "mountain"),
                    explanation = "いえ có nghĩa là nhà",
                    points = 15,
                    timeLimit = 25
                ),
                
                // Speed Quiz - Medium
                GameQuestion(
                    id = "sq_1",
                    type = GameType.SPEED_QUIZ,
                    difficulty = GameDifficulty.MEDIUM,
                    question = "Câu nào đúng ngữ pháp?",
                    japaneseText = "どちらが正しいですか？",
                    correctAnswer = "わたしはにほんじんです",
                    options = listOf(
                        "わたしはにほんじんです",
                        "わたしはにほんじんにです",
                        "わたしはにほんじんをです",
                        "わたしはにほんじんがです"
                    ),
                    explanation = "Khi nói về quốc tịch, dùng です",
                    points = 20,
                    timeLimit = 20
                ),
                GameQuestion(
                    id = "sq_2",
                    type = GameType.SPEED_QUIZ,
                    difficulty = GameDifficulty.MEDIUM,
                    question = "Chọn từ phù hợp:",
                    japaneseText = "いま ____ です",
                    correctAnswer = "はん",
                    options = listOf("はん", "じ", "ふん", "ぷん"),
                    explanation = "はん là từ dùng để nói giờ rưỡi",
                    points = 20,
                    timeLimit = 20
                ),
                GameQuestion(
                    id = "sq_3",
                    type = GameType.SPEED_QUIZ,
                    difficulty = GameDifficulty.MEDIUM,
                    question = "Chọn câu đúng:",
                    japaneseText = "おはようございます はいつ使いますか？",
                    correctAnswer = "Morning",
                    options = listOf("Morning", "Afternoon", "Evening", "Night"),
                    explanation = "おはようございます dùng để chào buổi sáng",
                    points = 20,
                    timeLimit = 20
                ),
                GameQuestion(
                    id = "sq_4",
                    type = GameType.SPEED_QUIZ,
                    difficulty = GameDifficulty.MEDIUM,
                    question = "Chọn từ đúng:",
                    japaneseText = "ありがとう ____",
                    correctAnswer = "ございます",
                    options = listOf("ございます", "ください", "です", "ます"),
                    explanation = "ありがとうございます là cách nói lịch sự của cảm ơn",
                    points = 20,
                    timeLimit = 20
                )
            )
            
            _questions.value = defaultQuestions
            _isLoading.value = false
        }
    }
    
    /**
     * Tải achievements mặc định
     */
    private fun loadDefaultAchievements() {
        val defaultAchievements = listOf(
            GameAchievement(
                id = "first_game",
                title = "Người chơi đầu tiên",
                description = "Chơi game đầu tiên",
                icon = "🎮",
                requirement = "Play your first game",
                points = 10
            ),
            GameAchievement(
                id = "perfect_score",
                title = "Điểm hoàn hảo",
                description = "Đạt 100% độ chính xác trong 1 game",
                icon = "💯",
                requirement = "Get 100% accuracy in a game",
                points = 50
            ),
            GameAchievement(
                id = "streak_master",
                title = "Bậc thầy chuỗi",
                description = "Đạt chuỗi 10 câu trả lời đúng liên tiếp",
                icon = "🔥",
                requirement = "Get 10 correct answers in a row",
                points = 100
            ),
            GameAchievement(
                id = "speed_demon",
                title = "Tốc độ ánh sáng",
                description = "Hoàn thành game trong vòng 60 giây",
                icon = "⚡",
                requirement = "Complete a game in under 60 seconds",
                points = 75
            )
        )
        
        _achievements.value = defaultAchievements
    }
    
    /**
     * Kiểm tra xem game có thể chơi với độ khó hiện tại không
     */
    fun canPlayGame(gameType: GameType, difficulty: GameDifficulty): Boolean {
        val userLevel = _userLevelInfo.value
        return difficulty.isUnlockedForUser(userLevel)
    }
    
    /**
     * Lấy danh sách độ khó có thể chơi
     */
    fun getAvailableDifficulties(gameType: GameType): List<GameDifficulty> {
        val userLevel = _userLevelInfo.value
        return GameDifficulty.values().filter { difficulty ->
            difficulty.isUnlockedForUser(userLevel)
        }
    }
    
    /**
     * Bắt đầu game mới
     */
    fun startNewGame(gameType: GameType, difficulty: GameDifficulty) {
        viewModelScope.launch {
            // Kiểm tra xem có thể chơi game này không
            if (!canPlayGame(gameType, difficulty)) {
                return@launch
            }
            
            _isLoading.value = true
            
            // Lọc câu hỏi theo loại và độ khó
            val filteredQuestions = _questions.value
                .filter { it.type == gameType && it.difficulty == difficulty }
                .shuffled()
                .take(10) // Lấy 10 câu hỏi
            
            if (filteredQuestions.isNotEmpty()) {
                val session = GameSession(
                    sessionId = UUID.randomUUID().toString(),
                    gameType = gameType,
                    difficulty = difficulty,
                    questions = filteredQuestions,
                    currentQuestionIndex = 0
                )
                
                _currentSession.value = session
                _currentScore.value = 0
                _currentStreak.value = 0
                _timeRemaining.value = filteredQuestions.first().timeLimit
                
                startTimer()
            }
            
            _isLoading.value = false
        }
    }
    
    /**
     * Bắt đầu timer
     */
    private fun startTimer() {
        viewModelScope.launch {
            while (_timeRemaining.value > 0 && _currentSession.value?.isCompleted != true) {
                delay(1000)
                _timeRemaining.value--
            }
            
            if (_timeRemaining.value == 0) {
                submitAnswer("") // Time out
            }
        }
    }
    
    /**
     * Gửi câu trả lời
     */
    fun submitAnswer(answer: String) {
        val session = _currentSession.value ?: return
        val currentQuestion = session.questions.getOrNull(session.currentQuestionIndex) ?: return
        
        val isCorrect = answer.equals(currentQuestion.correctAnswer, ignoreCase = true)
        
        if (isCorrect) {
            _currentScore.value += currentQuestion.points
            _currentStreak.value++
        } else {
            _currentStreak.value = 0
        }
        
        // Thêm câu trả lời vào session
        val updatedSession = session.copy(
            answers = session.answers.toMutableList().apply { add(answer) }
        )
        _currentSession.value = updatedSession
        
        // Kiểm tra xem có còn câu hỏi nào không
        if (updatedSession.currentQuestionIndex < updatedSession.questions.size - 1) {
            // Chuyển sang câu hỏi tiếp theo
            val nextSession = updatedSession.copy(
                currentQuestionIndex = updatedSession.currentQuestionIndex + 1
            )
            _currentSession.value = nextSession
            _timeRemaining.value = nextSession.questions[nextSession.currentQuestionIndex].timeLimit
            startTimer()
        } else {
            // Hoàn thành game
            completeGame()
        }
    }
    
    /**
     * Hoàn thành game
     */
    private fun completeGame() {
        val session = _currentSession.value ?: return
        val correctAnswers = session.answers.zip(session.questions).count { (answer, question) ->
            answer.equals(question.correctAnswer, ignoreCase = true)
        }
        
        val result = GameResult(
            gameId = session.sessionId,
            gameType = session.gameType,
            difficulty = session.difficulty,
            score = _currentScore.value,
            totalQuestions = session.questions.size,
            correctAnswers = correctAnswers,
            timeSpent = System.currentTimeMillis() - session.startTime,
            streak = _currentStreak.value,
            maxStreak = _currentStreak.value,
            accuracy = correctAnswers.toFloat() / session.questions.size
        )
        
        _gameResult.value = result
        _currentSession.value = session.copy(isCompleted = true)
        
        // Cập nhật thống kê
        updatePlayerStats(result)
        
        // Kiểm tra achievements
        checkAchievements(result)
    }
    
    /**
     * Cập nhật thống kê người chơi
     */
    private fun updatePlayerStats(result: GameResult) {
        val currentStats = _playerStats.value
        
        val updatedStats = currentStats.copy(
            totalGamesPlayed = currentStats.totalGamesPlayed + 1,
            totalScore = currentStats.totalScore + result.score,
            averageAccuracy = (currentStats.averageAccuracy * currentStats.totalGamesPlayed + result.accuracy) / (currentStats.totalGamesPlayed + 1),
            bestStreak = maxOf(currentStats.bestStreak, result.streak),
            gamesByType = currentStats.gamesByType.toMutableMap().apply {
                put(result.gameType, getOrDefault(result.gameType, 0) + 1)
            },
            gamesByDifficulty = currentStats.gamesByDifficulty.toMutableMap().apply {
                put(result.difficulty, getOrDefault(result.difficulty, 0) + 1)
            },
            lastPlayedAt = result.completedAt,
            experience = currentStats.experience + result.score
        )
        
        _playerStats.value = updatedStats
        
        // Cập nhật level progress và kiểm tra unlock level mới
        updateLevelProgress(result)
    }
    
    /**
     * Cập nhật level progress dựa trên kết quả game
     */
    private fun updateLevelProgress(result: GameResult) {
        val currentUser = _userLevelInfo.value
        val gameCategory = LearningCategory.GAMES
        
        // Cập nhật progress cho category GAMES
        val key = "${result.difficulty.getRequiredLevel().name}_${gameCategory.name}"
        val currentProgress = currentUser.getLevelProgress(result.difficulty.getRequiredLevel(), gameCategory)
        
        val updatedProgress = currentProgress.copy(
            totalQuestions = currentProgress.totalQuestions + result.totalQuestions,
            correctAnswers = currentProgress.correctAnswers + result.correctAnswers,
            timeSpent = currentProgress.timeSpent + result.timeSpent,
            lastAccessed = System.currentTimeMillis(),
            masteryScore = if (currentProgress.totalQuestions > 0) {
                (currentProgress.masteryScore * currentProgress.totalQuestions + result.accuracy * 100) / (currentProgress.totalQuestions + result.totalQuestions)
            } else result.accuracy * 100,
            streak = if (result.accuracy >= 0.8f) currentProgress.streak + 1 else 0,
            completionPercentage = if (currentProgress.totalLessons > 0) {
                ((currentProgress.completedLessons + (if (result.accuracy >= 0.8f) 1 else 0)).toFloat() / currentProgress.totalLessons) * 100f
            } else 0f
        )
        
        val updatedProgressMap = currentUser.levelProgress.toMutableMap()
        updatedProgressMap[key] = updatedProgress
        
        // Cập nhật user info
        val updatedUser = currentUser.copy(
            levelProgress = updatedProgressMap,
            totalExperience = currentUser.totalExperience + result.score.toLong(),
            totalStudyTime = currentUser.totalStudyTime + result.timeSpent
        )
        
        _userLevelInfo.value = updatedUser
        
        // Kiểm tra xem có thể unlock level tiếp theo không
        checkAndUnlockNextLevel()
    }
    
    /**
     * Kiểm tra và unlock level tiếp theo
     */
    private fun checkAndUnlockNextLevel() {
        val currentUser = _userLevelInfo.value
        
        if (currentUser.canUnlockNextLevel()) {
            val nextLevel = currentUser.getNextUnlockableLevel()
            if (nextLevel != null && nextLevel !in currentUser.unlockedLevels) {
                val updatedUnlockedLevels = currentUser.unlockedLevels + nextLevel
                val updatedUser = currentUser.copy(
                    unlockedLevels = updatedUnlockedLevels,
                    currentLevel = nextLevel
                )
                _userLevelInfo.value = updatedUser
            }
        }
    }
    
    /**
     * Kiểm tra achievements
     */
    private fun checkAchievements(result: GameResult) {
        val currentAchievements = _achievements.value.toMutableList()
        
        // Kiểm tra từng achievement
        currentAchievements.forEachIndexed { index, achievement ->
            if (!achievement.isUnlocked) {
                var shouldUnlock = false
                
                when (achievement.id) {
                    "perfect_score" -> {
                        shouldUnlock = result.accuracy >= 1.0f
                    }
                    "streak_master" -> {
                        shouldUnlock = result.streak >= 10
                    }
                    "speed_demon" -> {
                        shouldUnlock = result.timeSpent < 60000 // 60 giây
                    }
                }
                
                if (shouldUnlock) {
                    currentAchievements[index] = achievement.copy(
                        isUnlocked = true,
                        unlockedAt = System.currentTimeMillis()
                    )
                }
            }
        }
        
        _achievements.value = currentAchievements
    }
    
    /**
     * Tạm dừng game
     */
    fun pauseGame() {
        _currentSession.value?.let { session ->
            _currentSession.value = session.copy(isPaused = true)
        }
    }
    
    /**
     * Tiếp tục game
     */
    fun resumeGame() {
        _currentSession.value?.let { session ->
            _currentSession.value = session.copy(isPaused = false)
            startTimer()
        }
    }
    
    /**
     * Thoát game
     */
    fun exitGame() {
        _currentSession.value = null
        _gameResult.value = null
        _currentScore.value = 0
        _currentStreak.value = 0
        _timeRemaining.value = 30
    }
    
    /**
     * Lọc câu hỏi theo loại
     */
    fun getQuestionsByType(gameType: GameType): List<GameQuestion> {
        return _questions.value.filter { it.type == gameType }
    }
    
    /**
     * Lọc câu hỏi theo độ khó
     */
    fun getQuestionsByDifficulty(difficulty: GameDifficulty): List<GameQuestion> {
        return _questions.value.filter { it.difficulty == difficulty }
    }
}
