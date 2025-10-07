package com.example.composeapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.repository.FirebaseRepository
import com.example.composeapp.models.*
import com.google.firebase.database.FirebaseDatabase
import com.example.composeapp.repository.QuizRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizViewModel : ViewModel() {
    private val repository = FirebaseRepository(
        com.google.firebase.auth.FirebaseAuth.getInstance(),
        com.google.firebase.database.FirebaseDatabase.getInstance()
    )
    val quizRepository = QuizRepository()

    private val _categories = MutableStateFlow<List<QuizCategory>>(emptyList())
    val categories: StateFlow<List<QuizCategory>> = _categories
    
    // Level system integration
    private val _userLevelInfo = MutableStateFlow<UserLevelInfo?>(null)
    val userLevelInfo: StateFlow<UserLevelInfo?> = _userLevelInfo

    private val _currentQuiz = MutableStateFlow<Quiz?>(null)
    val currentQuiz: StateFlow<Quiz?> = _currentQuiz
    private val _levels = MutableStateFlow<List<QuizLevel>>(emptyList())
    val levels: StateFlow<List<QuizLevel>> = _levels

    private var currentCategoryId: String? = null
    private var currentLevelId: String? = null

    private val _currentQuestion = MutableStateFlow<Question?>(null)
    val currentQuestion: StateFlow<Question?> = _currentQuestion

    private val _questionIndex = MutableStateFlow(0)
    val questionIndex: StateFlow<Int> = _questionIndex

    private val _score = MutableStateFlow(0)
    val score: StateFlow<Int> = _score

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    // Thêm các state mới
    private val _userAnswers = MutableStateFlow<MutableMap<String, String>>(mutableMapOf())
    val userAnswers: StateFlow<Map<String, String>> = _userAnswers

    private val _correctAnswersCount = MutableStateFlow(0)
    val correctAnswersCount: StateFlow<Int> = _correctAnswersCount

    private val _showResults = MutableStateFlow(false)
    val showResults: StateFlow<Boolean> = _showResults

    private val _answerResults = MutableStateFlow<List<AnswerResult>>(emptyList())
    val answerResults: StateFlow<List<AnswerResult>> = _answerResults

    private var startTime: Long = 0
    
    init {
        initializeUserLevel()
    }
    
    /**
     * Khởi tạo level của user
     */
    private fun initializeUserLevel() {
        // TODO: Load from Firebase or local storage
        val defaultUserInfo = UserLevelInfo(
            userId = "current_user",
            currentLevel = JLPTLevel.N5,
            unlockedLevels = setOf(JLPTLevel.N5)
        )
        _userLevelInfo.value = defaultUserInfo
    }

    // Lấy danh mục từ Firebase quizzes (cấu trúc mới)
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            _error.value = null
            try {
                // Lấy categories từ Firebase quizzes root
                val categoriesFromFirebase = repository.getQuizCategories()
                if (categoriesFromFirebase.isNotEmpty()) {
                    _categories.value = categoriesFromFirebase
                } else {
                    // Fallback to default categories
                    _categories.value = getDefaultCategories()
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải danh mục: ${e.message}"
                // Fallback to default categories
                _categories.value = getDefaultCategories()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Danh sách categories mặc định
    private fun getDefaultCategories(): List<QuizCategory> {
        return listOf(
            QuizCategory(
                id = "animals",
                title = "Động Vật",
                description = "Học từ vựng về các loài động vật",
                icon = "pets"
            ),
            QuizCategory(
                id = "colors",
                title = "Màu Sắc",
                description = "Học từ vựng về màu sắc",
                icon = "palette"
            ),
            QuizCategory(
                id = "family",
                title = "Gia Đình",
                description = "Học từ vựng về gia đình",
                icon = "people"
            ),
            QuizCategory(
                id = "food",
                title = "Thức Ăn",
                description = "Học từ vựng về thức ăn",
                icon = "restaurant"
            ),
            QuizCategory(
                id = "numbers",
                title = "Số Đếm",
                description = "Học từ vựng về số đếm",
                icon = "calculate"
            ),
            QuizCategory(
                id = "time",
                title = "Thời Gian",
                description = "Học từ vựng về thời gian",
                icon = "schedule"
            ),
            QuizCategory(
                id = "transportation",
                title = "Phương Tiện",
                description = "Học từ vựng về phương tiện giao thông",
                icon = "directions_car"
            ),
            QuizCategory(
                id = "weather",
                title = "Thời Tiết",
                description = "Học từ vựng về thời tiết",
                icon = "wb_sunny"
            )
        )
    }
    
    // Lấy levels theo category từ Firebase quizzes
    fun loadLevelsForCategory(categoryId: String) {
        viewModelScope.launch {
            try {
                // Lấy levels từ Firebase quizzes/{category}/levels
                val database = com.google.firebase.database.FirebaseDatabase.getInstance()
                val levelsRef = database.getReference("quizzes/$categoryId/levels")
                val snapshot = levelsRef.get().await()
                
                val levelsList = mutableListOf<QuizLevel>()
                for (levelSnapshot in snapshot.children) {
                    val levelId = levelSnapshot.key ?: continue
                    val description = levelSnapshot.child("description").getValue(String::class.java) ?: ""
                    val color = levelSnapshot.child("color").getValue(String::class.java) ?: "#9C27B0"
                    
                    levelsList.add(
                        QuizLevel(
                            id = levelId,
                            name = levelId, // Giữ nguyên format N5, N4, N3, N2, N1
                            description = description,
                            color = color
                        )
                    )
                }
                
                _levels.value = levelsList
                
                // Nếu không có levels từ Firebase, tạo danh sách mặc định
                if (levelsList.isEmpty()) {
                    _levels.value = getDefaultLevels()
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải levels: ${e.message}"
                // Fallback to default levels
                _levels.value = getDefaultLevels()
            }
        }
    }
    
    // Danh sách levels mặc định
    private fun getDefaultLevels(): List<QuizLevel> {
        return listOf(
            QuizLevel(
                id = "N5",
                name = "N5",
                description = "Cấp độ cơ bản nhất",
                color = "#4CAF50"
            ),
            QuizLevel(
                id = "N4",
                name = "N4",
                description = "Cấp độ sơ cấp",
                color = "#2196F3"
            ),
            QuizLevel(
                id = "N3",
                name = "N3",
                description = "Cấp độ trung cấp",
                color = "#FF9800"
            ),
            QuizLevel(
                id = "N2",
                name = "N2",
                description = "Cấp độ trung cao cấp",
                color = "#9C27B0"
            ),
            QuizLevel(
                id = "N1",
                name = "N1",
                description = "Cấp độ cao cấp nhất",
                color = "#F44336"
            )
        )
    }

    // Lấy quiz theo cấp độ và danh mục từ Firebase
    fun loadQuiz(level: String, category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                currentCategoryId = category
                currentLevelId = level
                
                // Lấy quiz từ Firebase quizzes/{category}/levels/{level}
                val quizzes = repository.getQuizzesByLevel(level, category)
                if (quizzes.isNotEmpty()) {
                    _currentQuiz.value = quizzes.first()
                    startQuiz()
                } else {
                    // Fallback to default quiz
                    _currentQuiz.value = getDefaultQuiz(category, level)
                    startQuiz()
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải quiz: ${e.message}"
                // Fallback to default quiz
                _currentQuiz.value = getDefaultQuiz(category, level)
                startQuiz()
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Tạo quiz mặc định
    private fun getDefaultQuiz(category: String, level: String): Quiz {
        val categoryTitle = when (category) {
            "animals" -> "Động Vật"
            "colors" -> "Màu Sắc"
            "family" -> "Gia Đình"
            "food" -> "Thức Ăn"
            "numbers" -> "Số Đếm"
            "time" -> "Thời Gian"
            "transportation" -> "Phương Tiện"
            "weather" -> "Thời Tiết"
            else -> category
        }
        
        val questions = mutableMapOf<String, Question>()
        
        // Tạo một số câu hỏi mặc định
        questions["q1"] = Question(
            id = "q1",
            type = QuestionType.MULTIPLE_CHOICE,
            question = "Câu hỏi mẫu về $categoryTitle - Cấp độ $level",
            options = mapOf(
                "a" to "Lựa chọn A",
                "b" to "Lựa chọn B", 
                "c" to "Lựa chọn C",
                "d" to "Lựa chọn D"
            ),
            correctAnswer = "a",
            explanation = "Đây là câu hỏi mẫu để kiểm tra chức năng quiz",
            points = 10
        )
        
        questions["q2"] = Question(
            id = "q2",
            type = QuestionType.TRUE_FALSE,
            question = "Đây là câu hỏi Đúng/Sai mẫu về $categoryTitle",
            options = mapOf(
                "a" to "Đúng",
                "b" to "Sai"
            ),
            correctAnswer = "a",
            explanation = "Câu trả lời đúng là Đúng",
            points = 10
        )
        
        return Quiz(
            id = "default_${category}_${level}",
            title = "Quiz $categoryTitle - $level",
            description = "Quiz mẫu cho $categoryTitle ở cấp độ $level",
            timeLimit = 300, // 5 phút
            questions = questions
        )
    }

    // Lấy quiz theo ID cụ thể từ Firebase
    fun loadQuizById(category: String, level: String, quizId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                currentCategoryId = category
                currentLevelId = level
                val quiz = repository.getQuizById(category, level, quizId)
                if (quiz != null) {
                    _currentQuiz.value = quiz
                    startQuiz()
                } else {
                    _error.value = "Không tìm thấy quiz"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải quiz: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun startQuiz() {
        startTime = System.currentTimeMillis()
        _questionIndex.value = 0
        _score.value = 0
        _correctAnswersCount.value = 0
        _userAnswers.value = mutableMapOf()
        _showResults.value = false
        _answerResults.value = emptyList()
        loadCurrentQuestion()
    }

    private fun loadCurrentQuestion() {
        _currentQuiz.value?.let { quiz ->
            val questions = quiz.questions.values.toList()
            if (_questionIndex.value < questions.size) {
                _currentQuestion.value = questions[_questionIndex.value]
            }
        }
    }

    // Lưu câu trả lời của người dùng
    fun submitAnswer(answer: String) {
        _currentQuestion.value?.let { question ->
            // Lưu câu trả lời vào map
            _userAnswers.value[question.id] = answer
            moveToNextQuestion()
        }
    }

    private fun moveToNextQuestion() {
        _questionIndex.value++
        if (_questionIndex.value < (_currentQuiz.value?.questions?.size ?: 0)) {
            loadCurrentQuestion()
        } else {
            evaluateQuiz()
        }
    }

    // Đánh giá kết quả quiz sau khi trả lời tất cả câu hỏi
    private fun evaluateQuiz() {
        _currentQuiz.value?.let { quiz ->
            var totalScore = 0
            var correctCount = 0
            val resultList = mutableListOf<AnswerResult>()

            // Kiểm tra từng câu trả lời
            quiz.questions.forEach { (questionId, question) ->
                val userAnswer = _userAnswers.value[questionId]
                val isCorrect = userAnswer == question.correctAnswer
                
                if (isCorrect) {
                    totalScore += question.points
                    correctCount++
                }
                
                resultList.add(
                    AnswerResult(
                        questionId = questionId,
                        question = question.question,
                        userAnswer = userAnswer ?: "",
                        correctAnswer = question.correctAnswer,
                        isCorrect = isCorrect,
                        explanation = question.explanation,
                        points = if (isCorrect) question.points else 0
                    )
                )
            }
            
            _score.value = totalScore
            _correctAnswersCount.value = correctCount
            _answerResults.value = resultList
            _showResults.value = true
            
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        viewModelScope.launch {
            _currentQuiz.value?.let { quiz ->
                val timeTakenSeconds = (System.currentTimeMillis() - startTime).toInt() / 1000
                val result = FirebaseQuizResult(
                    quizId = quiz.id,
                    score = _score.value,
                    totalQuestions = quiz.questions.size,
                    correctAnswers = _correctAnswersCount.value,
                    timeSpentSeconds = timeTakenSeconds,
                    completedAt = System.currentTimeMillis(),
                    category = currentCategoryId ?: "",
                    level = currentLevelId ?: ""
                )
                try {
                    repository.saveQuizResult(result)
                } catch (e: Exception) {
                    _error.value = "Lỗi khi lưu kết quả: ${e.message}"
                }
            }
        }
    }

    fun resetQuiz() {
        startQuiz()
    }

    fun resetError() {
        _error.value = null
    }
    
    /**
     * Lấy danh sách quiz theo level của user
     */
    fun getAvailableQuizzes(): List<QuizCategory> {
        val userInfo = _userLevelInfo.value ?: return emptyList()
        val currentLevel = userInfo.currentLevel
        
        // Trả về quiz của level hiện tại và các level đã unlock
        return _categories.value.filter { category ->
            val categoryLevel = when (category.title.lowercase()) {
                "n5" -> JLPTLevel.N5
                "n4" -> JLPTLevel.N4
                "n3" -> JLPTLevel.N3
                "n2" -> JLPTLevel.N2
                "n1" -> JLPTLevel.N1
                else -> JLPTLevel.N5
            }
            
            // Cho phép truy cập level hiện tại và các level đã unlock
            userInfo.isLevelUnlocked(categoryLevel)
        }
    }
    
    /**
     * Lấy danh sách level có thể truy cập
     */
    fun getAvailableLevels(): List<QuizLevel> {
        val userInfo = _userLevelInfo.value ?: return emptyList()
        
        return _levels.value.filter { level ->
            val levelEnum = when (level.name.lowercase()) {
                "n5" -> JLPTLevel.N5
                "n4" -> JLPTLevel.N4
                "n3" -> JLPTLevel.N3
                "n2" -> JLPTLevel.N2
                "n1" -> JLPTLevel.N1
                else -> JLPTLevel.N5
            }
            
            userInfo.isLevelUnlocked(levelEnum)
        }
    }
    
    /**
     * Cập nhật user level info
     */
    fun updateUserLevelInfo(newUserInfo: UserLevelInfo) {
        _userLevelInfo.value = newUserInfo
    }
} 

// Lớp mới để lưu kết quả từng câu trả lời
data class AnswerResult(
    val questionId: String,
    val question: String,
    val userAnswer: String,
    val correctAnswer: String,
    val isCorrect: Boolean,
    val explanation: String,
    val points: Int
) 