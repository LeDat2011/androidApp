package com.example.composeapp.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapp.models.StudyCategory

@Composable
fun CategoryCard(
    category: StudyCategory,
    modifier: Modifier = Modifier,
    onClick: () -> Unit = {},
    completed: Boolean = false,
    progressPercent: Float = 0f
) {
    // Enhanced animations
    var isPressed by remember { mutableStateOf(false) }
    
    // Animation for scale when pressed
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessLow
        ),
        label = "card_scale"
    )
    
    // Animation for elevation
    val elevation by animateDpAsState(
        targetValue = if (isPressed) 2.dp else 4.dp,
        animationSpec = spring(stiffness = Spring.StiffnessLow),
        label = "card_elevation"
    )
    
    // Choose color pair based on category
    val (mainColor, gradientColor) = when (category.id) {
        "food_drinks" -> Pair(Color(0xFF42A5F5), Color(0xFF1976D2)) // Blue
        "home" -> Pair(Color(0xFF66BB6A), Color(0xFF388E3C)) // Green
        "travel" -> Pair(Color(0xFFFFD54F), Color(0xFFFFA000)) // Yellow/Orange
        "business" -> Pair(Color(0xFF9575CD), Color(0xFF5E35B1)) // Purple
        else -> Pair(Color(0xFF42A5F5), Color(0xFF1976D2)) // Default blue
    }
    
    // Create gradient brush
    val gradientBrush = Brush.linearGradient(
        colors = listOf(mainColor.copy(alpha = 0.7f), gradientColor.copy(alpha = 0.7f))
    )
    
    Card(
        modifier = modifier
            .size(width = 160.dp, height = 120.dp)
            .shadow(elevation, RoundedCornerShape(16.dp))
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable {
                isPressed = true
                onClick()
            },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        )
    ) {
        LaunchedEffect(isPressed) {
            if (isPressed) {
                kotlinx.coroutines.delay(100)
                isPressed = false
            }
        }
        
        Box(modifier = Modifier.fillMaxSize()) {
            // Background gradient
            Spacer(
                modifier = Modifier
                    .fillMaxSize()
                    .clip(RoundedCornerShape(16.dp))
                    .background(gradientBrush)
            )
            
            // Content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top row with emoji and completed indicator if needed
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    // Emoji with larger size
                    Text(
                        text = category.emoji,
                        fontSize = 28.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                    
                    // Completion indicator
                    if (completed) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color(0xFF4CAF50))
                                .padding(4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Completed",
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Column {
                    // Category title
                    Text(
                        text = category.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = Color.White
                    )
                    
                    // Word count
                    Text(
                        text = "${category.wordCount} words",
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    
                    // Progress bar if needed
                    if (progressPercent > 0f && !completed) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        LinearProgressIndicator(
                            progress = { progressPercent },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(4.dp)
                                .clip(RoundedCornerShape(2.dp)),
                            color = Color.White,
                            trackColor = Color.White.copy(alpha = 0.3f)
                        )
                        
                        Text(
                            text = "${(progressPercent * 100).toInt()}% complete",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 10.sp
                        )
                    }
                }
            }
            
            // Arrow indicator
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = "Open Category",
                tint = Color.White,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(8.dp)
                    .size(16.dp)
            )
        }
    }
} 