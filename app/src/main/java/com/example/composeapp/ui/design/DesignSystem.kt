package com.example.composeapp.ui.design

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

/**
 * Hệ thống thiết kế thống nhất cho ứng dụng học tiếng Nhật
 * 
 * QUY TẮC THIẾT KẾ:
 * 1. Tất cả các màn hình phải tuân theo DesignSystem này
 * 2. Sử dụng Spacing, Shapes, Elevation đã định nghĩa
 * 3. Áp dụng Typography và Color scheme nhất quán
 * 4. Tất cả animations phải mượt mà và có timing thống nhất
 */

// ========== SPACING SYSTEM ==========
@Immutable
data class AppSpacing(
    val xs: Dp = 4.dp,      // 4dp - Spacing rất nhỏ
    val sm: Dp = 8.dp,      // 8dp - Spacing nhỏ
    val md: Dp = 16.dp,     // 16dp - Spacing trung bình (default)
    val lg: Dp = 24.dp,     // 24dp - Spacing lớn
    val xl: Dp = 32.dp,     // 32dp - Spacing rất lớn
    val xxl: Dp = 48.dp     // 48dp - Spacing cực lớn
)

// ========== SHAPE SYSTEM ==========
@Immutable
data class AppShapes(
    val small: RoundedCornerShape = RoundedCornerShape(8.dp),    // Small radius
    val medium: RoundedCornerShape = RoundedCornerShape(12.dp),  // Medium radius (default)
    val large: RoundedCornerShape = RoundedCornerShape(16.dp),   // Large radius
    val extraLarge: RoundedCornerShape = RoundedCornerShape(24.dp), // Extra large radius
    val circular: RoundedCornerShape = RoundedCornerShape(50)    // Circular
)

// ========== ELEVATION SYSTEM ==========
@Immutable
data class AppElevation(
    val none: Dp = 0.dp,        // Không có shadow
    val low: Dp = 2.dp,         // Shadow thấp
    val medium: Dp = 4.dp,      // Shadow trung bình
    val high: Dp = 8.dp,        // Shadow cao
    val extraHigh: Dp = 16.dp   // Shadow rất cao
)

// ========== ANIMATION SYSTEM ==========
@Immutable
data class AppAnimation(
    val short: Int = 200,       // Animation ngắn
    val medium: Int = 300,      // Animation trung bình
    val long: Int = 500,        // Animation dài
    val extraLong: Int = 1000   // Animation rất dài
)

// ========== COMPONENT STYLES ==========
@Immutable
data class AppComponentStyles(
    val buttonHeight: Dp = 56.dp,           // Chiều cao button chuẩn
    val textFieldHeight: Dp = 56.dp,        // Chiều cao text field chuẩn
    val cardMinHeight: Dp = 80.dp,          // Chiều cao tối thiểu của card
    val iconSize: Dp = 24.dp,               // Kích thước icon chuẩn
    val avatarSize: Dp = 120.dp,            // Kích thước avatar
    val topBarHeight: Dp = 64.dp,           // Chiều cao top bar
    val bottomBarHeight: Dp = 80.dp         // Chiều cao bottom bar
)

// ========== DESIGN SYSTEM OBJECT ==========
@Immutable
data class DesignSystem(
    val spacing: AppSpacing = AppSpacing(),
    val shapes: AppShapes = AppShapes(),
    val elevation: AppElevation = AppElevation(),
    val animation: AppAnimation = AppAnimation(),
    val components: AppComponentStyles = AppComponentStyles()
)

// ========== COMPOSITION LOCAL ==========
val LocalDesignSystem = staticCompositionLocalOf { 
    DesignSystem(
        spacing = AppSpacing(),
        shapes = AppShapes(),
        elevation = AppElevation(),
        animation = AppAnimation(),
        components = AppComponentStyles()
    )
}

// ========== COMPOSABLE EXTENSIONS ==========
@Composable
fun DesignSystem(): DesignSystem = LocalDesignSystem.current

// Extension functions for easy access
@Composable
fun spacing(): AppSpacing = LocalDesignSystem.current.spacing

@Composable
fun shapes(): AppShapes = LocalDesignSystem.current.shapes

@Composable
fun elevation(): AppElevation = LocalDesignSystem.current.elevation

@Composable
fun animation(): AppAnimation = LocalDesignSystem.current.animation

@Composable
fun components(): AppComponentStyles = LocalDesignSystem.current.components

// ========== GRADIENT SYSTEM ==========
object AppGradients {
    val primaryGradient = listOf(
        Color(0xFFFF5B94),  // Pink
        Color(0xFF8441A4)   // Purple
    )
    
    val secondaryGradient = listOf(
        Color(0xFF42A5F5),  // Blue
        Color(0xFF26C6DA)   // Teal
    )
    
    val successGradient = listOf(
        Color(0xFF66BB6A),  // Green
        Color(0xFF4CAF50)   // Green Dark
    )
    
    val warningGradient = listOf(
        Color(0xFFFFCA28),  // Amber
        Color(0xFFFF7043)   // Orange
    )
    
    val errorGradient = listOf(
        Color(0xFFEF5350),  // Red Light
        Color(0xFFE53935)   // Red
    )
}

// ========== CATEGORY COLORS ==========
object CategoryColors {
    val animals = Color(0xFF42A5F5)      // Blue
    val food = Color(0xFFFF7043)         // Orange
    val transportation = Color(0xFF66BB6A) // Green
    val family = Color(0xFFAB47BC)       // Purple
    val weather = Color(0xFF26C6DA)      // Teal
    val dailyLife = Color(0xFFFFCA28)    // Amber
    val colors = Color(0xFF78909C)       // Blue Grey
    val numbers = Color(0xFFEC407A)      // Pink
    val time = Color(0xFF8BC34A)         // Light Green
    val misc = Color(0xFF78909C)         // Blue Grey
}

// ========== CARD STYLES ==========
@Composable
fun CardStyles() = object {
    val default = CardDefaults.cardColors()
    val elevated = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.95f)
    )
    val primary = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.primaryContainer
    )
    val secondary = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.secondaryContainer
    )
    val tertiary = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.tertiaryContainer
    )
    val error = CardDefaults.cardColors(
        containerColor = MaterialTheme.colorScheme.errorContainer
    )
}

// ========== BUTTON STYLES ==========
@Composable
fun ButtonStyles() = object {
    val primary = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.primary
    )
    val secondary = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.secondary
    )
    val tertiary = ButtonDefaults.buttonColors(
        containerColor = MaterialTheme.colorScheme.tertiary
    )
    val outline = ButtonDefaults.outlinedButtonColors()
    val text = ButtonDefaults.textButtonColors()
}
