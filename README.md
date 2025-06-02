# Ứng Dụng Học Tiếng Nhật

Ứng dụng học tiếng Nhật được phát triển bằng Kotlin và Jetpack Compose, giúp người dùng học tiếng Nhật một cách hiệu quả thông qua các tính năng tương tác.

## Tính Năng Chính

- 🏠 **Màn hình chính**: Hiển thị tổng quan và điều hướng nhanh đến các chức năng
- 📚 **Học tập**: 
  - Flashcard để học từ vựng
  - Bài tập ngữ pháp
  - Luyện tập Kanji
- ✍️ **Kiểm tra**: 
  - Quiz đa dạng theo chủ đề
  - Theo dõi tiến độ học tập
- 👤 **Hồ sơ cá nhân**: 
  - Quản lý thông tin cá nhân
  - Theo dõi mục tiêu học tập
  - Xem lịch sử học tập

## Công Nghệ Sử Dụng

- **Kotlin**: Ngôn ngữ lập trình chính
- **Jetpack Compose**: Framework UI hiện đại
- **Material Design 3**: Thiết kế giao diện
- **Firebase**: 
  - Authentication: Xác thực người dùng
  - Firestore: Lưu trữ dữ liệu
  - Storage: Lưu trữ hình ảnh và tài liệu

## Yêu Cầu Hệ Thống

- Android Studio Hedgehog | 2023.1.1
- Kotlin 1.9.0
- Gradle 8.0
- minSdkVersion: 24
- targetSdkVersion: 34

## Cài Đặt

1. Clone repository:
```bash
git clone [repository-url]
```

2. Mở project trong Android Studio

3. Sync Gradle và cài đặt dependencies

4. Chạy ứng dụng trên thiết bị hoặc máy ảo Android

## Cấu Trúc Project

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/composeapp/
│   │   │   ├── components/       # Các component tái sử dụng
│   │   │   ├── models/          # Data models
│   │   │   ├── navigation/      # Navigation và routing
│   │   │   ├── screens/         # Các màn hình chính
│   │   │   ├── ui/theme/        # Theme và styling
│   │   │   ├── utils/          # Utility functions
│   │   │   └── viewmodels/     # ViewModels
│   │   └── res/                # Resources
└── build.gradle                # Project configuration
```

## Đóng Góp

Nếu bạn muốn đóng góp vào dự án, hãy:

1. Fork repository
2. Tạo branch mới (`git checkout -b feature/AmazingFeature`)
3. Commit thay đổi (`git commit -m 'Add some AmazingFeature'`)
4. Push to branch (`git push origin feature/AmazingFeature`)
5. Tạo Pull Request

## License

Distributed under the MIT License. See `LICENSE` for more information.

## Liên Hệ

Your Name - [@yourtwitter](https://twitter.com/yourtwitter) - email@example.com

Project Link: [https://github.com/yourusername/repo_name](https://github.com/yourusername/repo_name)
