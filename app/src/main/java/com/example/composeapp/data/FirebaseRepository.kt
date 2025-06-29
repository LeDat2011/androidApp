package com.example.composeapp.data

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.database
import com.google.firebase.database.ktx.snapshots
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.tasks.await
import com.example.composeapp.models.*

class FirebaseRepository {
    private val database = Firebase.database
    
    // Quiz Repository
    suspend fun getQuizCategories(): List<QuizCategory> {
        val categoriesRef = database.getReference("quizzes")
        return try {
            val snapshot = categoriesRef.get().await()
            val categories = mutableListOf<QuizCategory>()
            
            snapshot.children.forEach { categorySnapshot ->
                val id = categorySnapshot.key ?: ""
                val description = categorySnapshot.child("description").getValue(String::class.java) ?: ""
                val icon = categorySnapshot.child("icon").getValue(String::class.java) ?: ""
                
                categories.add(
                    QuizCategory(
                        id = id,
                        title = id.capitalize(),
                        description = description,
                        icon = icon
                    )
                )
            }
            
            categories
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getQuizzesByLevel(level: String, category: String): List<Quiz> {
        val quizRef = database.getReference("quizzes/$category/levels/$level")
        return try {
            val snapshot = quizRef.get().await()
            val quizzes = mutableListOf<Quiz>()
            
            snapshot.children.forEach { quizSnapshot ->
                val id = quizSnapshot.child("id").getValue(String::class.java) ?: ""
                val title = quizSnapshot.child("title").getValue(String::class.java) ?: ""
                val description = quizSnapshot.child("description").getValue(String::class.java) ?: ""
                val timeLimit = quizSnapshot.child("timeLimit").getValue(Int::class.java) ?: 0
                
                // Lấy danh sách câu hỏi
                val questionsMap = mutableMapOf<String, Question>()
                val questionsSnapshot = quizSnapshot.child("questions")
                
                questionsSnapshot.children.forEach { questionSnapshot ->
                    val questionId = questionSnapshot.key ?: ""
                    val questionText = questionSnapshot.child("question").getValue(String::class.java) ?: ""
                    val questionType = try {
                        QuestionType.valueOf(questionSnapshot.child("type").getValue(String::class.java) ?: "MULTIPLE_CHOICE")
                    } catch (e: Exception) {
                        QuestionType.MULTIPLE_CHOICE
                    }
                    val correctAnswer = questionSnapshot.child("correctAnswer").getValue(String::class.java) ?: ""
                    val explanation = questionSnapshot.child("explanation").getValue(String::class.java) ?: ""
                    val points = questionSnapshot.child("points").getValue(Int::class.java) ?: 0
                    
                    // Lấy các lựa chọn
                    val options = mutableMapOf<String, String>()
                    val optionsSnapshot = questionSnapshot.child("options")
                    
                    optionsSnapshot.children.forEach { optionSnapshot ->
                        val optionKey = optionSnapshot.key ?: ""
                        val optionValue = optionSnapshot.getValue(String::class.java) ?: ""
                        options[optionKey] = optionValue
                    }
                    
                    val question = Question(
                        id = questionId,
                        type = questionType,
                        question = questionText,
                        options = options,
                        correctAnswer = correctAnswer,
                        explanation = explanation,
                        points = points
                    )
                    
                    questionsMap[questionId] = question
                }
                
                quizzes.add(
                    Quiz(
                        id = id,
                        title = title,
                        description = description,
                        timeLimit = timeLimit,
                        questions = questionsMap
                    )
                )
            }
            
            quizzes
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    suspend fun getQuizById(category: String, level: String, quizId: String): Quiz? {
        val quizRef = database.getReference("quizzes/$category/levels/$level/$quizId")
        return try {
            val snapshot = quizRef.get().await()
            
            val id = snapshot.child("id").getValue(String::class.java) ?: ""
            val title = snapshot.child("title").getValue(String::class.java) ?: ""
            val description = snapshot.child("description").getValue(String::class.java) ?: ""
            val timeLimit = snapshot.child("timeLimit").getValue(Int::class.java) ?: 0
            
            // Lấy danh sách câu hỏi
            val questionsMap = mutableMapOf<String, Question>()
            val questionsSnapshot = snapshot.child("questions")
            
            questionsSnapshot.children.forEach { questionSnapshot ->
                val questionId = questionSnapshot.key ?: ""
                val questionText = questionSnapshot.child("question").getValue(String::class.java) ?: ""
                val questionType = try {
                    QuestionType.valueOf(questionSnapshot.child("type").getValue(String::class.java) ?: "MULTIPLE_CHOICE")
                } catch (e: Exception) {
                    QuestionType.MULTIPLE_CHOICE
                }
                val correctAnswer = questionSnapshot.child("correctAnswer").getValue(String::class.java) ?: ""
                val explanation = questionSnapshot.child("explanation").getValue(String::class.java) ?: ""
                val points = questionSnapshot.child("points").getValue(Int::class.java) ?: 0
                
                // Lấy các lựa chọn
                val options = mutableMapOf<String, String>()
                val optionsSnapshot = questionSnapshot.child("options")
                
                optionsSnapshot.children.forEach { optionSnapshot ->
                    val optionKey = optionSnapshot.key ?: ""
                    val optionValue = optionSnapshot.getValue(String::class.java) ?: ""
                    options[optionKey] = optionValue
                }
                
                val question = Question(
                    id = questionId,
                    type = questionType,
                    question = questionText,
                    options = options,
                    correctAnswer = correctAnswer,
                    explanation = explanation,
                    points = points
                )
                
                questionsMap[questionId] = question
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

    suspend fun saveQuizResult(userId: String, quizId: String, result: QuizResult) {
        val resultRef = database.getReference("users/$userId/progress/completed_quizzes/$quizId")
        try {
            resultRef.setValue(result).await()
        } catch (e: Exception) {
            throw e
        }
    }

    // User Progress Repository
    suspend fun getUserProgress(userId: String): UserProgress? {
        val progressRef = database.getReference("users/$userId/progress")
        return try {
            val snapshot = progressRef.get().await()
            snapshot.getValue(UserProgress::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProgress(userId: String, progress: UserProgress) {
        val progressRef = database.getReference("users/$userId/progress")
        try {
            progressRef.setValue(progress).await()
        } catch (e: Exception) {
            throw e
        }
    }

    // Leaderboard Repository
    suspend fun getWeeklyLeaderboard(): List<LeaderboardEntry> {
        val leaderboardRef = database.getReference("leaderboard/weekly")
        return try {
            val snapshot = leaderboardRef.orderByChild("points").limitToLast(10).get().await()
            snapshot.children.mapNotNull { it.getValue(LeaderboardEntry::class.java) }.reversed()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getMonthlyLeaderboard(): List<LeaderboardEntry> {
        val leaderboardRef = database.getReference("leaderboard/monthly")
        return try {
            val snapshot = leaderboardRef.orderByChild("points").limitToLast(10).get().await()
            snapshot.children.mapNotNull { it.getValue(LeaderboardEntry::class.java) }.reversed()
        } catch (e: Exception) {
            emptyList()
        }
    }
    
    // Extension function to capitalize first letter
    private fun String.capitalize(): String {
        return this.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }
} 