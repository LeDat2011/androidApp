package com.example.composeapp.models

enum class JapaneseLevel(val displayName: String) {
    N1("N1 - Cao cấp"),
    N2("N2 - Trung cấp cao"),
    N3("N3 - Trung cấp"),
    N4("N4 - Sơ cấp cao"),
    N5("N5 - Sơ cấp"),
    BEGINNER("Mới bắt đầu");

    companion object {
        fun getAvailableTargetLevels(currentLevel: JapaneseLevel): List<JapaneseLevel> {
            // Trả về danh sách các level cao hơn hoặc bằng level hiện tại
            return values().filter { it.ordinal <= currentLevel.ordinal }
        }
    }
} 