package com.example.composeapp.models

data class UserProfileData(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val age: Int = 0,
    val currentLevel: String = "N5", // JLPT Level
    val targetLevel: String = "N3", // JLPT Level
    val studyTimeMinutes: Int = 30,
    val streak: Int = 0,
    val wordsLearned: Int = 0,
    val lessonsCompleted: Int = 0,
    val daysActive: Int = 0,
    val registrationDate: Long? = null,
    val lastActiveDate: Long? = null,
    val avatarUrl: String? = null,
    val totalExperience: Long = 0L,
    val unlockedLevels: List<String> = listOf("N5"), // Danh sách level đã unlock
    val levelProgress: Map<String, Map<String, Any>> = emptyMap(), // Progress của từng level/category
    val achievements: List<String> = emptyList() // Danh sách achievement đã đạt được
) {
    // Helper methods to convert between String and JLPTLevel enum
    fun getCurrentLevelEnum(): JLPTLevel {
        return when (currentLevel) {
            "N1" -> JLPTLevel.N1
            "N2" -> JLPTLevel.N2
            "N3" -> JLPTLevel.N3
            "N4" -> JLPTLevel.N4
            "N5" -> JLPTLevel.N5
            else -> JLPTLevel.N5
        }
    }
    
    fun getTargetLevelEnum(): JLPTLevel {
        return when (targetLevel) {
            "N1" -> JLPTLevel.N1
            "N2" -> JLPTLevel.N2
            "N3" -> JLPTLevel.N3
            "N4" -> JLPTLevel.N4
            "N5" -> JLPTLevel.N5
            else -> JLPTLevel.N3
        }
    }
    
    fun getUnlockedLevelsEnum(): Set<JLPTLevel> {
        return unlockedLevels.mapNotNull { levelStr ->
            when (levelStr) {
                "N1" -> JLPTLevel.N1
                "N2" -> JLPTLevel.N2
                "N3" -> JLPTLevel.N3
                "N4" -> JLPTLevel.N4
                "N5" -> JLPTLevel.N5
                else -> null
            }
        }.toSet()
    }
    
    companion object {
        fun fromEnum(
            userId: String = "",
            name: String = "",
            email: String = "",
            age: Int = 0,
            currentLevel: JLPTLevel = JLPTLevel.N5,
            targetLevel: JLPTLevel = JLPTLevel.N3,
            studyTimeMinutes: Int = 30,
            streak: Int = 0,
            wordsLearned: Int = 0,
            lessonsCompleted: Int = 0,
            daysActive: Int = 0,
            registrationDate: Long? = null,
            lastActiveDate: Long? = null,
            avatarUrl: String? = null,
            totalExperience: Long = 0L,
            unlockedLevels: Set<JLPTLevel> = setOf(JLPTLevel.N5),
            achievements: List<String> = emptyList()
        ): UserProfileData {
            return UserProfileData(
                userId = userId,
                name = name,
                email = email,
                age = age,
                currentLevel = currentLevel.name,
                targetLevel = targetLevel.name,
                studyTimeMinutes = studyTimeMinutes,
                streak = streak,
                wordsLearned = wordsLearned,
                lessonsCompleted = lessonsCompleted,
                daysActive = daysActive,
                registrationDate = registrationDate,
                lastActiveDate = lastActiveDate,
                avatarUrl = avatarUrl,
                totalExperience = totalExperience,
                unlockedLevels = unlockedLevels.map { it.name },
                achievements = achievements
            )
        }
    }
}

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