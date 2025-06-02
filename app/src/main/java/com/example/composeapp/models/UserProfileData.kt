package com.example.composeapp.models

data class UserProfileData(
    val userId: String = "",
    val name: String = "",
    val age: Int = 0,
    val currentLevel: JapaneseLevel = JapaneseLevel.BEGINNER,
    val targetLevel: JapaneseLevel = JapaneseLevel.N5,
    val studyTimeMinutes: Int = 30,
    val streak: Int = 0,
    val wordsLearned: Int = 0,
    val lessonsCompleted: Int = 0,
    val daysActive: Int = 0,
    val registrationDate: Long? = null,
    val lastActiveDate: Long? = null,
    val avatarUrl: String? = null
)

// Các tùy chọn thời gian học
object StudyTimeOptions {
    val options = listOf(
        15 to "15 phút",
        30 to "30 phút",
        45 to "45 phút",
        60 to "1 giờ",
        90 to "1 giờ 30 phút",
        120 to "2 giờ"
    )
} 