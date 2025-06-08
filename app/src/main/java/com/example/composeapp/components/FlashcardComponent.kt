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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapp.screens.FlashcardData
import kotlin.math.abs
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun FlashcardComponent(
    flashcard: FlashcardData,
    onNext: () -> Unit,
    onPrevious: () -> Unit,
    canGoNext: Boolean,
    canGoPrevious: Boolean
) {
    var isFlipped by remember { mutableStateOf(false) }
    var offsetX by remember { mutableStateOf(0f) }
    val coroutineScope = rememberCoroutineScope()
    
    // Animation values
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(500, easing = FastOutSlowInEasing)
    )
    
    val scale by animateFloatAsState(
        targetValue = if (abs(offsetX) > 0) 0.9f else 1f,
        animationSpec = spring(stiffness = Spring.StiffnessLow)
    )

    // Danh sách màu đẹp cho flashcard
    val cardColors = listOf(
        Color(0xFFE3F2FD), // Light Blue
        Color(0xFFE8F5E9), // Light Green
        Color(0xFFFFF3E0), // Light Orange
        Color(0xFFF3E5F5), // Light Purple
        Color(0xFFE0F7FA), // Light Cyan
        Color(0xFFFCE4EC), // Light Pink
        Color(0xFFF1F8E9), // Light Lime
        Color(0xFFE8EAF6), // Light Indigo
        Color(0xFFFFEBEE), // Light Red
        Color(0xFFEFEBE9)  // Light Brown
    )

    // Chọn màu ngẫu nhiên và lưu trữ nó
    val cardColor = remember { cardColors[Random.nextInt(cardColors.size)] }

    // Danh sách màu sắc nổi bật
    val vibrantColors = listOf(
        Color(0xFFFF1744), // Bright Red
        Color(0xFF00E5FF), // Bright Cyan
        Color(0xFFFFEA00), // Bright Yellow
        Color(0xFF7C4DFF), // Bright Purple
        Color(0xFF00E676), // Bright Green
        Color(0xFFFF4081), // Bright Pink
        Color(0xFF2979FF), // Bright Blue
        Color(0xFFFF9100), // Bright Orange
        Color(0xFF00BFA5), // Bright Teal
        Color(0xFF651FFF), // Deep Purple
        Color(0xFFD500F9), // Bright Magenta
        Color(0xFF00E676), // Bright Emerald
        Color(0xFFFF3D00), // Bright Orange Red
        Color(0xFF00B0FF), // Bright Sky Blue
        Color(0xFFC6FF00), // Bright Lime
        Color(0xFFFFD600), // Bright Amber
        Color(0xFF00E5FF), // Bright Aqua
        Color(0xFFFF1744), // Bright Crimson
        Color(0xFF00E676), // Bright Mint
        Color(0xFF7C4DFF), // Bright Indigo
        Color(0xFFFF4081), // Bright Rose
        Color(0xFF00BFA5), // Bright Turquoise
        Color(0xFFFF9100), // Bright Amber
        Color(0xFF2979FF), // Bright Azure
        Color(0xFF00E676)  // Bright Jade
    )

    // Animation cho gradient
    val infiniteTransition = rememberInfiniteTransition(label = "gradient")
    val angle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(10000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "angle"
    )

    // Chọn 2 màu ngẫu nhiên cho gradient và cập nhật khi flashcard thay đổi
    val color1 = remember(flashcard.word) { vibrantColors[Random.nextInt(vibrantColors.size)] }
    val color2 = remember(flashcard.word) { vibrantColors[Random.nextInt(vibrantColors.size)] }

    // Tạo màu tương phản cho mặt sau
    val backColor1 = remember(flashcard.word) { color1.copy(alpha = 0.8f) }
    val backColor2 = remember(flashcard.word) { color2.copy(alpha = 0.8f) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(400.dp)
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
                    defaultElevation = 8.dp,
                    pressedElevation = 12.dp,
                    focusedElevation = 10.dp,
                    hoveredElevation = 10.dp
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
                                Brush.sweepGradient(
                                    colors = listOf(color1, color2, color1),
                                    center = androidx.compose.ui.geometry.Offset.Zero
                                )
                            } else {
                                Brush.sweepGradient(
                                    colors = listOf(backColor1, backColor2, backColor1),
                                    center = androidx.compose.ui.geometry.Offset.Zero
                                )
                            }
                        )
                        .padding(24.dp),
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
                                    text = flashcard.word,
                                    fontSize = 48.sp,
                                    fontWeight = FontWeight.Bold,
                                    textAlign = TextAlign.Center,
                                    color = Color.White
                                )
                                Spacer(modifier = Modifier.height(16.dp))
                                Text(
                                    text = flashcard.reading,
                                    fontSize = 24.sp,
                                    color = Color.White.copy(alpha = 0.7f),
                                    textAlign = TextAlign.Center
                                )
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
                                    text = flashcard.meaning,
                                    fontSize = 32.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(24.dp))
                                Text(
                                    text = flashcard.example,
                                    fontSize = 20.sp,
                                    color = Color.White.copy(alpha = 0.8f),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = flashcard.exampleMeaning,
                                    fontSize = 16.sp,
                                    color = Color.White.copy(alpha = 0.6f),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation and flip buttons in one row
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            FilledTonalIconButton(
                onClick = {
                    if (canGoPrevious) {
                        onPrevious()
                        isFlipped = false
                    }
                },
                enabled = canGoPrevious,
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Previous",
                    tint = if (canGoPrevious) 
                        MaterialTheme.colorScheme.onSecondaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f)
                )
            }

            // Flip button
            FilledTonalButton(
                onClick = { isFlipped = !isFlipped },
                modifier = Modifier.size(64.dp),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                elevation = ButtonDefaults.filledTonalButtonElevation(
                    defaultElevation = 4.dp,
                    pressedElevation = 8.dp,
                    hoveredElevation = 6.dp,
                    focusedElevation = 6.dp
                )
            ) {
                Icon(
                    imageVector = if (!isFlipped) Icons.Default.Flip else Icons.Default.Flip,
                    contentDescription = "Flip card",
                    modifier = Modifier.size(32.dp),
                    tint = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }

            // Next button
            FilledTonalIconButton(
                onClick = {
                    if (canGoNext) {
                        onNext()
                        isFlipped = false
                    }
                },
                enabled = canGoNext,
                modifier = Modifier.size(56.dp),
                colors = IconButtonDefaults.filledTonalIconButtonColors(
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    disabledContainerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.3f)
                )
            ) {
                Icon(
                    imageVector = Icons.Default.ArrowForward,
                    contentDescription = "Next",
                    tint = if (canGoNext) 
                        MaterialTheme.colorScheme.onSecondaryContainer 
                    else 
                        MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.3f)
                )
            }
        }
    }
}