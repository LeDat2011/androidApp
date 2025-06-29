package com.example.composeapp.models

enum class AlphabetType {
    HIRAGANA,
    KATAKANA,
    KANJI
}

data class JapaneseCharacter(
    val character: String,          // Ký tự (Hiragana hoặc Katakana)
    val romanization: String,       // Phiên âm La-tinh
    val image: String? = null,      // Đường dẫn đến hình ảnh thứ tự nét viết (vd: asset:///stroke_order_image/hiragana/a.png)
    val imageUrl: String? = null,   // URL hình ảnh minh họa từ Firebase Storage
    val audioUrl: String? = null,   // URL file âm thanh phát âm từ Firebase Storage
    val examples: List<Example> = emptyList(), // Ví dụ sử dụng
    val meaning: String? = null,    // Nghĩa của Kanji (chỉ dùng cho Kanji)
    val onReading: String? = null,  // Âm On của Kanji (chỉ dùng cho Kanji)
    val kunReading: String? = null  // Âm Kun của Kanji (chỉ dùng cho Kanji)
)

// Object chứa dữ liệu bảng chữ cái Hiragana và Katakana
object JapaneseAlphabet {
    // Bảng chữ cái Hiragana
    val hiragana = listOf(
        // Nguyên âm
        JapaneseCharacter(
            character = "あ",
            romanization = "a",
            image = "asset:///stroke_order_image/hiragana/a.png"
        ),
        JapaneseCharacter(
            character = "い",
            romanization = "i",
            image = "asset:///stroke_order_image/hiragana/i.png"
        ),
        JapaneseCharacter(
            character = "う",
            romanization = "u",
            image = "asset:///stroke_order_image/hiragana/u.png"
        ),
        JapaneseCharacter(
            character = "え",
            romanization = "e",
            image = "asset:///stroke_order_image/hiragana/e.png"
        ),
        JapaneseCharacter(
            character = "お",
            romanization = "o",
            image = "asset:///stroke_order_image/hiragana/o.png"
        ),
        
        // Phụ âm K
        JapaneseCharacter(
            character = "か",
            romanization = "ka",
            image = "asset:///stroke_order_image/hiragana/ka.png"
        ),
        JapaneseCharacter(
            character = "き",
            romanization = "ki",
            image = "asset:///stroke_order_image/hiragana/ki.png"
        ),
        JapaneseCharacter(
            character = "く",
            romanization = "ku",
            image = "asset:///stroke_order_image/hiragana/ku.png"
        ),
        JapaneseCharacter(
            character = "け",
            romanization = "ke",
            image = "asset:///stroke_order_image/hiragana/ke.png"
        ),
        JapaneseCharacter(
            character = "こ",
            romanization = "ko",
            image = "asset:///stroke_order_image/hiragana/ko.png"
        ),
        
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
        JapaneseCharacter(
            character = "ア",
            romanization = "a",
            image = "asset:///stroke_order_image/katakana/a.png"
        ),
        JapaneseCharacter(
            character = "イ",
            romanization = "i",
            image = "asset:///stroke_order_image/katakana/i.png"
        ),
        JapaneseCharacter(
            character = "ウ",
            romanization = "u",
            image = "asset:///stroke_order_image/katakana/u.png"
        ),
        JapaneseCharacter(
            character = "エ",
            romanization = "e",
            image = "asset:///stroke_order_image/katakana/e.png"
        ),
        JapaneseCharacter(
            character = "オ",
            romanization = "o",
            image = "asset:///stroke_order_image/katakana/o.png"
        ),
        
        // Phụ âm K
        JapaneseCharacter(
            character = "カ",
            romanization = "ka"
        ),
        JapaneseCharacter(
            character = "キ",
            romanization = "ki"
        ),
        JapaneseCharacter(
            character = "ク",
            romanization = "ku"
        ),
        JapaneseCharacter(
            character = "ケ",
            romanization = "ke"
        ),
        JapaneseCharacter(
            character = "コ",
            romanization = "ko"
        ),
        
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
    
    // Bảng chữ Kanji cơ bản (JLPT N5 và N4)
    val kanji = listOf(
        // JLPT N5 Kanji
        // Chủ đề: Thời gian
        JapaneseCharacter(
            character = "日",
            romanization = "nichi/jitsu",
            meaning = "ngày, mặt trời",
            onReading = "ニチ、ジツ",
            kunReading = "ひ、-び、-か",
            examples = listOf(
                Example("今日は良い日です。", "Hôm nay là một ngày đẹp."),
                Example("日本語を勉強しています。", "Tôi đang học tiếng Nhật.")
            )
        ),
        JapaneseCharacter(
            character = "月",
            romanization = "getsu/gatsu",
            meaning = "tháng, mặt trăng",
            onReading = "ゲツ、ガツ",
            kunReading = "つき",
            examples = listOf(
                Example("今月は忙しいです。", "Tháng này tôi rất bận."),
                Example("月がきれいですね。", "Mặt trăng đẹp quá nhỉ.")
            )
        ),
        JapaneseCharacter(
            character = "年",
            romanization = "nen",
            meaning = "năm",
            onReading = "ネン",
            kunReading = "とし",
            examples = listOf(
                Example("今年は2023年です。", "Năm nay là năm 2023."),
                Example("新年おめでとうございます。", "Chúc mừng năm mới.")
            )
        ),
        JapaneseCharacter(
            character = "時",
            romanization = "ji",
            meaning = "giờ, thời gian",
            onReading = "ジ",
            kunReading = "とき",
            examples = listOf(
                Example("今何時ですか？", "Bây giờ là mấy giờ?"),
                Example("時間がありません。", "Tôi không có thời gian.")
            )
        ),

        // Chủ đề: Con người và gia đình
        JapaneseCharacter(
            character = "人",
            romanization = "jin/nin",
            meaning = "người",
            onReading = "ジン、ニン",
            kunReading = "ひと",
            examples = listOf(
                Example("日本人です。", "Tôi là người Nhật."),
                Example("あの人は先生です。", "Người kia là giáo viên.")
            )
        ),
        JapaneseCharacter(
            character = "父",
            romanization = "fu",
            meaning = "bố, cha",
            onReading = "フ",
            kunReading = "ちち、とう",
            examples = listOf(
                Example("父は医者です。", "Bố tôi là bác sĩ."),
                Example("お父さんはどこですか？", "Bố bạn đâu rồi?")
            )
        ),
        JapaneseCharacter(
            character = "母",
            romanization = "bo",
            meaning = "mẹ",
            onReading = "ボ",
            kunReading = "はは、かあ",
            examples = listOf(
                Example("母は料理が上手です。", "Mẹ tôi nấu ăn rất giỏi."),
                Example("お母さんに電話しました。", "Tôi đã gọi điện cho mẹ.")
            )
        ),
        JapaneseCharacter(
            character = "子",
            romanization = "shi/su",
            meaning = "con, trẻ em",
            onReading = "シ、ス",
            kunReading = "こ",
            examples = listOf(
                Example("子供が公園で遊んでいます。", "Trẻ em đang chơi ở công viên."),
                Example("彼女は三人の子供がいます。", "Cô ấy có ba đứa con.")
            )
        ),

        // Chủ đề: Số đếm
        JapaneseCharacter(
            character = "一",
            romanization = "ichi",
            meaning = "một",
            onReading = "イチ",
            kunReading = "ひと-",
            examples = listOf(
                Example("一人で行きます。", "Tôi sẽ đi một mình."),
                Example("第一課を勉強しています。", "Tôi đang học bài 1.")
            )
        ),
        JapaneseCharacter(
            character = "二",
            romanization = "ni",
            meaning = "hai",
            onReading = "ニ",
            kunReading = "ふた-",
        ),
        JapaneseCharacter(
            character = "三",
            romanization = "san",
            meaning = "ba",
            onReading = "サン",
            kunReading = "み-",
        ),
        JapaneseCharacter(
            character = "四",
            romanization = "shi",
            meaning = "bốn",
            onReading = "シ",
            kunReading = "よ-",
        ),
        JapaneseCharacter(
            character = "五",
            romanization = "go",
            meaning = "năm",
            onReading = "ゴ",
            kunReading = "いつ-",
        ),

        // Chủ đề: Học tập
        JapaneseCharacter(
            character = "本",
            romanization = "hon",
            meaning = "sách, gốc, nguồn gốc",
            onReading = "ホン",
            kunReading = "もと",
        ),
        JapaneseCharacter(
            character = "学",
            romanization = "gaku",
            meaning = "học",
            onReading = "ガク",
            kunReading = "まな.ぶ"
        ),
        JapaneseCharacter(
            character = "校",
            romanization = "kou",
            meaning = "trường học",
            onReading = "コウ",
            kunReading = ""
        ),
        JapaneseCharacter(
            character = "先",
            romanization = "sen",
            meaning = "trước, tiên",
            onReading = "セン",
            kunReading = "さき"
        ),

        // JLPT N4 Kanji
        // Chủ đề: Công việc và xã hội
        JapaneseCharacter(
            character = "会",
            romanization = "kai",
            meaning = "gặp gỡ, hội họp",
            onReading = "カイ、エ",
            kunReading = "あ.う、あ.わせる"
        ),
        JapaneseCharacter(
            character = "社",
            romanization = "sha",
            meaning = "công ty, xã hội",
            onReading = "シャ",
            kunReading = "やしろ"
        ),
        JapaneseCharacter(
            character = "仕",
            romanization = "shi",
            meaning = "phục vụ, công việc",
            onReading = "シ",
            kunReading = "つか.える"
        ),
        JapaneseCharacter(
            character = "事",
            romanization = "ji",
            meaning = "việc, sự việc",
            onReading = "ジ",
            kunReading = "こと"
        ),

        // Chủ đề: Địa điểm
        JapaneseCharacter(
            character = "国",
            romanization = "koku",
            meaning = "quốc gia",
            onReading = "コク",
            kunReading = "くに"
        ),
        JapaneseCharacter(
            character = "家",
            romanization = "ka/ke",
            meaning = "nhà",
            onReading = "カ、ケ",
            kunReading = "いえ、や"
        ),
        JapaneseCharacter(
            character = "駅",
            romanization = "eki",
            meaning = "nhà ga",
            onReading = "エキ",
            kunReading = ""
        ),
        JapaneseCharacter(
            character = "店",
            romanization = "ten",
            meaning = "cửa hàng",
            onReading = "テン",
            kunReading = "みせ"
        ),

        // Chủ đề: Hoạt động
        JapaneseCharacter(
            character = "行",
            romanization = "kou/gyou",
            meaning = "đi, thực hiện",
            onReading = "コウ、ギョウ",
            kunReading = "い.く、おこな.う"
        ),
        JapaneseCharacter(
            character = "来",
            romanization = "rai",
            meaning = "đến, tương lai",
            onReading = "ライ",
            kunReading = "く.る"
        ),
        JapaneseCharacter(
            character = "見",
            romanization = "ken",
            meaning = "nhìn, xem",
            onReading = "ケン",
            kunReading = "み.る、み.える"
        ),
        JapaneseCharacter(
            character = "聞",
            romanization = "bun/mon",
            meaning = "nghe, hỏi",
            onReading = "ブン、モン",
            kunReading = "き.く、き.こえる"
        ),

        // Chủ đề: Tính chất
        JapaneseCharacter(
            character = "新",
            romanization = "shin",
            meaning = "mới",
            onReading = "シン",
            kunReading = "あたら.しい、あら.た"
        ),
        JapaneseCharacter(
            character = "古",
            romanization = "ko",
            meaning = "cũ",
            onReading = "コ",
            kunReading = "ふる.い"
        ),
        JapaneseCharacter(
            character = "多",
            romanization = "ta",
            meaning = "nhiều",
            onReading = "タ",
            kunReading = "おお.い"
        ),
        JapaneseCharacter(
            character = "少",
            romanization = "shou",
            meaning = "ít",
            onReading = "ショウ",
            kunReading = "すく.ない、すこ.し"
        ),

        // JLPT N3 Kanji
        // Chủ đề: Cảm xúc và tính cách
        JapaneseCharacter(
            character = "愛",
            romanization = "ai",
            meaning = "tình yêu, yêu thương",
            onReading = "アイ",
            kunReading = "あい、いと.しい"
        ),
        JapaneseCharacter(
            character = "楽",
            romanization = "raku/gaku",
            meaning = "vui vẻ, thoải mái, âm nhạc",
            onReading = "ガク、ラク",
            kunReading = "たの.しい"
        ),
        JapaneseCharacter(
            character = "苦",
            romanization = "ku",
            meaning = "đắng, khổ sở",
            onReading = "ク",
            kunReading = "くる.しい、にが.い"
        ),
        JapaneseCharacter(
            character = "悲",
            romanization = "hi",
            meaning = "buồn bã",
            onReading = "ヒ",
            kunReading = "かな.しい"
        ),

        // Chủ đề: Thiên nhiên
        JapaneseCharacter(
            character = "山",
            romanization = "san/zan",
            meaning = "núi",
            onReading = "サン、ザン",
            kunReading = "やま"
        ),
        JapaneseCharacter(
            character = "川",
            romanization = "sen",
            meaning = "sông",
            onReading = "セン",
            kunReading = "かわ"
        ),
        JapaneseCharacter(
            character = "海",
            romanization = "kai",
            meaning = "biển",
            onReading = "カイ",
            kunReading = "うみ"
        ),
        JapaneseCharacter(
            character = "空",
            romanization = "kuu/sora",
            meaning = "bầu trời, trống rỗng",
            onReading = "クウ",
            kunReading = "そら、から"
        ),

        // Chủ đề: Thời tiết
        JapaneseCharacter(
            character = "雨",
            romanization = "u/ame",
            meaning = "mưa",
            onReading = "ウ",
            kunReading = "あめ"
        ),
        JapaneseCharacter(
            character = "雪",
            romanization = "setsu",
            meaning = "tuyết",
            onReading = "セツ",
            kunReading = "ゆき"
        ),
        JapaneseCharacter(
            character = "風",
            romanization = "fuu/kaze",
            meaning = "gió",
            onReading = "フウ",
            kunReading = "かぜ"
        ),
        JapaneseCharacter(
            character = "雲",
            romanization = "un",
            meaning = "mây",
            onReading = "ウン",
            kunReading = "くも"
        ),

        // Chủ đề: Hoạt động hàng ngày
        JapaneseCharacter(
            character = "食",
            romanization = "shoku",
            meaning = "ăn, thức ăn",
            onReading = "ショク",
            kunReading = "た.べる"
        ),
        JapaneseCharacter(
            character = "飲",
            romanization = "in",
            meaning = "uống",
            onReading = "イン",
            kunReading = "の.む"
        ),
        JapaneseCharacter(
            character = "寝",
            romanization = "shin",
            meaning = "ngủ",
            onReading = "シン",
            kunReading = "ね.る"
        ),
        JapaneseCharacter(
            character = "働",
            romanization = "dou",
            meaning = "làm việc",
            onReading = "ドウ",
            kunReading = "はたら.く"
        ),

        // Chủ đề: Giao thông
        JapaneseCharacter(
            character = "車",
            romanization = "sha",
            meaning = "xe",
            onReading = "シャ",
            kunReading = "くるま"
        ),
        JapaneseCharacter(
            character = "道",
            romanization = "dou",
            meaning = "đường",
            onReading = "ドウ",
            kunReading = "みち"
        ),
        JapaneseCharacter(
            character = "橋",
            romanization = "kyou",
            meaning = "cầu",
            onReading = "キョウ",
            kunReading = "はし"
        ),
        JapaneseCharacter(
            character = "駐",
            romanization = "chuu",
            meaning = "đỗ xe, đậu xe",
            onReading = "チュウ",
            kunReading = ""
        ),

        // Chủ đề: Thời gian nâng cao
        JapaneseCharacter(
            character = "朝",
            romanization = "chou",
            meaning = "buổi sáng",
            onReading = "チョウ",
            kunReading = "あさ"
        ),
        JapaneseCharacter(
            character = "昼",
            romanization = "chuu",
            meaning = "buổi trưa",
            onReading = "チュウ",
            kunReading = "ひる"
        ),
        JapaneseCharacter(
            character = "夜",
            romanization = "ya",
            meaning = "buổi tối",
            onReading = "ヤ",
            kunReading = "よる"
        ),
        JapaneseCharacter(
            character = "週",
            romanization = "shuu",
            meaning = "tuần",
            onReading = "シュウ",
            kunReading = ""
        )
    )
    
    // Hàm lấy danh sách ký tự dựa trên loại bảng chữ cái
    fun getCharacters(type: AlphabetType): List<JapaneseCharacter> {
        return when (type) {
            AlphabetType.HIRAGANA -> hiragana
            AlphabetType.KATAKANA -> katakana
            AlphabetType.KANJI -> kanji // You'll need to define this list
        }
    }
    
    // Hàm tìm kiếm ký tự theo romanization
    fun findByRomanization(romanization: String, type: AlphabetType): List<JapaneseCharacter> {
        val characters = when (type) {
            AlphabetType.HIRAGANA -> hiragana
            AlphabetType.KATAKANA -> katakana
            AlphabetType.KANJI -> kanji // You'll need to define this list
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
            AlphabetType.KANJI -> kanji
        }
        
        // Nếu là Kanji, phân loại theo chủ đề
        if (type == AlphabetType.KANJI) {
            val map = mutableMapOf<String, MutableList<JapaneseCharacter>>()
            
            // Nhóm Kanji theo chủ đề
            map["Thời gian cơ bản"] = characters.filter { 
                it.character in listOf("日", "月", "年", "時") 
            }.toMutableList()
            
            map["Con người và gia đình"] = characters.filter { 
                it.character in listOf("人", "父", "母", "子") 
            }.toMutableList()
            
            map["Số đếm"] = characters.filter { 
                it.character in listOf("一", "二", "三", "四", "五") 
            }.toMutableList()
            
            map["Học tập"] = characters.filter { 
                it.character in listOf("本", "学", "校", "先") 
            }.toMutableList()
            
            map["Công việc và xã hội"] = characters.filter { 
                it.character in listOf("会", "社", "仕", "事") 
            }.toMutableList()
            
            map["Địa điểm"] = characters.filter { 
                it.character in listOf("国", "家", "駅", "店") 
            }.toMutableList()
            
            map["Hoạt động"] = characters.filter { 
                it.character in listOf("行", "来", "見", "聞") 
            }.toMutableList()
            
            map["Tính chất"] = characters.filter { 
                it.character in listOf("新", "古", "多", "少") 
            }.toMutableList()
            
            map["Cảm xúc và tính cách"] = characters.filter { 
                it.character in listOf("愛", "楽", "苦", "悲") 
            }.toMutableList()
            
            map["Thiên nhiên"] = characters.filter { 
                it.character in listOf("山", "川", "海", "空") 
            }.toMutableList()
            
            map["Thời tiết"] = characters.filter { 
                it.character in listOf("雨", "雪", "風", "雲") 
            }.toMutableList()
            
            map["Hoạt động hàng ngày"] = characters.filter { 
                it.character in listOf("食", "飲", "寝", "働") 
            }.toMutableList()
            
            map["Giao thông"] = characters.filter { 
                it.character in listOf("車", "道", "橋", "駐") 
            }.toMutableList()
            
            map["Thời gian nâng cao"] = characters.filter { 
                it.character in listOf("朝", "昼", "夜", "週") 
            }.toMutableList()
            
            return map
        }
        
        // Xử lý cho Hiragana và Katakana
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