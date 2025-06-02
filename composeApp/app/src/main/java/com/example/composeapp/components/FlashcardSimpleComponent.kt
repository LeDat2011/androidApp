package com.example.composeapp.components

import androidx.compose.animation.core.*
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapp.models.Flashcard
import kotlin.math.min

@Composable
fun FlashcardSimple(
    flashcard: Flashcard,
    modifier: Modifier = Modifier,
    onFlip: () -> Unit = {}
) {
    var isRevealed by remember { mutableStateOf(false) }
    
    // Animation for rotation
    val rotation by animateFloatAsState(
        targetValue = if (isRevealed) 180f else 0f,
        animationSpec = tween(
            durationMillis = 400,
            easing = FastOutSlowInEasing
        ),
        label = "card_flip"
    )
    
    // Animation for pressing effect
    var isPressed by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )
    
    // Dynamic color based on the flipped state
    val cardColor by animateColorAsState(
        targetValue = if (isRevealed) 
            Color(0xFF1E88E5) // Deeper blue when flipped
        else 
            Color(0xFF2196F3), // Standard blue
        animationSpec = tween(durationMillis = 400),
        label = "card_color"
    )
    
    // Card shadow elevation animation for depth effect
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 6.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "card_elevation"
    )
    
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(180.dp)
            .padding(horizontal = 16.dp)
    ) {
        // Main card with flip animation
        Card(
            modifier = Modifier
                .fillMaxSize()
                .shadow(elevation)
                .graphicsLayer {
                    rotationY = rotation
                    scaleX = scale
                    scaleY = scale
                    cameraDistance = 12f * density
                }
                .clickable {
                    isPressed = true
                    isRevealed = !isRevealed
                    onFlip()
                },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(
                containerColor = cardColor
            )
        ) {
            LaunchedEffect(isPressed) {
                if (isPressed) {
                    kotlinx.coroutines.delay(100)
                    isPressed = false
                }
            }
            
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                // Front side content (Japanese word)
                if (rotation < 90f) {
                    FlashcardContent(
                        mainText = flashcard.japaneseWord,
                        secondaryText = "Tap to reveal meaning",
                        mainTextSize = 32.sp,
                        // Make more transparent as we rotate
                        alpha = 1f - (min(1f, rotation / 90f))
                    )
                } else {
                    // Back side content (Vietnamese translation)
                    // Apply 180 degree rotation to make text readable after card flip
                    val exampleText = if (flashcard.examples.isNotEmpty()) {
                        flashcard.examples[0].japanese
                    } else {
                        ""
                    }
                    
                    FlashcardContent(
                        mainText = flashcard.vietnameseMeaning,
                        secondaryText = exampleText,
                        mainTextSize = 24.sp,
                        modifier = Modifier.graphicsLayer { rotationY = 180f },
                        // Fade in as we rotate past 90
                        alpha = (min(1f, (rotation - 90f) / 90f))
                    )
                }
            }
        }
        
        // Visual hint for user that card is flippable
        Text(
            text = "Tap card to flip",
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray,
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 4.dp)
        )
    }
}

@Composable
private fun FlashcardContent(
    mainText: String,
    secondaryText: String,
    mainTextSize: androidx.compose.ui.unit.TextUnit,
    modifier: Modifier = Modifier,
    alpha: Float = 1f
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = modifier.alpha(alpha)
    ) {
        Text(
            text = mainText,
            fontSize = mainTextSize,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Text(
            text = secondaryText,
            fontSize = 16.sp,
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )
    }
} 