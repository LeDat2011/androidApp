package com.example.composeapp.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
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

@Composable
fun BoldWritingCanvas(
    character: WritingCharacter,
    currentStrokeIndex: Int,
    completedStrokes: List<Int>,
    onStrokeCompleted: (Int) -> Unit,
    onNextStroke: () -> Unit,
    onPreviousStroke: () -> Unit,
    modifier: Modifier = Modifier
) {
    var userPath by remember { mutableStateOf<List<Offset>>(emptyList()) }
    var isDrawing by remember { mutableStateOf(false) }
    
    val currentStroke = character.strokes.getOrNull(currentStrokeIndex)
    val isCompleted = completedStrokes.contains(currentStrokeIndex)
    val isLastStroke = currentStrokeIndex == character.strokes.size - 1
    
    // Reset user path when stroke changes
    LaunchedEffect(currentStrokeIndex) {
        userPath = emptyList()
    }
    
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header với thông tin ký tự
        BoldCharacterInfoCard(character)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Canvas vẽ với nét đen đậm
        Box(
            modifier = Modifier
                .size(350.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color.White)
                .border(
                    width = 3.dp,
                    color = Color.Black,
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
                // Vẽ lưới hướng dẫn đậm hơn
                drawBoldGuideLines()
                
                // Vẽ tất cả các nét mẫu đen đậm
                character.strokes.forEachIndexed { index, stroke ->
                    if (index != currentStrokeIndex) {
                        drawBoldStrokeTemplate(stroke, index + 1)
                    }
                }
                
                // Vẽ các nét đã hoàn thành
                completedStrokes.forEach { strokeIndex ->
                    val stroke = character.strokes.getOrNull(strokeIndex)
                    stroke?.let { drawBoldCompletedStroke(it, strokeIndex + 1) }
                }
                
                // Vẽ nét hiện tại của người dùng
                if (userPath.size > 1) {
                    drawBoldUserStroke(userPath)
                }
                
                // Vẽ nét hiện tại nổi bật
                if (!isCompleted && currentStroke != null) {
                    drawBoldCurrentStroke(currentStroke, currentStrokeIndex + 1)
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Thông tin nét hiện tại
        BoldCurrentStrokeInfo(
            currentStroke = currentStroke,
            currentIndex = currentStrokeIndex,
            totalStrokes = character.strokes.size,
            isCompleted = isCompleted
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Điều khiển
        BoldStrokeControls(
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
private fun BoldCharacterInfoCard(character: WritingCharacter) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF2E2E2E)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = character.character,
                fontSize = 56.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Đọc: ${character.reading ?: "N/A"}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "Loại: ${character.type.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "Cấp: ${character.level}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun BoldCurrentStrokeInfo(
    currentStroke: com.example.composeapp.models.Stroke?,
    currentIndex: Int,
    totalStrokes: Int,
    isCompleted: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (isCompleted) 
                Color(0xFF4CAF50) 
            else 
                Color(0xFF2196F3)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Nét ${currentIndex + 1}/$totalStrokes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            
            if (currentStroke != null) {
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Hướng: ${getDirectionText(currentStroke.direction)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "Loại: ${getStrokeTypeText(currentStroke.type)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

@Composable
private fun BoldStrokeControls(
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
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Nút trước
        Button(
            onClick = onPreviousStroke,
            enabled = currentStrokeIndex > 0,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF757575)
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Trước",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Nút reset
        Button(
            onClick = onReset,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFFF5722)
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Xóa",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
        
        // Nút tiếp theo
        Button(
            onClick = onNextStroke,
            enabled = currentStrokeIndex < totalStrokes - 1,
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4CAF50)
            ),
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = "Tiếp",
                color = Color.White,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Hàm vẽ lưới hướng dẫn đậm
private fun DrawScope.drawBoldGuideLines() {
    val centerX = size.width / 2
    val centerY = size.height / 2
    
    // Vẽ lưới đậm hơn
    drawLine(
        color = Color.Black.copy(alpha = 0.6f),
        start = Offset(centerX - 120f, centerY - 120f),
        end = Offset(centerX + 120f, centerY - 120f),
        strokeWidth = 2.dp.toPx()
    )
    
    drawLine(
        color = Color.Black.copy(alpha = 0.6f),
        start = Offset(centerX - 120f, centerY + 120f),
        end = Offset(centerX + 120f, centerY + 120f),
        strokeWidth = 2.dp.toPx()
    )
    
    drawLine(
        color = Color.Black.copy(alpha = 0.6f),
        start = Offset(centerX - 120f, centerY - 120f),
        end = Offset(centerX - 120f, centerY + 120f),
        strokeWidth = 2.dp.toPx()
    )
    
    drawLine(
        color = Color.Black.copy(alpha = 0.6f),
        start = Offset(centerX + 120f, centerY - 120f),
        end = Offset(centerX + 120f, centerY + 120f),
        strokeWidth = 2.dp.toPx()
    )
    
    // Đường giữa đậm hơn
    drawLine(
        color = Color.Black.copy(alpha = 0.4f),
        start = Offset(centerX - 120f, centerY),
        end = Offset(centerX + 120f, centerY),
        strokeWidth = 1.5.dp.toPx()
    )
    
    drawLine(
        color = Color.Black.copy(alpha = 0.4f),
        start = Offset(centerX, centerY - 120f),
        end = Offset(centerX, centerY + 120f),
        strokeWidth = 1.5.dp.toPx()
    )
}

// Hàm vẽ nét mẫu đen đậm
private fun DrawScope.drawBoldStrokeTemplate(
    stroke: com.example.composeapp.models.Stroke,
    strokeNumber: Int
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val startPoint = Offset(centerX + stroke.startPoint.x, centerY + stroke.startPoint.y)
    val endPoint = Offset(centerX + stroke.endPoint.x, centerY + stroke.endPoint.y)
    
    // Vẽ nét mẫu đen đậm với viền trắng
    drawLine(
        color = Color.White,
        start = startPoint,
        end = endPoint,
        strokeWidth = 10.dp.toPx()
    )
    
    drawLine(
        color = Color.Black,
        start = startPoint,
        end = endPoint,
        strokeWidth = 8.dp.toPx()
    )
    
    // Vẽ số thứ tự nét
    val midPoint = Offset(
        (startPoint.x + endPoint.x) / 2,
        (startPoint.y + endPoint.y) / 2
    )
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 20f
            isAntiAlias = true
            style = android.graphics.Paint.Style.FILL_AND_STROKE
            strokeWidth = 4f
            setShadowLayer(6f, 0f, 0f, android.graphics.Color.BLACK)
        }
        drawText(
            strokeNumber.toString(),
            midPoint.x - 10,
            midPoint.y + 7,
            paint
        )
    }
}

// Hàm vẽ nét hoàn thành đậm
private fun DrawScope.drawBoldCompletedStroke(
    stroke: com.example.composeapp.models.Stroke,
    strokeNumber: Int
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val startPoint = Offset(centerX + stroke.startPoint.x, centerY + stroke.startPoint.y)
    val endPoint = Offset(centerX + stroke.endPoint.x, centerY + stroke.endPoint.y)
    
    // Vẽ nét đã hoàn thành với màu xanh đậm
    drawLine(
        color = Color(0xFF2E7D32),
        start = startPoint,
        end = endPoint,
        strokeWidth = 8.dp.toPx()
    )
    
    // Vẽ số thứ tự
    val midPoint = Offset(
        (startPoint.x + endPoint.x) / 2,
        (startPoint.y + endPoint.y) / 2
    )
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 18f
            isAntiAlias = true
            style = android.graphics.Paint.Style.FILL_AND_STROKE
            strokeWidth = 3f
            setShadowLayer(4f, 0f, 0f, android.graphics.Color.BLACK)
        }
        drawText(
            strokeNumber.toString(),
            midPoint.x - 8,
            midPoint.y + 6,
            paint
        )
    }
}

// Hàm vẽ nét hiện tại đậm
private fun DrawScope.drawBoldCurrentStroke(
    stroke: com.example.composeapp.models.Stroke,
    strokeNumber: Int
) {
    val centerX = size.width / 2
    val centerY = size.height / 2
    val startPoint = Offset(centerX + stroke.startPoint.x, centerY + stroke.startPoint.y)
    val endPoint = Offset(centerX + stroke.endPoint.x, centerY + stroke.endPoint.y)
    
    // Vẽ nét hiện tại với màu đỏ đậm và viền
    drawLine(
        color = Color.White,
        start = startPoint,
        end = endPoint,
        strokeWidth = 12.dp.toPx()
    )
    
    drawLine(
        color = Color(0xFFD32F2F),
        start = startPoint,
        end = endPoint,
        strokeWidth = 10.dp.toPx()
    )
    
    drawLine(
        color = Color.Black,
        start = startPoint,
        end = endPoint,
        strokeWidth = 8.dp.toPx()
    )
    
    // Vẽ số thứ tự nổi bật
    val midPoint = Offset(
        (startPoint.x + endPoint.x) / 2,
        (startPoint.y + endPoint.y) / 2
    )
    drawContext.canvas.nativeCanvas.apply {
        val paint = android.graphics.Paint().apply {
            color = android.graphics.Color.WHITE
            textSize = 24f
            isAntiAlias = true
            style = android.graphics.Paint.Style.FILL_AND_STROKE
            strokeWidth = 5f
            setShadowLayer(8f, 0f, 0f, android.graphics.Color.BLACK)
        }
        drawText(
            strokeNumber.toString(),
            midPoint.x - 12,
            midPoint.y + 8,
            paint
        )
    }
}

// Hàm vẽ nét của người dùng đậm
private fun DrawScope.drawBoldUserStroke(path: List<Offset>) {
    if (path.size < 2) return
    
    val userPath = Path().apply {
        moveTo(path[0].x, path[0].y)
        for (i in 1 until path.size) {
            lineTo(path[i].x, path[i].y)
        }
    }
    
    // Vẽ nét của người dùng với hiệu ứng đậm
    drawPath(
        path = userPath,
        color = Color.Black,
        style = Stroke(
            width = 6.dp.toPx(), 
            cap = StrokeCap.Round, 
            join = StrokeJoin.Round
        )
    )
    
    // Vẽ hiệu ứng sáng
    drawPath(
        path = userPath,
        color = Color.Black.copy(alpha = 0.3f),
        style = Stroke(
            width = 10.dp.toPx(), 
            cap = StrokeCap.Round, 
            join = StrokeJoin.Round
        )
    )
}

// Helper functions
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

private fun calculateStrokeAccuracy(
    userPath: List<Offset>,
    targetStroke: com.example.composeapp.models.Stroke,
    canvasWidth: Float,
    canvasHeight: Float
): Float {
    if (userPath.isEmpty()) return 0f
    
    val centerX = canvasWidth / 2
    val centerY = canvasHeight / 2
    
    val startPoint = Offset(centerX + targetStroke.startPoint.x, centerY + targetStroke.startPoint.y)
    val endPoint = Offset(centerX + targetStroke.endPoint.x, centerY + targetStroke.endPoint.y)
    
    val startDistance = kotlin.math.sqrt(
        ((userPath.first().x - startPoint.x) * (userPath.first().x - startPoint.x) +
        (userPath.first().y - startPoint.y) * (userPath.first().y - startPoint.y)).toDouble()
    ).toFloat()
    
    val endDistance = kotlin.math.sqrt(
        ((userPath.last().x - endPoint.x) * (userPath.last().x - endPoint.x) +
        (userPath.last().y - endPoint.y) * (userPath.last().y - endPoint.y)).toDouble()
    ).toFloat()
    
    val maxDistance = 50f
    val startAccuracy = 1f - (startDistance / maxDistance).coerceAtMost(1f)
    val endAccuracy = 1f - (endDistance / maxDistance).coerceAtMost(1f)
    
    return (startAccuracy + endAccuracy) / 2f
}
