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
            
            // Save profile data theo cấu trúc Firebase mới - chia thành profile, progress, settings
            val profileData = mutableMapOf<String, Any>()
            profileData["userId"] = profile.userId.ifEmpty { uid }
            profileData["name"] = profile.name
            profileData["email"] = profile.email
            profileData["age"] = profile.age
            profileData["currentLevel"] = profile.currentLevel
            profileData["targetLevel"] = profile.targetLevel
            profileData["registrationDate"] = profile.registrationDate ?: System.currentTimeMillis()
            profileData["lastActiveDate"] = profile.lastActiveDate ?: System.currentTimeMillis()
            if (profile.avatarUrl != null) profileData["avatarUrl"] = profile.avatarUrl
            
            // Save progress data
            val progressData = mutableMapOf<String, Any>()
            progressData["streak"] = profile.streak
            progressData["wordsLearned"] = profile.wordsLearned
            progressData["lessonsCompleted"] = profile.lessonsCompleted
            progressData["daysActive"] = profile.daysActive
            progressData["lastActiveDate"] = profile.lastActiveDate ?: System.currentTimeMillis()
            progressData["totalExperience"] = profile.totalExperience
            
            // Save settings data
            val settingsData = mutableMapOf<String, Any>()
            settingsData["studyTimeMinutes"] = profile.studyTimeMinutes
            
            // Save to Realtime Database với cấu trúc đúng
            val updates = hashMapOf<String, Any>(
                "profile" to profileData,
                "progress" to progressData,
                "settings" to settingsData
            )
            
            database.reference
                .child("app_data")
                .child("users")
                .child(uid)
                .updateChildren(updates)
                .await()
            
            Result.success(profile)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    
    suspend fun getUserProfile(): Result<UserProfileData?> {
        return try {
            val uid = userId ?: return Result.failure(Exception("User not authenticated"))
            
            // Đọc dữ liệu từ cấu trúc mới - profile, progress, settings riêng biệt
            val userRef = database.reference
                .child("app_data")
                .child("users")
                .child(uid)
            
            val snapshot = userRef.get().await()
            
            if (!snapshot.child("profile").exists()) {
                return Result.success(null)
            }
            
            // Đọc profile data
            val profileSnapshot = snapshot.child("profile")
            val progressSnapshot = snapshot.child("progress")
            val settingsSnapshot = snapshot.child("settings")
            
            val profileData = profileSnapshot.getValue(UserProfileData::class.java)
            if (profileData != null) {
                // Cập nhật thêm dữ liệu từ progress và settings
                val updatedProfile = profileData.copy(
                    streak = progressSnapshot.child("streak").getValue(Int::class.java) ?: profileData.streak,
                    wordsLearned = progressSnapshot.child("wordsLearned").getValue(Int::class.java) ?: profileData.wordsLearned,
                    lessonsCompleted = progressSnapshot.child("lessonsCompleted").getValue(Int::class.java) ?: profileData.lessonsCompleted,
                    daysActive = progressSnapshot.child("daysActive").getValue(Int::class.java) ?: profileData.daysActive,
                    totalExperience = progressSnapshot.child("totalExperience").getValue(Long::class.java) ?: profileData.totalExperience,
                    studyTimeMinutes = settingsSnapshot.child("studyTimeMinutes").getValue(Int::class.java) ?: profileData.studyTimeMinutes
                )
                
                Result.success(updatedProfile)
            } else {
                Result.success(null)
            }
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

    // Quiz Repository (API đặc thù cho ViewModel Quiz)
    suspend fun getQuizCategories(): List<QuizCategory> {
        val categoriesRef = database.reference.child("quizzes")
        return try {
            val snapshot = categoriesRef.get().await()
            val categories = mutableListOf<QuizCategory>()

            snapshot.children.forEach { categorySnapshot ->
                val id = categorySnapshot.key ?: ""
                val description = categorySnapshot.child("description").getValue(String::class.java) ?: ""
                val icon = categorySnapshot.child("icon").getValue(String::class.java) ?: ""

                val title = when (id.lowercase()) {
                    "animals" -> "Động Vật"
                    "colors" -> "Màu Sắc"
                    "family" -> "Gia Đình"
                    "food" -> "Thức Ăn"
                    "numbers" -> "Số Đếm"
                    "time" -> "Thời Gian"
                    "transportation" -> "Phương Tiện"
                    "weather" -> "Thời Tiết"
                    "body" -> "Cơ Thể"
                    "clothing" -> "Quần Áo"
                    "house" -> "Nhà Cửa"
                    "nature" -> "Thiên Nhiên"
                    "school" -> "Trường Học"
                    "work" -> "Công Việc"
                    "hobby" -> "Sở Thích"
                    "sports" -> "Thể Thao"
                    "music" -> "Âm Nhạc"
                    "travel" -> "Du Lịch"
                    "shopping" -> "Mua Sắm"
                    "health" -> "Sức Khỏe"
                    else -> id.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
                }

                categories.add(
                    QuizCategory(
                        id = id,
                        title = title,
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
        val quizRef = database.reference.child("quizzes/$category/levels/$level")
        return try {
            val snapshot = quizRef.get().await()
            val quizzes = mutableListOf<Quiz>()

            snapshot.children.forEach { quizSnapshot ->
                val id = quizSnapshot.child("id").getValue(String::class.java) ?: ""
                val title = quizSnapshot.child("title").getValue(String::class.java) ?: ""
                val description = quizSnapshot.child("description").getValue(String::class.java) ?: ""
                val timeLimit = quizSnapshot.child("timeLimit").getValue(Int::class.java) ?: 0

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
        val quizRef = database.reference.child("quizzes/$category/levels/$level/$quizId")
        return try {
            val snapshot = quizRef.get().await()

            val id = snapshot.child("id").getValue(String::class.java) ?: ""
            val title = snapshot.child("title").getValue(String::class.java) ?: ""
            val description = snapshot.child("description").getValue(String::class.java) ?: ""
            val timeLimit = snapshot.child("timeLimit").getValue(Int::class.java) ?: 0

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
