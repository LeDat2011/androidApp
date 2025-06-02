# Japanese Learning App

Ứng dụng học tiếng Nhật dành cho người Việt, được xây dựng bằng Jetpack Compose với giao diện người dùng hiện đại và tương tác hấp dẫn.

## Tính năng

- **Flashcards**: Học từ vựng thông qua các thẻ lật với hiệu ứng 3D mượt mà
- **Kiểm tra trắc nghiệm**: Kiểm tra kiến thức với các câu hỏi trắc nghiệm tương tác
- **Theo dõi tiến độ**: Theo dõi quá trình học tập thông qua biểu đồ và thống kê
- **Hồ sơ người dùng**: Quản lý thông tin cá nhân, thành tích và thống kê học tập
- **Điều hướng ngang**: Chuyển đổi giữa các màn hình bằng cách vuốt ngang
- **Cuộn dọc**: Hỗ trợ cuộn dọc cho tất cả các màn hình với nội dung phong phú

## Công nghệ sử dụng

- Kotlin
- Jetpack Compose
- Material Design 3
- Accompanist Pager (Google)
- AndroidX Core KTX
- AndroidX Credentials

## Cấu trúc dự án

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/composeapp/
│   │   │   ├── components/           # Các thành phần UI có thể tái sử dụng
│   │   │   │   ├── FlashcardComponent.kt
│   │   │   │   ├── QuizComponent.kt
│   │   │   │   ├── ProgressComponent.kt
│   │   │   │   └── ProfileComponent.kt
│   │   │   ├── models/               # Các mô hình dữ liệu
│   │   │   │   └── Models.kt
│   │   │   ├── screens/              # Các màn hình chính của ứng dụng
│   │   │   │   ├── HomeScreen.kt
│   │   │   │   ├── FlashcardScreen.kt
│   │   │   │   ├── QuizScreen.kt
│   │   │   │   ├── ProgressScreen.kt
│   │   │   │   └── ProfileScreen.kt
│   │   │   ├── navigation/           # Điều hướng ứng dụng
│   │   │   │   └── Navigation.kt
│   │   │   ├── theme/                # Chủ đề và styling
│   │   │   │   └── Theme.kt
│   │   │   └── MainActivity.kt       # Điểm khởi chạy ứng dụng
│   │   └── res/                      # Tài nguyên Android
│   └── ...
└── ...
```

## Các tính năng UI nâng cao

### Hiệu ứng lật thẻ 3D

Ứng dụng sử dụng hiệu ứng lật thẻ 3D với các tính năng:
- Góc nhìn 3D thực tế thông qua `cameraDistance`
- Hiệu ứng đàn hồi và co giãn khi lật
- Bóng động khi lật
- Phản hồi xúc giác khi tương tác

### Hiệu ứng nút và thành phần

- Hiệu ứng scale khi nhấn
- Hiệu ứng đàn hồi cho hoạt ảnh
- Animation cho thay đổi màu sắc
- Hiệu ứng thay đổi độ cao

## Cài đặt

1. Clone repository:
```
git clone https://github.com/your-username/japanese-learning-app.git
```

2. Mở dự án trong Android Studio.

3. Chạy ứng dụng trên thiết bị hoặc giả lập.

## Yêu cầu

- Android 7.0 (API level 26) trở lên
- Android Studio Arctic Fox trở lên
- Kotlin 1.6.10 trở lên

## Giấy phép

```
MIT License

Copyright (c) 2023 Your Name

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
``` 