# Hướng Dẫn Thiết Kế UI/UX - Ứng Dụng Học Tiếng Nhật

## 🎨 **NGUYÊN TẮC THIẾT KẾ CHUNG**

### 1. **Hệ Thống Thiết Kế Thống Nhất**
- **TUYỆT ĐỐI** sử dụng `DesignSystem.kt` cho tất cả components
- Không được tự ý tạo spacing, colors, shapes riêng
- Tất cả màn hình phải tuân theo cùng một phong cách thiết kế

### 2. **Phong Cách Thiết Kế**
- **Material Design 3** làm nền tảng
- **Gradient backgrounds** cho các section chính
- **Rounded corners** với radius thống nhất (8dp, 12dp, 16dp, 24dp)
- **Smooth animations** với timing nhất quán
- **Elevation system** rõ ràng (0dp, 2dp, 4dp, 8dp, 16dp)

## 📏 **HỆ THỐNG SPACING**

```kotlin
// Sử dụng spacing system đã định nghĩa
val spacing = spacing()
spacing.xs   // 4dp - Spacing rất nhỏ
spacing.sm   // 8dp - Spacing nhỏ  
spacing.md   // 16dp - Spacing trung bình (DEFAULT)
spacing.lg   // 24dp - Spacing lớn
spacing.xl   // 32dp - Spacing rất lớn
spacing.xxl  // 48dp - Spacing cực lớn
```

**Quy tắc:**
- Giữa các element: `spacing.sm` (8dp) hoặc `spacing.md` (16dp)
- Padding của card: `spacing.md` (16dp)
- Margin giữa các section: `spacing.lg` (24dp)

## 🔲 **HỆ THỐNG SHAPES**

```kotlin
val shapes = shapes()
shapes.small      // 8dp - Button, small cards
shapes.medium     // 12dp - Text fields, medium cards (DEFAULT)
shapes.large      // 16dp - Large cards, containers
shapes.extraLarge // 24dp - Special containers, modals
shapes.circular   // 50% - Avatars, floating buttons
```

## 🎯 **HỆ THỐNG ELEVATION**

```kotlin
val elevation = elevation()
elevation.none      // 0dp - Flat elements
elevation.low       // 2dp - Subtle separation
elevation.medium    // 4dp - Cards, buttons (DEFAULT)
elevation.high      // 8dp - Floating elements
elevation.extraHigh // 16dp - Modals, overlays
```

## 🌈 **HỆ THỐNG MÀU SẮC**

### **Primary Colors (Nhật Bản Theme)**
- **Primary**: Đỏ truyền thống Nhật Bản (#E53935)
- **Secondary**: Hoa anh đào (#F8BBD0) 
- **Tertiary**: Navy blue (#0D47A1)
- **Background**: Cream nhẹ (#FFF8E1)

### **Category Colors**
```kotlin
CategoryColors.animals         // Blue (#42A5F5)
CategoryColors.food           // Orange (#FF7043)
CategoryColors.transportation // Green (#66BB6A)
CategoryColors.family         // Purple (#AB47BC)
CategoryColors.weather        // Teal (#26C6DA)
CategoryColors.dailyLife      // Amber (#FFCA28)
```

### **Gradients**
```kotlin
AppGradients.primaryGradient   // Pink to Purple
AppGradients.secondaryGradient // Blue to Teal
AppGradients.successGradient   // Green tones
```

## 🧩 **COMPONENT STYLES**

### **Cards**
```kotlin
// Sử dụng CardStyles đã định nghĩa
CardStyles.default    // Card thông thường
CardStyles.elevated   // Card có elevation
CardStyles.primary    // Card với primary color
CardStyles.error      // Card lỗi
```

### **Buttons**
```kotlin
// Sử dụng ButtonStyles đã định nghĩa
ButtonStyles.primary   // Button chính
ButtonStyles.secondary // Button phụ
ButtonStyles.outline   // Button viền
ButtonStyles.text      // Text button
```

## 📱 **LAYOUT PATTERNS**

### **1. Màn Hình Chính (Home)**
- **Header**: Gradient background với greeting
- **Content**: LazyColumn với spacing.md
- **Cards**: Rounded corners (16dp), elevation medium
- **Grid**: 2 columns với spacing.md

### **2. Màn Hình Form (Login/Register)**
- **Container**: Full screen với padding lg
- **Fields**: OutlinedTextField với shape medium
- **Buttons**: Height 56dp, shape medium, full width
- **Error**: Card với error color, elevation low

### **3. Màn Hình Profile**
- **Header**: Gradient background với avatar
- **Stats**: Grid 2x2 với cards
- **Sections**: Cards với elevation medium

### **4. Navigation**
- **Bottom Bar**: Height 80dp, elevation low
- **Top Bar**: Height 64dp, elevation medium
- **Icons**: Size 24dp (default), 20dp (navigation)

## ✨ **ANIMATION GUIDELINES**

### **Timing**
```kotlin
val animation = animation()
animation.short    // 200ms - Hover effects, micro-interactions
animation.medium   // 300ms - Button press, card selection (DEFAULT)
animation.long     // 500ms - Page transitions
animation.extraLong // 1000ms - Loading, progress animations
```

### **Easing**
- **Standard**: `FastOutSlowInEasing` cho transitions
- **Enter/Exit**: `FadeIn` + `SlideIn` cho page transitions
- **Progress**: `LinearEasing` cho progress bars

## 📐 **RESPONSIVE DESIGN**

### **Breakpoints**
- **Small**: < 600dp - Single column
- **Medium**: 600dp - 840dp - Two columns
- **Large**: > 840dp - Three columns

### **Grid System**
```kotlin
// Sử dụng LazyVerticalGrid với GridCells
GridCells.Fixed(2)        // 2 columns (default)
GridCells.Adaptive(160.dp) // Responsive columns
```

## 🎭 **DARK MODE SUPPORT**

- **Tự động** theo system preference
- **Smooth transition** giữa light/dark mode
- **Consistent colors** trong cả hai mode
- **Proper contrast** đảm bảo accessibility

## ♿ **ACCESSIBILITY**

### **Colors**
- **Contrast ratio** tối thiểu 4.5:1
- **Color blind friendly** - không chỉ dựa vào màu sắc
- **Focus indicators** rõ ràng

### **Typography**
- **Minimum size** 14sp cho body text
- **Line height** 1.5x font size
- **Font weight** đủ tương phản

## 🚫 **NHỮNG ĐIỀU KHÔNG ĐƯỢC LÀM**

1. **KHÔNG** tự tạo spacing, colors, shapes riêng
2. **KHÔNG** sử dụng hardcoded values (8.dp, 16.dp)
3. **KHÔNG** tạo animation timing riêng
4. **KHÔNG** mix nhiều phong cách thiết kế
5. **KHÔNG** bỏ qua elevation system
6. **KHÔNG** sử dụng màu sắc không có trong palette

## ✅ **CHECKLIST TRƯỚC KHI SUBMIT**

- [ ] Sử dụng `DesignSystem.kt` cho tất cả components
- [ ] Spacing tuân theo hệ thống (xs, sm, md, lg, xl, xxl)
- [ ] Shapes sử dụng đúng radius (small, medium, large, extraLarge)
- [ ] Colors nằm trong palette đã định nghĩa
- [ ] Elevation phù hợp với hierarchy
- [ ] Animations có timing nhất quán
- [ ] Dark mode hoạt động tốt
- [ ] Accessibility được đảm bảo
- [ ] Responsive trên các kích thước màn hình

## 📚 **VÍ DỤ IMPLEMENTATION**

```kotlin
@Composable
fun StandardCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val spacing = spacing()
    val shapes = shapes()
    val elevation = elevation()
    
    Card(
        modifier = modifier.padding(spacing.md),
        shape = shapes.large,
        elevation = CardDefaults.cardElevation(defaultElevation = elevation.medium),
        colors = CardStyles.default
    ) {
        Column(
            modifier = Modifier.padding(spacing.md),
            content = content
        )
    }
}
```

---

**LƯU Ý QUAN TRỌNG**: Tài liệu này là **BẮT BUỘC** phải tuân theo. Mọi thay đổi thiết kế phải được thông qua và cập nhật vào `DesignSystem.kt` trước khi áp dụng.
