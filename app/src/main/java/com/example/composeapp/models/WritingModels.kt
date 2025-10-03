package com.example.composeapp.models

/**
 * Model cho dữ liệu luyện viết ký tự
 */
data class WritingCharacter(
    val id: String = "",
    val character: String = "",           // Ký tự cần viết (あ, ア, 漢, etc.)
    val type: WritingType = WritingType.HIRAGANA,
    val level: String = "N5",             // Cấp độ JLPT
    val strokes: List<Stroke> = emptyList(), // Danh sách các nét vẽ
    val meaning: String? = null,          // Nghĩa (cho Kanji)
    val reading: String? = null,          // Cách đọc
    val examples: List<String> = emptyList() // Ví dụ sử dụng
)

/**
 * Model cho một nét vẽ
 */
data class Stroke(
    val id: String = "",
    val order: Int = 0,                   // Thứ tự nét (1, 2, 3...)
    val path: String = "",                // Đường dẫn SVG hoặc path data
    val startPoint: Point = Point(0f, 0f),
    val endPoint: Point = Point(0f, 0f),
    val direction: StrokeDirection = StrokeDirection.RIGHT,
    val type: StrokeType = StrokeType.HORIZONTAL
)

/**
 * Model cho điểm tọa độ
 */
data class Point(
    val x: Float = 0f,
    val y: Float = 0f
)

/**
 * Enum cho loại ký tự
 */
enum class WritingType {
    HIRAGANA,
    KATAKANA,
    KANJI
}

/**
 * Enum cho hướng nét vẽ
 */
enum class StrokeDirection {
    RIGHT,      // Phải
    LEFT,       // Trái
    DOWN,       // Xuống
    UP,         // Lên
    DIAGONAL_RIGHT_DOWN,  // Chéo xuống phải
    DIAGONAL_RIGHT_UP,    // Chéo lên phải
    DIAGONAL_LEFT_DOWN,   // Chéo xuống trái
    DIAGONAL_LEFT_UP,     // Chéo lên trái
    CURVE       // Nét cong
}

/**
 * Enum cho loại nét vẽ
 */
enum class StrokeType {
    HORIZONTAL,     // Nét ngang
    VERTICAL,       // Nét dọc
    DIAGONAL,       // Nét chéo
    CURVE,          // Nét cong
    HOOK,           // Nét móc
    DOT             // Chấm
}

/**
 * Model cho tiến độ luyện viết
 */
data class WritingProgress(
    val characterId: String = "",
    val userId: String = "",
    val completedStrokes: List<Int> = emptyList(), // Danh sách nét đã hoàn thành
    val totalAttempts: Int = 0,
    val successfulAttempts: Int = 0,
    val lastPracticeDate: Long = System.currentTimeMillis(),
    val masteryLevel: WritingMasteryLevel = WritingMasteryLevel.NEW
)

/**
 * Enum cho mức độ thành thạo
 */
enum class WritingMasteryLevel {
    NEW,            // Mới
    LEARNING,       // Đang học
    PRACTICING,     // Đang luyện tập
    MASTERED        // Đã thành thạo
}

/**
 * Model cho kết quả luyện viết
 */
data class WritingResult(
    val characterId: String = "",
    val userId: String = "",
    val completedAt: Long = System.currentTimeMillis(),
    val timeSpentSeconds: Int = 0,
    val strokesCompleted: Int = 0,
    val totalStrokes: Int = 0,
    val accuracy: Float = 0f,         // Độ chính xác (0.0 - 1.0)
    val mode: WritingMode = WritingMode.STROKE_BY_STROKE
)

/**
 * Enum cho chế độ luyện viết
 */
enum class WritingMode {
    STROKE_BY_STROKE,  // Viết theo từng nét
    FREE_WRITING       // Tự viết hoàn chỉnh
}

/**
 * Model cho cài đặt luyện viết
 */
data class WritingSettings(
    val showStrokeOrder: Boolean = true,    // Hiển thị thứ tự nét
    val showGuideLines: Boolean = true,     // Hiển thị đường kẻ
    val autoAdvance: Boolean = false,       // Tự động chuyển nét tiếp theo
    val soundEnabled: Boolean = true,       // Bật âm thanh
    val vibrationEnabled: Boolean = true,   // Bật rung
    val strokeWidth: Float = 3f,            // Độ dày nét vẽ
    val guideLineOpacity: Float = 0.3f,     // Độ trong suốt đường kẻ
    val templateDisplayMode: TemplateDisplayMode = TemplateDisplayMode.BOLD_BLACK, // Chế độ hiển thị nét mẫu
    val templateOpacity: Float = 0.4f,      // Độ trong suốt nét mẫu
    val showDirectionArrows: Boolean = true, // Hiển thị mũi tên hướng
    val animateTemplate: Boolean = true,    // Animation nét mẫu
    val showStrokeNumbers: Boolean = true   // Hiển thị số thứ tự nét
)

/**
 * Enum cho chế độ hiển thị nét mẫu
 */
enum class TemplateDisplayMode {
    FADING,         // Nét mờ dần
    OUTLINE,        // Chỉ viền nét
    FILLED,         // Nét đầy
    ANIMATED,       // Nét có animation
    TRACING,        // Nét có hiệu ứng theo dõi
    BOLD_BLACK,     // Nét đen đậm rõ ràng
    THICK_GUIDE     // Nét dày để dễ theo dõi
}
