package com.example.composeapp.repository

import com.example.composeapp.models.JapaneseCharacter
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.tasks.await
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class JapaneseAlphabetRepository {
    private val database = FirebaseDatabase.getInstance()
    private val hiraganaRef = database.getReference("app_data/japanese_alphabet/hiragana")
    private val katakanaRef = database.getReference("app_data/japanese_alphabet/katakana")
    private val kanjiRef = database.getReference("app_data/japanese_alphabet/kanji")
    
    private val _hiragana = MutableStateFlow<List<JapaneseCharacter>>(emptyList())
    val hiragana: StateFlow<List<JapaneseCharacter>> = _hiragana
    
    private val _katakana = MutableStateFlow<List<JapaneseCharacter>>(emptyList())
    val katakana: StateFlow<List<JapaneseCharacter>> = _katakana
    
    private val _kanji = MutableStateFlow<List<JapaneseCharacter>>(emptyList())
    val kanji: StateFlow<List<JapaneseCharacter>> = _kanji
    
    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading
    
    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error
    
    // Tải dữ liệu Hiragana từ Firebase
    suspend fun loadHiragana() {
        try {
            _isLoading.value = true
            _error.value = null
            
            val snapshot = hiraganaRef.get().await()
            val characters = mutableListOf<JapaneseCharacter>()
            
            for (charSnapshot in snapshot.children) {
                val character = parseJapaneseCharacter(charSnapshot)
                if (character != null) {
                    characters.add(character)
                }
            }
            
            _hiragana.value = characters
        } catch (e: Exception) {
            _error.value = "Không thể tải dữ liệu Hiragana: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    // Tải dữ liệu Katakana từ Firebase
    suspend fun loadKatakana() {
        try {
            _isLoading.value = true
            _error.value = null
            
            val snapshot = katakanaRef.get().await()
            val characters = mutableListOf<JapaneseCharacter>()
            
            for (charSnapshot in snapshot.children) {
                val character = parseJapaneseCharacter(charSnapshot)
                if (character != null) {
                    characters.add(character)
                }
            }
            
            _katakana.value = characters
        } catch (e: Exception) {
            _error.value = "Không thể tải dữ liệu Katakana: ${e.message}"
        } finally {
            _isLoading.value = false
        }
    }
    
    // Tải dữ liệu Kanji từ Firebase
    suspend fun loadKanji() {
        try {
            _isLoading.value = true
            _error.value = null
            
            val snapshot = kanjiRef.get().await()
            val characters = mutableListOf<JapaneseCharacter>()
            
            for (charSnapshot in snapshot.children) {
                val character = parseJapaneseCharacter(charSnapshot)
                if (character != null) {
                    characters.add(character)
                }
            }
            
            // Nếu không tải được dữ liệu từ Firebase, sử dụng dữ liệu mặc định
            if (characters.isEmpty()) {
                _kanji.value = getDefaultKanjiData()
            } else {
                _kanji.value = characters
            }
        } catch (e: Exception) {
            _error.value = "Không thể tải dữ liệu Kanji từ Firebase, sử dụng dữ liệu mặc định: ${e.message}"
            // Sử dụng dữ liệu mặc định khi có lỗi
            _kanji.value = getDefaultKanjiData()
        } finally {
            _isLoading.value = false
        }
    }
    
    // Parse JapaneseCharacter từ Firebase snapshot
    private fun parseJapaneseCharacter(snapshot: DataSnapshot): JapaneseCharacter? {
        return try {
            val character = snapshot.child("character").getValue(String::class.java) ?: return null
            val romanization = snapshot.child("romanization").getValue(String::class.java) ?: return null
            val image = snapshot.child("image").getValue(String::class.java)
            val imageUrl = snapshot.child("imageUrl").getValue(String::class.java)
            val audioUrl = snapshot.child("audioUrl").getValue(String::class.java)
            val meaning = snapshot.child("meaning").getValue(String::class.java)
            val onReading = snapshot.child("onReading").getValue(String::class.java)
            val kunReading = snapshot.child("kunReading").getValue(String::class.java)
            
            // Parse examples
            val examples = mutableListOf<com.example.composeapp.models.Example>()
            val examplesNode = snapshot.child("examples")
            if (examplesNode.exists()) {
                for (exampleSnapshot in examplesNode.children) {
                    val japanese = exampleSnapshot.child("japanese").getValue(String::class.java) ?: ""
                    val vietnamese = exampleSnapshot.child("vietnamese").getValue(String::class.java) ?: ""
                    if (japanese.isNotEmpty() && vietnamese.isNotEmpty()) {
                        examples.add(com.example.composeapp.models.Example(japanese, vietnamese))
                    }
                }
            }
            
            JapaneseCharacter(
                character = character,
                romanization = romanization,
                image = image,
                imageUrl = imageUrl,
                audioUrl = audioUrl,
                examples = examples,
                meaning = meaning,
                onReading = onReading,
                kunReading = kunReading
            )
        } catch (e: Exception) {
            null
        }
    }
    
    // Tải tất cả dữ liệu
    suspend fun loadAllData() {
        loadHiragana()
        loadKatakana()
        loadKanji()
    }
    
    fun resetError() {
        _error.value = null
    }
    
    // Dữ liệu Kanji mặc định khi không tải được từ Firebase
    private fun getDefaultKanjiData(): List<JapaneseCharacter> {
        return listOf(
            // N5 Kanji - Thời gian cơ bản
            JapaneseCharacter("日", "nichi", meaning = "Mặt trời, ngày", onReading = "ニチ", kunReading = "ひ"),
            JapaneseCharacter("月", "getsu", meaning = "Mặt trăng, tháng", onReading = "ゲツ", kunReading = "つき"),
            JapaneseCharacter("年", "nen", meaning = "Năm", onReading = "ネン", kunReading = "とし"),
            JapaneseCharacter("時", "ji", meaning = "Thời gian, giờ", onReading = "ジ", kunReading = "とき"),
            
            // N5 Kanji - Con người và gia đình
            JapaneseCharacter("人", "jin", meaning = "Người", onReading = "ジン", kunReading = "ひと"),
            JapaneseCharacter("父", "chichi", meaning = "Cha", onReading = "フ", kunReading = "ちち"),
            JapaneseCharacter("母", "haha", meaning = "Mẹ", onReading = "ボ", kunReading = "はは"),
            JapaneseCharacter("子", "ko", meaning = "Con", onReading = "シ", kunReading = "こ"),
            
            // N5 Kanji - Số đếm
            JapaneseCharacter("一", "ichi", meaning = "Một", onReading = "イチ", kunReading = "ひと"),
            JapaneseCharacter("二", "ni", meaning = "Hai", onReading = "ニ", kunReading = "ふた"),
            JapaneseCharacter("三", "san", meaning = "Ba", onReading = "サン", kunReading = "みっ"),
            JapaneseCharacter("四", "shi", meaning = "Bốn", onReading = "シ", kunReading = "よん"),
            JapaneseCharacter("五", "go", meaning = "Năm", onReading = "ゴ", kunReading = "いつ"),
            
            // N5 Kanji - Học tập
            JapaneseCharacter("本", "hon", meaning = "Sách, gốc", onReading = "ホン", kunReading = "ほん"),
            JapaneseCharacter("学", "gaku", meaning = "Học", onReading = "ガク", kunReading = "まな"),
            JapaneseCharacter("校", "kou", meaning = "Trường", onReading = "コウ", kunReading = "こう"),
            JapaneseCharacter("先", "sen", meaning = "Trước, trước đó", onReading = "セン", kunReading = "さき"),
            
            // N5 Kanji - Công việc và xã hội
            JapaneseCharacter("会", "kai", meaning = "Hội, gặp", onReading = "カイ", kunReading = "あ"),
            JapaneseCharacter("社", "sha", meaning = "Công ty, xã hội", onReading = "シャ", kunReading = "やしろ"),
            JapaneseCharacter("仕", "shi", meaning = "Làm việc", onReading = "シ", kunReading = "つか"),
            JapaneseCharacter("事", "ji", meaning = "Việc, sự việc", onReading = "ジ", kunReading = "こと"),
            
            // N5 Kanji - Địa điểm
            JapaneseCharacter("国", "kuni", meaning = "Nước, quốc gia", onReading = "コク", kunReading = "くに"),
            JapaneseCharacter("家", "ie", meaning = "Nhà", onReading = "カ", kunReading = "いえ"),
            JapaneseCharacter("駅", "eki", meaning = "Ga tàu", onReading = "エキ", kunReading = "えき"),
            JapaneseCharacter("店", "mise", meaning = "Cửa hàng", onReading = "テン", kunReading = "みせ"),
            
            // N5 Kanji - Hoạt động
            JapaneseCharacter("行", "iku", meaning = "Đi", onReading = "コウ", kunReading = "い"),
            JapaneseCharacter("来", "kuru", meaning = "Đến", onReading = "ライ", kunReading = "く"),
            JapaneseCharacter("見", "miru", meaning = "Xem", onReading = "ケン", kunReading = "み"),
            JapaneseCharacter("聞", "kiku", meaning = "Nghe", onReading = "ブン", kunReading = "き"),
            
            // N5 Kanji - Tính chất
            JapaneseCharacter("新", "shin", meaning = "Mới", onReading = "シン", kunReading = "あたら"),
            JapaneseCharacter("古", "furu", meaning = "Cũ", onReading = "コ", kunReading = "ふる"),
            JapaneseCharacter("多", "ooi", meaning = "Nhiều", onReading = "タ", kunReading = "おお"),
            JapaneseCharacter("少", "sukunai", meaning = "Ít", onReading = "ショウ", kunReading = "すく"),
            
            // N4 Kanji - Cảm xúc và tính cách
            JapaneseCharacter("愛", "ai", meaning = "Yêu, tình yêu", onReading = "アイ", kunReading = "あい"),
            JapaneseCharacter("楽", "raku", meaning = "Vui vẻ, dễ chịu", onReading = "ガク", kunReading = "たの"),
            JapaneseCharacter("苦", "kurushii", meaning = "Khổ, đắng", onReading = "ク", kunReading = "くる"),
            JapaneseCharacter("悲", "kanashii", meaning = "Buồn", onReading = "ヒ", kunReading = "かな"),
            
            // N4 Kanji - Thiên nhiên
            JapaneseCharacter("山", "yama", meaning = "Núi", onReading = "サン", kunReading = "やま"),
            JapaneseCharacter("川", "kawa", meaning = "Sông", onReading = "セン", kunReading = "かわ"),
            JapaneseCharacter("海", "umi", meaning = "Biển", onReading = "カイ", kunReading = "うみ"),
            JapaneseCharacter("空", "sora", meaning = "Bầu trời, không khí", onReading = "クウ", kunReading = "そら"),
            
            // N4 Kanji - Thời tiết
            JapaneseCharacter("雨", "ame", meaning = "Mưa", onReading = "ウ", kunReading = "あめ"),
            JapaneseCharacter("雪", "yuki", meaning = "Tuyết", onReading = "セツ", kunReading = "ゆき"),
            JapaneseCharacter("風", "kaze", meaning = "Gió", onReading = "フウ", kunReading = "かぜ"),
            JapaneseCharacter("雲", "kumo", meaning = "Mây", onReading = "ウン", kunReading = "くも"),
            
            // N4 Kanji - Hoạt động hàng ngày
            JapaneseCharacter("食", "taberu", meaning = "Ăn, thức ăn", onReading = "ショク", kunReading = "た"),
            JapaneseCharacter("飲", "nomu", meaning = "Uống", onReading = "イン", kunReading = "の"),
            JapaneseCharacter("寝", "neru", meaning = "Ngủ", onReading = "シン", kunReading = "ね"),
            JapaneseCharacter("働", "hataraku", meaning = "Làm việc", onReading = "ドウ", kunReading = "はたら"),
            
            // N4 Kanji - Giao thông
            JapaneseCharacter("車", "kuruma", meaning = "Xe hơi", onReading = "シャ", kunReading = "くるま"),
            JapaneseCharacter("道", "michi", meaning = "Đường", onReading = "ドウ", kunReading = "みち"),
            JapaneseCharacter("橋", "hashi", meaning = "Cầu", onReading = "キョウ", kunReading = "はし"),
            JapaneseCharacter("駐", "chuu", meaning = "Đỗ xe", onReading = "チュウ", kunReading = "ちゅう"),
            
            // N4 Kanji - Thời gian nâng cao
            JapaneseCharacter("朝", "asa", meaning = "Buổi sáng", onReading = "チョウ", kunReading = "あさ"),
            JapaneseCharacter("昼", "hiru", meaning = "Buổi trưa", onReading = "チュウ", kunReading = "ひる"),
            JapaneseCharacter("夜", "yoru", meaning = "Buổi tối", onReading = "ヤ", kunReading = "よる"),
            JapaneseCharacter("週", "shuu", meaning = "Tuần", onReading = "シュウ", kunReading = "しゅう")
        )
    }
}
