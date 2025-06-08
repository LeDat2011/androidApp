# Cấu Trúc Cơ Sở Dữ Liệu - Ứng Dụng Học Tiếng Nhật

Tài liệu này mô tả cấu trúc dữ liệu của ứng dụng học tiếng Nhật, bao gồm Firebase Realtime Database, các mô hình dữ liệu, và mối quan hệ giữa chúng.

## Firebase Realtime Database

### Cấu Trúc Tổng Thể

```
app_data/
|-- users/
|   |-- {userId}/
|   |   |-- profile/
|   |   |   |-- name: String
|   |   |   |-- age: Number
|   |   |   |-- currentLevel: String
|   |   |   |-- targetLevel: String
|   |   |   |-- avatarUrl: String
|   |   |   |-- registrationDate: Number (timestamp)
|   |   |
|   |   |-- progress/
|   |   |   |-- streak: Number
|   |   |   |-- wordsLearned: Number
|   |   |   |-- lessonsCompleted: Number
|   |   |   |-- daysActive: Number
|   |   |   |-- lastActiveDate: Number (timestamp)
|   |   |
|   |   |-- settings/
|   |   |   |-- studyTimeMinutes: Number
|   |   |
|   |   |-- learning/
|   |       |-- vocabulary/
|   |       |   |-- {wordId}: Boolean
|   |       |
|   |       |-- completedLessons/
|   |           |-- {lessonId}: Number (timestamp)
|-- vocabulary/
|   |-- {wordId}/
|   |   |-- japanese: String
|   |   |-- reading: String
|   |   |-- vietnamese: String
|   |   |-- level: String (N5, N4, N3, N2, N1)
|   |   |-- category: String[]
|   |   |-- exampleSentences: Array
|   |       |-- 0/
|   |       |   |-- japanese: String
|   |       |   |-- vietnamese: String
|-- lessons/
|   |-- {lessonId}/
|   |   |-- title: String
|   |   |-- description: String
|   |   |-- level: String
|   |   |-- category: String
|   |   |-- order: Number
|   |   |-- vocabularyIds: Array<String>
|   |   |-- grammarPoints: Array
|   |       |-- 0/
|   |       |   |-- rule: String
|   |       |   |-- explanation: String
|   |       |   |-- examples: Array
|-- categories/
    |-- {categoryId}/
        |-- name: String
        |-- description: String
        |-- iconUrl: String
|-- quizzes/
    |-- {quizId}/
        |-- title: String
        |-- description: String
        |-- level: String (N5-N1)
        |-- category: String
        |-- timeLimit: Number (seconds)
        |-- questions: {
            {questionId}: {
                type: String (MULTIPLE_CHOICE, TRUE_FALSE, FILL_BLANK, MATCHING, AUDIO_CHOICE)
                question: String
                options: {
                    a: String
                    b: String
                    c: String
                    d: String
                }
                correctAnswer: String
                explanation: String
                points: Number
                audioUrl: String (optional)
            }
        }

|-- user_quiz_progress/
    |-- {userId}/
        |-- completed_quizzes/
            |-- {quizId}/
                |-- score: Number
                |-- completedAt: Timestamp
                |-- timeTaken: Number (seconds)
                |-- answers: {
                    {questionId}: {
                        selectedAnswer: String
                        isCorrect: Boolean
                        timeSpent: Number (seconds)
                    }
                }
        |-- quiz_statistics/
            |-- totalQuizzesTaken: Number
            |-- averageScore: Number
            |-- bestScore: Number
            |-- totalTimePlayed: Number
            |-- categoryProgress: {
                vocabulary: Number (percentage)
                grammar: Number (percentage)
                kanji: Number (percentage)
                listening: Number (percentage)
            }
            |-- levelProgress: {
                N5: Number (percentage)
                N4: Number (percentage)
                N3: Number (percentage)
                N2: Number (percentage)
                N1: Number (percentage)
            }
```

## Mô Hình Dữ Liệu (Kotlin)

### UserProfileData

```kotlin
data class UserProfileData(
    val userId: String = "", // Firebase Auth UID
    val name: String = "",
    val age: Int = 0,
    val currentLevel: String = "",
    val targetLevel: String = "",
    val studyTimeMinutes: Int = 30,
    val streak: Int = 0,
    val wordsLearned: Int = 0,
    val lessonsCompleted: Int = 0,
    val daysActive: Int = 0,
    val lastActiveDate: Long = System.currentTimeMillis(),
    val registrationDate: Long = System.currentTimeMillis(),
    val avatarUrl: String? = null
)
```

### VocabularyItem

```kotlin
data class VocabularyItem(
    val id: String = "",
    val japanese: String = "",
    val reading: String = "",
    val vietnamese: String = "",
    val level: String = "",
    val categories: List<String> = emptyList(),
    val exampleSentences: List<ExampleSentence> = emptyList()
)

data class ExampleSentence(
    val japanese: String = "",
    val vietnamese: String = ""
)
```

### Lesson

```kotlin
data class Lesson(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val level: String = "",
    val category: String = "",
    val order: Int = 0,
    val vocabularyIds: List<String> = emptyList(),
    val grammarPoints: List<GrammarPoint> = emptyList()
)

data class GrammarPoint(
    val rule: String = "",
    val explanation: String = "",
    val examples: List<String> = emptyList()
)
```

### Category

```kotlin
data class Category(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val iconUrl: String = ""
)
```

## Truy Cập Dữ Liệu

### Ví dụ: Tải thông tin người dùng

```kotlin
// Truy cập thông tin hồ sơ người dùng
val userProfileRef = database.child("app_data").child("users").child(userId).child("profile")
userProfileRef.addValueEventListener(object : ValueEventListener {
    override fun onDataChange(snapshot: DataSnapshot) {
        // Xử lý dữ liệu
    }
    
    override fun onCancelled(error: DatabaseError) {
        // Xử lý lỗi
    }
})
```

### Ví dụ: Lưu tiến độ học tập

```kotlin
// Cập nhật tiến độ học tập
val userProgressRef = database.child("app_data").child("users").child(userId).child("progress")
val updates = hashMapOf<String, Any>(
    "wordsLearned" to 120,
    "lessonsCompleted" to 15
)
userProgressRef.updateChildren(updates)
```

### Ví dụ: Đánh dấu từ vựng đã học

```kotlin
// Đánh dấu từ vựng đã học
val wordRef = database.child("app_data").child("users").child(userId)
    .child("learning").child("vocabulary").child(wordId)
wordRef.setValue(true)
```

## Quy Tắc Bảo Mật Firebase

```json
{
  "rules": {
    "app_data": {
      "users": {
        "$uid": {
          ".read": "$uid === auth.uid",
          ".write": "$uid === auth.uid",
          "profile": {
            ".validate": "newData.hasChildren(['name', 'age', 'currentLevel', 'targetLevel'])"
          },
          "progress": {
            ".validate": "newData.hasChildren(['streak', 'wordsLearned', 'lessonsCompleted'])"
          }
        }
      },
      "vocabulary": {
        ".read": "auth !== null",
        ".write": "auth !== null && root.child('app_data/users').child(auth.uid).child('admin').exists()"
      },
      "lessons": {
        ".read": "auth !== null",
        ".write": "auth !== null && root.child('app_data/users').child(auth.uid).child('admin').exists()"
      },
      "categories": {
        ".read": "auth !== null",
        ".write": "auth !== null && root.child('app_data/users').child(auth.uid).child('admin').exists()"
      }
    }
  }
}
```

## Mối Quan Hệ Dữ Liệu

1. **User - Vocabulary**: Mối quan hệ nhiều-nhiều
   - Người dùng có thể học nhiều từ vựng
   - Từ vựng có thể được học bởi nhiều người dùng
   - Liên kết qua `users/{userId}/learning/vocabulary/{wordId}` 

2. **User - Lessons**: Mối quan hệ nhiều-nhiều
   - Người dùng có thể hoàn thành nhiều bài học
   - Bài học có thể được học bởi nhiều người dùng
   - Liên kết qua `users/{userId}/learning/completedLessons/{lessonId}`

3. **Lessons - Vocabulary**: Mối quan hệ một-nhiều
   - Mỗi bài học chứa nhiều từ vựng
   - Liên kết qua trường `vocabularyIds` trong đối tượng Lesson

4. **Categories - Lessons**: Mối quan hệ một-nhiều
   - Mỗi danh mục chứa nhiều bài học
   - Liên kết qua trường `category` trong đối tượng Lesson

## Các Trình Độ Tiếng Nhật

```kotlin
object JapaneseLevel {
    val levels = listOf(
        "Mới bắt đầu",
        "N5",
        "N4",
        "N3", 
        "N2",
        "N1"
    )
}
```

## Các Tùy Chọn Thời Gian Học

```kotlin
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
``` 