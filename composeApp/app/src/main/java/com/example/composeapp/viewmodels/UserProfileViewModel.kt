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

    init {
        loadUserProfile()
    }

    fun saveUserProfile(profile: UserProfileData) {
        viewModelScope.launch {
            try {
                _saveProfileState.value = SaveProfileState.Loading
                
                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
                
                // Save profile data to Realtime Database
                database.reference
                    .child("users")
                    .child(userId)
                    .child("profile")
                    .setValue(profile)
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
                
        database.reference.child("users").child(userId).child("profile")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                    val profile = snapshot.getValue(UserProfileData::class.java)
                    _profileData.value = profile
                    _loadProfileState.value = LoadProfileState.Success
                    }
                    
                    override fun onCancelled(error: DatabaseError) {
                        _loadProfileState.value = LoadProfileState.Error(error.message)
                    }
                })
    }
    
    fun checkUserHasProfile(onResult: (Boolean) -> Unit) {
        val userId = auth.currentUser?.uid
        if (userId == null) {
            onResult(false)
            return
                }
                
        database.reference.child("users").child(userId).child("profile")
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

                val userRef = database.reference.child("users").child(currentUser.uid).child("progress")
                
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
            val userRef = database.reference.child("users").child(currentUser.uid).child("profile").child("avatarUrl")
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
                var avatarUrl = profile.avatarUrl
                
                // Upload image if provided
                if (imageUri != null) {
                    val userRef = database.reference.child("users").child(userId).child("profile").child("avatarUrl")
                    userRef.setValue(imageUri.toString()).await()
                    avatarUrl = imageUri.toString()
                }
                
                // Save profile data
                val profileData = mutableMapOf<String, Any>()
                profile.name?.let { profileData["name"] = it }
                profile.age?.let { profileData["age"] = it }
                profile.currentLevel?.let { profileData["currentLevel"] = it.name }
                profile.targetLevel?.let { profileData["targetLevel"] = it.name }
                avatarUrl?.let { profileData["avatarUrl"] = it }
                profile.registrationDate?.let { profileData["registrationDate"] = it }
                
                // Save progress data
                val progressData = mutableMapOf<String, Any>()
                progressData["streak"] = profile.streak
                progressData["wordsLearned"] = profile.wordsLearned
                progressData["lessonsCompleted"] = profile.lessonsCompleted
                progressData["daysActive"] = profile.daysActive
                profile.lastActiveDate?.let { progressData["lastActiveDate"] = it }
                
                // Save settings data
                val settingsData = mutableMapOf<String, Any>()
                profile.studyTimeMinutes?.let { settingsData["studyTimeMinutes"] = it }
                
                // Save to Realtime Database
                val updates = hashMapOf<String, Any>(
                    "profile" to profileData,
                    "progress" to progressData,
                    "settings" to settingsData
                )
                database.reference.child("users").child(userId).updateChildren(updates).await()
                
                _profileData.value = profile
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
} 