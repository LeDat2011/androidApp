package com.example.composeapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.composeapp.models.GameQuestion
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SpeedQuizComponent(
    question: GameQuestion,
    onAnswer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedAnswer by remember { mutableStateOf<String?>(null) }
    var isAnswered by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf<Boolean?>(null) }
    
    // Reset state khi câu hỏi thay đổi
    LaunchedEffect(question.id) {
        selectedAnswer = null
        isAnswered = false
        showResult = null
    }
    
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Tiêu đề câu hỏi
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            // Nội dung câu hỏi (tiếng Nhật)
            if (question.japaneseText.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = question.japaneseText,
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Các lựa chọn
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(question.options) { option ->
                    SpeedQuizOption(
                        option = option,
                        isSelected = selectedAnswer == option,
                        isCorrect = showResult == true && option == question.correctAnswer,
                        isWrong = showResult == false && option == selectedAnswer && option != question.correctAnswer,
                        isDisabled = isAnswered && option != selectedAnswer && option != question.correctAnswer,
                        onClick = {
                            if (!isAnswered) {
                                selectedAnswer = option
                                // Tự động xác nhận sau khi chọn
                                isAnswered = true
                                showResult = option == question.correctAnswer
                                
                                // Tự động chuyển câu sau 1.5 giây
                                GlobalScope.launch {
                                    delay(1500)
                                    onAnswer(option)
                                }
                            }
                        }
                    )
                }
            }
            
            // Hiển thị kết quả
            when (showResult) {
                true -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "✅",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Chính xác!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                        }
                    }
                }
                false -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF44336).copy(alpha = 0.1f)
                        )
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "❌",
                                fontSize = 24.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Sai rồi!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF44336)
                            )
                        }
                    }
                }
                null -> {}
            }
            
            // Hiển thị giải thích nếu có
            if (isAnswered && question.explanation.isNotEmpty()) {
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surface
                    )
                ) {
                    Text(
                        text = question.explanation,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.padding(12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            // Nút tiếp tục (hiển thị khi chưa tự động chuyển)
            if (isAnswered && showResult != null) {
                Button(
                    onClick = { onAnswer(selectedAnswer ?: "") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (showResult == true) 
                            Color(0xFF4CAF50) 
                        else 
                            Color(0xFFF44336)
                    )
                ) {
                    Text(
                        text = "Tiếp tục",
                        style = MaterialTheme.typography.titleMedium,
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
private fun SpeedQuizOption(
    option: String,
    isSelected: Boolean,
    isCorrect: Boolean,
    isWrong: Boolean,
    isDisabled: Boolean,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isCorrect -> Color(0xFF4CAF50)
        isWrong -> Color(0xFFF44336)
        isSelected -> MaterialTheme.colorScheme.primary
        isDisabled -> MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.secondaryContainer
    }
    
    val textColor = when {
        isCorrect -> Color.White
        isWrong -> Color.White
        isSelected -> MaterialTheme.colorScheme.onPrimary
        isDisabled -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
        else -> MaterialTheme.colorScheme.onSecondaryContainer
    }
    
    val icon = when {
        isCorrect -> "✅"
        isWrong -> "❌"
        else -> ""
    }
    
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = backgroundColor),
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (icon.isNotEmpty()) {
                    Text(
                        text = icon,
                        fontSize = 20.sp
                    )
                }
                Text(
                    text = option,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = textColor
                )
            }
            
            // Hiển thị ký hiệu cho option được chọn
            if (isSelected && !isCorrect && !isWrong) {
                Text(
                    text = "→",
                    style = MaterialTheme.typography.titleLarge,
                    color = textColor
                )
            }
        }
    }
}
