package com.example.composeapp.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.models.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class WritingViewModel : ViewModel() {
    
    // State cho danh sách ký tự
    private val _characters = MutableStateFlow<List<WritingCharacter>>(emptyList())
    val characters: StateFlow<List<WritingCharacter>> = _characters.asStateFlow()
    
    // State cho ký tự hiện tại
    private val _currentCharacter = MutableStateFlow<WritingCharacter?>(null)
    val currentCharacter: StateFlow<WritingCharacter?> = _currentCharacter.asStateFlow()
    
    // State cho nét hiện tại
    private val _currentStrokeIndex = MutableStateFlow(0)
    val currentStrokeIndex: StateFlow<Int> = _currentStrokeIndex.asStateFlow()
    
    // State cho chế độ luyện viết
    private val _writingMode = MutableStateFlow(WritingMode.STROKE_BY_STROKE)
    val writingMode: StateFlow<WritingMode> = _writingMode.asStateFlow()
    
    // State cho tiến độ
    private val _progress = MutableStateFlow<WritingProgress?>(null)
    val progress: StateFlow<WritingProgress?> = _progress.asStateFlow()
    
    // State cho cài đặt
    private val _settings = MutableStateFlow(WritingSettings())
    val settings: StateFlow<WritingSettings> = _settings.asStateFlow()
    
    // State cho loading
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // State cho kết quả
    private val _writingResult = MutableStateFlow<WritingResult?>(null)
    val writingResult: StateFlow<WritingResult?> = _writingResult.asStateFlow()
    
    init {
        loadDefaultCharacters()
    }
    
    /**
     * Tải danh sách ký tự mặc định
     */
    private fun loadDefaultCharacters() {
        viewModelScope.launch {
            _isLoading.value = true
            
            val defaultCharacters = listOf(
                // Hiragana cơ bản
                WritingCharacter(
                    id = "hiragana_a",
                    character = "あ",
                    type = WritingType.HIRAGANA,
                    level = "N5",
                    strokes = listOf(
                        Stroke(
                            id = "stroke_1",
                            order = 1,
                            path = "M 20,80 Q 30,20 50,30 Q 70,40 80,80",
                            startPoint = Point(20f, 80f),
                            endPoint = Point(80f, 80f),
                            direction = StrokeDirection.CURVE,
                            type = StrokeType.CURVE
                        ),
                        Stroke(
                            id = "stroke_2",
                            order = 2,
                            path = "M 50,30 L 50,100",
                            startPoint = Point(50f, 30f),
                            endPoint = Point(50f, 100f),
                            direction = StrokeDirection.DOWN,
                            type = StrokeType.VERTICAL
                        ),
                        Stroke(
                            id = "stroke_3",
                            order = 3,
                            path = "M 50,60 L 80,60",
                            startPoint = Point(50f, 60f),
                            endPoint = Point(80f, 60f),
                            direction = StrokeDirection.RIGHT,
                            type = StrokeType.HORIZONTAL
                        )
                    ),
                    reading = "a",
                    examples = listOf("あめ (ame) - mưa", "あさ (asa) - buổi sáng")
                ),
                WritingCharacter(
                    id = "hiragana_i",
                    character = "い",
                    type = WritingType.HIRAGANA,
                    level = "N5",
                    strokes = listOf(
                        Stroke(
                            id = "stroke_1",
                            order = 1,
                            path = "M 30,20 L 30,80",
                            startPoint = Point(30f, 20f),
                            endPoint = Point(30f, 80f),
                            direction = StrokeDirection.DOWN,
                            type = StrokeType.VERTICAL
                        ),
                        Stroke(
                            id = "stroke_2",
                            order = 2,
                            path = "M 70,30 L 70,90",
                            startPoint = Point(70f, 30f),
                            endPoint = Point(70f, 90f),
                            direction = StrokeDirection.DOWN,
                            type = StrokeType.VERTICAL
                        )
                    ),
                    reading = "i",
                    examples = listOf("いぬ (inu) - con chó", "いえ (ie) - nhà")
                ),
                WritingCharacter(
                    id = "hiragana_u",
                    character = "う",
                    type = WritingType.HIRAGANA,
                    level = "N5",
                    strokes = listOf(
                        Stroke(
                            id = "stroke_1",
                            order = 1,
                            path = "M 30,30 Q 50,20 70,30",
                            startPoint = Point(30f, 30f),
                            endPoint = Point(70f, 30f),
                            direction = StrokeDirection.RIGHT,
                            type = StrokeType.CURVE
                        ),
                        Stroke(
                            id = "stroke_2",
                            order = 2,
                            path = "M 30,30 Q 30,60 30,90",
                            startPoint = Point(30f, 30f),
                            endPoint = Point(30f, 90f),
                            direction = StrokeDirection.DOWN,
                            type = StrokeType.VERTICAL
                        )
                    ),
                    reading = "u",
                    examples = listOf("うま (uma) - con ngựa", "うみ (umi) - biển")
                ),
                WritingCharacter(
                    id = "hiragana_e",
                    character = "え",
                    type = WritingType.HIRAGANA,
                    level = "N5",
                    strokes = listOf(
                        Stroke(
                            id = "stroke_1",
                            order = 1,
                            path = "M 30,30 L 70,30",
                            startPoint = Point(30f, 30f),
                            endPoint = Point(70f, 30f),
                            direction = StrokeDirection.RIGHT,
                            type = StrokeType.HORIZONTAL
                        ),
                        Stroke(
                            id = "stroke_2",
                            order = 2,
                            path = "M 50,30 L 50,90",
                            startPoint = Point(50f, 30f),
                            endPoint = Point(50f, 90f),
                            direction = StrokeDirection.DOWN,
                            type = StrokeType.VERTICAL
                        ),
                        Stroke(
                            id = "stroke_3",
                            order = 3,
                            path = "M 30,60 Q 50,70 70,60",
                            startPoint = Point(30f, 60f),
                            endPoint = Point(70f, 60f),
                            direction = StrokeDirection.RIGHT,
                            type = StrokeType.CURVE
                        )
                    ),
                    reading = "e",
                    examples = listOf("えき (eki) - ga tàu", "えんぴつ (enpitsu) - bút chì")
                ),
                WritingCharacter(
                    id = "hiragana_o",
                    character = "お",
                    type = WritingType.HIRAGANA,
                    level = "N5",
                    strokes = listOf(
                        Stroke(
                            id = "stroke_1",
                            order = 1,
                            path = "M 50,20 Q 30,30 30,50 Q 30,70 50,80 Q 70,90 90,80",
                            startPoint = Point(50f, 20f),
                            endPoint = Point(90f, 80f),
                            direction = StrokeDirection.CURVE,
                            type = StrokeType.CURVE
                        ),
                        Stroke(
                            id = "stroke_2",
                            order = 2,
                            path = "M 50,20 L 50,100",
                            startPoint = Point(50f, 20f),
                            endPoint = Point(50f, 100f),
                            direction = StrokeDirection.DOWN,
                            type = StrokeType.VERTICAL
                        ),
                        Stroke(
                            id = "stroke_3",
                            order = 3,
                            path = "M 50,50 L 80,50",
                            startPoint = Point(50f, 50f),
                            endPoint = Point(80f, 50f),
                            direction = StrokeDirection.RIGHT,
                            type = StrokeType.HORIZONTAL
                        )
                    ),
                    reading = "o",
                    examples = listOf("おかね (okane) - tiền", "おちゃ (ocha) - trà")
                )
            )
            
            _characters.value = defaultCharacters
            _isLoading.value = false
        }
    }
    
    /**
     * Chọn ký tự để luyện viết
     */
    fun selectCharacter(character: WritingCharacter) {
        _currentCharacter.value = character
        _currentStrokeIndex.value = 0
        _progress.value = WritingProgress(
            characterId = character.id,
            completedStrokes = emptyList()
        )
    }
    
    /**
     * Chuyển sang nét tiếp theo
     */
    fun nextStroke() {
        val current = _currentCharacter.value
        if (current != null && _currentStrokeIndex.value < current.strokes.size - 1) {
            _currentStrokeIndex.value++
        }
    }
    
    /**
     * Quay lại nét trước
     */
    fun previousStroke() {
        if (_currentStrokeIndex.value > 0) {
            _currentStrokeIndex.value--
        }
    }
    
    /**
     * Hoàn thành nét hiện tại
     */
    fun completeStroke(strokeIndex: Int) {
        val currentProgress = _progress.value ?: return
        val completedStrokes = currentProgress.completedStrokes.toMutableList()
        
        if (!completedStrokes.contains(strokeIndex)) {
            completedStrokes.add(strokeIndex)
            _progress.value = currentProgress.copy(
                completedStrokes = completedStrokes,
                successfulAttempts = currentProgress.successfulAttempts + 1
            )
        }
        
        // Tự động chuyển sang nét tiếp theo nếu bật auto advance
        if (_settings.value.autoAdvance && strokeIndex < (_currentCharacter.value?.strokes?.size ?: 0) - 1) {
            nextStroke()
        }
    }
    
    /**
     * Thay đổi chế độ luyện viết
     */
    fun setWritingMode(mode: WritingMode) {
        _writingMode.value = mode
        _currentStrokeIndex.value = 0
    }
    
    /**
     * Cập nhật cài đặt
     */
    fun updateSettings(newSettings: WritingSettings) {
        _settings.value = newSettings
    }
    
    /**
     * Hoàn thành luyện viết ký tự
     */
    fun completeCharacterWriting(timeSpentSeconds: Int, accuracy: Float) {
        val current = _currentCharacter.value ?: return
        val currentProgress = _progress.value ?: return
        
        val result = WritingResult(
            characterId = current.id,
            completedAt = System.currentTimeMillis(),
            timeSpentSeconds = timeSpentSeconds,
            strokesCompleted = currentProgress.completedStrokes.size,
            totalStrokes = current.strokes.size,
            accuracy = accuracy,
            mode = _writingMode.value
        )
        
        _writingResult.value = result
        
        // Cập nhật tiến độ
        val masteryLevel = when {
            accuracy >= 0.9f -> WritingMasteryLevel.MASTERED
            accuracy >= 0.7f -> WritingMasteryLevel.PRACTICING
            accuracy >= 0.5f -> WritingMasteryLevel.LEARNING
            else -> WritingMasteryLevel.NEW
        }
        
        _progress.value = currentProgress.copy(
            masteryLevel = masteryLevel,
            lastPracticeDate = System.currentTimeMillis(),
            totalAttempts = currentProgress.totalAttempts + 1
        )
    }
    
    /**
     * Lọc ký tự theo loại
     */
    fun filterCharactersByType(type: WritingType) {
        // Implementation để lọc ký tự theo loại
        // Có thể mở rộng để lọc từ Firebase
    }
    
    /**
     * Lọc ký tự theo cấp độ
     */
    fun filterCharactersByLevel(level: String) {
        // Implementation để lọc ký tự theo cấp độ
        // Có thể mở rộng để lọc từ Firebase
    }
    
    /**
     * Reset tiến độ
     */
    fun resetProgress() {
        _currentStrokeIndex.value = 0
        _progress.value = null
        _writingResult.value = null
    }
    
    /**
     * Quay lại màn hình chọn ký tự
     */
    fun backToCharacterSelection() {
        _currentCharacter.value = null
        _currentStrokeIndex.value = 0
        _progress.value = null
        _writingResult.value = null
    }
}
