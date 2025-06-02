package com.example.composeapp.models

enum class AlphabetType {
    HIRAGANA,
    KATAKANA
}

data class JapaneseCharacter(
    val character: String,          // Ký tự (Hiragana hoặc Katakana)
    val romanization: String,       // Phiên âm La-tinh
    val strokeOrder: String? = null, // URL hình ảnh thứ tự nét viết từ Firebase Storage (vd: gs://my-app.appspot.com/stroke-orders/hiragana/あ.gif)
    val imageUrl: String? = null,   // URL hình ảnh minh họa từ Firebase Storage
    val audioUrl: String? = null,   // URL file âm thanh phát âm từ Firebase Storage
    val examples: List<Example> = emptyList() // Ví dụ sử dụng
)

// Object chứa dữ liệu bảng chữ cái Hiragana và Katakana
object JapaneseAlphabet {
    // Bảng chữ cái Hiragana
    val hiragana = listOf(
        // Nguyên âm
        JapaneseCharacter("あ", "a"),
        JapaneseCharacter("い", "i"),
        JapaneseCharacter("う", "u"),
        JapaneseCharacter("え", "e"),
        JapaneseCharacter("お", "o"),
        
        // Phụ âm K
        JapaneseCharacter("か", "ka"),
        JapaneseCharacter("き", "ki"),
        JapaneseCharacter("く", "ku"),
        JapaneseCharacter("け", "ke"),
        JapaneseCharacter("こ", "ko"),
        
        // Phụ âm S
        JapaneseCharacter("さ", "sa"),
        JapaneseCharacter("し", "shi"),
        JapaneseCharacter("す", "su"),
        JapaneseCharacter("せ", "se"),
        JapaneseCharacter("そ", "so"),
        
        // Phụ âm T
        JapaneseCharacter("た", "ta"),
        JapaneseCharacter("ち", "chi"),
        JapaneseCharacter("つ", "tsu"),
        JapaneseCharacter("て", "te"),
        JapaneseCharacter("と", "to"),
        
        // Phụ âm N
        JapaneseCharacter("な", "na"),
        JapaneseCharacter("に", "ni"),
        JapaneseCharacter("ぬ", "nu"),
        JapaneseCharacter("ね", "ne"),
        JapaneseCharacter("の", "no"),
        
        // Phụ âm H
        JapaneseCharacter("は", "ha"),
        JapaneseCharacter("ひ", "hi"),
        JapaneseCharacter("ふ", "fu"),
        JapaneseCharacter("へ", "he"),
        JapaneseCharacter("ほ", "ho"),
        
        // Phụ âm M
        JapaneseCharacter("ま", "ma"),
        JapaneseCharacter("み", "mi"),
        JapaneseCharacter("む", "mu"),
        JapaneseCharacter("め", "me"),
        JapaneseCharacter("も", "mo"),
        
        // Phụ âm Y
        JapaneseCharacter("や", "ya"),
        JapaneseCharacter("ゆ", "yu"),
        JapaneseCharacter("よ", "yo"),
        
        // Phụ âm R
        JapaneseCharacter("ら", "ra"),
        JapaneseCharacter("り", "ri"),
        JapaneseCharacter("る", "ru"),
        JapaneseCharacter("れ", "re"),
        JapaneseCharacter("ろ", "ro"),
        
        // Phụ âm W
        JapaneseCharacter("わ", "wa"),
        JapaneseCharacter("を", "wo"),
        
        // Phụ âm N độc lập
        JapaneseCharacter("ん", "n"),
        
        // Phụ âm G (dakuten của K)
        JapaneseCharacter("が", "ga"),
        JapaneseCharacter("ぎ", "gi"),
        JapaneseCharacter("ぐ", "gu"),
        JapaneseCharacter("げ", "ge"),
        JapaneseCharacter("ご", "go"),
        
        // Phụ âm Z (dakuten của S)
        JapaneseCharacter("ざ", "za"),
        JapaneseCharacter("じ", "ji"),
        JapaneseCharacter("ず", "zu"),
        JapaneseCharacter("ぜ", "ze"),
        JapaneseCharacter("ぞ", "zo"),
        
        // Phụ âm D (dakuten của T)
        JapaneseCharacter("だ", "da"),
        JapaneseCharacter("ぢ", "ji"),
        JapaneseCharacter("づ", "zu"),
        JapaneseCharacter("で", "de"),
        JapaneseCharacter("ど", "do"),
        
        // Phụ âm B (dakuten của H)
        JapaneseCharacter("ば", "ba"),
        JapaneseCharacter("び", "bi"),
        JapaneseCharacter("ぶ", "bu"),
        JapaneseCharacter("べ", "be"),
        JapaneseCharacter("ぼ", "bo"),
        
        // Phụ âm P (handakuten của H)
        JapaneseCharacter("ぱ", "pa"),
        JapaneseCharacter("ぴ", "pi"),
        JapaneseCharacter("ぷ", "pu"),
        JapaneseCharacter("ぺ", "pe"),
        JapaneseCharacter("ぽ", "po"),
        
        // Các phụ âm kép (yōon)
        JapaneseCharacter("きゃ", "kya"),
        JapaneseCharacter("きゅ", "kyu"),
        JapaneseCharacter("きょ", "kyo"),
        JapaneseCharacter("しゃ", "sha"),
        JapaneseCharacter("しゅ", "shu"),
        JapaneseCharacter("しょ", "sho"),
        JapaneseCharacter("ちゃ", "cha"),
        JapaneseCharacter("ちゅ", "chu"),
        JapaneseCharacter("ちょ", "cho"),
        JapaneseCharacter("にゃ", "nya"),
        JapaneseCharacter("にゅ", "nyu"),
        JapaneseCharacter("にょ", "nyo"),
        JapaneseCharacter("ひゃ", "hya"),
        JapaneseCharacter("ひゅ", "hyu"),
        JapaneseCharacter("ひょ", "hyo"),
        JapaneseCharacter("みゃ", "mya"),
        JapaneseCharacter("みゅ", "myu"),
        JapaneseCharacter("みょ", "myo"),
        JapaneseCharacter("りゃ", "rya"),
        JapaneseCharacter("りゅ", "ryu"),
        JapaneseCharacter("りょ", "ryo"),
        JapaneseCharacter("ぎゃ", "gya"),
        JapaneseCharacter("ぎゅ", "gyu"),
        JapaneseCharacter("ぎょ", "gyo"),
        JapaneseCharacter("じゃ", "ja"),
        JapaneseCharacter("じゅ", "ju"),
        JapaneseCharacter("じょ", "jo"),
        JapaneseCharacter("びゃ", "bya"),
        JapaneseCharacter("びゅ", "byu"),
        JapaneseCharacter("びょ", "byo"),
        JapaneseCharacter("ぴゃ", "pya"),
        JapaneseCharacter("ぴゅ", "pyu"),
        JapaneseCharacter("ぴょ", "pyo")
    )
    
    // Bảng chữ cái Katakana
    val katakana = listOf(
        // Nguyên âm
        JapaneseCharacter("ア", "a"),
        JapaneseCharacter("イ", "i"),
        JapaneseCharacter("ウ", "u"),
        JapaneseCharacter("エ", "e"),
        JapaneseCharacter("オ", "o"),
        
        // Phụ âm K
        JapaneseCharacter("カ", "ka"),
        JapaneseCharacter("キ", "ki"),
        JapaneseCharacter("ク", "ku"),
        JapaneseCharacter("ケ", "ke"),
        JapaneseCharacter("コ", "ko"),
        
        // Phụ âm S
        JapaneseCharacter("サ", "sa"),
        JapaneseCharacter("シ", "shi"),
        JapaneseCharacter("ス", "su"),
        JapaneseCharacter("セ", "se"),
        JapaneseCharacter("ソ", "so"),
        
        // Phụ âm T
        JapaneseCharacter("タ", "ta"),
        JapaneseCharacter("チ", "chi"),
        JapaneseCharacter("ツ", "tsu"),
        JapaneseCharacter("テ", "te"),
        JapaneseCharacter("ト", "to"),
        
        // Phụ âm N
        JapaneseCharacter("ナ", "na"),
        JapaneseCharacter("ニ", "ni"),
        JapaneseCharacter("ヌ", "nu"),
        JapaneseCharacter("ネ", "ne"),
        JapaneseCharacter("ノ", "no"),
        
        // Phụ âm H
        JapaneseCharacter("ハ", "ha"),
        JapaneseCharacter("ヒ", "hi"),
        JapaneseCharacter("フ", "fu"),
        JapaneseCharacter("ヘ", "he"),
        JapaneseCharacter("ホ", "ho"),
        
        // Phụ âm M
        JapaneseCharacter("マ", "ma"),
        JapaneseCharacter("ミ", "mi"),
        JapaneseCharacter("ム", "mu"),
        JapaneseCharacter("メ", "me"),
        JapaneseCharacter("モ", "mo"),
        
        // Phụ âm Y
        JapaneseCharacter("ヤ", "ya"),
        JapaneseCharacter("ユ", "yu"),
        JapaneseCharacter("ヨ", "yo"),
        
        // Phụ âm R
        JapaneseCharacter("ラ", "ra"),
        JapaneseCharacter("リ", "ri"),
        JapaneseCharacter("ル", "ru"),
        JapaneseCharacter("レ", "re"),
        JapaneseCharacter("ロ", "ro"),
        
        // Phụ âm W
        JapaneseCharacter("ワ", "wa"),
        JapaneseCharacter("ヲ", "wo"),
        
        // Phụ âm N độc lập
        JapaneseCharacter("ン", "n"),
        
        // Phụ âm G (dakuten của K)
        JapaneseCharacter("ガ", "ga"),
        JapaneseCharacter("ギ", "gi"),
        JapaneseCharacter("グ", "gu"),
        JapaneseCharacter("ゲ", "ge"),
        JapaneseCharacter("ゴ", "go"),
        
        // Phụ âm Z (dakuten của S)
        JapaneseCharacter("ザ", "za"),
        JapaneseCharacter("ジ", "ji"),
        JapaneseCharacter("ズ", "zu"),
        JapaneseCharacter("ゼ", "ze"),
        JapaneseCharacter("ゾ", "zo"),
        
        // Phụ âm D (dakuten của T)
        JapaneseCharacter("ダ", "da"),
        JapaneseCharacter("ヂ", "ji"),
        JapaneseCharacter("ヅ", "zu"),
        JapaneseCharacter("デ", "de"),
        JapaneseCharacter("ド", "do"),
        
        // Phụ âm B (dakuten của H)
        JapaneseCharacter("バ", "ba"),
        JapaneseCharacter("ビ", "bi"),
        JapaneseCharacter("ブ", "bu"),
        JapaneseCharacter("ベ", "be"),
        JapaneseCharacter("ボ", "bo"),
        
        // Phụ âm P (handakuten của H)
        JapaneseCharacter("パ", "pa"),
        JapaneseCharacter("ピ", "pi"),
        JapaneseCharacter("プ", "pu"),
        JapaneseCharacter("ペ", "pe"),
        JapaneseCharacter("ポ", "po"),
        
        // Các phụ âm kép (yōon)
        JapaneseCharacter("キャ", "kya"),
        JapaneseCharacter("キュ", "kyu"),
        JapaneseCharacter("キョ", "kyo"),
        JapaneseCharacter("シャ", "sha"),
        JapaneseCharacter("シュ", "shu"),
        JapaneseCharacter("ショ", "sho"),
        JapaneseCharacter("チャ", "cha"),
        JapaneseCharacter("チュ", "chu"),
        JapaneseCharacter("チョ", "cho"),
        JapaneseCharacter("ニャ", "nya"),
        JapaneseCharacter("ニュ", "nyu"),
        JapaneseCharacter("ニョ", "nyo"),
        JapaneseCharacter("ヒャ", "hya"),
        JapaneseCharacter("ヒュ", "hyu"),
        JapaneseCharacter("ヒョ", "hyo"),
        JapaneseCharacter("ミャ", "mya"),
        JapaneseCharacter("ミュ", "myu"),
        JapaneseCharacter("ミョ", "myo"),
        JapaneseCharacter("リャ", "rya"),
        JapaneseCharacter("リュ", "ryu"),
        JapaneseCharacter("リョ", "ryo"),
        JapaneseCharacter("ギャ", "gya"),
        JapaneseCharacter("ギュ", "gyu"),
        JapaneseCharacter("ギョ", "gyo"),
        JapaneseCharacter("ジャ", "ja"),
        JapaneseCharacter("ジュ", "ju"),
        JapaneseCharacter("ジョ", "jo"),
        JapaneseCharacter("ビャ", "bya"),
        JapaneseCharacter("ビュ", "byu"),
        JapaneseCharacter("ビョ", "byo"),
        JapaneseCharacter("ピャ", "pya"),
        JapaneseCharacter("ピュ", "pyu"),
        JapaneseCharacter("ピョ", "pyo")
    )
    
    // Hàm lấy danh sách ký tự dựa trên loại bảng chữ cái
    fun getCharacters(type: AlphabetType): List<JapaneseCharacter> {
        return when (type) {
            AlphabetType.HIRAGANA -> hiragana
            AlphabetType.KATAKANA -> katakana
        }
    }
    
    // Hàm tìm kiếm ký tự theo romanization
    fun findByRomanization(romanization: String, type: AlphabetType): List<JapaneseCharacter> {
        val characters = when (type) {
            AlphabetType.HIRAGANA -> hiragana
            AlphabetType.KATAKANA -> katakana
        }
        
        return characters.filter { 
            it.romanization.contains(romanization) || 
            romanization.contains(it.romanization)
        }
    }
    
    // Hàm tìm kiếm ký tự theo character (chữ cái)
    fun findByCharacter(character: String): JapaneseCharacter? {
        return (hiragana + katakana).find { it.character == character }
    }
    
    // Lấy các hàng chữ cái để hiển thị theo bảng
    fun getRows(type: AlphabetType): Map<String, List<JapaneseCharacter>> {
        val characters = when (type) {
            AlphabetType.HIRAGANA -> hiragana
            AlphabetType.KATAKANA -> katakana
        }
        
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
} 