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
            .child("app_data")
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
            .child("app_data")
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

                val snapshot = vocabularyRef.get().await()
                val candidates = mutableListOf<FlashcardData>()
                val tasks = mutableListOf<kotlin.coroutines.Continuation<Unit>>()

                for (vocabSnapshot in snapshot.children) {
                    val id = vocabSnapshot.key ?: continue
                    val itemLevel = vocabSnapshot.child("level").getValue(String::class.java) ?: ""
                    val categories = vocabSnapshot.child("categories").children.mapNotNull { it.getValue(String::class.java) }

                    // Chuẩn hóa so khớp level + category: không phân biệt hoa thường, bỏ khoảng trắng thừa
                    val normalizedRequestedLevel = level.trim()
                    val normalizedRequestedCategory = categoryName.trim().lowercase()
                    val vocabCategoriesNormalized = categories.map { it.trim().lowercase() }

                    val matches = (
                        itemLevel.equals(normalizedRequestedLevel, ignoreCase = true)
                    ) && (
                        vocabCategoriesNormalized.contains(normalizedRequestedCategory)
                    )
                    if (!matches) continue

                    val japanese = vocabSnapshot.child("japanese").getValue(String::class.java) ?: ""
                    val reading = vocabSnapshot.child("reading").getValue(String::class.java) ?: ""
                    val vietnamese = vocabSnapshot.child("vietnamese").getValue(String::class.java) ?: ""
                    // Lấy ví dụ: ưu tiên exampleSentences[0], fallback sang trường 'example' dạng "jp - vi"
                    val examplesNode = vocabSnapshot.child("exampleSentences")
                    var japaneseExample = ""
                    var vietnameseExample = ""
                    if (examplesNode.exists()) {
                        // Cố lấy theo key "0"
                        val zero = examplesNode.child("0")
                        if (zero.exists()) {
                            japaneseExample = zero.child("japanese").getValue(String::class.java) ?: ""
                            vietnameseExample = zero.child("vietnamese").getValue(String::class.java) ?: ""
                        }
                        // Nếu vẫn rỗng, duyệt children tìm bản ghi hợp lệ đầu tiên
                        if (japaneseExample.isBlank() && vietnameseExample.isBlank()) {
                            for (child in examplesNode.children) {
                                val jp = child.child("japanese").getValue(String::class.java) ?: ""
                                val vi = child.child("vietnamese").getValue(String::class.java) ?: ""
                                if (jp.isNotBlank() || vi.isNotBlank()) {
                                    japaneseExample = jp
                                    vietnameseExample = vi
                                    break
                                }
                            }
                        }
                    }
                    if (japaneseExample.isBlank() && vietnameseExample.isBlank()) {
                        val exInline = vocabSnapshot.child("example").getValue(String::class.java) ?: ""
                        if (exInline.isNotBlank()) {
                            val parts = exInline.split(" - ")
                            japaneseExample = parts.getOrNull(0)?.trim() ?: ""
                            vietnameseExample = parts.getOrNull(1)?.trim() ?: ""
                        }
                    }
                    if (japaneseExample.isBlank() && vietnameseExample.isBlank()) {
                        japaneseExample = "Chưa có ví dụ"
                        vietnameseExample = ""
                    }

                    // Kiểm tra đã học
                    val isLearned = suspendCoroutine<Boolean> { cont ->
                        checkWordLearned(id) { learned -> cont.resume(learned) }
                    }

                    candidates.add(
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
                }

                // Sắp xếp ưu tiên theo SRS: từ quá hạn ôn tập > từ đang học > từ mới
                val currentUser = auth.currentUser
                val progressSnap = if (currentUser != null) {
                    database.reference
                        .child("app_data")
                        .child("users")
                        .child(currentUser.uid)
                        .child("learning")
                        .child("flashcardProgress")
                        .get().await()
                } else null

                fun priorityScore(wordId: String): Long {
                    val s = progressSnap?.child(wordId)
                    val next = s?.child("nextReviewDate")?.getValue(Long::class.java) ?: Long.MAX_VALUE
                    val mastery = s?.child("masteryLevel")?.getValue(String::class.java) ?: "NEW"
                    val now = System.currentTimeMillis()
                    val overdue = if (next == Long.MAX_VALUE) 0L else (now - next)
                    val masteryBias = when (mastery) {
                        "NEW" -> 0L
                        "LEARNING" -> 1_000L
                        "REVIEWING" -> 2_000L
                        "MASTERED" -> 3_000L
                        else -> 0L
                    }
                    return overdue + masteryBias
                }

                val sorted = candidates.sortedByDescending { priorityScore(it.id) }
                _wordsLearned.value = sorted.count { it.isLearned }
                _flashcards.value = sorted
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
            .child("app_data")
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
                val userLearningWordRef = database.reference
                    .child("app_data")
                    .child("users")
                    .child(currentUser.uid)
                    .child("learning")
                    .child("vocabulary")
                    .child(wordId)

                // Kiểm tra trạng thái trước đó để chỉ tăng tiến độ khi chuyển từ chưa học -> đã học
                val wasLearnedBefore = userLearningWordRef.get().await().getValue(Boolean::class.java) == true

                userLearningWordRef.setValue(true).await()
                
                // Cập nhật tiến độ flashcard theo SRS
                val fcRef = database.reference
                    .child("app_data")
                    .child("users")
                    .child(currentUser.uid)
                    .child("learning")
                    .child("flashcardProgress")
                    .child(wordId)
                val snap = fcRef.get().await()
                val currentLevelStr = snap.child("masteryLevel").getValue(String::class.java) ?: "NEW"
                val newLevelStr = when (currentLevelStr) {
                    "NEW" -> "LEARNING"
                    "LEARNING" -> "REVIEWING"
                    "REVIEWING" -> "MASTERED"
                    else -> "MASTERED"
                }
                val cal = java.util.Calendar.getInstance()
                when (newLevelStr) {
                    "LEARNING" -> cal.add(java.util.Calendar.DAY_OF_MONTH, 1)
                    "REVIEWING" -> cal.add(java.util.Calendar.DAY_OF_MONTH, 3)
                    "MASTERED" -> cal.add(java.util.Calendar.DAY_OF_MONTH, 7)
                }
                val updates = mapOf(
                    "wordId" to wordId,
                    "masteryLevel" to newLevelStr,
                    "lastReviewDate" to System.currentTimeMillis(),
                    "nextReviewDate" to cal.timeInMillis,
                    "correctCount" to ((snap.child("correctCount").getValue(Int::class.java) ?: 0) + 1)
                )
                fcRef.updateChildren(updates).await()
                
                // Nếu trước đó chưa học, tăng tiến độ theo category/level tương ứng
                if (!wasLearnedBefore) {
                    val vocabSnap = database.reference
                        .child("app_data")
                        .child("vocabulary")
                        .child(wordId)
                        .get()
                        .await()

                    val levelStr = vocabSnap.child("level").getValue(String::class.java)?.trim().orEmpty()
                    val categories = vocabSnap.child("categories").children.mapNotNull { it.getValue(String::class.java) }

                    if (levelStr.isNotBlank() && categories.isNotEmpty()) {
                        categories.forEach { cat ->
                            val categoryId = cat.trim()
                            val progressRef = database.reference
                                .child("app_data")
                                .child("users")
                                .child(currentUser.uid)
                                .child("learning")
                                .child("category_progress")
                                .child(categoryId)
                                .child(levelStr)
                                .child("wordsLearned")

                            val current = progressRef.get().await().getValue(Int::class.java) ?: 0
                            progressRef.setValue(current + 1).await()
                        }
                    }
                }

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
                val userRef = database.reference.child("app_data").child("users").child(currentUser.uid).child("progress")
                
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
                        .child("app_data")
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
                    .child("app_data")
                    .child("users")
                    .child(currentUser.uid)
                    .child("settings")
                    .child("studyTimeMinutes")
                
                val settingsSnapshot = settingsRef.get().await()
                val targetStudyTime = settingsSnapshot.getValue(Int::class.java) ?: 30
                
                // Nếu đạt mục tiêu, cập nhật streak
                if (todayStudyTime >= targetStudyTime) {
                    val userRef = database.reference.child("app_data").child("users").child(currentUser.uid).child("progress")
                    
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
                    .child("app_data")
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
                    // KHÔNG reset counter ngay lập tức - để hiển thị trong completion dialog
                }
            } catch (e: Exception) {
                // Xử lý lỗi nếu cần
            }
        }
    }
    
    // Reset counter sau khi đóng completion dialog
    fun resetWordsLearnedCounter() {
        _wordsLearned.value = 0
    }
    
    // Lấy số từ vựng đã học trong một chủ đề cụ thể
    suspend fun getTopicProgress(categoryName: String, level: String): Pair<Int, Int> {
        return try {
            val currentUser = auth.currentUser ?: return Pair(0, 0)
            
            // Lấy tất cả từ vựng và filter theo category và level
            val vocabularySnapshot = database.reference
                .child("app_data")
                .child("vocabulary")
                .get()
                .await()
            
            var totalWords = 0
            var learnedWords = 0
            
            vocabularySnapshot.children.forEach { wordSnapshot ->
                val wordId = wordSnapshot.key ?: return@forEach
                val wordLevel = wordSnapshot.child("level").getValue(String::class.java)
                val wordCategories = wordSnapshot.child("categories").children.mapNotNull { it.getValue(String::class.java) }
                
                // Chỉ đếm nếu level và category khớp
                if (wordLevel == level && wordCategories.contains(categoryName)) {
                    totalWords++
                    
                    // Kiểm tra xem từ này đã được học chưa
                    val isLearned = database.reference
                        .child("app_data")
                        .child("users")
                        .child(currentUser.uid)
                        .child("learning")
                        .child("vocabulary")
                        .child(wordId)
                        .get()
                        .await()
                        .getValue(Boolean::class.java) == true
                    
                    if (isLearned) learnedWords++
                }
            }
            
            Pair(learnedWords, totalWords)
        } catch (e: Exception) {
            _error.value = "Không thể tải tiến độ chủ đề: ${e.message}"
            Pair(0, 0)
        }
    }

    // Hàm để reset lỗi
    fun resetError() {
        _error.value = null
    }
} 