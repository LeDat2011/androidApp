package com.example.composeapp.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.models.AlphabetType
import com.example.composeapp.models.JapaneseAlphabet
import com.example.composeapp.models.JapaneseCharacter
// import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
// import kotlinx.coroutines.tasks.await

class JapaneseAlphabetViewModel : ViewModel() {
    
    // State cho loại bảng chữ cái đang hiển thị
    private val _currentAlphabetType = mutableStateOf(AlphabetType.HIRAGANA)
    val currentAlphabetType: State<AlphabetType> = _currentAlphabetType
    
    // State cho ký tự được chọn
    private val _selectedCharacter = mutableStateOf<JapaneseCharacter?>(null)
    val selectedCharacter: State<JapaneseCharacter?> = _selectedCharacter
    
    // State cho URL hình ảnh
    private val _characterImageUrl = MutableStateFlow<String?>(null)
    val characterImageUrl: StateFlow<String?> = _characterImageUrl
    
    // State cho trạng thái tải
    private val _isLoading = mutableStateOf(false)
    val isLoading: State<Boolean> = _isLoading
    
    // State cho thông báo lỗi
    private val _errorMessage = mutableStateOf<String?>(null)
    val errorMessage: State<String?> = _errorMessage
    
    // Đổi loại bảng chữ cái
    fun switchAlphabetType(type: AlphabetType) {
        _currentAlphabetType.value = type
        // Xóa ký tự đang chọn khi chuyển đổi
        _selectedCharacter.value = null
        _characterImageUrl.value = null
    }
    
    // Lấy danh sách ký tự theo hàng
    fun getCharacterRows(): Map<String, List<JapaneseCharacter>> {
        return JapaneseAlphabet.getRows(_currentAlphabetType.value)
    }
    
    // Lấy tên của bảng chữ cái hiện tại
    fun getCurrentAlphabetName(): String {
        return when (_currentAlphabetType.value) {
            AlphabetType.HIRAGANA -> "Hiragana (ひらがな)"
            AlphabetType.KATAKANA -> "Katakana (カタカナ)"
        }
    }
    
    // Chọn một ký tự
    fun selectCharacter(character: JapaneseCharacter) {
        _selectedCharacter.value = character
        // loadCharacterImage(character)
        _characterImageUrl.value = null // Tạm thời không tải hình ảnh
    }
    
    // Đóng chi tiết ký tự
    fun closeCharacterDetail() {
        _selectedCharacter.value = null
        _characterImageUrl.value = null
    }
    
    // Tải hình ảnh từ Firebase Storage (sẽ triển khai sau)
    private fun loadCharacterImage(character: JapaneseCharacter) {
        viewModelScope.launch {
            try {
                _isLoading.value = true
                _errorMessage.value = null
                
                // TODO: Triển khai sau khi đã cấu hình Firebase Storage
                /* 
                // Path ví dụ: japanese_alphabet/{hiragana|katakana}/{character}.jpg
                val alphabetFolder = when (_currentAlphabetType.value) {
                    AlphabetType.HIRAGANA -> "hiragana"
                    AlphabetType.KATAKANA -> "katakana"
                }
                
                val storageRef = FirebaseStorage.getInstance().reference
                    .child("japanese_alphabet")
                    .child(alphabetFolder)
                    .child("${character.romanization}.jpg")
                
                try {
                    // Lấy URL tải xuống
                    val url = storageRef.downloadUrl.await().toString()
                    _characterImageUrl.value = url
                } catch (e: Exception) {
                    // Ẩn lỗi này và sử dụng null cho URL nếu không tìm thấy
                    _characterImageUrl.value = null
                }
                */
                
                // Giả lập tải hình ảnh
                kotlinx.coroutines.delay(500)
                _characterImageUrl.value = null
                
                _isLoading.value = false
            } catch (e: Exception) {
                _isLoading.value = false
                _errorMessage.value = "Không thể tải hình ảnh: ${e.message}"
                _characterImageUrl.value = null
            }
        }
    }
    
    // Tìm kiếm ký tự theo romanization
    fun searchCharacters(query: String): List<JapaneseCharacter> {
        if (query.isBlank()) return emptyList()
        
        return JapaneseAlphabet.findByRomanization(
            romanization = query.trim().lowercase(),
            type = _currentAlphabetType.value
        )
    }
} 