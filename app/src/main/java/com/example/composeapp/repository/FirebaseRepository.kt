package com.example.composeapp.repository

import com.example.composeapp.models.*
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseRepository @Inject constructor(
    private val auth: FirebaseAuth,
    private val database: FirebaseDatabase
) {
    
    private val currentUser get() = auth.currentUser
    private val userId get() = currentUser?.uid
    
    // User Profile Operations
    suspend fun saveUserProfile(profile: UserProfileData): Result<UserProfileData> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            val profileRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .child("profile")
            
            profileRef.setValue(profile).await()
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserProfile(): Result<UserProfileData?> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            val profileRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .child("profile")
            
            val snapshot = profileRef.get().await()
            val profile = snapshot.getValue(UserProfileData::class.java)
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun checkUserHasProfile(): Result<Boolean> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            val profileRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .child("profile")
            
            val snapshot = profileRef.get().await()
            Result.success(snapshot.exists())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Progress Operations
    suspend fun updateProgress(progress: DetailedLearningProgress): Result<Unit> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            val progressRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .child("progress")
            
            progressRef.setValue(progress).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getProgress(): Result<DetailedLearningProgress?> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            val progressRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .child("progress")
            
            val snapshot = progressRef.get().await()
            val progress = snapshot.getValue(DetailedLearningProgress::class.java)
            Result.success(progress)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Settings Operations
    suspend fun saveSettings(settings: UserSettings): Result<Unit> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            val settingsRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .child("settings")
            
            settingsRef.setValue(settings).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getSettings(): Result<UserSettings?> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            val settingsRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .child("settings")
            
            val snapshot = settingsRef.get().await()
            val settings = snapshot.getValue(UserSettings::class.java)
            Result.success(settings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Learning Data Operations
    suspend fun markWordAsLearned(wordId: String): Result<Unit> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            val wordRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .child("learning")
                .child("vocabulary")
                .child(wordId)
            
            wordRef.setValue(true).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun markLessonAsCompleted(lessonId: String): Result<Unit> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            val lessonRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .child("learning")
                .child("completedLessons")
                .child(lessonId)
            
            lessonRef.setValue(System.currentTimeMillis()).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun saveQuizResult(quizResult: FirebaseQuizResult): Result<Unit> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            val quizRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .child("learning")
                .child("quizResults")
                .child(quizResult.quizId)
            
            quizRef.setValue(quizResult).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Statistics Operations
    suspend fun updateStudyTime(date: String, minutes: Int): Result<Unit> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            val studyTimeRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .child("learning_stats")
                .child("study_time")
                .child(date)
            
            studyTimeRef.setValue(minutes).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun updateDailyProgress(date: String, progress: DailyProgress): Result<Unit> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            val progressRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .child("learning_stats")
                .child("daily_progress")
                .child(date)
            
            progressRef.setValue(progress).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Vocabulary Operations
    suspend fun getVocabulary(level: String? = null, category: String? = null): Result<List<FirebaseVocabulary>> {
        return try {
            val vocabularyRef = database.reference
                .child("app_data")
                .child("vocabulary")
            
            val snapshot = vocabularyRef.get().await()
            val vocabularyList = mutableListOf<FirebaseVocabulary>()
            
            snapshot.children.forEach { child ->
                val vocabulary = child.getValue(FirebaseVocabulary::class.java)
                vocabulary?.let {
                    val matchesLevel = level == null || it.level == level
                    val matchesCategory = category == null || it.categories.contains(category)
                    
                    if (matchesLevel && matchesCategory) {
                        vocabularyList.add(it)
                    }
                }
            }
            
            Result.success(vocabularyList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getVocabularyById(id: String): Result<FirebaseVocabulary?> {
        return try {
            val vocabularyRef = database.reference
                .child("app_data")
                .child("vocabulary")
                .child(id)
            
            val snapshot = vocabularyRef.get().await()
            val vocabulary = snapshot.getValue(FirebaseVocabulary::class.java)
            Result.success(vocabulary)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Lessons Operations
    suspend fun getLessons(level: String? = null, category: String? = null): Result<List<FirebaseLesson>> {
        return try {
            val lessonsRef = database.reference
                .child("app_data")
                .child("lessons")
            
            val snapshot = lessonsRef.get().await()
            val lessonsList = mutableListOf<FirebaseLesson>()
            
            snapshot.children.forEach { child ->
                val lesson = child.getValue(FirebaseLesson::class.java)
                lesson?.let {
                    val matchesLevel = level == null || it.level == level
                    val matchesCategory = category == null || it.category == category
                    
                    if (matchesLevel && matchesCategory) {
                        lessonsList.add(it)
                    }
                }
            }
            
            // Sort by order
            lessonsList.sortBy { it.order }
            Result.success(lessonsList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getLessonById(id: String): Result<FirebaseLesson?> {
        return try {
            val lessonRef = database.reference
                .child("app_data")
                .child("lessons")
                .child(id)
            
            val snapshot = lessonRef.get().await()
            val lesson = snapshot.getValue(FirebaseLesson::class.java)
            Result.success(lesson)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Categories Operations
    suspend fun getCategories(): Result<List<FirebaseCategory>> {
        return try {
            val categoriesRef = database.reference
                .child("app_data")
                .child("categories")
            
            val snapshot = categoriesRef.get().await()
            val categoriesList = mutableListOf<FirebaseCategory>()
            
            snapshot.children.forEach { child ->
                val category = child.getValue(FirebaseCategory::class.java)
                category?.let { categoriesList.add(it) }
            }
            
            // Sort by order
            categoriesList.sortBy { it.order }
            Result.success(categoriesList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Quizzes Operations
    suspend fun getQuizzes(category: String? = null, level: String? = null): Result<List<FirebaseQuiz>> {
        return try {
            val quizzesRef = database.reference
                .child("quizzes")
            
            val snapshot = quizzesRef.get().await()
            val quizzesList = mutableListOf<FirebaseQuiz>()
            
            snapshot.children.forEach { categorySnapshot ->
                val categoryName = categorySnapshot.key
                
                if (category == null || categoryName == category) {
                    categorySnapshot.child("levels").children.forEach { levelSnapshot ->
                        val levelName = levelSnapshot.key
                        
                        if (level == null || levelName == level) {
                            levelSnapshot.children.forEach { quizSnapshot ->
                                val quiz = quizSnapshot.getValue(FirebaseQuiz::class.java)
                                quiz?.let { 
                                    quizzesList.add(it.copy(category = categoryName ?: "", level = levelName ?: ""))
                                }
                            }
                        }
                    }
                }
            }
            
            Result.success(quizzesList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // Achievements Operations
    suspend fun getAchievements(): Result<List<Achievement>> {
        return try {
            val achievementsRef = database.reference
                .child("app_data")
                .child("achievements")
            
            val snapshot = achievementsRef.get().await()
            val achievementsList = mutableListOf<Achievement>()
            
            snapshot.children.forEach { child ->
                val achievement = child.getValue(Achievement::class.java)
                achievement?.let { achievementsList.add(it) }
            }
            
            Result.success(achievementsList)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun unlockAchievement(achievementId: String): Result<Unit> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            val achievementRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .child("achievements")
                .child(achievementId)
            
            val achievementData = mapOf(
                "unlocked" to true,
                "unlockedAt" to System.currentTimeMillis()
            )
            
            achievementRef.updateChildren(achievementData).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    // System Settings Operations
    suspend fun getSystemSettings(): Result<SystemSettings?> {
        return try {
            val settingsRef = database.reference
                .child("app_data")
                .child("system_settings")
            
            val snapshot = settingsRef.get().await()
            val settings = snapshot.getValue(SystemSettings::class.java)
            Result.success(settings)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
