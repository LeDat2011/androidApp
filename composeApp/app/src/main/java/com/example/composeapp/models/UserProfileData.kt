package com.example.composeapp.models

data class UserProfileData(
    val userId: String = "", // Firebase Auth UID
    val name: String? = null,
    val age: Int? = null,
    val currentLevel: JapaneseLevel? = null,
    val targetLevel: JapaneseLevel? = null,
    val studyTimeMinutes: Int? = null,
    val streak: Int = 0,
    val wordsLearned: Int = 0,
    val lessonsCompleted: Int = 0,
    val daysActive: Int = 0,
    val lastActiveDate: Long? = null,
    val registrationDate: Long? = null,
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