package com.example.composeapp.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.composeapp.models.AlphabetType
import com.example.composeapp.models.JapaneseAlphabet
import com.example.composeapp.models.JapaneseCharacter

class JapaneseAlphabetViewModel : ViewModel() {
    
    // State cho loại bảng chữ cái đang hiển thị
    private val _currentAlphabetType = mutableStateOf(AlphabetType.HIRAGANA)
    val currentAlphabetType: State<AlphabetType> = _currentAlphabetType
    
    // State cho ký tự được chọn
    private val _selectedCharacter = mutableStateOf<JapaneseCharacter?>(null)
    val selectedCharacter: State<JapaneseCharacter?> = _selectedCharacter
    
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
    }
    
    // Lấy danh sách ký tự theo hàng
    fun getCharacterRows(): Map<String, List<JapaneseCharacter>> {
        return when (_currentAlphabetType.value) {
            AlphabetType.HIRAGANA -> JapaneseAlphabet.getRows(AlphabetType.HIRAGANA)
            AlphabetType.KATAKANA -> JapaneseAlphabet.getRows(AlphabetType.KATAKANA)
            AlphabetType.KANJI -> getKanjiByLevel()
        }
    }
    
    // Lấy tên của bảng chữ cái hiện tại
    fun getCurrentAlphabetName(): String {
        return when (_currentAlphabetType.value) {
            AlphabetType.HIRAGANA -> "Hiragana (ひらがな)"
            AlphabetType.KATAKANA -> "Katakana (カタカナ)"
            AlphabetType.KANJI -> "Kanji (漢字)"
        }
    }
    
    // Chọn một ký tự
    fun selectCharacter(character: JapaneseCharacter) {
        _selectedCharacter.value = character
    }
    
    // Đóng chi tiết ký tự
    fun closeCharacterDetail() {
        _selectedCharacter.value = null
    }
    

    
    // Tìm kiếm ký tự theo romanization
    fun searchCharacters(query: String): List<JapaneseCharacter> {
        if (query.isBlank()) return emptyList()
        
        return JapaneseAlphabet.findByRomanization(
            romanization = query.trim().lowercase(),
            type = _currentAlphabetType.value
        )
    }

    private fun getKanjiByLevel(): Map<String, List<JapaneseCharacter>> {
        val allKanji = JapaneseAlphabet.getCharacters(AlphabetType.KANJI)
        val kanjiByTheme = mutableMapOf<String, MutableList<JapaneseCharacter>>()

        // Nhóm Kanji theo chủ đề
        allKanji.forEach { kanji ->
            when {
                // N5 Kanji
                kanji.character in listOf("日", "月", "年", "時") -> 
                    kanjiByTheme.getOrPut("Thời gian cơ bản") { mutableListOf() }.add(kanji)
                
                kanji.character in listOf("人", "父", "母", "子") -> 
                    kanjiByTheme.getOrPut("Con người và gia đình") { mutableListOf() }.add(kanji)
                
                kanji.character in listOf("一", "二", "三", "四", "五") -> 
                    kanjiByTheme.getOrPut("Số đếm") { mutableListOf() }.add(kanji)
                
                kanji.character in listOf("本", "学", "校", "先") -> 
                    kanjiByTheme.getOrPut("Học tập") { mutableListOf() }.add(kanji)
                
                kanji.character in listOf("会", "社", "仕", "事") -> 
                    kanjiByTheme.getOrPut("Công việc và xã hội") { mutableListOf() }.add(kanji)
                
                kanji.character in listOf("国", "家", "駅", "店") -> 
                    kanjiByTheme.getOrPut("Địa điểm") { mutableListOf() }.add(kanji)
                
                kanji.character in listOf("行", "来", "見", "聞") -> 
                    kanjiByTheme.getOrPut("Hoạt động") { mutableListOf() }.add(kanji)
                
                kanji.character in listOf("新", "古", "多", "少") -> 
                    kanjiByTheme.getOrPut("Tính chất") { mutableListOf() }.add(kanji)

                // N3 Kanji và các chủ đề mới
                kanji.character in listOf("愛", "楽", "苦", "悲") ->
                    kanjiByTheme.getOrPut("Cảm xúc và tính cách") { mutableListOf() }.add(kanji)

                kanji.character in listOf("山", "川", "海", "空") ->
                    kanjiByTheme.getOrPut("Thiên nhiên") { mutableListOf() }.add(kanji)

                kanji.character in listOf("雨", "雪", "風", "雲") ->
                    kanjiByTheme.getOrPut("Thời tiết") { mutableListOf() }.add(kanji)

                kanji.character in listOf("食", "飲", "寝", "働") ->
                    kanjiByTheme.getOrPut("Hoạt động hàng ngày") { mutableListOf() }.add(kanji)

                kanji.character in listOf("車", "道", "橋", "駐") ->
                    kanjiByTheme.getOrPut("Giao thông") { mutableListOf() }.add(kanji)

                kanji.character in listOf("朝", "昼", "夜", "週") ->
                    kanjiByTheme.getOrPut("Thời gian nâng cao") { mutableListOf() }.add(kanji)
            }
        }

        return kanjiByTheme
    }
} 