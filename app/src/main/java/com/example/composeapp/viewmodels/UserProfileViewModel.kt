package com.example.composeapp.viewmodels

import android.icu.util.Calendar
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.models.JapaneseLevel
import com.example.composeapp.models.UserProfileData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

sealed class SaveProfileState {
    object Idle : SaveProfileState()
    object Loading : SaveProfileState()
    object Success : SaveProfileState()
    data class Error(val message: String) : SaveProfileState()
}

sealed class LoadProfileState {
    object Loading : LoadProfileState()
    object Success : LoadProfileState()
    data class Error(val message: String) : LoadProfileState()
}

class UserProfileViewModel : ViewModel() {

    private val auth = FirebaseAuth.getInstance()
    private val database = FirebaseDatabase.getInstance()

    private val _saveProfileState = MutableStateFlow<SaveProfileState>(SaveProfileState.Idle)
    val saveProfileState: StateFlow<SaveProfileState> = _saveProfileState

    private val _loadProfileState = MutableStateFlow<LoadProfileState>(LoadProfileState.Loading)
    val loadProfileState: StateFlow<LoadProfileState> = _loadProfileState

    private val _profileData = MutableStateFlow<UserProfileData?>(null)
    val profileData: StateFlow<UserProfileData?> = _profileData

    // Thêm StateFlow để theo dõi thời gian học tập
    private val _studyTimeToday = MutableStateFlow(0)
    val studyTimeToday: StateFlow<Int> = _studyTimeToday

    // Thêm StateFlow để theo dõi số từ vựng đã học
    private val _wordsLearned = MutableStateFlow(0)
    val wordsLearned: StateFlow<Int> = _wordsLearned

    // Thêm StateFlow để theo dõi tiến độ học tập
    private val _learningProgress = MutableStateFlow(0f)
    val learningProgress: StateFlow<Float> = _learningProgress

    // Tiến độ theo danh mục cho HomeScreen (FlashcardCategory -> progress 0..1)
    private val _categoryProgress = MutableStateFlow<Map<String, Float>>(emptyMap())
    val categoryProgress: StateFlow<Map<String, Float>> = _categoryProgress

    init {
        loadUserProfile()
        loadStudyTimeToday()
        loadWordsLearned()
        loadCategoryProgress()
    }

    fun saveUserProfile(profile: UserProfileData) {
        viewModelScope.launch {
            try {
                _saveProfileState.value = SaveProfileState.Loading
                
                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
                
                // Save profile data theo cấu trúc Firebase mới - chia thành profile, progress, settings
                val profileData = mutableMapOf<String, Any>()
                profileData["userId"] = profile.userId.ifEmpty { userId }
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
                    .child(userId)
                    .updateChildren(updates)
                    .addOnSuccessListener {
                        _profileData.value = profile
                        _saveProfileState.value = SaveProfileState.Success
                    }
                    .addOnFailureListener { e ->
                        _saveProfileState.value = SaveProfileState.Error(e.message ?: "Failed to save profile")
                    }
            } catch (e: Exception) {
                _saveProfileState.value = SaveProfileState.Error(e.message ?: "Unknown error occurred")
            }
        }
    }

    fun loadUserProfile() {
        val userId = auth.currentUser?.uid ?: return
        
        _loadProfileState.value = LoadProfileState.Loading
        
        // Đọc dữ liệu từ cấu trúc mới - profile, progress, settings riêng biệt
        database.reference.child("app_data").child("users").child(userId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    try {
                        // Đọc profile data
                        val profileSnapshot = snapshot.child("profile")
                        val progressSnapshot = snapshot.child("progress")
                        val settingsSnapshot = snapshot.child("settings")
                        
                        if (profileSnapshot.exists()) {
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
                                
                                _profileData.value = updatedProfile
                                _loadProfileState.value = LoadProfileState.Success
                            } else {
                                _loadProfileState.value = LoadProfileState.Error("Không thể đọc dữ liệu profile")
                            }
                        } else {
                            _loadProfileState.value = LoadProfileState.Error("Không tìm thấy dữ liệu profile")
                        }
                    } catch (e: Exception) {
                        _loadProfileState.value = LoadProfileState.Error(e.message ?: "Lỗi khi đọc profile")
                    }
                }
                
                override fun onCancelled(error: DatabaseError) {
                    _loadProfileState.value = LoadProfileState.Error(error.message)
                }
            })
    }

    // Tải tiến độ theo từng danh mục từ app_data/users/{uid}/learning_stats/category_progress
    private fun loadCategoryProgress() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val progressRef = database.reference
                    .child("app_data")
                    .child("users")
                    .child(currentUser.uid)
                    .child("learning_stats")
                    .child("category_progress")

                val snapshot = progressRef.get().await()
                val map = mutableMapOf<String, Float>()
                for (catSnap in snapshot.children) {
                    val category = catSnap.key ?: continue
                    val accuracy = catSnap.child("accuracy").getValue(Double::class.java)?.toFloat()
                    val wordsLearned = catSnap.child("wordsLearned").getValue(Int::class.java) ?: 0
                    val lessonsCompleted = catSnap.child("lessonsCompleted").getValue(Int::class.java) ?: 0

                    // Quy đổi về 0..1: 50% theo accuracy, 50% theo số liệu tiến độ tương đối (heuristic)
                    val accuracyPart = (accuracy ?: 0f).coerceIn(0f, 1f) * 0.5f
                    val progressPart = (minOf(wordsLearned / 50f, 1f) * 0.35f) + (minOf(lessonsCompleted / 10f, 1f) * 0.15f)
                    map[category] = (accuracyPart + progressPart).coerceIn(0f, 1f)
                }
                _categoryProgress.value = map
            } catch (e: Exception) {
                // Ignore
            }
        }
    }
    
    fun checkUserHasProfile(onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onResult(false)
            return
                }
                
        database.reference.child("app_data").child("users").child(userId).child("profile")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    onResult(snapshot.exists())
                }
                
                override fun onCancelled(error: DatabaseError) {
                    onResult(false)
                }
            })
    }
    
    fun resetState() {
        _saveProfileState.value = SaveProfileState.Idle
        _loadProfileState.value = LoadProfileState.Success
    }

    fun updateUserActivity() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser
                if (currentUser == null) return@launch

                val userRef = database.reference.child("app_data").child("users").child(currentUser.uid).child("progress")
                
                // Lấy thông tin hiện tại
                val progressSnapshot = userRef.get().await()
                val lastActiveDate = progressSnapshot.child("lastActiveDate").getValue(Long::class.java) ?: 0
                val daysActive = progressSnapshot.child("daysActive").getValue(Int::class.java) ?: 0
                
                val currentTime = System.currentTimeMillis()
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = lastActiveDate
                val lastActiveDay = calendar.get(Calendar.DAY_OF_YEAR)
                
                calendar.timeInMillis = currentTime
                val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
                
                // Nếu là ngày mới, tăng số ngày hoạt động
                if (currentDay != lastActiveDay) {
                    val updates = mapOf(
                        "lastActiveDate" to currentTime,
                        "daysActive" to (daysActive + 1)
                    )
                    userRef.updateChildren(updates).await()
                } else {
                    // Chỉ cập nhật thời gian hoạt động cuối
                    userRef.child("lastActiveDate").setValue(currentTime).await()
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }

    // Upload ảnh đại diện và cập nhật avatarUrl
    /*
    suspend fun uploadProfileImage(imageUri: Uri): String? {
        return try {
            val currentUser = auth.currentUser ?: return null
            
            // Tạo đường dẫn lưu trữ trong Firebase Storage
            val storageRef = storage.reference.child("profile_images/${currentUser.uid}_${System.currentTimeMillis()}.jpg")
            
            // Tải ảnh lên Firebase Storage
            val uploadTask = storageRef.putFile(imageUri).await()
            
            // Lấy URL của ảnh
            val downloadUrl = storageRef.downloadUrl.await()
            
            // Cập nhật avatarUrl trong database
            val userRef = database.reference.child("app_data").child("users").child(currentUser.uid).child("profile").child("avatarUrl")
            userRef.setValue(downloadUrl.toString()).await()
            
            // Trả về URL để cập nhật trong local state
            downloadUrl.toString()
            
        } catch (e: Exception) {
            null
        }
    }
    */
    
    // Cập nhật hồ sơ người dùng với ảnh đại diện mới (nếu có)
    fun saveUserProfileWithImage(profile: UserProfileData, imageUri: Uri?) {
        viewModelScope.launch {
            try {
                _saveProfileState.value = SaveProfileState.Loading
                
                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
                var updatedAvatarUrl = profile.avatarUrl
                
                // Upload image if provided
                if (imageUri != null) {
                    val userRef = database.reference.child("app_data").child("users").child(userId).child("profile").child("avatarUrl")
                    userRef.setValue(imageUri.toString()).await()
                    updatedAvatarUrl = imageUri.toString()
                }
                
                // Save profile data
                val profileData = mutableMapOf<String, Any>()
                profileData["name"] = profile.name
                profileData["email"] = profile.email
                profileData["age"] = profile.age
                profileData["currentLevel"] = profile.currentLevel
                profileData["targetLevel"] = profile.targetLevel
                if (updatedAvatarUrl != null) profileData["avatarUrl"] = updatedAvatarUrl
                if (profile.registrationDate != null) profileData["registrationDate"] = profile.registrationDate
                
                // Save progress data
                val progressData = mutableMapOf<String, Any>()
                progressData["streak"] = profile.streak
                progressData["wordsLearned"] = profile.wordsLearned
                progressData["lessonsCompleted"] = profile.lessonsCompleted
                progressData["daysActive"] = profile.daysActive
                if (profile.lastActiveDate != null) progressData["lastActiveDate"] = profile.lastActiveDate
                
                // Save settings data
                val settingsData = mutableMapOf<String, Any>()
                settingsData["studyTimeMinutes"] = profile.studyTimeMinutes
                
                // Save to Realtime Database
                val updates = hashMapOf<String, Any>(
                    "profile" to profileData,
                    "progress" to progressData,
                    "settings" to settingsData
                )
                database.reference.child("app_data").child("users").child(userId).updateChildren(updates).await()
                
                _profileData.value = profile.copy(avatarUrl = updatedAvatarUrl)
                _saveProfileState.value = SaveProfileState.Success
            } catch (e: Exception) {
                _saveProfileState.value = SaveProfileState.Error(e.message ?: "Lỗi không xác định")
            }
        }
    }

    // Hàm hỗ trợ chuyển đổi String sang JapaneseLevel
    private fun getJapaneseLevelFromString(level: String): JapaneseLevel {
        return when (level) {
            "N1" -> JapaneseLevel.N1
            "N2" -> JapaneseLevel.N2
            "N3" -> JapaneseLevel.N3
            "N4" -> JapaneseLevel.N4
            else -> JapaneseLevel.N5 // Mặc định là N5
        }
    }

    // Cập nhật số từ vựng đã học
    fun updateWordsLearned(newWords: Int) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userRef = database.reference.child("app_data").child("users").child(currentUser.uid).child("progress")
                
                // Lấy số từ vựng hiện tại
                val progressSnapshot = userRef.get().await()
                val currentWordsLearned = progressSnapshot.child("wordsLearned").getValue(Int::class.java) ?: 0
                
                // Cập nhật số từ vựng mới
                val updatedWordsLearned = currentWordsLearned + newWords
                userRef.child("wordsLearned").setValue(updatedWordsLearned).await()
                
                // Cập nhật state
                _wordsLearned.value = updatedWordsLearned
                
                // Cập nhật profile data
                _profileData.value = _profileData.value?.copy(wordsLearned = updatedWordsLearned)
                
                // Cập nhật tiến độ học tập
                calculateLearningProgress()
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
    
    // Cập nhật thời gian học tập
    fun updateStudyTime(minutes: Int) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userRef = database.reference.child("app_data").child("users").child(currentUser.uid).child("learning_stats")
                
                val calendar = Calendar.getInstance()
                val today = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
                
                // Lấy thời gian học hôm nay
                val statsSnapshot = userRef.child("study_time").child(today).get().await()
                val currentStudyTime = statsSnapshot.getValue(Int::class.java) ?: 0
                
                // Cập nhật thời gian học
                val updatedStudyTime = currentStudyTime + minutes
                userRef.child("study_time").child(today).setValue(updatedStudyTime).await()
                
                // Cập nhật state
                _studyTimeToday.value = updatedStudyTime
                
                // Cập nhật streak nếu đạt mục tiêu học tập
                val settingsSnapshot = database.reference.child("app_data").child("users").child(currentUser.uid).child("settings").get().await()
                val targetStudyTime = settingsSnapshot.child("studyTimeMinutes").getValue(Int::class.java) ?: 30
                
                if (updatedStudyTime >= targetStudyTime) {
                    updateStreak()
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
    
    // Cập nhật streak khi hoàn thành mục tiêu học tập hàng ngày
    private fun updateStreak() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userRef = database.reference.child("app_data").child("users").child(currentUser.uid).child("progress")
                
                // Lấy thông tin streak hiện tại
                val progressSnapshot = userRef.get().await()
                val currentStreak = progressSnapshot.child("streak").getValue(Int::class.java) ?: 0
                val lastStreakUpdate = progressSnapshot.child("lastStreakUpdate").getValue(Long::class.java) ?: 0
                
                val currentTime = System.currentTimeMillis()
                val calendar = Calendar.getInstance()
                
                calendar.timeInMillis = lastStreakUpdate
                val lastUpdateDay = calendar.get(Calendar.DAY_OF_YEAR)
                val lastUpdateYear = calendar.get(Calendar.YEAR)
                
                calendar.timeInMillis = currentTime
                val currentDay = calendar.get(Calendar.DAY_OF_YEAR)
                val currentYear = calendar.get(Calendar.YEAR)
                
                // Kiểm tra xem đã cập nhật streak hôm nay chưa
                if (currentDay != lastUpdateDay || currentYear != lastUpdateYear) {
                    // Kiểm tra xem có bị mất streak không (không học liên tục)
                    val isConsecutiveDay = if (lastStreakUpdate == 0L) {
                        true // Lần đầu tiên học
                    } else {
                        val dayDiff = if (currentYear > lastUpdateYear) {
                            // Xử lý trường hợp năm mới
                            val daysInLastYear = if (lastUpdateYear % 4 == 0) 366 else 365
                            (currentDay + (daysInLastYear - lastUpdateDay)) % 2 == 1
                        } else {
                            (currentDay - lastUpdateDay) == 1
                        }
                        dayDiff
                    }
                    
                    val newStreak = if (isConsecutiveDay) currentStreak + 1 else 1
                    
                    val updates = mapOf(
                        "streak" to newStreak,
                        "lastStreakUpdate" to currentTime
                    )
                    userRef.updateChildren(updates).await()
                    
                    // Cập nhật profile data
                    _profileData.value = _profileData.value?.copy(streak = newStreak)
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
    
    // Cập nhật số bài học đã hoàn thành
    fun updateLessonsCompleted(lessonId: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userRef = database.reference.child("app_data").child("users").child(currentUser.uid)
                
                // Đánh dấu bài học đã hoàn thành
                userRef.child("learning").child("completedLessons").child(lessonId)
                    .setValue(System.currentTimeMillis()).await()
                
                // Cập nhật số bài học đã hoàn thành
                val progressRef = userRef.child("progress")
                val progressSnapshot = progressRef.get().await()
                val currentLessonsCompleted = progressSnapshot.child("lessonsCompleted").getValue(Int::class.java) ?: 0
                
                val newLessonsCompleted = currentLessonsCompleted + 1
                progressRef.child("lessonsCompleted").setValue(newLessonsCompleted).await()
                
                // Cập nhật profile data
                _profileData.value = _profileData.value?.copy(lessonsCompleted = newLessonsCompleted)
                
                // Cập nhật tiến độ học tập
                calculateLearningProgress()
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
    
    // Đánh dấu từ vựng đã học
    fun markWordAsLearned(wordId: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userRef = database.reference.child("app_data").child("users").child(currentUser.uid)
                
                // Đánh dấu từ vựng đã học
                userRef.child("learning").child("vocabulary").child(wordId)
                    .setValue(true).await()
                
                // Cập nhật số từ vựng đã học
                updateWordsLearned(1)
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
    
    // Tính toán tiến độ học tập dựa trên số từ vựng và bài học đã hoàn thành
    private fun calculateLearningProgress() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userRef = database.reference.child("app_data").child("users").child(currentUser.uid)
                
                // Lấy thông tin tiến độ
                val progressSnapshot = userRef.child("progress").get().await()
                val wordsLearned = progressSnapshot.child("wordsLearned").getValue(Int::class.java) ?: 0
                val lessonsCompleted = progressSnapshot.child("lessonsCompleted").getValue(Int::class.java) ?: 0
                
                // Lấy thông tin level hiện tại
                val profileSnapshot = userRef.child("profile").get().await()
                val currentLevelStr = profileSnapshot.child("currentLevel").getValue(String::class.java) ?: "BEGINNER"
                val currentLevel = getJapaneseLevelFromString(currentLevelStr)
                
                // Tính toán tiến độ dựa trên level
                val targetWords = when (currentLevel) {
                    JapaneseLevel.BEGINNER -> 100
                    JapaneseLevel.N5 -> 800
                    JapaneseLevel.N4 -> 1500
                    JapaneseLevel.N3 -> 3000
                    JapaneseLevel.N2 -> 6000
                    JapaneseLevel.N1 -> 10000
                }
                
                val targetLessons = when (currentLevel) {
                    JapaneseLevel.BEGINNER -> 10
                    JapaneseLevel.N5 -> 30
                    JapaneseLevel.N4 -> 50
                    JapaneseLevel.N3 -> 80
                    JapaneseLevel.N2 -> 120
                    JapaneseLevel.N1 -> 200
                }
                
                // Tính tiến độ tổng thể (70% từ vựng, 30% bài học)
                val wordsProgress = (wordsLearned.toFloat() / targetWords).coerceAtMost(1f) * 0.7f
                val lessonsProgress = (lessonsCompleted.toFloat() / targetLessons).coerceAtMost(1f) * 0.3f
                val totalProgress = wordsProgress + lessonsProgress
                
                // Cập nhật tiến độ
                _learningProgress.value = totalProgress
                
                // Lưu tiến độ vào Firebase
                userRef.child("progress").child("learningProgress").setValue(totalProgress).await()
                
                // Kiểm tra xem có thể lên level không
                if (totalProgress >= 0.9f && currentLevel != JapaneseLevel.N1) {
                    // Đề xuất lên level
                    userRef.child("progress").child("readyForLevelUp").setValue(true).await()
                }
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
    
    // Lấy thời gian học tập hôm nay
    private fun loadStudyTimeToday() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val calendar = Calendar.getInstance()
                val today = "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
                
                val statsRef = database.reference
                    .child("app_data")
                    .child("users")
                    .child(currentUser.uid)
                    .child("learning_stats")
                    .child("study_time")
                    .child(today)
                
                val snapshot = statsRef.get().await()
                val studyTime = snapshot.getValue(Int::class.java) ?: 0
                _studyTimeToday.value = studyTime
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
    
    // Lấy số từ vựng đã học
    private fun loadWordsLearned() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val progressRef = database.reference
                    .child("app_data")
                    .child("users")
                    .child(currentUser.uid)
                    .child("progress")
                    .child("wordsLearned")
                
                val snapshot = progressRef.get().await()
                val words = snapshot.getValue(Int::class.java) ?: 0
                _wordsLearned.value = words
            } catch (e: Exception) {
                // Handle error if needed
            }
        }
    }
} 