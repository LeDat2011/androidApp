# Ứng Dụng Học Tiếng Nhật - JapaneseApp

Ứng dụng học tiếng Nhật được xây dựng bằng Jetpack Compose với Firebase.

## Tài Liệu

Thư mục này chứa các tài liệu về thiết kế và cấu trúc ứng dụng:

- [DATABASE_STRUCTURE.md](./DATABASE_STRUCTURE.md): Mô tả cấu trúc cơ sở dữ liệu Firebase
- [FIREBASE_RULES.md](./FIREBASE_RULES.md): Quy tắc bảo mật cho Firebase Realtime Database

## Cấu Trúc Ứng Dụng

Ứng dụng được phát triển theo mô hình MVVM (Model-View-ViewModel) với các thành phần chính:

### 1. Màn Hình (Screens)

- **LoginScreen**: Đăng nhập với email/password thông qua Firebase Authentication
- **RegisterScreen**: Đăng ký tài khoản mới
- **UserProfileSetupScreen**: Thiết lập hồ sơ người dùng sau khi đăng ký
- **HomeScreen**: Màn hình chính hiển thị các bài học và chức năng
- **ProfileScreen**: Hiển thị thông tin người dùng và tiến độ học tập
- **EditProfileScreen**: Chỉnh sửa thông tin cá nhân

### 2. ViewModel

- **AuthViewModel**: Quản lý xác thực người dùng (đăng nhập, đăng ký, đăng xuất)
- **UserProfileViewModel**: Quản lý hồ sơ người dùng và tương tác với Firebase Realtime Database
- **HomeViewModel**: Quản lý dữ liệu cho màn hình chính
- **LearningViewModel**: Quản lý dữ liệu học tập, bài học và từ vựng

### 3. Models

- **UserProfileData**: Dữ liệu hồ sơ người dùng
- **VocabularyItem**: Dữ liệu từ vựng
- **Lesson**: Dữ liệu bài học
- **Category**: Danh mục nội dung học

### 4. Navigation

- **AppNavigation**: Quản lý điều hướng giữa các màn hình trong ứng dụng

## Công Nghệ Sử Dụng

- **Jetpack Compose**: UI toolkit hiện đại cho Android 
- **ViewModel & StateFlow**: Quản lý trạng thái ứng dụng
- **Firebase Authentication**: Quản lý xác thực người dùng
- **Firebase Realtime Database**: Lưu trữ dữ liệu ứng dụng
- **Coroutines**: Xử lý bất đồng bộ

## Cơ Sở Dữ Liệu

Xem chi tiết cấu trúc cơ sở dữ liệu tại [DATABASE_STRUCTURE.md](./DATABASE_STRUCTURE.md)

## Tính Năng Chính

1. **Xác thực người dùng**: Đăng ký, đăng nhập, đăng xuất
2. **Thiết lập hồ sơ**: Cá nhân hóa trải nghiệm học tập
3. **Bài học từ vựng**: Theo cấp độ từ N5 đến N1
4. **Theo dõi tiến độ**: Số từ học được, bài học hoàn thành, streak
5. **Cá nhân hóa**: Điều chỉnh mục tiêu và thời gian học

## Hướng Phát Triển

1. **Flashcards**: Chức năng thẻ ghi nhớ học từ vựng
2. **Quizzes**: Bài kiểm tra để đánh giá tiến độ
3. **Offline Mode**: Hỗ trợ học tập khi không có mạng
4. **Gamification**: Thêm yếu tố trò chơi để tăng động lực học tập

## Cài Đặt

```
git clone <repository-url>
cd composeapp
```

## Cấu Hình Firebase

1. Tạo dự án Firebase mới tại https://console.firebase.google.com/
2. Thêm ứng dụng Android vào dự án
3. Tải file google-services.json và đặt vào thư mục app/
4. Bật Firebase Authentication và Realtime Database 