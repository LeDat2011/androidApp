package com.example.composeapp.components

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.unit.dp
import com.example.composeapp.models.QuizQuestion

@Composable
fun QuizItem(
    question: QuizQuestion,
    onAnswerSelected: (Int) -> Unit
) {
    var selectedAnswer by remember { mutableStateOf<Int?>(null) }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioMediumBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Spacer(modifier = Modifier.height(24.dp))
            
            question.options.forEachIndexed { index, option ->
                val isSelected = selectedAnswer == index
                val isCorrect = index == question.correctAnswer
                val showResult = selectedAnswer != null
                
                val backgroundColor by animateColorAsState(
                    targetValue = when {
                        !showResult -> MaterialTheme.colorScheme.secondaryContainer
                        isSelected && isCorrect -> Color(0xFF4CAF50) // Success Green
                        isSelected -> Color(0xFFF44336) // Error Red
                        else -> MaterialTheme.colorScheme.secondaryContainer
                    },
                    animationSpec = tween(300),
                    label = "button_color"
                )
                
                val contentColor by animateColorAsState(
                    targetValue = when {
                        !showResult -> MaterialTheme.colorScheme.onSecondaryContainer
                        isSelected && isCorrect -> Color.White
                        isSelected -> Color.White
                        else -> MaterialTheme.colorScheme.onSecondaryContainer
                    },
                    animationSpec = tween(300),
                    label = "text_color"
                )
                
                val elevation by animateFloatAsState(
                    targetValue = if (isSelected) 8f else 2f,
                    label = "elevation"
                )
                
                val scale by animateFloatAsState(
                    targetValue = if (isSelected) 1.03f else 1f,
                    animationSpec = spring(
                        dampingRatio = Spring.DampingRatioMediumBouncy,
                        stiffness = Spring.StiffnessLow
                    ),
                    label = "scale"
                )
                
                OutlinedButton(
                    onClick = {
                        if (selectedAnswer == null) {
                            selectedAnswer = index
                            onAnswerSelected(index)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .graphicsLayer {
                            scaleX = scale
                            scaleY = scale
                            this.shadowElevation = elevation
                        },
                    shape = RoundedCornerShape(12.dp),
                    border = BorderStroke(
                        width = 1.dp,
                        color = if (isSelected) backgroundColor else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                    ),
                    colors = ButtonDefaults.outlinedButtonColors(
                        containerColor = backgroundColor,
                        contentColor = contentColor
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${('A' + index)}.",
                            style = MaterialTheme.typography.labelLarge,
                            modifier = Modifier.padding(end = 12.dp)
                        )
                        Text(
                            text = option,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            }
        }
    }
} 