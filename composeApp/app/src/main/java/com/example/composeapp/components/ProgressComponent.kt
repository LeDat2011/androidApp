package com.example.composeapp.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapp.models.LearningProgress

@Composable
fun ProgressCard(progress: LearningProgress) {
    val progressAnimation by animateFloatAsState(
        targetValue = progress.accuracy,
        animationSpec = tween(1000, easing = FastOutSlowInEasing),
        label = "progress_animation"
    )
    
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Progress circle
            Box(
                modifier = Modifier
                    .size(180.dp)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                ProgressArc(
                    progress = progressAnimation,
                    modifier = Modifier.fillMaxSize()
                )
                
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "${(progressAnimation * 100).toInt()}%",
                        fontSize = 28.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Chính xác",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Stats row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                StatColumn("Từ vựng", progress.wordsLearned.toString())
                StatColumn("Bài kiểm tra", progress.quizzesCompleted.toString())
            }
        }
    }
}

@Composable
private fun ProgressArc(progress: Float, modifier: Modifier = Modifier) {
    val progressColor = when {
        progress >= 0.8f -> Color(0xFF4CAF50) // Green
        progress >= 0.6f -> Color(0xFF8BC34A) // Light Green
        progress >= 0.4f -> Color(0xFFFFC107) // Yellow
        else -> Color(0xFFF44336) // Red
    }
    
    val surfaceVariant = MaterialTheme.colorScheme.surfaceVariant
    
    Canvas(modifier = modifier) {
        // Background circle
        drawArc(
            color = surfaceVariant,
            startAngle = 0f,
            sweepAngle = 360f,
            useCenter = false,
            style = Stroke(width = 24f, cap = StrokeCap.Round)
        )
        
        // Progress arc
        drawArc(
            color = progressColor,
            startAngle = 270f,
            sweepAngle = progress * 360f,
            useCenter = false,
            style = Stroke(width = 24f, cap = StrokeCap.Round)
        )
    }
}

@Composable
private fun StatColumn(
    title: String,
    value: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = value,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
        Text(
            text = title,
            fontSize = 14.sp,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            textAlign = TextAlign.Center
        )
    }
} 