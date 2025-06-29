package com.example.composeapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.data.FirebaseRepository
import com.example.composeapp.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class QuizViewModel : ViewModel() {
    private val repository = FirebaseRepository()

    private val _categories = MutableStateFlow<List<QuizCategory>>(emptyList())
    val categories: StateFlow<List<QuizCategory>> = _categories

    private val _currentQuiz = MutableStateFlow<Quiz?>(null)
    val currentQuiz: StateFlow<Quiz?> = _currentQuiz

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

    // Lấy danh sách các danh mục quiz
    fun loadCategories() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _categories.value = repository.getQuizCategories()
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải danh mục: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Lấy quiz theo cấp độ và danh mục
    fun loadQuiz(level: String, category: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val quizzes = repository.getQuizzesByLevel(level, category)
                if (quizzes.isNotEmpty()) {
                    _currentQuiz.value = quizzes.first()
                    startQuiz()
                } else {
                    _error.value = "Không tìm thấy quiz cho cấp độ này"
                }
            } catch (e: Exception) {
                _error.value = "Lỗi khi tải quiz: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Lấy quiz theo ID cụ thể
    fun loadQuizById(category: String, level: String, quizId: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
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
                val timeTaken = (System.currentTimeMillis() - startTime).toInt() / 1000
                val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
                val currentTime = dateFormat.format(Date())
                
                val result = QuizResult(
                    score = _score.value,
                    completedAt = currentTime,
                    timeTaken = timeTaken,
                    correctAnswers = _correctAnswersCount.value,
                    totalQuestions = quiz.questions.size
                )
                try {
                    // Thay thế USER_ID bằng ID thực tế của người dùng
                    repository.saveQuizResult("USER_ID", quiz.id, result)
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