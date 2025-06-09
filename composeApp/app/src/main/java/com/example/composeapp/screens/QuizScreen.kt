package com.example.composeapp.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.composeapp.components.QuizItem
import com.example.composeapp.models.FlashcardCategory
import com.example.composeapp.models.SampleData
import com.example.composeapp.screens.components.QuizHistoryItem

data class Level(
    val id: String,
    val name: String,
    val description: String,
    val color: Color
)

val levels = listOf(
    Level(
        id = "N1",
        name = "N1",
        description = "Cấp độ cao nhất",
        color = Color(0xFFE91E63)
    ),
    Level(
        id = "N2",
        name = "N2",
        description = "Cấp độ cao cấp",
        color = Color(0xFF2196F3)
    ),
    Level(
        id = "N3",
        name = "N3",
        description = "Cấp độ trung cấp",
        color = Color(0xFF4CAF50)
    ),
    Level(
        id = "N4",
        name = "N4",
        description = "Cấp độ sơ trung cấp",
        color = Color(0xFFFF9800)
    ),
    Level(
        id = "N5",
        name = "N5",
        description = "Cấp độ sơ cấp",
        color = Color(0xFF9C27B0)
    )
)

data class QuizCategory(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val questionCount: Int
)

val categories = listOf(
    QuizCategory(
        title = "Từ vựng",
        icon = Icons.Default.Book,
        color = Color(0xFF2196F3),
        questionCount = 20
    ),
    QuizCategory(
        title = "Ngữ pháp",
        icon = Icons.Default.Grammar,
        color = Color(0xFF4CAF50),
        questionCount = 15
    ),
    QuizCategory(
        title = "Kanji",
        icon = Icons.Default.TextFields,
        color = Color(0xFFE91E63),
        questionCount = 25
    ),
    QuizCategory(
        title = "Nghe hiểu",
        icon = Icons.Default.Headphones,
        color = Color(0xFFFF9800),
        questionCount = 10
    )
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen() {
    var selectedCategory by remember { mutableStateOf<QuizCategory?>(null) }
    var showQuizDetail by remember { mutableStateOf(false) }
    var selectedLevel by remember { mutableStateOf<String?>(null) }

    if (showQuizDetail && selectedCategory != null && selectedLevel != null) {
        QuizDetailScreen(
            category = selectedCategory!!.title,
            level = selectedLevel!!,
            onBackPress = {
                showQuizDetail = false
                selectedLevel = null
            }
        )
    } else {
    Column(
            modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            tonalElevation = 4.dp
        ) {
            Text(
                    text = "Kiểm Tra Kiến Thức",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.padding(24.dp),
                textAlign = TextAlign.Center
            )
        }
        
        // Categories Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {
                items(categories) { category ->
                QuizCategoryCard(
                    category = category,
                        onClick = { selectedCategory = category }
                )
            }
            }
        }

        // Level Selection Dialog
        selectedCategory?.let { category ->
            LevelSelectionDialog(
                category = category,
                onDismiss = { selectedCategory = null },
                onLevelSelected = { level ->
                    selectedLevel = level
                    showQuizDetail = true
                    selectedCategory = null
                }
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizCategoryCard(
    category: QuizCategory,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = category.color
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = category.icon,
                contentDescription = category.title,
                modifier = Modifier.size(48.dp),
                tint = Color.White
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(4.dp))
            
            Text(
                text = "${category.questionCount} câu hỏi",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LevelSelectionDialog(
    category: QuizCategory,
    onDismiss: () -> Unit,
    onLevelSelected: (String) -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Chọn cấp độ ${category.title}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                levels.forEach { level ->
                    Button(
                        onClick = { onLevelSelected(level.id) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = level.color
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                Text(
                                text = level.name,
                    style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = level.description,
                                style = MaterialTheme.typography.bodySmall
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End)
                ) {
                    Text("Đóng")
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizContentScreen(
    category: FlashcardCategory,
    onBackPress: () -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    
    val questions = remember(category) {
        SampleData.quizQuestions.filter { it.category == category }
    }
    
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Quiz Header
        TopAppBar(
            title = { Text(text = category.displayName) },
            navigationIcon = {
                IconButton(onClick = onBackPress) {
                    Icon(
                        Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )
        
        if (questions.isEmpty()) {
            EmptyStateCard(
                message = "Chưa có câu hỏi cho chủ đề này",
                icon = Icons.Default.QuestionMark
            )
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Progress
                Text(
                    text = "Câu hỏi ${currentQuestionIndex + 1}/${questions.size}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                LinearProgressIndicator(
                    progress = { (currentQuestionIndex + 1).toFloat() / questions.size },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp)
                )
                
                // Question
                QuizItem(
                    question = questions[currentQuestionIndex],
                    onAnswerSelected = { selectedAnswer ->
                        if (selectedAnswer == questions[currentQuestionIndex].correctAnswer) {
                            score++
                        }
                        if (currentQuestionIndex < questions.size - 1) {
                            currentQuestionIndex++
                        }
                    }
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Score
                Text(
                    text = "Điểm: $score/${questions.size}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EmptyStateCard(
    message: String,
    icon: ImageVector
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = message,
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun QuizHistoryItem(
    title: String,
    date: String,
    score: Int,
    totalQuestions: Int
) {
    // Implementation of QuizHistoryItem composable
} 