package com.example.composeapp.components

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.gestures.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.abs
import kotlinx.coroutines.launch
import com.example.composeapp.screens.FlashcardData

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

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(400.dp)
            .padding(16.dp)
    ) {
        // Swipe gesture
        Card(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = rotation
                    scaleX = scale
                    scaleY = scale
                    translationX = offsetX
                }
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragEnd = {
                            coroutineScope.launch {
                                if (abs(offsetX) > size.width / 3) {
                                    if (offsetX > 0 && canGoPrevious) {
                                        onPrevious()
                                    } else if (offsetX < 0 && canGoNext) {
                                        onNext()
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
                }
                .pointerInput(Unit) {
                    detectTapGestures(
                        onDoubleTap = { isFlipped = !isFlipped }
                    )
                },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                // Front side
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { alpha = if (!isFlipped) 1f else 0f }
                ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                ) {
                    Text(
                            text = flashcard.word,
                            fontSize = 48.sp,
                        fontWeight = FontWeight.Bold,
                            textAlign = TextAlign.Center
                    )
                        Spacer(modifier = Modifier.height(16.dp))
                    Text(
                            text = flashcard.reading,
                            fontSize = 24.sp,
                            color = MaterialTheme.colorScheme.primary,
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
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        Text(
                            text = flashcard.meaning,
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        Text(
                            text = flashcard.example,
                            fontSize = 20.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = flashcard.exampleMeaning,
                            fontSize = 16.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                // Gesture indicators
            Box(
                modifier = Modifier
                    .fillMaxSize()
                        .padding(16.dp)
                ) {
                    // Previous card indicator
                    if (canGoPrevious && offsetX > 50) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Previous",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.CenterStart)
                                .size(32.dp)
                        )
                    }

                    // Next card indicator
                    if (canGoNext && offsetX < -50) {
                        Icon(
                            Icons.Default.ArrowForward,
                            contentDescription = "Next",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .align(Alignment.CenterEnd)
                                .size(32.dp)
                        )
                    }

                    // Flip indicator
                    if (abs(offsetX) < 50) {
                        Text(
                            text = "Nhấn đúp để lật",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
                            modifier = Modifier.align(Alignment.BottomCenter)
                        )
                    }
                }
            }
        }
    }
}