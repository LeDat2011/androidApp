package com.example.composeapp.components

import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapp.models.Flashcard
import kotlinx.coroutines.delay
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.util.lerp

@Composable
fun FlashcardItem(
    flashcard: Flashcard,
    isFlipped: Boolean,
    onFlip: () -> Unit
) {
    // Enhanced haptic feedback on flip
    val haptic = LocalHapticFeedback.current
    
    // Enhanced 3D rotation with perspective adjustment
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 600, easing = EaseInOutQuad),
        label = "card_flip"
    )
    
    // Scale animation for card
    var isPressed by remember { mutableStateOf(false) }
    val pressScale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "press_scale"
    )
    
    // Animation for bounce effect after completion
    val flipScale by animateFloatAsState(
        targetValue = if ((rotation % 360) == 0f || (rotation % 360) == 180f) 1f else 
                      lerp(1f, 1.05f, ((rotation % 180) / 90f) * (1f - ((rotation % 180) / 90f)) * 4f),
        label = "flip_scale"
    )
    
    // Elevation animation
    val elevation by animateFloatAsState(
        targetValue = if (isPressed) 4f else 8f,
        animationSpec = tween(200),
        label = "card_elevation"
    )
    
    // Enhanced color animations
    val frontColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.primaryContainer,
        animationSpec = tween(300),
        label = "front_color"
    )
    
    val backColor by animateColorAsState(
        targetValue = MaterialTheme.colorScheme.secondaryContainer,
        animationSpec = tween(300),
        label = "back_color"
    )
    
    // Dynamic shadow based on card orientation
    val shadowAlpha by animateFloatAsState(
        targetValue = if (rotation > 90f) 0f else 0.2f * (1f - (rotation / 90f)),
        label = "shadow_alpha"
    )
    
    // Main container
    Box(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .height(240.dp),
        contentAlignment = Alignment.Center
    ) {
        // Enhanced shadow effect for depth
        Box(
            modifier = Modifier
                .size(width = 300.dp, height = 200.dp)
                .clip(RoundedCornerShape(16.dp))
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(16.dp),
                    ambientColor = Color.Black.copy(alpha = shadowAlpha),
                    spotColor = Color.Black.copy(alpha = shadowAlpha)
                )
                .align(Alignment.Center)
        )
        
        // Front card (Japanese)
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(200.dp)
                .graphicsLayer {
                    rotationY = rotation
                    scaleX = pressScale * flipScale 
                    scaleY = pressScale * flipScale
                    cameraDistance = 16f * density // Enhanced perspective
                    alpha = if (rotation >= 90f) 0f else 1f
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isPressed = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress) // Added haptic feedback
                    onFlip()
                },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
            colors = CardDefaults.cardColors(containerColor = frontColor)
        ) {
            LaunchedEffect(isPressed) {
                if (isPressed) {
                    delay(150)
                    isPressed = false
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = flashcard.japaneseWord,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    
                    Text(
                        text = "Nhấn để xem nghĩa",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
        
        // Back card (Vietnamese meaning)
        Card(
            modifier = Modifier
                .fillMaxWidth(0.85f)
                .height(200.dp)
                .graphicsLayer {
                    rotationY = rotation - 180f
                    scaleX = pressScale * flipScale
                    scaleY = pressScale * flipScale
                    cameraDistance = 16f * density // Enhanced perspective
                    alpha = if (rotation < 90f) 0f else 1f
                }
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = null
                ) {
                    isPressed = true
                    haptic.performHapticFeedback(HapticFeedbackType.LongPress) // Added haptic feedback
                    onFlip()
                },
            shape = RoundedCornerShape(16.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = elevation.dp),
            colors = CardDefaults.cardColors(containerColor = backColor)
        ) {
            LaunchedEffect(isPressed) {
                if (isPressed) {
                    delay(150)
                    isPressed = false
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = flashcard.vietnameseMeaning,
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    
                    if (flashcard.examples.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = flashcard.examples[0].japanese,
                            fontSize = 16.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                        )
                        Text(
                            text = flashcard.examples[0].vietnamese,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
                        )
                    }
                }
            }
        }
    }
}

// Custom easing for a smoother flip animation
private val EaseInOutQuad = CubicBezierEasing(0.455f, 0.03f, 0.515f, 0.955f) 