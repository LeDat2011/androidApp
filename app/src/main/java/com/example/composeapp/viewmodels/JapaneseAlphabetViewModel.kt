package com.example.composeapp.viewmodels

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.composeapp.models.AlphabetType
import com.example.composeapp.models.JapaneseCharacter
import com.example.composeapp.repository.JapaneseAlphabetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class JapaneseAlphabetViewModel : ViewModel() {
    private val repository = JapaneseAlphabetRepository()
    
    // State cho loại bảng chữ cái đang hiển thị
    private val _currentAlphabetType = mutableStateOf(AlphabetType.HIRAGANA)
    val currentAlphabetType: State<AlphabetType> = _currentAlphabetType
    
    // State cho ký tự được chọn
    private val _selectedCharacter = mutableStateOf<JapaneseCharacter?>(null)
    val selectedCharacter: State<JapaneseCharacter?> = _selectedCharacter
    
    // State cho danh sách ký tự hiện tại
    private val _characters = MutableStateFlow<List<JapaneseCharacter>>(emptyList())
    val characters: StateFlow<List<JapaneseCharacter>> = _characters
    
    // State cho trạng thái tải
    val isLoading: StateFlow<Boolean> = repository.isLoading
    
    // State cho thông báo lỗi
    val errorMessage: StateFlow<String?> = repository.error
    
    init {
        // Tải dữ liệu mặc định cho Hiragana
        loadCharacters(AlphabetType.HIRAGANA)
    }
    
    // Đổi loại bảng chữ cái
    fun switchAlphabetType(type: AlphabetType) {
        _currentAlphabetType.value = type
        // Xóa ký tự đang chọn khi chuyển đổi
        _selectedCharacter.value = null
        // Tải dữ liệu mới
        loadCharacters(type)
    }
    
    // Tải dữ liệu ký tự từ Firebase
    private fun loadCharacters(type: AlphabetType) {
        viewModelScope.launch {
            when (type) {
                AlphabetType.HIRAGANA -> {
                    repository.loadHiragana()
                    _characters.value = repository.hiragana.value
                }
                AlphabetType.KATAKANA -> {
                    repository.loadKatakana()
                    _characters.value = repository.katakana.value
                }
                AlphabetType.KANJI -> {
                    repository.loadKanji()
                    _characters.value = repository.kanji.value
                }
            }
        }
    }
    
    // Lấy danh sách ký tự theo hàng
    fun getCharacterRows(): Map<String, List<JapaneseCharacter>> {
        return when (_currentAlphabetType.value) {
            AlphabetType.HIRAGANA -> getHiraganaRows()
            AlphabetType.KATAKANA -> getKatakanaRows()
            AlphabetType.KANJI -> getKanjiByLevel()
        }
    }
    
    // Nhóm Hiragana theo hàng
    private fun getHiraganaRows(): Map<String, List<JapaneseCharacter>> {
        val characters = _characters.value
        val map = mutableMapOf<String, MutableList<JapaneseCharacter>>()
        
        // Nhóm nguyên âm
        map["Nguyên âm"] = characters.filter { it.romanization in listOf("a", "i", "u", "e", "o") }.toMutableList()
        
        // Nhóm phụ âm K
        map["Phụ âm K"] = characters.filter { it.romanization.startsWith("k") && it.romanization.length == 2 }.toMutableList()
        
        // Nhóm phụ âm S
        map["Phụ âm S"] = characters.filter { 
            (it.romanization.startsWith("s") || it.romanization == "shi") && it.romanization.length <= 3 
        }.toMutableList()
        
        // Nhóm phụ âm T
        map["Phụ âm T"] = characters.filter { 
            (it.romanization.startsWith("t") || it.romanization == "chi" || it.romanization == "tsu") && it.romanization.length <= 3 
        }.toMutableList()
        
        // Nhóm phụ âm N
        map["Phụ âm N"] = characters.filter { 
            it.romanization.startsWith("n") && it.romanization.length == 2 
        }.toMutableList()
        
        // Phụ âm H
        map["Phụ âm H"] = characters.filter { 
            (it.romanization.startsWith("h") || it.romanization == "fu") && it.romanization.length <= 2 
        }.toMutableList()
        
        // Phụ âm M
        map["Phụ âm M"] = characters.filter { 
            it.romanization.startsWith("m") && it.romanization.length == 2 
        }.toMutableList()
        
        // Phụ âm Y
        map["Phụ âm Y"] = characters.filter { 
            it.romanization.startsWith("y") && it.romanization.length == 2 
        }.toMutableList()
        
        // Phụ âm R
        map["Phụ âm R"] = characters.filter { 
            it.romanization.startsWith("r") && it.romanization.length == 2 
        }.toMutableList()
        
        // Phụ âm W
        map["Phụ âm W"] = characters.filter { 
            it.romanization.startsWith("w") && it.romanization.length == 2 
        }.toMutableList()
        
        // Phụ âm N độc lập
        map["Phụ âm N độc lập"] = characters.filter { 
            it.romanization == "n" 
        }.toMutableList()
        
        // Các phụ âm biến âm và mở rộng
        map["Các phụ âm biến âm"] = characters.filter { 
            val r = it.romanization
            (r.startsWith("g") || r.startsWith("z") || r.startsWith("d") || r.startsWith("b") || r.startsWith("p")) && r.length == 2 
        }.toMutableList()
        
        // Các phụ âm kép
        map["Các phụ âm kép"] = characters.filter { 
            it.romanization.length >= 3 && it.romanization != "shi" && it.romanization != "chi" && it.romanization != "tsu"
        }.toMutableList()
        
        return map
    }
    
    // Nhóm Katakana theo hàng
    private fun getKatakanaRows(): Map<String, List<JapaneseCharacter>> {
        val characters = _characters.value
        val map = mutableMapOf<String, MutableList<JapaneseCharacter>>()
        
        // Nhóm nguyên âm
        map["Nguyên âm"] = characters.filter { it.romanization in listOf("a", "i", "u", "e", "o") }.toMutableList()
        
        // Nhóm phụ âm K
        map["Phụ âm K"] = characters.filter { it.romanization.startsWith("k") && it.romanization.length == 2 }.toMutableList()
        
        // Nhóm phụ âm S
        map["Phụ âm S"] = characters.filter { 
            (it.romanization.startsWith("s") || it.romanization == "shi") && it.romanization.length <= 3 
        }.toMutableList()
        
        // Nhóm phụ âm T
        map["Phụ âm T"] = characters.filter { 
            (it.romanization.startsWith("t") || it.romanization == "chi" || it.romanization == "tsu") && it.romanization.length <= 3 
        }.toMutableList()
        
        // Nhóm phụ âm N
        map["Phụ âm N"] = characters.filter { 
            it.romanization.startsWith("n") && it.romanization.length == 2 
        }.toMutableList()
        
        // Phụ âm H
        map["Phụ âm H"] = characters.filter { 
            (it.romanization.startsWith("h") || it.romanization == "fu") && it.romanization.length <= 2 
        }.toMutableList()
        
        // Phụ âm M
        map["Phụ âm M"] = characters.filter { 
            it.romanization.startsWith("m") && it.romanization.length == 2 
        }.toMutableList()
        
        // Phụ âm Y
        map["Phụ âm Y"] = characters.filter { 
            it.romanization.startsWith("y") && it.romanization.length == 2 
        }.toMutableList()
        
        // Phụ âm R
        map["Phụ âm R"] = characters.filter { 
            it.romanization.startsWith("r") && it.romanization.length == 2 
        }.toMutableList()
        
        // Phụ âm W
        map["Phụ âm W"] = characters.filter { 
            it.romanization.startsWith("w") && it.romanization.length == 2 
        }.toMutableList()
        
        // Phụ âm N độc lập
        map["Phụ âm N độc lập"] = characters.filter { 
            it.romanization == "n" 
        }.toMutableList()
        
        // Các phụ âm biến âm và mở rộng
        map["Các phụ âm biến âm"] = characters.filter { 
            val r = it.romanization
            (r.startsWith("g") || r.startsWith("z") || r.startsWith("d") || r.startsWith("b") || r.startsWith("p")) && r.length == 2 
        }.toMutableList()
        
        // Các phụ âm kép
        map["Các phụ âm kép"] = characters.filter { 
            it.romanization.length >= 3 && it.romanization != "shi" && it.romanization != "chi" && it.romanization != "tsu"
        }.toMutableList()
        
        return map
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
        
        val characters = _characters.value
        return characters.filter { 
            it.romanization.contains(query.trim().lowercase()) || 
            query.trim().lowercase().contains(it.romanization)
        }
    }

    private fun getKanjiByLevel(): Map<String, List<JapaneseCharacter>> {
        val allKanji = _characters.value
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