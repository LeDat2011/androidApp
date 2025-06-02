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
    suspend fun getQuizzesByLevel(level: String, category: String): List<Quiz> {
        val quizRef = database.getReference("quizzes/$category/$level")
        return try {
            val snapshot = quizRef.get().await()
            snapshot.children.mapNotNull { it.getValue(Quiz::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveQuizResult(userId: String, quizId: String, result: QuizResult) {
        val resultRef = database.getReference("user_progress/$userId/completed_quizzes/$quizId")
        try {
            resultRef.setValue(result).await()
        } catch (e: Exception) {
            throw e
        }
    }

    // User Progress Repository
    suspend fun getUserProgress(userId: String): UserProgress? {
        val progressRef = database.getReference("user_progress/$userId")
        return try {
            val snapshot = progressRef.get().await()
            snapshot.getValue(UserProgress::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUserProgress(userId: String, progress: UserProgress) {
        val progressRef = database.getReference("user_progress/$userId")
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
} 