package com.example.composeapp.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.draganddrop.dragAndDropTarget
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
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
import com.example.composeapp.models.PuzzlePiece

@Composable
fun WordPuzzleGame(
    question: GameQuestion,
    onAnswer: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedPieces by remember { mutableStateOf(mutableListOf<PuzzlePiece>()) }
    var availablePieces by remember { mutableStateOf(mutableListOf<PuzzlePiece>()) }
    var isCorrect by remember { mutableStateOf<Boolean?>(null) }
    
    // Khởi tạo puzzle pieces
    LaunchedEffect(question.id) {
        availablePieces = question.options.mapIndexed { index, option ->
            PuzzlePiece(
                id = "piece_$index",
                text = option,
                isCorrect = false,
                isSelected = false,
                position = index
            )
        }.toMutableList()
        selectedPieces.clear()
        isCorrect = null
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
            // Tiêu đề và câu hỏi
            Text(
                text = question.question,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Nghĩa tiếng Anh
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer
                )
            ) {
                Text(
                    text = "English: ${question.englishText}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                    modifier = Modifier.padding(12.dp),
                    textAlign = TextAlign.Center
                )
            }
            
            // Khu vực đáp án đã chọn
            PuzzleAnswerArea(
                selectedPieces = selectedPieces,
                onPieceRemoved = { piece ->
                    selectedPieces.remove(piece)
                    availablePieces.add(piece.copy(isSelected = false))
                }
            )
            
            // Khu vực các mảnh puzzle có sẵn
            PuzzlePieceArea(
                availablePieces = availablePieces,
                onPieceSelected = { piece ->
                    if (!piece.isSelected) {
                        selectedPieces.add(piece.copy(isSelected = true))
                        availablePieces.remove(piece)
                    }
                }
            )
            
            // Hiển thị kết quả
            when (isCorrect) {
                true -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFF4CAF50).copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = "✅ Chính xác!",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFF4CAF50),
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                false -> {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF44336).copy(alpha = 0.1f)
                        )
                    ) {
                        Text(
                            text = "❌ Chưa đúng. Thử lại!",
                            style = MaterialTheme.typography.titleMedium,
                            color = Color(0xFFF44336),
                            modifier = Modifier.padding(12.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                null -> {}
            }
            
            // Nút kiểm tra
            Button(
                onClick = {
                    val userAnswer = selectedPieces.joinToString("") { it.text }
                    val correctAnswer = question.correctAnswer
                    
                    isCorrect = userAnswer.equals(correctAnswer, ignoreCase = true)
                    
                    if (isCorrect == true) {
                        onAnswer(userAnswer)
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = selectedPieces.isNotEmpty()
            ) {
                Text(
                    text = if (isCorrect == true) "Tiếp tục" else "Kiểm tra",
                    style = MaterialTheme.typography.titleMedium
                )
            }
            
            // Nút reset
            if (selectedPieces.isNotEmpty()) {
                OutlinedButton(
                    onClick = {
                        selectedPieces.forEach { piece ->
                            availablePieces.add(piece.copy(isSelected = false))
                        }
                        selectedPieces.clear()
                        isCorrect = null
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Làm lại")
                }
            }
        }
    }
}

@Composable
private fun PuzzleAnswerArea(
    selectedPieces: List<PuzzlePiece>,
    onPieceRemoved: (PuzzlePiece) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .border(
                    width = 2.dp,
                    color = if (selectedPieces.isEmpty()) 
                        MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                    else 
                        MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(8.dp)
        ) {
            if (selectedPieces.isEmpty()) {
                Text(
                    text = "Kéo các từ vào đây để tạo câu",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(selectedPieces) { piece ->
                        PuzzlePieceChip(
                            piece = piece,
                            onRemove = { onPieceRemoved(piece) },
                            isSelected = true
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun PuzzlePieceArea(
    availablePieces: List<PuzzlePiece>,
    onPieceSelected: (PuzzlePiece) -> Unit
) {
    Column {
        Text(
            text = "Các từ có sẵn:",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(availablePieces) { piece ->
                PuzzlePieceChip(
                    piece = piece,
                    onRemove = null,
                    isSelected = false,
                    onClick = { onPieceSelected(piece) }
                )
            }
        }
    }
}

@Composable
private fun PuzzlePieceChip(
    piece: PuzzlePiece,
    onRemove: ((PuzzlePiece) -> Unit)?,
    isSelected: Boolean,
    onClick: (() -> Unit)? = null
) {
    Card(
        onClick = { onClick?.invoke() },
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) 
                MaterialTheme.colorScheme.primary 
            else 
                MaterialTheme.colorScheme.secondaryContainer
        ),
        modifier = Modifier.height(48.dp)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = piece.text,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = if (isSelected) 
                    MaterialTheme.colorScheme.onPrimary 
                else 
                    MaterialTheme.colorScheme.onSecondaryContainer
            )
            
            if (onRemove != null) {
                TextButton(
                    onClick = { onRemove(piece) },
                    modifier = Modifier.size(24.dp),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Text(
                        text = "×",
                        style = MaterialTheme.typography.titleLarge,
                        color = if (isSelected) 
                            MaterialTheme.colorScheme.onPrimary 
                        else 
                            MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }
        }
    }
}
