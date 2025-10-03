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
fun FreeWritingComponent(
    character: WritingCharacter,
    onWritingCompleted: (Float, Int) -> Unit,
    settings: WritingSettings,
    modifier: Modifier = Modifier
) {
    var userPath by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var isDrawing by remember { mutableStateOf(false) }
    var startTime by remember { mutableStateOf(System.currentTimeMillis()) }
    
    // Reset when character changes
    LaunchedEffect(character.id) {
        userPath = emptyList()
        startTime = System.currentTimeMillis()
    }
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header với thông tin ký tự
        FreeWritingCharacterInfoCard(character)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Canvas để vẽ tự do
        Box(
            modifier = Modifier
                .size(350.dp)
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
                    .pointerInput(character.id) {
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
                            }
                        )
                    }
            ) {
                // Vẽ đường kẻ hướng dẫn
                if (settings.showGuideLines) {
                    drawGuideLines()
                }
                
                // Vẽ nét mẫu (mờ)
                drawCharacterTemplate(character, settings.guideLineOpacity, size.width, size.height)
                
                // Vẽ nét của người dùng
                if (userPath.size > 1) {
                    drawUserStroke(userPath, settings.strokeWidth)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Thông tin và điều khiển
        FreeWritingControls(
            userPath = userPath,
            onClear = { 
                userPath = emptyList()
                startTime = System.currentTimeMillis()
            },
            onCheck = {
                val timeSpent = ((System.currentTimeMillis() - startTime) / 1000).toInt()
                val accuracy = calculateOverallAccuracy(userPath, character, 350f, 350f)
                onWritingCompleted(accuracy, timeSpent)
            },
            onShowHint = {
                // Có thể thêm logic hiển thị gợi ý
            }
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Gợi ý
        WritingHints(character)
    }
}

@Composable
private fun FreeWritingCharacterInfoCard(character: WritingCharacter) {
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
private fun FreeWritingControls(
    userPath: List<Offset>,
    onClear: () -> Unit,
    onCheck: () -> Unit,
    onShowHint: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        // Nút xóa
        OutlinedButton(
            onClick = onClear,
            enabled = userPath.isNotEmpty()
        ) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = "Xóa",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Xóa")
        }
        
        // Nút gợi ý
        OutlinedButton(onClick = onShowHint) {
            Icon(
                imageVector = Icons.Default.Lightbulb,
                contentDescription = "Gợi ý",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Gợi ý")
        }
        
        // Nút kiểm tra
        Button(
            onClick = onCheck,
            enabled = userPath.isNotEmpty()
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = "Kiểm tra",
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text("Kiểm tra")
        }
    }
}

@Composable
private fun WritingHints(character: WritingCharacter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "💡 Gợi ý viết",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "• Viết từ trên xuống dưới, từ trái sang phải",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Text(
                text = "• Giữ nét vẽ mượt mà và đều đặn",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            Text(
                text = "• Chú ý đến tỷ lệ và khoảng cách",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            if (character.examples.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Ví dụ: ${character.examples.first()}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// Drawing functions
private fun DrawScope.drawGuideLines() {
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    // Vẽ khung lớn hơn cho viết tự do
    drawLine(
        color = Color.Gray.copy(alpha = 0.2f),
        start = Offset(centerX - 120f, centerY - 120f),
        end = Offset(centerX + 120f, centerY - 120f),
        strokeWidth = 1.dp.toPx()
    )
    
    drawLine(
        color = Color.Gray.copy(alpha = 0.2f),
        start = Offset(centerX - 120f, centerY + 120f),
        end = Offset(centerX + 120f, centerY + 120f),
        strokeWidth = 1.dp.toPx()
    )
    
    drawLine(
        color = Color.Gray.copy(alpha = 0.2f),
        start = Offset(centerX - 120f, centerY - 120f),
        end = Offset(centerX - 120f, centerY + 120f),
        strokeWidth = 1.dp.toPx()
    )
    
    drawLine(
        color = Color.Gray.copy(alpha = 0.2f),
        start = Offset(centerX + 120f, centerY - 120f),
        end = Offset(centerX + 120f, centerY + 120f),
        strokeWidth = 1.dp.toPx()
    )
    
    // Vẽ đường giữa
    drawLine(
        color = Color.Gray.copy(alpha = 0.15f),
        start = Offset(centerX - 120f, centerY),
        end = Offset(centerX + 120f, centerY),
        strokeWidth = 1.dp.toPx()
    )
    
    drawLine(
        color = Color.Gray.copy(alpha = 0.15f),
        start = Offset(centerX, centerY - 120f),
        end = Offset(centerX, centerY + 120f),
        strokeWidth = 1.dp.toPx()
    )
}

private fun DrawScope.drawCharacterTemplate(character: WritingCharacter, opacity: Float, canvasWidth: Float, canvasHeight: Float) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    // Vẽ tất cả các nét của ký tự (mờ)
    character.strokes.forEach { stroke ->
        drawLine(
            color = Color.Blue.copy(alpha = opacity * 0.5f),
            start = Offset(centerX + stroke.startPoint.x, centerY + stroke.startPoint.y),
            end = Offset(centerX + stroke.endPoint.x, centerY + stroke.endPoint.y),
            strokeWidth = 2.dp.toPx(),
            pathEffect = PathEffect.dashPathEffect(floatArrayOf(8f, 4f))
        )
    }
}

private fun DrawScope.drawUserStroke(path: List<Offset>, strokeWidth: Float) {
    if (path.size < 2) return
    
    val path = Path().apply {
        moveTo(path[0].x, path[0].y)
        for (i in 1 until path.size) {
            lineTo(path[i].x, path[i].y)
        }
    }
    
    drawPath(
        path = path,
        color = Color.Black,
        style = Stroke(width = strokeWidth.dp.toPx(), cap = StrokeCap.Round, join = StrokeJoin.Round)
    )
}

// Helper functions
private fun calculateOverallAccuracy(
    userPath: List<Offset>,
    character: WritingCharacter,
    canvasWidth: Float,
    canvasHeight: Float
): Float {
    if (userPath.isEmpty()) return 0f
    
    val centerX = canvasWidth / 2
    val centerY = canvasHeight / 2
    
    var totalAccuracy = 0f
    var validStrokes = 0
    
    // So sánh với từng nét của ký tự mẫu
    character.strokes.forEach { stroke ->
        val targetStart = Offset(centerX + stroke.startPoint.x, centerY + stroke.startPoint.y)
        val targetEnd = Offset(centerX + stroke.endPoint.x, centerY + stroke.endPoint.y)
        
        // Tìm nét gần nhất trong userPath
        val closestStroke = findClosestStroke(userPath, targetStart, targetEnd)
        if (closestStroke != null) {
            val accuracy = calculateStrokeSimilarity(closestStroke, targetStart, targetEnd)
            totalAccuracy += accuracy
            validStrokes++
        }
    }
    
    return if (validStrokes > 0) totalAccuracy / validStrokes else 0f
}

private fun findClosestStroke(
    userPath: List<Offset>,
    targetStart: Offset,
    targetEnd: Offset
): Pair<Offset, Offset>? {
    if (userPath.size < 2) return null
    
    var minDistance = Float.MAX_VALUE
    var bestStroke: Pair<Offset, Offset>? = null
    
    for (i in 0 until userPath.size - 1) {
        val start = userPath[i]
        val end = userPath[i + 1]
        
        val distance = calculateStrokeDistance(start, end, targetStart, targetEnd)
        if (distance < minDistance) {
            minDistance = distance
            bestStroke = Pair(start, end)
        }
    }
    
    return bestStroke
}

private fun calculateStrokeDistance(
    userStart: Offset,
    userEnd: Offset,
    targetStart: Offset,
    targetEnd: Offset
): Float {
    val startDistance = sqrt(
        ((userStart.x - targetStart.x) * (userStart.x - targetStart.x) + (userStart.y - targetStart.y) * (userStart.y - targetStart.y)).toDouble()
    ).toFloat()
    
    val endDistance = sqrt(
        ((userEnd.x - targetEnd.x) * (userEnd.x - targetEnd.x) + (userEnd.y - targetEnd.y) * (userEnd.y - targetEnd.y)).toDouble()
    ).toFloat()
    
    return startDistance + endDistance
}

private fun calculateStrokeSimilarity(
    userStroke: Pair<Offset, Offset>,
    targetStart: Offset,
    targetEnd: Offset
): Float {
    val (userStart, userEnd) = userStroke
    
    // Tính độ chính xác dựa trên vị trí và hướng
    val startAccuracy = 1f - (sqrt(
        ((userStart.x - targetStart.x) * (userStart.x - targetStart.x) + (userStart.y - targetStart.y) * (userStart.y - targetStart.y)).toDouble()
    ).toFloat() / 100f).coerceAtMost(1f)
    
    val endAccuracy = 1f - (sqrt(
        ((userEnd.x - targetEnd.x) * (userEnd.x - targetEnd.x) + (userEnd.y - targetEnd.y) * (userEnd.y - targetEnd.y)).toDouble()
    ).toFloat() / 100f).coerceAtMost(1f)
    
    // Tính độ chính xác dựa trên hướng
    val targetDirection = Offset(targetEnd.x - targetStart.x, targetEnd.y - targetStart.y)
    val userDirection = Offset(userEnd.x - userStart.x, userEnd.y - userStart.y)
    
    val directionAccuracy = if (targetDirection != Offset.Zero && userDirection != Offset.Zero) {
        val dotProduct = targetDirection.x * userDirection.x + targetDirection.y * userDirection.y
        val targetLength = sqrt((targetDirection.x * targetDirection.x + targetDirection.y * targetDirection.y).toDouble()).toFloat()
        val userLength = sqrt((userDirection.x * userDirection.x + userDirection.y * userDirection.y).toDouble()).toFloat()
        
        (dotProduct / (targetLength * userLength)).coerceIn(-1f, 1f)
    } else {
        0f
    }
    
    return (startAccuracy + endAccuracy + directionAccuracy) / 3f
}