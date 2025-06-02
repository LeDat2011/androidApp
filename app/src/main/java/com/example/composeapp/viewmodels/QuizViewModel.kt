package com.example.composeapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.data.FirebaseRepository
import com.example.composeapp.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.time.Instant

class QuizViewModel : ViewModel() {
    private val repository = FirebaseRepository()

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

    private var startTime: Long = 0

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

    private fun startQuiz() {
        startTime = System.currentTimeMillis()
        _questionIndex.value = 0
        _score.value = 0
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

    fun submitAnswer(answer: String) {
        _currentQuestion.value?.let { question ->
            if (answer == question.correctAnswer) {
                _score.value += question.points
            }
            moveToNextQuestion()
        }
    }

    private fun moveToNextQuestion() {
        _questionIndex.value++
        if (_questionIndex.value < (_currentQuiz.value?.questions?.size ?: 0)) {
            loadCurrentQuestion()
        } else {
            finishQuiz()
        }
    }

    private fun finishQuiz() {
        viewModelScope.launch {
            _currentQuiz.value?.let { quiz ->
                val timeTaken = (System.currentTimeMillis() - startTime).toInt() / 1000
                val result = QuizResult(
                    score = _score.value,
                    completedAt = Instant.now().toString(),
                    timeTaken = timeTaken
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

    fun resetError() {
        _error.value = null
    }
} 