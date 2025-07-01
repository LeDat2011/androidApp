package com.example.composeapp.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapp.models.Flashcard
import com.example.composeapp.models.MasteryLevel
import kotlin.math.abs
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun RecommendedFlashcardComponent(
    flashcard: Flashcard,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    onMarkLearned: () -> Unit,
    onMarkDifficult: () -> Unit,
    onMarkEasy: () -> Unit,
    canGoNext: Boolean,
    canGoPrevious: Boolean,
    modifier: Modifier = Modifier
) {
    var isFlipped by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    
    // Animation values
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing),
        label = "card_rotation"
    )
    
    val scale by animateFloatAsState(
        targetValue = if (abs(offsetX) > 0) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "card_scale"
    )

    // Danh sách màu sắc nổi bật
    val vibrantColors = listOf(
        Color(0xFFFF1744), // Bright Red
        Color(0xFF00E5FF), // Bright Cyan
        Color(0xFFFFEA00), // Bright Yellow
        Color(0xFF7C4DFF), // Bright Purple
        Color(0xFF00E676), // Bright Green
        Color(0xFF4CAF50), // Green
        Color(0xFF2979FF), // Bright Blue
        Color(0xFFFF9100), // Bright Orange
        Color(0xFF00BFA5), // Bright Teal
        Color(0xFF651FFF)  // Deep Purple
    )

    // Chọn màu dựa trên mức độ thành thạo
    val (color1, color2) = when (flashcard.masteryLevel) {
        MasteryLevel.NEW -> Pair(Color(0xFF2196F3), Color(0xFF03A9F4))       // Blue
        MasteryLevel.LEARNING -> Pair(Color(0xFFFF9800), Color(0xFFFFB74D))   // Orange
        MasteryLevel.REVIEWING -> Pair(Color(0xFF8BC34A), Color(0xFFAED581))  // Light Green
        MasteryLevel.MASTERED -> Pair(Color(0xFF4CAF50), Color(0xFF81C784))   // Green
    }

    // Tạo màu tương phản cho mặt sau
    val backColor1 = remember(flashcard.japaneseWord) { color1.copy(alpha = 0.8f) }
    val backColor2 = remember(flashcard.japaneseWord) { color2.copy(alpha = 0.8f) }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(175.dp)
        ) {
            // Main Flashcard
            Card(
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer {
                        rotationY = rotation
                        scaleX = scale
                        scaleY = scale
                        translationX = offsetX
                        cameraDistance = 12f * density
                    }
                    .pointerInput(Unit) {
                        detectTapGestures(
                            onTap = { isFlipped = !isFlipped }
                        )
                    }
                    .pointerInput(Unit) {
                        detectDragGestures(
                            onDragEnd = {
                                coroutineScope.launch {
                                    if (abs(offsetX) > size.width / 3) {
                                        if (offsetX > 0 && canGoPrevious) {
                                            onPrevious()
                                            isFlipped = false
                                        } else if (offsetX < 0 && canGoNext) {
                                            onNext()
                                            isFlipped = false
                                        }
                                    }
                                    offsetX = 0f
                                }
                            },
                            onDrag = { change, dragAmount ->
                                change.consume()
                                val newOffset = offsetX + dragAmount.x
                                if ((newOffset > 0 && canGoPrevious) || (newOffset < 0 && canGoNext)) {
                                    offsetX = newOffset.coerceIn(-size.width.toFloat(), size.width.toFloat())
                                }
                            }
                        )
                    },
                shape = RoundedCornerShape(16.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp,
                    focusedElevation = 6.dp,
                    hoveredElevation = 6.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = Color.Transparent
                )
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            brush = if (!isFlipped) {
                                Brush.linearGradient(
                                    colors = listOf(color1, color2)
                                )
                            } else {
                                Brush.linearGradient(
                                    colors = listOf(backColor1, backColor2)
                                )
                            }
                        )
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        // Front side
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { alpha = if (!isFlipped) 1f else 0f }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = flashcard.japaneseWord,
                                    fontSize = 36.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = flashcard.reading,
                                    fontSize = 18.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
                                
                                Spacer(modifier = Modifier.height(8.dp))
                                
                                // Hiển thị mức độ thành thạo
                                Card(
                                    shape = RoundedCornerShape(50),
                                    colors = CardDefaults.cardColors(
                                        containerColor = Color.White.copy(alpha = 0.3f)
                                    ),
                                    modifier = Modifier.padding(4.dp)
                                ) {
                                    Text(
                                        text = flashcard.masteryLevel.displayName,
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                                        color = Color.White,
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }
                        
                        // Back side
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer { 
                                    rotationY = 180f
                                    alpha = if (isFlipped) 1f else 0f
                                }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                Text(
                                    text = flashcard.vietnameseMeaning,
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                
                                if (flashcard.examples.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(8.dp))
                                    val example = flashcard.examples.first()
                                    Text(
                                        text = example.japanese,
                                        fontSize = 16.sp,
                                        color = Color.White.copy(alpha = 0.8f),
                                        textAlign = TextAlign.Center
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = example.vietnamese,
                                        fontSize = 14.sp,
                                        color = Color.White.copy(alpha = 0.6f),
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Hiển thị badge mức độ thành thạo ở góc
            Box(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(4.dp)
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(
                        when (flashcard.masteryLevel) {
                            MasteryLevel.NEW -> Color(0xFF2196F3)
                            MasteryLevel.LEARNING -> Color(0xFFFF9800)
                            MasteryLevel.REVIEWING -> Color(0xFF8BC34A)
                            MasteryLevel.MASTERED -> Color(0xFF4CAF50)
                        }
                    )
                    .padding(4.dp),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = when (flashcard.masteryLevel) {
                        MasteryLevel.NEW -> Icons.Default.Star
                        MasteryLevel.LEARNING -> Icons.Default.School
                        MasteryLevel.REVIEWING -> Icons.Default.Refresh
                        MasteryLevel.MASTERED -> Icons.Default.Check
                    },
                    contentDescription = flashcard.masteryLevel.displayName,
                    tint = Color.White,
                    modifier = Modifier.size(16.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Hiển thị hướng dẫn
        Text(
            text = "Chạm vào thẻ để lật",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Các nút đánh giá và điều hướng
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Hàng nút đánh giá
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Nút đánh dấu khó
                FilledTonalButton(
                    onClick = onMarkDifficult,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFFFCDD2)
                    ),
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentVeryDissatisfied,
                            contentDescription = "Khó",
                            tint = Color(0xFFF44336),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            "Khó", 
                            color = Color(0xFFF44336),
                            fontSize = 12.sp
                        )
                    }
                }
                
                // Nút đánh dấu đã học
                FilledTonalButton(
                    onClick = onMarkLearned,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFE8F5E9)
                    ),
                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Đã học",
                            tint = Color(0xFF4CAF50),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            "Đã học", 
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp
                        )
                    }
                }
                
                // Nút đánh dấu dễ
                FilledTonalButton(
                    onClick = onMarkEasy,
                    colors = ButtonDefaults.filledTonalButtonColors(
                        containerColor = Color(0xFFE3F2FD)
                    ),
                    modifier = Modifier.weight(1f).padding(start = 4.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SentimentVerySatisfied,
                            contentDescription = "Dễ",
                            tint = Color(0xFF2196F3),
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(2.dp))
                        Text(
                            "Dễ", 
                            color = Color(0xFF2196F3),
                            fontSize = 12.sp
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Hàng nút điều hướng
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Nút trước
                FilledTonalIconButton(
                    onClick = {
                        if (canGoPrevious) {
                            onPrevious()
                            isFlipped = false
                        }
                    },
                    enabled = canGoPrevious,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Trước",
                        tint = if (canGoPrevious) 
                            MaterialTheme.colorScheme.onSecondaryContainer 
                        else 
                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f),
                        modifier = Modifier.size(16.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Nút lật
                FilledTonalIconButton(
                    onClick = { isFlipped = !isFlipped },
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Flip,
                        contentDescription = "Lật thẻ",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(20.dp)
                    )
                }
                
                Spacer(modifier = Modifier.width(8.dp))
                
                // Nút tiếp theo
                FilledTonalIconButton(
                    onClick = {
                        if (canGoNext) {
                            onNext()
                            isFlipped = false
                        }
                    },
                    enabled = canGoNext,
                    colors = IconButtonDefaults.filledTonalIconButtonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                    ),
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowForward,
                        contentDescription = "Tiếp theo",
                        tint = if (canGoNext) 
                            MaterialTheme.colorScheme.onSecondaryContainer 
                        else 
                            MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
} 