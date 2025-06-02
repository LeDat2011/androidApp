# Cấu Trúc Database

```json
{
  "app_data": {
    "users": {
      "[user_id]": {
        "profile": {
          "age": Number,
          "currentLevel": String,
          "name": String,
          "targetLevel": String
        },
        "progress": {
          "daysActive": Number,
          "lessonsCompleted": Number,
          "streak": Number,
          "wordsLearned": Number
        },
        "settings": {
          "studyTimeMinutes": Number
        }
      }
    },
    "vocabulary": {
      "[category]": {  // ANIMALS, COLORS, DAILY_LIFE, FAMILY, FOOD, NUMBERS, TIME, TRANSPORTATION, WEATHER
        "[level]": {   // N4, N5
          "[vocab_id]": {
            "example": String,
            "japanese": String,
            "reading": String,
            "vietnamese": String
          }
        }
      }
    }
  }
}
```

## Chi Tiết Cấu Trúc

### Users Collection
- **[user_id]**: Định danh người dùng từ Firebase Authentication
  - **profile**:
    - `age`: Tuổi người dùng
    - `currentLevel`: Trình độ hiện tại (N5, N4, N3, N2, N1)
    - `name`: Tên người dùng
    - `targetLevel`: Mục tiêu trình độ
  - **progress**:
    - `daysActive`: Số ngày hoạt động
    - `lessonsCompleted`: Số bài học đã hoàn thành
    - `streak`: Số ngày học liên tiếp
    - `wordsLearned`: Số từ vựng đã học
  - **settings**:
    - `studyTimeMinutes`: Thời gian học mỗi ngày (phút)

### Vocabulary Collection
- **[category]**: Danh mục từ vựng
  - ANIMALS: Động vật
  - COLORS: Màu sắc
  - DAILY_LIFE: Cuộc sống hàng ngày
  - FAMILY: Gia đình
  - FOOD: Thức ăn
  - NUMBERS: Số đếm
  - TIME: Thời gian
  - TRANSPORTATION: Phương tiện giao thông
  - WEATHER: Thời tiết
  
  - **[level]**: Cấp độ JLPT
    - N4: Trung cấp cơ bản
    - N5: Sơ cấp
    
    - **[vocab_id]**: ID của từ vựng
      - `example`: Ví dụ sử dụng
      - `japanese`: Từ vựng tiếng Nhật
      - `reading`: Cách đọc
      - `vietnamese`: Nghĩa tiếng Việt

## Quy Ước Đặt Tên
- **user_id**: Sử dụng ID từ Firebase Authentication
- **category**: Viết hoa và phân tách bằng gạch dưới
- **level**: Định dạng "N" + số (N5, N4, etc.)
- **vocab_id**: Định dạng "vocab" + số (vocab1, vocab2, etc.) 