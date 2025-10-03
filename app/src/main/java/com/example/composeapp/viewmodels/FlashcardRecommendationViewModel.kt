package com.example.composeapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.models.Flashcard
import com.example.composeapp.models.FlashcardCategory
import com.example.composeapp.models.MasteryLevel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit
import kotlin.math.exp

class FlashcardRecommendationViewModel : ViewModel() {
    private val database = FirebaseDatabase.getInstance()
    private val auth = FirebaseAuth.getInstance()
    
    // StateFlow cho flashcard được đề xuất
    private val _recommendedFlashcards = MutableStateFlow<List<Flashcard>>(emptyList())
    val recommendedFlashcards: StateFlow<List<Flashcard>> = _recommendedFlashcards
    
    // StateFlow cho flashcard đang được hiển thị
    private val _currentRecommendedFlashcard = MutableStateFlow<Flashcard?>(null)
    val currentRecommendedFlashcard: StateFlow<Flashcard?> = _currentRecommendedFlashcard
    
    // StateFlow cho trạng thái loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    // StateFlow cho lỗi
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // Số lượng flashcard đề xuất tối đa
    private val MAX_RECOMMENDATIONS = 10
    
    init {
        loadRecommendedFlashcards()
    }
    
    /**
     * Tải danh sách flashcard được đề xuất cho người dùng
     */
    fun loadRecommendedFlashcards() {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _error.value = null
                
                val currentUser = auth.currentUser
                if (currentUser == null) {
                    _error.value = "Người dùng chưa đăng nhập"
                    _isLoading.value = false
                    return@launch
                }
                
                // 1. Tải tất cả flashcard từ Firebase (app_data/vocabulary)
                val allFlashcards = mutableListOf<Flashcard>()
                val vocabularyRef = database.getReference("app_data/vocabulary")
                val vocabSnapshot = vocabularyRef.get().await()

                for (cardSnapshot in vocabSnapshot.children) {
                    val cardId = cardSnapshot.key ?: continue
                    val japanese = cardSnapshot.child("japanese").getValue(String::class.java) ?: ""
                    val reading = cardSnapshot.child("reading").getValue(String::class.java) ?: ""
                    val vietnamese = cardSnapshot.child("vietnamese").getValue(String::class.java) ?: ""
                    val categories = cardSnapshot.child("categories").children.mapNotNull { it.getValue(String::class.java) }
                    val level = cardSnapshot.child("level").getValue(String::class.java) ?: ""

                    // Lấy 1 ví dụ đầu tiên nếu có
                    val exampleSentences = cardSnapshot.child("exampleSentences").children.toList()
                    val firstExample = exampleSentences.firstOrNull()
                    val exampleObj = if (firstExample != null) {
                        com.example.composeapp.models.Example(
                            japanese = firstExample.child("japanese").getValue(String::class.java) ?: "",
                            vietnamese = firstExample.child("vietnamese").getValue(String::class.java) ?: ""
                        )
                    } else {
                        com.example.composeapp.models.Example(japanese = "", vietnamese = "")
                    }

                    // Suy ra danh mục chính từ mảng categories (nếu có)
                    val categoryEnum = categories.firstOrNull()?.let {
                        try { FlashcardCategory.valueOf(it) } catch (_: Exception) { null }
                    } ?: FlashcardCategory.MISC

                    // 2. Tải dữ liệu học tập của người dùng cho từng flashcard
                    val userLearningRef = database.getReference("app_data/users/${currentUser.uid}/learning/flashcardProgress/$cardId")
                    val userLearningSnapshot = userLearningRef.get().await()

                    val masteryLevel = userLearningSnapshot.child("masteryLevel")
                        .getValue(String::class.java)?.let {
                            try { MasteryLevel.valueOf(it) } catch (_: Exception) { MasteryLevel.NEW }
                        } ?: MasteryLevel.NEW

                    val lastReviewTimestamp = userLearningSnapshot.child("lastReviewDate").getValue(Long::class.java)
                    val lastReviewDate = if (lastReviewTimestamp != null) Date(lastReviewTimestamp) else null

                    val nextReviewTimestamp = userLearningSnapshot.child("nextReviewDate").getValue(Long::class.java)
                    val nextReviewDate = if (nextReviewTimestamp != null) Date(nextReviewTimestamp) else null

                    val difficulty = userLearningSnapshot.child("difficulty").getValue(Float::class.java) ?: 0.3f
                    val viewCount = userLearningSnapshot.child("viewCount").getValue(Int::class.java) ?: 0
                    val correctCount = userLearningSnapshot.child("correctCount").getValue(Int::class.java) ?: 0
                    val incorrectCount = userLearningSnapshot.child("incorrectCount").getValue(Int::class.java) ?: 0

                    val flashcard = Flashcard(
                        id = cardId,
                        japaneseWord = japanese,
                        vietnameseMeaning = vietnamese,
                        reading = reading,
                        category = categoryEnum,
                        examples = listOf(exampleObj),
                        masteryLevel = masteryLevel,
                        lastReviewDate = lastReviewDate,
                        nextReviewDate = nextReviewDate,
                        difficulty = difficulty,
                        viewCount = viewCount,
                        correctCount = correctCount,
                        incorrectCount = incorrectCount
                    )

                    allFlashcards.add(flashcard)
                }
                
                // 3. Áp dụng thuật toán đề xuất
                val recommendedFlashcards = recommendFlashcards(allFlashcards)
                
                _recommendedFlashcards.value = recommendedFlashcards
                _currentRecommendedFlashcard.value = recommendedFlashcards.firstOrNull()
                
            } catch (e: Exception) {
                _error.value = "Không thể tải flashcard đề xuất: ${e.message}"
            } finally {
                _isLoading.value = false
            }
        }
    }
    
    /**
     * Thuật toán đề xuất flashcard dựa trên nhiều yếu tố
     */
    private fun recommendFlashcards(allFlashcards: List<Flashcard>): List<Flashcard> {
        // Nếu không có flashcard nào, trả về danh sách rỗng
        if (allFlashcards.isEmpty()) return emptyList()
        
        val now = Date()
        val calendar = Calendar.getInstance()
        
        // Tính điểm ưu tiên cho mỗi flashcard
        val scoredFlashcards = allFlashcards.map { flashcard ->
            // 1. Điểm dựa trên mức độ thành thạo
            val masteryScore = when (flashcard.masteryLevel) {
                MasteryLevel.NEW -> 10.0      // Ưu tiên cao nhất cho từ mới
                MasteryLevel.LEARNING -> 7.0  // Ưu tiên cao cho từ đang học
                MasteryLevel.REVIEWING -> 5.0 // Ưu tiên trung bình cho từ đang ôn tập
                MasteryLevel.MASTERED -> 1.0  // Ưu tiên thấp cho từ đã thành thạo
            }
            
            // 2. Điểm dựa trên thời gian từ lần học cuối cùng (spaced repetition)
            val lastReviewScore = if (flashcard.lastReviewDate != null) {
                val daysSinceLastReview = TimeUnit.MILLISECONDS.toDays(
                    now.time - flashcard.lastReviewDate.time
                )
                
                // Công thức spaced repetition: điểm tăng theo thời gian, nhưng có giới hạn
                val optimalInterval = when (flashcard.masteryLevel) {
                    MasteryLevel.NEW -> 1       // Nên ôn tập sau 1 ngày
                    MasteryLevel.LEARNING -> 3  // Nên ôn tập sau 3 ngày
                    MasteryLevel.REVIEWING -> 7 // Nên ôn tập sau 7 ngày
                    MasteryLevel.MASTERED -> 14 // Nên ôn tập sau 14 ngày
                }
                
                // Nếu đã đến hoặc quá thời gian ôn tập tối ưu, điểm cao hơn
                if (daysSinceLastReview >= optimalInterval) {
                    // Điểm tăng theo số ngày quá hạn, nhưng có giới hạn
                    val overdueFactor = minOf((daysSinceLastReview - optimalInterval) / 5.0 + 1.0, 3.0)
                    8.0 * overdueFactor
                } else {
                    // Chưa đến thời gian ôn tập tối ưu
                    val progress = daysSinceLastReview.toDouble() / optimalInterval
                    8.0 * progress
                }
            } else {
                // Chưa từng học, điểm cao nhất
                10.0
            }
            
            // 3. Điểm dựa trên độ khó
            // Từ khó (difficulty cao) được ưu tiên hơn
            val difficultyScore = flashcard.difficulty * 5.0
            
            // 4. Điểm dựa trên tỷ lệ trả lời đúng/sai
            val accuracyScore = if (flashcard.correctCount + flashcard.incorrectCount > 0) {
                val correctRatio = flashcard.correctCount.toDouble() / 
                                  (flashcard.correctCount + flashcard.incorrectCount)
                // Từ có tỷ lệ trả lời đúng thấp được ưu tiên hơn
                5.0 * (1.0 - correctRatio)
            } else {
                // Chưa có dữ liệu trả lời, điểm trung bình
                2.5
            }
            
            // 5. Điểm dựa trên nextReviewDate
            val nextReviewScore = if (flashcard.nextReviewDate != null) {
                if (now.after(flashcard.nextReviewDate)) {
                    // Đã quá hạn ôn tập, điểm cao nhất
                    val daysOverdue = TimeUnit.MILLISECONDS.toDays(
                        now.time - flashcard.nextReviewDate.time
                    )
                    // Điểm tăng theo số ngày quá hạn, nhưng có giới hạn
                    minOf(10.0, 5.0 + daysOverdue * 0.5)
                } else {
                    // Chưa đến hạn ôn tập
                    val daysUntilReview = TimeUnit.MILLISECONDS.toDays(
                        flashcard.nextReviewDate.time - now.time
                    )
                    // Điểm giảm theo số ngày còn lại, nhưng có giới hạn dưới
                    maxOf(0.0, 5.0 - daysUntilReview * 0.5)
                }
            } else {
                // Chưa có nextReviewDate, điểm cao
                8.0
            }
            
            // Tổng hợp điểm với trọng số
            val totalScore = (masteryScore * 0.3) + 
                            (lastReviewScore * 0.25) + 
                            (difficultyScore * 0.2) + 
                            (accuracyScore * 0.15) + 
                            (nextReviewScore * 0.1)
            
            Pair(flashcard, totalScore)
        }
        
        // Sắp xếp theo điểm giảm dần và lấy MAX_RECOMMENDATIONS flashcard đầu tiên
        return scoredFlashcards
            .sortedByDescending { it.second }
            .take(MAX_RECOMMENDATIONS)
            .map { it.first }
    }
    
    /**
     * Đánh dấu flashcard hiện tại đã học và chuyển sang flashcard tiếp theo
     */
    fun markCurrentFlashcardAsLearned() {
        val currentFlashcard = _currentRecommendedFlashcard.value ?: return
        
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                
                // Cập nhật thông tin học tập lên Firebase
                val userLearningRef = database.getReference(
                    "app_data/users/${'$'}{currentUser.uid}/learning/flashcardProgress/${'$'}{currentFlashcard.id}"
                )
                
                // Tính toán mức độ thành thạo mới
                val newMasteryLevel = when (currentFlashcard.masteryLevel) {
                    MasteryLevel.NEW -> MasteryLevel.LEARNING
                    MasteryLevel.LEARNING -> MasteryLevel.REVIEWING
                    MasteryLevel.REVIEWING -> MasteryLevel.MASTERED
                    MasteryLevel.MASTERED -> MasteryLevel.MASTERED
                }
                
                // Tính toán ngày ôn tập tiếp theo dựa trên mức độ thành thạo mới
                val calendar = Calendar.getInstance()
                calendar.time = Date()
                
                when (newMasteryLevel) {
                    MasteryLevel.LEARNING -> calendar.add(Calendar.DAY_OF_MONTH, 1) // 1 ngày sau
                    MasteryLevel.REVIEWING -> calendar.add(Calendar.DAY_OF_MONTH, 3) // 3 ngày sau
                    MasteryLevel.MASTERED -> calendar.add(Calendar.DAY_OF_MONTH, 7) // 7 ngày sau
                    else -> calendar.add(Calendar.DAY_OF_MONTH, 1) // Mặc định 1 ngày sau
                }
                
                val nextReviewDate = calendar.time
                
                // Cập nhật dữ liệu lên Firebase
                val updates = mapOf(
                    "masteryLevel" to newMasteryLevel.name,
                    "lastReviewDate" to Date().time,
                    "nextReviewDate" to nextReviewDate.time,
                    "viewCount" to (currentFlashcard.viewCount + 1),
                    "correctCount" to (currentFlashcard.correctCount + 1)
                )
                
                userLearningRef.updateChildren(updates).await()
                
                // Chuyển sang flashcard tiếp theo
                moveToNextRecommendedFlashcard()
                
            } catch (e: Exception) {
                _error.value = "Không thể cập nhật thông tin học tập: ${e.message}"
            }
        }
    }
    
    /**
     * Chuyển sang flashcard đề xuất tiếp theo
     */
    fun moveToNextRecommendedFlashcard() {
        val currentFlashcards = _recommendedFlashcards.value
        val currentIndex = currentFlashcards.indexOf(_currentRecommendedFlashcard.value)
        
        if (currentIndex < currentFlashcards.size - 1) {
            _currentRecommendedFlashcard.value = currentFlashcards[currentIndex + 1]
        } else {
            // Đã hết danh sách đề xuất, tải lại danh sách mới
            loadRecommendedFlashcards()
        }
    }
    
    /**
     * Chuyển về flashcard đề xuất trước đó
     */
    fun moveToPreviousRecommendedFlashcard() {
        val currentFlashcards = _recommendedFlashcards.value
        val currentIndex = currentFlashcards.indexOf(_currentRecommendedFlashcard.value)
        
        if (currentIndex > 0) {
            _currentRecommendedFlashcard.value = currentFlashcards[currentIndex - 1]
        }
    }
    
    /**
     * Đánh dấu flashcard hiện tại là khó
     */
    fun markCurrentFlashcardAsDifficult() {
        val currentFlashcard = _currentRecommendedFlashcard.value ?: return
        
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                
                // Cập nhật độ khó lên Firebase
                val userLearningRef = database.getReference(
                    "app_data/users/${'$'}{currentUser.uid}/learning/flashcardProgress/${'$'}{currentFlashcard.id}"
                )
                
                // Tăng độ khó lên nhưng không vượt quá 1.0
                val newDifficulty = minOf(currentFlashcard.difficulty + 0.1f, 1.0f)
                
                userLearningRef.child("difficulty").setValue(newDifficulty).await()
                
                // Chuyển sang flashcard tiếp theo
                moveToNextRecommendedFlashcard()
                
            } catch (e: Exception) {
                _error.value = "Không thể cập nhật độ khó: ${e.message}"
            }
        }
    }
    
    /**
     * Đánh dấu flashcard hiện tại là dễ
     */
    fun markCurrentFlashcardAsEasy() {
        val currentFlashcard = _currentRecommendedFlashcard.value ?: return
        
        viewModelScope.launch {
            try {
                val currentUser = auth.currentUser ?: return@launch
                
                // Cập nhật độ khó lên Firebase
                val userLearningRef = database.getReference(
                    "app_data/users/${'$'}{currentUser.uid}/learning/flashcardProgress/${'$'}{currentFlashcard.id}"
                )
                
                // Giảm độ khó xuống nhưng không nhỏ hơn 0.0
                val newDifficulty = maxOf(currentFlashcard.difficulty - 0.1f, 0.0f)
                
                userLearningRef.child("difficulty").setValue(newDifficulty).await()
                
                // Chuyển sang flashcard tiếp theo
                moveToNextRecommendedFlashcard()
                
            } catch (e: Exception) {
                _error.value = "Không thể cập nhật độ khó: ${e.message}"
            }
        }
    }
    
    /**
     * Reset lỗi
     */
    fun resetError() {
        _error.value = null
    }
} 