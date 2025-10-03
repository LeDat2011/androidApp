package com.example.composeapp.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapp.models.*
import com.example.composeapp.ui.design.DesignSystem
import kotlin.math.abs
import kotlin.math.sqrt

@Composable
fun StrokeWritingComponent(
    character: WritingCharacter,
    currentStrokeIndex: Int,
    completedStrokes: List<Int>,
    onStrokeCompleted: (Int) -> Unit,
    onNextStroke: () -> Unit,
    onPreviousStroke: () -> Unit,
    settings: WritingSettings,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    var userPath by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var isDrawing by remember { mutableStateOf(false) }
    var animationProgress by remember { mutableStateOf(0f) }
    
    val currentStroke = character.strokes.getOrNull(currentStrokeIndex)
    val isCompleted = completedStrokes.contains(currentStrokeIndex)
    val isLastStroke = currentStrokeIndex == character.strokes.size - 1
    
    // Reset user path when stroke changes
    LaunchedEffect(currentStrokeIndex) {
        userPath = emptyList()
        animationProgress = 0f
    }
    
    // Animation cho nét mẫu
    LaunchedEffect(currentStrokeIndex, settings.animateTemplate) {
        if (settings.animateTemplate && currentStroke != null) {
            animationProgress = 0f
            repeat(3) { // Lặp lại 3 lần
                animationProgress = 0f
                while (animationProgress < 1f) {
                    animationProgress += 0.02f
                    kotlinx.coroutines.delay(16) // ~60fps
                }
                kotlinx.coroutines.delay(1000) // Nghỉ 1 giây
            }
        }
    }
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header với thông tin ký tự
        CharacterInfoCard(character)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Canvas để vẽ
        Box(
            modifier = Modifier
                .size(300.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
                .border(
                    width = 2.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Canvas(
                modifier = Modifier
                    .fillMaxSize()
                    .pointerInput(currentStrokeIndex) {
                        detectDragGestures(
                            onDragStart = { offset ->
                                isDrawing = true
                                userPath = listOf(offset)
                            },
                            onDrag = { _, dragAmount ->
                                if (isDrawing) {
                                    userPath = userPath + (userPath.lastOrNull()?.plus(dragAmount) ?: Offset.Zero)
                                }
                            },
                            onDragEnd = {
                                isDrawing = false
                                if (currentStroke != null && userPath.isNotEmpty()) {
                                    val accuracy = calculateStrokeAccuracy(
                                        userPath, 
                                        currentStroke, 
                                        size.width.toFloat(), 
                                        size.height.toFloat()
                                    )
                                    if (accuracy > 0.6f) {
                                        onStrokeCompleted(currentStrokeIndex)
                                    }
                                }
                            }
                        )
                    }
            ) {
                // Vẽ đường kẻ hướng dẫn
                if (settings.showGuideLines) {
                    drawGuideLines()
                }
                
                // Vẽ tất cả các nét mẫu (mờ) cho ký tự hoàn chỉnh
                character.strokes.forEachIndexed { index, stroke ->
                    if (index != currentStrokeIndex) {
                        drawStrokeTemplate(
                            stroke, 
                            settings.templateOpacity * 0.3f,
                            settings.templateDisplayMode,
                            animationProgress,
                            settings.showStrokeNumbers,
                            index + 1
                        )
                    }
                }
                
                // Vẽ các nét đã hoàn thành
                completedStrokes.forEach { strokeIndex ->
                    val stroke = character.strokes.getOrNull(strokeIndex)
                    stroke?.let { 
                        drawCompletedStroke(it, strokeIndex + 1) 
                    }
                }
                
                // Vẽ nét hiện tại của người dùng
                if (userPath.size > 1) {
                    drawUserStroke(userPath, settings.strokeWidth)
                }
                
                // Vẽ nét hiện tại (nổi bật)
                if (!isCompleted && currentStroke != null) {
                    drawCurrentStrokeTemplate(
                        currentStroke, 
                        settings.templateDisplayMode,
                        settings.templateOpacity,
                        animationProgress,
                        currentStrokeIndex + 1
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Thông tin nét hiện tại
        CurrentStrokeInfo(
            currentStroke = currentStroke,
            currentIndex = currentStrokeIndex,
            totalStrokes = character.strokes.size,
            isCompleted = isCompleted
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Điều khiển
        StrokeControls(
            currentStrokeIndex = currentStrokeIndex,
            totalStrokes = character.strokes.size,
            isCompleted = isCompleted,
            isLastStroke = isLastStroke,
            onNextStroke = onNextStroke,
            onPreviousStroke = onPreviousStroke,
            onReset = { userPath = emptyList() }
        )
    }
}

@Composable
private fun CharacterInfoCard(character: WritingCharacter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = character.character,
                fontSize = 48.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Đọc: ${character.reading ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Text(
                    text = "Loại: ${character.type.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Text(
                    text = "Cấp: ${character.level}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
            
            if (character.meaning != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "Nghĩa: ${character.meaning}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CurrentStrokeInfo(
    currentStroke: com.example.composeapp.models.Stroke?,
    currentIndex: Int,
    totalStrokes: Int,
    isCompleted: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) 
                MaterialTheme.colorScheme.tertiaryContainer 
            else 
                MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nét ${currentIndex + 1}/$totalStrokes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            if (currentStroke != null) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Hướng: ${getDirectionText(currentStroke.direction)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    Text(
                        text = "Loại: ${getStrokeTypeText(currentStroke.type)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
            
            if (isCompleted) {
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = "Hoàn thành",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Đã hoàn thành",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.tertiary,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun StrokeControls(
    currentStrokeIndex: Int,
    totalStrokes: Int,
    isCompleted: Boolean,
    isLastStroke: Boolean,
    onNextStroke: () -> Unit,
    onPreviousStroke: () -> Unit,
    onReset: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Nút quay lại
        IconButton(
            onClick = onPreviousStroke,
            enabled = currentStrokeIndex > 0
        ) {
            Icon(
                imageVector = Icons.Default.SkipPrevious,
                contentDescription = "Nét trước",
                tint = if (currentStrokeIndex > 0) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
        
        // Nút reset
        IconButton(onClick = onReset) {
            Icon(
                imageVector = Icons.Default.Refresh,
                contentDescription = "Làm lại",
                tint = MaterialTheme.colorScheme.primary
            )
        }
        
        // Nút tiếp theo
        IconButton(
            onClick = onNextStroke,
            enabled = currentStrokeIndex < totalStrokes - 1
        ) {
            Icon(
                imageVector = Icons.Default.SkipNext,
                contentDescription = "Nét tiếp",
                tint = if (currentStrokeIndex < totalStrokes - 1) 
                    MaterialTheme.colorScheme.primary 
                else 
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
            )
        }
    }
}

// Drawing functions
private fun DrawScope.drawGuideLines() {
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    // Vẽ khung
    drawLine(
        color = Color.Gray.copy(alpha = 0.3f),
        start = Offset(centerX - 100f, centerY - 100f),
        end = Offset(centerX + 100f, centerY - 100f),
        strokeWidth = 1.dp.toPx()
    )
    
    drawLine(
        color = Color.Gray.copy(alpha = 0.3f),
        start = Offset(centerX - 100f, centerY + 100f),
        end = Offset(centerX + 100f, centerY + 100f),
        strokeWidth = 1.dp.toPx()
    )
    
    drawLine(
        color = Color.Gray.copy(alpha = 0.3f),
        start = Offset(centerX - 100f, centerY - 100f),
        end = Offset(centerX - 100f, centerY + 100f),
        strokeWidth = 1.dp.toPx()
    )
    
    drawLine(
        color = Color.Gray.copy(alpha = 0.3f),
        start = Offset(centerX + 100f, centerY - 100f),
        end = Offset(centerX + 100f, centerY + 100f),
        strokeWidth = 1.dp.toPx()
    )
    
    // Vẽ đường giữa
    drawLine(
        color = Color.Gray.copy(alpha = 0.2f),
        start = Offset(centerX - 100f, centerY),
        end = Offset(centerX + 100f, centerY),
        strokeWidth = 1.dp.toPx()
    )
    
    drawLine(
        color = Color.Gray.copy(alpha = 0.2f),
        start = Offset(centerX, centerY - 100f),
        end = Offset(centerX, centerY + 100f),
        strokeWidth = 1.dp.toPx()
    )
}

private fun DrawScope.drawStrokeTemplate(
    stroke: com.example.composeapp.models.Stroke, 
    opacity: Float,
    mode: TemplateDisplayMode,
    animationProgress: Float = 1f,
    showNumber: Boolean = false,
    strokeNumber: Int = 0
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val startPoint = Offset(centerX + stroke.startPoint.x, centerY + stroke.startPoint.y)
    val endPoint = Offset(centerX + stroke.endPoint.x, centerY + stroke.endPoint.y)
    
    when (mode) {
        TemplateDisplayMode.FADING -> {
            // Nét mờ dần với hiệu ứng gradient
    drawLine(
        color = Color.Blue.copy(alpha = opacity),
                start = startPoint,
                end = endPoint,
        strokeWidth = 3.dp.toPx(),
        pathEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 5f))
    )
        }
        TemplateDisplayMode.OUTLINE -> {
            // Chỉ viền nét với màu nhạt
            drawLine(
                color = Color.Gray.copy(alpha = opacity),
                start = startPoint,
                end = endPoint,
                strokeWidth = 2.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f))
            )
        }
        TemplateDisplayMode.FILLED -> {
            // Nét đầy với màu nhạt
            drawLine(
                color = Color.Blue.copy(alpha = opacity * 0.6f),
                start = startPoint,
                end = endPoint,
                strokeWidth = 4.dp.toPx()
            )
        }
        TemplateDisplayMode.ANIMATED -> {
            // Nét có animation chạy theo
            if (animationProgress > 0f) {
                val animatedEndX = startPoint.x + (endPoint.x - startPoint.x) * animationProgress
                val animatedEndY = startPoint.y + (endPoint.y - startPoint.y) * animationProgress
                val animatedEndPoint = Offset(animatedEndX, animatedEndY)
                
                drawLine(
                    color = Color.Blue.copy(alpha = opacity),
                    start = startPoint,
                    end = animatedEndPoint,
                    strokeWidth = 3.dp.toPx()
                )
            }
        }
        TemplateDisplayMode.TRACING -> {
            // Nét có hiệu ứng theo dõi với gradient
            drawLine(
                color = Color.Blue.copy(alpha = opacity),
                start = startPoint,
                end = endPoint,
                strokeWidth = 3.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(15f, 5f))
            )
        }
        TemplateDisplayMode.BOLD_BLACK -> {
            // Nét đen đậm rõ ràng để dễ theo dõi
            drawLine(
                color = Color.Black.copy(alpha = 0.8f),
                start = startPoint,
                end = endPoint,
                strokeWidth = 6.dp.toPx()
            )
            // Vẽ thêm viền trắng để nổi bật
            drawLine(
                color = Color.White,
                start = startPoint,
                end = endPoint,
                strokeWidth = 8.dp.toPx()
            )
            drawLine(
                color = Color.Black.copy(alpha = 0.8f),
                start = startPoint,
                end = endPoint,
                strokeWidth = 6.dp.toPx()
            )
        }
        TemplateDisplayMode.THICK_GUIDE -> {
            // Nét dày để dễ theo dõi
            drawLine(
                color = Color.DarkGray.copy(alpha = 0.9f),
                start = startPoint,
                end = endPoint,
                strokeWidth = 8.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 8f))
            )
        }
    }
    
    // Vẽ số thứ tự nét
    if (showNumber && strokeNumber > 0) {
        val midPoint = Offset(
            (startPoint.x + endPoint.x) / 2,
            (startPoint.y + endPoint.y) / 2
        )
        drawContext.canvas.nativeCanvas.apply {
            val paint = android.graphics.Paint().apply {
                color = android.graphics.Color.BLUE
                textSize = 16f
                isAntiAlias = true
                alpha = (opacity * 255).toInt()
            }
            drawText(
                strokeNumber.toString(),
                midPoint.x - 8,
                midPoint.y + 6,
                paint
            )
        }
    }
}

private fun DrawScope.drawCompletedStroke(stroke: com.example.composeapp.models.Stroke, strokeNumber: Int) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val startPoint = Offset(centerX + stroke.startPoint.x, centerY + stroke.startPoint.y)
    val endPoint = Offset(centerX + stroke.endPoint.x, centerY + stroke.endPoint.y)
    
    // Vẽ nét đã hoàn thành với màu xanh
    drawLine(
        color = Color.Green,
        start = startPoint,
        end = endPoint,
        strokeWidth = 4.dp.toPx()
    )
    
    // Vẽ số thứ tự nét
    val midPoint = Offset(
        (startPoint.x + endPoint.x) / 2,
        (startPoint.y + endPoint.y) / 2
    )
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.GREEN
            textSize = 14f
            isAntiAlias = true
            style = android.graphics.Paint.Style.FILL_AND_STROKE
            strokeWidth = 2f
            setShadowLayer(4f, 0f, 0f, android.graphics.Color.WHITE)
        }
        drawText(
            strokeNumber.toString(),
            midPoint.x - 6,
            midPoint.y + 5,
            paint
        )
    }
}

private fun DrawScope.drawCurrentStrokeTemplate(
    stroke: com.example.composeapp.models.Stroke,
    mode: TemplateDisplayMode,
    opacity: Float,
    animationProgress: Float,
    strokeNumber: Int
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val startPoint = Offset(centerX + stroke.startPoint.x, centerY + stroke.startPoint.y)
    val endPoint = Offset(centerX + stroke.endPoint.x, centerY + stroke.endPoint.y)
    
    // Vẽ nét hiện tại với hiệu ứng nổi bật
    when (mode) {
        TemplateDisplayMode.FADING -> {
            drawLine(
                color = Color.Red.copy(alpha = opacity + 0.2f),
                start = startPoint,
                end = endPoint,
                strokeWidth = 5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(20f, 10f))
            )
        }
        TemplateDisplayMode.OUTLINE -> {
            // Viền đôi cho nét hiện tại
            drawLine(
                color = Color.Red.copy(alpha = opacity + 0.3f),
                start = startPoint,
                end = endPoint,
                strokeWidth = 6.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(12f, 6f))
            )
        }
        TemplateDisplayMode.FILLED -> {
            drawLine(
                color = Color.Red.copy(alpha = opacity + 0.4f),
                start = startPoint,
                end = endPoint,
                strokeWidth = 5.dp.toPx()
            )
        }
        TemplateDisplayMode.ANIMATED -> {
            if (animationProgress > 0f) {
                val animatedEndX = startPoint.x + (endPoint.x - startPoint.x) * animationProgress
                val animatedEndY = startPoint.y + (endPoint.y - startPoint.y) * animationProgress
                val animatedEndPoint = Offset(animatedEndX, animatedEndY)
                
                drawLine(
                    color = Color.Red.copy(alpha = opacity + 0.3f),
                    start = startPoint,
                    end = animatedEndPoint,
                    strokeWidth = 5.dp.toPx()
                )
            }
        }
        TemplateDisplayMode.TRACING -> {
            drawLine(
                color = Color.Red.copy(alpha = opacity + 0.3f),
                start = startPoint,
                end = endPoint,
                strokeWidth = 5.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(25f, 8f))
            )
        }
        TemplateDisplayMode.BOLD_BLACK -> {
            // Nét hiện tại đen đậm với viền đỏ
            drawLine(
                color = Color.Red.copy(alpha = 0.9f),
                start = startPoint,
                end = endPoint,
                strokeWidth = 8.dp.toPx()
            )
            drawLine(
                color = Color.Black.copy(alpha = 0.9f),
                start = startPoint,
                end = endPoint,
                strokeWidth = 6.dp.toPx()
            )
        }
        TemplateDisplayMode.THICK_GUIDE -> {
            // Nét hiện tại dày với màu đỏ nổi bật
            drawLine(
                color = Color.Red.copy(alpha = 0.9f),
                start = startPoint,
                end = endPoint,
                strokeWidth = 10.dp.toPx(),
                pathEffect = PathEffect.dashPathEffect(floatArrayOf(30f, 10f))
            )
        }
    }
    
    // Vẽ mũi tên hướng nếu cần
    // drawDirectionArrow(startPoint, endPoint, stroke.direction, opacity + 0.3f)
    
    // Vẽ số thứ tự nét nổi bật
    val midPoint = Offset(
        (startPoint.x + endPoint.x) / 2,
        (startPoint.y + endPoint.y) / 2
    )
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.RED
            textSize = 18f
            isAntiAlias = true
            style = android.graphics.Paint.Style.FILL_AND_STROKE
            strokeWidth = 3f
            setShadowLayer(6f, 0f, 0f, android.graphics.Color.WHITE)
        }
        drawText(
            strokeNumber.toString(),
            midPoint.x - 8,
            midPoint.y + 6,
            paint
        )
    }
}

private fun DrawScope.drawDirectionArrow(
    startPoint: Offset, 
    endPoint: Offset, 
    direction: StrokeDirection,
    opacity: Float
) {
    val arrowSize = 8.dp.toPx()
    val angle = kotlin.math.atan2(endPoint.y - startPoint.y, endPoint.x - startPoint.x)
    
    val arrowHead1 = Offset(
        (endPoint.x - arrowSize * kotlin.math.cos(angle - kotlin.math.PI / 6)).toFloat(),
        (endPoint.y - arrowSize * kotlin.math.sin(angle - kotlin.math.PI / 6)).toFloat()
    )
    val arrowHead2 = Offset(
        (endPoint.x - arrowSize * kotlin.math.cos(angle + kotlin.math.PI / 6)).toFloat(),
        (endPoint.y - arrowSize * kotlin.math.sin(angle + kotlin.math.PI / 6)).toFloat()
    )
    
    drawLine(
        color = Color.Red.copy(alpha = opacity),
        start = endPoint,
        end = arrowHead1,
        strokeWidth = 2.dp.toPx()
    )
    drawLine(
        color = Color.Red.copy(alpha = opacity),
        start = endPoint,
        end = arrowHead2,
        strokeWidth = 2.dp.toPx()
    )
}

private fun DrawScope.drawUserStroke(path: List<Offset>, strokeWidth: Float) {
    if (path.size < 2) return
    
    val userPath = Path().apply {
        moveTo(path[0].x, path[0].y)
        for (i in 1 until path.size) {
            lineTo(path[i].x, path[i].y)
        }
    }
    
    // Vẽ nét của người dùng với hiệu ứng đẹp
    drawPath(
        path = userPath,
        color = Color.Black,
        style = Stroke(
            width = strokeWidth.dp.toPx(), 
            cap = StrokeCap.Round, 
            join = StrokeJoin.Round
        )
    )
    
    // Vẽ hiệu ứng sáng cho nét của người dùng
    drawPath(
        path = userPath,
        color = Color.Black.copy(alpha = 0.3f),
        style = Stroke(
            width = (strokeWidth + 2).dp.toPx(), 
            cap = StrokeCap.Round, 
            join = StrokeJoin.Round
        )
    )
}

// Helper functions
private fun calculateStrokeAccuracy(
    userPath: List<Offset>,
    targetStroke: com.example.composeapp.models.Stroke,
    canvasWidth: Float,
    canvasHeight: Float
): Float {
    if (userPath.isEmpty()) return 0f
    
    val centerX = canvasWidth / 2
    val centerY = canvasHeight / 2
    
    val targetStart = Offset(centerX + targetStroke.startPoint.x, centerY + targetStroke.startPoint.y)
    val targetEnd = Offset(centerX + targetStroke.endPoint.x, centerY + targetStroke.endPoint.y)
    
    val userStart = userPath.first()
    val userEnd = userPath.last()
    
    val startDistance = sqrt(
        ((userStart.x - targetStart.x) * (userStart.x - targetStart.x) + (userStart.y - targetStart.y) * (userStart.y - targetStart.y)).toDouble()
    ).toFloat()
    
    val endDistance = sqrt(
        ((userEnd.x - targetEnd.x) * (userEnd.x - targetEnd.x) + (userEnd.y - targetEnd.y) * (userEnd.y - targetEnd.y)).toDouble()
    ).toFloat()
    
    val maxDistance = sqrt((canvasWidth * canvasWidth + canvasHeight * canvasHeight).toDouble()).toFloat()
    
    val startAccuracy = 1f - (startDistance / maxDistance).coerceAtMost(1f)
    val endAccuracy = 1f - (endDistance / maxDistance).coerceAtMost(1f)
    
    return (startAccuracy + endAccuracy) / 2f
}

private fun getDirectionText(direction: com.example.composeapp.models.StrokeDirection): String {
    return when (direction) {
        com.example.composeapp.models.StrokeDirection.RIGHT -> "Phải"
        com.example.composeapp.models.StrokeDirection.LEFT -> "Trái"
        com.example.composeapp.models.StrokeDirection.DOWN -> "Xuống"
        com.example.composeapp.models.StrokeDirection.UP -> "Lên"
        com.example.composeapp.models.StrokeDirection.DIAGONAL_RIGHT_DOWN -> "Chéo xuống phải"
        com.example.composeapp.models.StrokeDirection.DIAGONAL_RIGHT_UP -> "Chéo lên phải"
        com.example.composeapp.models.StrokeDirection.DIAGONAL_LEFT_DOWN -> "Chéo xuống trái"
        com.example.composeapp.models.StrokeDirection.DIAGONAL_LEFT_UP -> "Chéo lên trái"
        com.example.composeapp.models.StrokeDirection.CURVE -> "Nét cong"
    }
}

private fun getStrokeTypeText(type: com.example.composeapp.models.StrokeType): String {
    return when (type) {
        com.example.composeapp.models.StrokeType.HORIZONTAL -> "Ngang"
        com.example.composeapp.models.StrokeType.VERTICAL -> "Dọc"
        com.example.composeapp.models.StrokeType.DIAGONAL -> "Chéo"
        com.example.composeapp.models.StrokeType.CURVE -> "Cong"
        com.example.composeapp.models.StrokeType.HOOK -> "Móc"
        com.example.composeapp.models.StrokeType.DOT -> "Chấm"
    }
}
