package com.example.composeapp.repository

import com.example.composeapp.models.Quiz
import com.example.composeapp.models.QuizCategory
import com.example.composeapp.models.QuizLevel
import com.example.composeapp.models.Question
import com.example.composeapp.models.QuestionType
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await

class QuizRepository {
    private val database = FirebaseDatabase.getInstance()
    private val quizzesRoot = database.getReference("app_data/quizzes")
    
    private val _categories = MutableStateFlow<List<QuizCategory>>(emptyList())
    val categories: StateFlow<List<QuizCategory>> = _categories
    
    private val _levels = MutableStateFlow<List<QuizLevel>>(emptyList())
    val levels: StateFlow<List<QuizLevel>> = _levels
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // Tải danh sách categories từ app_data/quizzes
    suspend fun loadCategories() {
        try {
            _isLoading.value = true
            _error.value = null
            
            val snapshot = quizzesRoot.get().await()
            val categoriesList = mutableListOf<QuizCategory>()
            
            for (categorySnapshot in snapshot.children) {
                val category = parseQuizCategory(categorySnapshot)
                if (category != null) {
                    categoriesList.add(category)
                }
            }
            
            _categories.value = categoriesList
        } catch (e: Exception) {
            _error.value = "Không thể tải danh sách categories: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    // Tải danh sách levels toàn cục (không ưu tiên). Giữ để tương thích UI cũ
    suspend fun loadLevels() {
        try {
            _isLoading.value = true
            _error.value = null
            
            val snapshot = quizzesRoot.get().await()
            val setIds = snapshot.children.flatMap { it.child("levels").children.mapNotNull { lv -> lv.key } }.toSet()
            val levelsList = setIds.map { id ->
                QuizLevel(id = id, name = id, description = "", color = "#9C27B0")
            }
            
            _levels.value = levelsList
        } catch (e: Exception) {
            _error.value = "Không thể tải danh sách levels: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    // Lấy levels cho 1 category
    suspend fun getLevelsForCategory(categoryId: String): List<QuizLevel> {
        return try {
            val snap = quizzesRoot.child(categoryId).child("levels").get().await()
            snap.children.mapNotNull { lvSnap ->
                val id = lvSnap.key ?: return@mapNotNull null
                QuizLevel(id = id, name = id, description = lvSnap.child("description").getValue(String::class.java) ?: "", color = "#9C27B0")
            }
        } catch (e: Exception) {
            _error.value = "Không thể tải levels: ${e.message}"
            emptyList()
        }
    }

    // Lấy danh sách quiz theo category/level
    suspend fun getQuizzesByLevel(categoryId: String, levelId: String): List<Quiz> {
        return try {
            val levelSnap = quizzesRoot.child(categoryId).child("levels").child(levelId).get().await()
            val quizzes = mutableListOf<Quiz>()
            for (quizSnap in levelSnap.children) {
                parseQuiz(quizSnap)?.let { quizzes.add(it) }
            }
            quizzes
        } catch (e: Exception) {
            _error.value = "Không thể tải quiz: ${e.message}"
            emptyList()
        }
    }

    // Lấy quiz theo ID
    suspend fun getQuizById(categoryId: String, levelId: String, quizId: String): Quiz? {
        return try {
            val snap = quizzesRoot.child(categoryId).child("levels").child(levelId).child(quizId).get().await()
            parseQuiz(snap)
        } catch (e: Exception) {
            _error.value = "Không thể tải quiz: ${e.message}"
            null
        }
    }
    
    // Parse QuizCategory từ Firebase snapshot
    private fun parseQuizCategory(snapshot: DataSnapshot): QuizCategory? {
        return try {
            val id = snapshot.key ?: return null
            val title = snapshot.child("title").getValue(String::class.java) ?: id
            val description = snapshot.child("description").getValue(String::class.java) ?: ""
            val iconUrl = snapshot.child("iconUrl").getValue(String::class.java)
            val color = snapshot.child("color").getValue(String::class.java)
            
            QuizCategory(
                id = id,
                title = title,
                description = description,
                iconUrl = iconUrl,
                color = color
            )
        } catch (e: Exception) {
            null
        }
    }

    // Parse Quiz (theo cấu trúc docs: questions là map)
    private fun parseQuiz(snapshot: DataSnapshot): Quiz? {
        return try {
            val id = snapshot.child("id").getValue(String::class.java) ?: snapshot.key ?: return null
            val title = snapshot.child("title").getValue(String::class.java) ?: ""
            val description = snapshot.child("description").getValue(String::class.java) ?: ""
            val timeLimit = snapshot.child("timeLimit").getValue(Int::class.java) ?: 0
            val questionsSnap = snapshot.child("questions")
            val questionsMap = mutableMapOf<String, Question>()
            for (q in questionsSnap.children) {
                val qId = q.key ?: continue
                val qText = q.child("question").getValue(String::class.java) ?: continue
                val typeStr = q.child("type").getValue(String::class.java) ?: "MULTIPLE_CHOICE"
                val type = runCatching { QuestionType.valueOf(typeStr) }.getOrDefault(QuestionType.MULTIPLE_CHOICE)
                val optionsNode = q.child("options")
                val options: Map<String, String>? = if (optionsNode.exists()) {
                    optionsNode.children.associate { it.key!! to (it.getValue(String::class.java) ?: "") }
                } else null
                val correctAnswer = q.child("correctAnswer").getValue(String::class.java) ?: ""
                val explanation = q.child("explanation").getValue(String::class.java) ?: ""
                val points = q.child("points").getValue(Int::class.java) ?: 0
                val audioUrl = q.child("audioUrl").getValue(String::class.java)
                questionsMap[qId] = Question(
                    id = qId,
                    type = type,
                    question = qText,
                    options = options,
                    correctAnswer = correctAnswer,
                    explanation = explanation,
                    points = points,
                    audioUrl = audioUrl
                )
            }
            Quiz(
                id = id,
                title = title,
                description = description,
                timeLimit = timeLimit,
                questions = questionsMap
            )
        } catch (e: Exception) {
            null
        }
    }
    
    fun resetError() {
        _error.value = null
    }
}

