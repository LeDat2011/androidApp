package com.example.composeapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.screens.FlashcardData
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FlashcardViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    private val vocabularyRef = database.getReference("app_data/vocabulary")
    
    private val _flashcards = MutableStateFlow<List<FlashcardData>>(emptyList())
    val flashcards: StateFlow<List<FlashcardData>> = _flashcards

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // Theo dõi số từ vựng đã học trong phiên hiện tại
    private val _wordsLearned = MutableStateFlow(0)
    val wordsLearned: StateFlow<Int> = _wordsLearned
    
    // Theo dõi tổng số từ vựng đã học
    private val _totalWordsLearned = MutableStateFlow(0)
    val totalWordsLearned: StateFlow<Int> = _totalWordsLearned
    
    // Theo dõi thời gian học tập hôm nay
    private val _todayStudyTime = MutableStateFlow(0)
    val todayStudyTime: StateFlow<Int> = _todayStudyTime
    
    // Theo dõi thời gian bắt đầu học
    private var startTime = System.currentTimeMillis()

    init {
        // Tải dữ liệu từ Firebase khi khởi tạo ViewModel
        loadUserLearningData()
    }

    // Tải dữ liệu học tập của người dùng từ Firebase
    private fun loadUserLearningData() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                
                // Tải tổng số từ vựng đã học
                loadTotalWordsLearned(currentUser.uid)
                
                // Tải thời gian học hôm nay
                loadTodayStudyTime(currentUser.uid)
            } catch (e: Exception) {
                _error.value = "Không thể tải dữ liệu học tập: ${e.message}"
            }
        }
    }
    
    // Tải tổng số từ vựng đã học từ Firebase
    private fun loadTotalWordsLearned(userId: String) {
        database.reference
            .child("users")
            .child(userId)
            .child("progress")
            .child("wordsLearned")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val wordsLearned = snapshot.getValue(Int::class.java) ?: 0
                    _totalWordsLearned.value = wordsLearned
                }
                
                override fun onCancelled(error: DatabaseError) {
                    _error.value = "Không thể tải số từ vựng đã học: ${error.message}"
                }
            })
    }
    
    // Tải thời gian học hôm nay từ Firebase
    private fun loadTodayStudyTime(userId: String) {
        val calendar = java.util.Calendar.getInstance()
        val today = "${calendar.get(java.util.Calendar.YEAR)}-${calendar.get(java.util.Calendar.MONTH) + 1}-${calendar.get(java.util.Calendar.DAY_OF_MONTH)}"
        
        database.reference
            .child("users")
            .child(userId)
            .child("learning_stats")
            .child("study_time")
            .child(today)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val studyTime = snapshot.getValue(Int::class.java) ?: 0
                    _todayStudyTime.value = studyTime
                }
                
                override fun onCancelled(error: DatabaseError) {
                    _error.value = "Không thể tải thời gian học: ${error.message}"
                }
            })
    }

    fun loadFlashcards(categoryName: String, level: String) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                // Đặt lại bộ đếm từ vựng đã học trong phiên hiện tại và thời gian bắt đầu
                _wordsLearned.value = 0
                startTime = System.currentTimeMillis()

                val flashcardList = suspendCoroutine<List<FlashcardData>> { continuation ->
                    vocabularyRef
                        .child(categoryName)
                        .child(level)
                        .addListenerForSingleValueEvent(object : ValueEventListener {
                            override fun onDataChange(snapshot: DataSnapshot) {
                                val flashcards = mutableListOf<FlashcardData>()
                                val totalVocabs = snapshot.childrenCount.toInt()
                                
                                if (totalVocabs == 0) {
                                    continuation.resume(emptyList())
                                    return
                                }
                                
                                var loadedCount = 0
                                
                                for (vocabSnapshot in snapshot.children) {
                                    val id = vocabSnapshot.key ?: continue // Lấy ID của từ vựng
                                    val japanese = vocabSnapshot.child("japanese").getValue(String::class.java) ?: ""
                                    val reading = vocabSnapshot.child("reading").getValue(String::class.java) ?: ""
                                    val vietnamese = vocabSnapshot.child("vietnamese").getValue(String::class.java) ?: ""
                                    val example = vocabSnapshot.child("example").getValue(String::class.java) ?: ""

                                    // Tách ví dụ thành tiếng Nhật và tiếng Việt
                                    val parts = example.split(" - ")
                                    val japaneseExample = parts.getOrNull(0) ?: ""
                                    val vietnameseExample = parts.getOrNull(1) ?: ""

                                    // Kiểm tra xem từ vựng đã được học chưa
                                    checkWordLearned(id) { isLearned ->
                                        flashcards.add(
                                            FlashcardData(
                                                id = id,
                                                word = japanese,
                                                reading = reading,
                                                meaning = vietnamese,
                                                example = japaneseExample,
                                                exampleMeaning = vietnameseExample,
                                                isLearned = isLearned
                                            )
                                        )
                                        
                                        loadedCount++
                                        
                                        // Nếu đã tải xong tất cả từ vựng
                                        if (loadedCount == totalVocabs) {
                                            // Đếm số từ đã học trong bộ này
                                            val learnedCount = flashcards.count { it.isLearned }
                                            _wordsLearned.value = learnedCount
                                            
                                            continuation.resume(flashcards)
                                        }
                                    }
                                }
                            }

                            override fun onCancelled(error: DatabaseError) {
                                continuation.resumeWithException(error.toException())
                            }
                        })
                }

                _flashcards.value = flashcardList
            } catch (e: Exception) {
                _error.value = "Không thể tải dữ liệu: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    // Kiểm tra xem từ vựng đã được học chưa
    private fun checkWordLearned(wordId: String, callback: (Boolean) -> Unit) {
        val currentUser = auth.currentUser
        if (currentUser == null) {
            callback(false)
            return
        }

        database.reference
            .child("users")
            .child(currentUser.uid)
            .child("learning")
            .child("vocabulary")
            .child(wordId)
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    callback(snapshot.exists() && snapshot.getValue(Boolean::class.java) == true)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(false)
                }
            })
    }
    
    // Đánh dấu một từ vựng đã học
    fun markWordAsLearned(wordId: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                
                // Đánh dấu từ vựng đã học trên Firebase
                database.reference
                    .child("users")
                    .child(currentUser.uid)
                    .child("learning")
                    .child("vocabulary")
                    .child(wordId)
                    .setValue(true)
                    .await()
                
                // Cập nhật trạng thái trong danh sách flashcard
                val updatedFlashcards = _flashcards.value.map { flashcard ->
                    if (flashcard.id == wordId) {
                        flashcard.copy(isLearned = true)
                    } else {
                        flashcard
                    }
                }
                _flashcards.value = updatedFlashcards
                
                // Tăng số từ vựng đã học trong phiên hiện tại
                _wordsLearned.value = _wordsLearned.value + 1
                
                // Cập nhật tổng số từ vựng đã học trên Firebase
                updateTotalWordsLearned(1)
            } catch (e: Exception) {
                _error.value = "Không thể đánh dấu từ vựng đã học: ${e.message}"
            }
        }
    }
    
    // Cập nhật tổng số từ vựng đã học
    private fun updateTotalWordsLearned(newWords: Int) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val userRef = database.reference.child("users").child(currentUser.uid).child("progress")
                
                // Lấy số từ vựng hiện tại
                val progressSnapshot = userRef.get().await()
                val currentWordsLearned = progressSnapshot.child("wordsLearned").getValue(Int::class.java) ?: 0
                
                // Cập nhật số từ vựng mới
                val updatedWordsLearned = currentWordsLearned + newWords
                userRef.child("wordsLearned").setValue(updatedWordsLearned).await()
                
                // Cập nhật state
                _totalWordsLearned.value = updatedWordsLearned
            } catch (e: Exception) {
                _error.value = "Không thể cập nhật tổng số từ vựng đã học: ${e.message}"
            }
        }
    }
    
    // Cập nhật thời gian học tập khi hoàn thành bộ flashcard
    fun updateStudyTime() {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                val endTime = System.currentTimeMillis()
                val studyTimeMinutes = ((endTime - startTime) / 1000 / 60).toInt()
                
                // Chỉ cập nhật nếu thời gian học lớn hơn 0
                if (studyTimeMinutes > 0) {
                    val calendar = java.util.Calendar.getInstance()
                    val today = "${calendar.get(java.util.Calendar.YEAR)}-${calendar.get(java.util.Calendar.MONTH) + 1}-${calendar.get(java.util.Calendar.DAY_OF_MONTH)}"
                    
                    // Lấy thời gian học hiện tại từ Firebase
                    val statsRef = database.reference
                        .child("users")
                        .child(currentUser.uid)
                        .child("learning_stats")
                        .child("study_time")
                        .child(today)
                    
                    val statsSnapshot = statsRef.get().await()
                    val currentStudyTime = statsSnapshot.getValue(Int::class.java) ?: 0
                    
                    // Cập nhật thời gian học lên Firebase
                    val updatedStudyTime = currentStudyTime + studyTimeMinutes
                    statsRef.setValue(updatedStudyTime).await()
                    
                    // Cập nhật state
                    _todayStudyTime.value = updatedStudyTime
                    
                    // Log để debug
                    println("Đã cập nhật thời gian học: $updatedStudyTime phút cho ngày $today")
                    
                    // Kiểm tra và cập nhật streak nếu cần
                    updateStreakIfNeeded(updatedStudyTime)
                    
                    // Reset thời gian bắt đầu
                    startTime = System.currentTimeMillis()
                }
            } catch (e: Exception) {
                _error.value = "Không thể cập nhật thời gian học: ${e.message}"
            }
        }
    }
    
    // Cập nhật streak nếu đạt mục tiêu học tập
    private fun updateStreakIfNeeded(todayStudyTime: Int) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                
                // Lấy mục tiêu học tập
                val settingsRef = database.reference
                    .child("users")
                    .child(currentUser.uid)
                    .child("settings")
                    .child("studyTimeMinutes")
                
                val settingsSnapshot = settingsRef.get().await()
                val targetStudyTime = settingsSnapshot.getValue(Int::class.java) ?: 30
                
                // Nếu đạt mục tiêu, cập nhật streak
                if (todayStudyTime >= targetStudyTime) {
                    val userRef = database.reference.child("users").child(currentUser.uid).child("progress")
                    
                    // Lấy thông tin streak hiện tại
                    val progressSnapshot = userRef.get().await()
                    val currentStreak = progressSnapshot.child("streak").getValue(Int::class.java) ?: 0
                    val lastStreakUpdate = progressSnapshot.child("lastStreakUpdate").getValue(Long::class.java) ?: 0
                    
                    val currentTime = System.currentTimeMillis()
                    val calendar = java.util.Calendar.getInstance()
                    
                    calendar.timeInMillis = lastStreakUpdate
                    val lastUpdateDay = calendar.get(java.util.Calendar.DAY_OF_YEAR)
                    val lastUpdateYear = calendar.get(java.util.Calendar.YEAR)
                    
                    calendar.timeInMillis = currentTime
                    val currentDay = calendar.get(java.util.Calendar.DAY_OF_YEAR)
                    val currentYear = calendar.get(java.util.Calendar.YEAR)
                    
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
                    }
                }
            } catch (e: Exception) {
                // Xử lý lỗi nếu cần
            }
        }
    }
    
    // Cập nhật khi hoàn thành bộ flashcard
    fun completeFlashcardSet(categoryName: String, level: String) {
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                
                // Đánh dấu bộ flashcard đã hoàn thành
                val completionRef = database.reference
                    .child("users")
                    .child(currentUser.uid)
                    .child("learning")
                    .child("completed_flashcards")
                    .child("${categoryName}_${level}")
                
                completionRef.setValue(System.currentTimeMillis()).await()
                
                // Cập nhật thời gian học
                updateStudyTime()
                
                // Cập nhật số từ vựng đã học nếu chưa được cập nhật
                if (_wordsLearned.value > 0) {
                    updateTotalWordsLearned(_wordsLearned.value)
                    _wordsLearned.value = 0 // Reset counter
                }
            } catch (e: Exception) {
                // Xử lý lỗi nếu cần
            }
        }
    }

    // Hàm để reset lỗi
    fun resetError() {
        _error.value = null
    }
} 