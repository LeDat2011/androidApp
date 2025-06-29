# Hướng dẫn đánh dấu từ vựng đã học trên Firebase

Tài liệu này hướng dẫn cách cấu trúc dữ liệu Firebase để lưu trữ thông tin về từ vựng đã học của người dùng.

## Cấu trúc dữ liệu

Khi người dùng đánh dấu một từ vựng đã học, thông tin này sẽ được lưu trữ trong Firebase với cấu trúc như sau:

```
users/
  ├── {userId}/
  │   ├── learning/
  │   │   ├── vocabulary/
  │   │   │   ├── {wordId}: true
```

Trong đó:
- `{userId}` là ID của người dùng (từ Firebase Authentication)
- `{wordId}` là ID của từ vựng (key của từ vựng trong Firebase)

## Cách triển khai

1. **Trong FlashcardViewModel**:
   - Phương thức `markWordAsLearned(wordId: String)` đã được cập nhật để lưu trữ ID từ vựng đã học
   - Khi người dùng nhấn nút đánh dấu đã học, phương thức này sẽ được gọi

2. **Trong FlashcardComponent**:
   - Đã thêm nút đánh dấu đã học với hai trạng thái: đã học và chưa học
   - Nút sẽ bị vô hiệu hóa sau khi từ vựng đã được đánh dấu là đã học

3. **Trong FlashcardLearningScreen**:
   - Đã thêm hàm xử lý để gọi `markWordAsLearned` khi người dùng nhấn nút đánh dấu đã học

## Cập nhật thống kê học tập

Khi một từ vựng được đánh dấu là đã học, các thống kê sau sẽ được cập nhật:

1. **Số từ vựng đã học**:
   - Được lưu trong `users/{userId}/progress/wordsLearned`
   - Được tăng lên mỗi khi một từ vựng mới được đánh dấu là đã học

2. **Thời gian học tập**:
   - Được lưu trong `users/{userId}/learning_stats/study_time/{date}`
   - Được cập nhật khi người dùng hoàn thành một bộ flashcard

3. **Streak (chuỗi ngày học liên tục)**:
   - Được lưu trong `users/{userId}/progress/streak`
   - Được cập nhật khi thời gian học tập trong ngày đạt mục tiêu

## Ví dụ dữ liệu

Ví dụ về dữ liệu từ vựng đã học của một người dùng:

```json
{
  "users": {
    "USER_ID_1": {
      "learning": {
        "vocabulary": {
          "vocab1": true,
          "vocab2": true,
          "vocab3": true
        }
      },
      "progress": {
        "wordsLearned": 3
      },
      "learning_stats": {
        "study_time": {
          "2023-5-20": 45
        }
      }
    }
  }
}
```

## Kiểm tra từ vựng đã học

Khi tải danh sách từ vựng, ứng dụng sẽ kiểm tra xem từ vựng nào đã được học bằng cách truy vấn Firebase:

```kotlin
private fun checkWordLearned(wordId: String, callback: (Boolean) -> Unit) {
    val currentUser = auth.currentUser
    if (currentUser == null) {
        callback(false)
        return
    }

    database.reference
        .child("users")
        .child(currentUser.uid)
        .child("learning")
        .child("vocabulary")
        .child(wordId)
        .addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                callback(snapshot.exists() && snapshot.getValue(Boolean::class.java) == true)
            }

            override fun onCancelled(error: DatabaseError) {
                callback(false)
            }
        })
}
```

## Quy tắc bảo mật Firebase

Để đảm bảo an toàn cho dữ liệu, bạn cần cập nhật quy tắc bảo mật Firebase:

```json
{
  "rules": {
    "users": {
      "$uid": {
        ".read": "$uid === auth.uid",
        ".write": "$uid === auth.uid",
        
        "learning": {
          ".read": "$uid === auth.uid",
          ".write": "$uid === auth.uid"
        },
        
        "learning_stats": {
          ".read": "$uid === auth.uid",
          ".write": "$uid === auth.uid"
        }
      }
    }
  }
}
``` 