package com.example.composeapp.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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

@Composable
fun MemoryGameComponent(
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
            
            // Từ tiếng Nhật
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = question.japaneseText,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(24.dp),
                    textAlign = TextAlign.Center
                )
            }
            
            // Các lựa chọn
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.height(200.dp)
            ) {
                items(question.options) { option ->
                    MemoryOptionButton(
                        option = option,
                        isSelected = selectedAnswer == option,
                        isCorrect = showResult == true && option == question.correctAnswer,
                        isWrong = showResult == false && option == selectedAnswer && option != question.correctAnswer,
                        isDisabled = isAnswered && option != selectedAnswer && option != question.correctAnswer,
                        onClick = {
                            if (!isAnswered) {
                                selectedAnswer = option
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
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "🎉 Chính xác!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF4CAF50)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = question.explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
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
                        Column(
                            modifier = Modifier.padding(16.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = "❌ Sai rồi!",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFF44336)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = "Đáp án đúng: ${question.correctAnswer}",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = question.explanation,
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
                null -> {}
            }
            
            // Nút xác nhận
            Button(
                onClick = {
                    if (selectedAnswer != null && !isAnswered) {
                        isAnswered = true
                        showResult = selectedAnswer == question.correctAnswer
                        
                        if (showResult == true) {
                            onAnswer(selectedAnswer!!)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedAnswer != null && !isAnswered
            ) {
                Text(
                    text = if (isAnswered) "Tiếp tục" else "Xác nhận",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            // Nút tiếp tục khi đã trả lời sai
            if (isAnswered && showResult == false) {
                Button(
                    onClick = {
                        onAnswer(question.correctAnswer) // Cho phép tiếp tục với đáp án đúng
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    )
                ) {
                    Text("Tiếp tục")
                }
            }
        }
    }
}

@Composable
private fun MemoryOptionButton(
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
            .height(80.dp)
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isSelected) 8.dp else 2.dp
        )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                if (icon.isNotEmpty()) {
                    Text(
                        text = icon,
                        fontSize = 24.sp,
                        modifier = Modifier.padding(bottom = 4.dp)
                    )
                }
                Text(
                    text = option,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = textColor,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}
