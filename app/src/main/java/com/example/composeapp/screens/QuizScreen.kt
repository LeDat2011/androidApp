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
import com.example.composeapp.components.QuizItem
import com.example.composeapp.models.FlashcardCategory
import com.example.composeapp.models.SampleData
import com.example.composeapp.screens.components.QuizHistoryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    modifier: Modifier = Modifier,
    onNavigateToQuiz: (FlashcardCategory) -> Unit
) {
    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Header
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = MaterialTheme.colorScheme.primary,
            tonalElevation = 4.dp
        ) {
            Text(
                text = "Kiểm tra kiến thức",
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
            items(FlashcardCategory.values()) { category ->
                QuizCategoryCard(
                    category = category,
                    onClick = { onNavigateToQuiz(category) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizCategoryCard(
    category: FlashcardCategory,
    onClick: () -> Unit
) {
    val categoryColor = when (category) {
        FlashcardCategory.ANIMALS -> Color(0xFF4CAF50)      // Green
        FlashcardCategory.FOOD -> Color(0xFFE91E63)         // Pink
        FlashcardCategory.TRANSPORTATION -> Color(0xFF2196F3) // Blue
        FlashcardCategory.WEATHER -> Color(0xFFFFC107)      // Amber
        FlashcardCategory.FAMILY -> Color(0xFF9C27B0)       // Purple
        FlashcardCategory.COLORS -> Color(0xFF00BCD4)       // Cyan
        FlashcardCategory.NUMBERS -> Color(0xFFFF5722)      // Deep Orange
        FlashcardCategory.TIME -> Color(0xFF3F51B5)         // Indigo
        FlashcardCategory.DAILY_LIFE -> Color(0xFF795548)   // Brown
        else -> Color(0xFF607D8B)                           // Blue Grey
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f),
        shape = RoundedCornerShape(20.dp),
        onClick = onClick,
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp,
            pressedElevation = 8.dp
        )
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            categoryColor,
                            categoryColor.copy(alpha = 0.8f)
                        )
                    )
                )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                // Category Icon with background
                Surface(
                    modifier = Modifier
                        .size(64.dp)
                        .clip(RoundedCornerShape(16.dp)),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    Icon(
                        imageVector = when (category) {
                            FlashcardCategory.ANIMALS -> Icons.Default.Pets
                            FlashcardCategory.FOOD -> Icons.Default.Restaurant
                            FlashcardCategory.TRANSPORTATION -> Icons.Default.DirectionsCar
                            FlashcardCategory.WEATHER -> Icons.Default.WbSunny
                            FlashcardCategory.FAMILY -> Icons.Default.People
                            FlashcardCategory.COLORS -> Icons.Default.Palette
                            FlashcardCategory.NUMBERS -> Icons.Default.Numbers
                            FlashcardCategory.TIME -> Icons.Default.Schedule
                            FlashcardCategory.DAILY_LIFE -> Icons.Default.Home
                            else -> Icons.Default.Book
                        },
                        contentDescription = null,
                        modifier = Modifier
                            .padding(16.dp)
                            .size(32.dp),
                        tint = Color.White
                    )
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                // Category Name
                Text(
                    text = category.displayName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                // Question Count with background
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp)),
                    color = Color.White.copy(alpha = 0.2f)
                ) {
                    val questionCount = SampleData.quizQuestions.count { it.category == category }
                    Text(
                        text = "$questionCount câu hỏi",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 4.dp)
                    )
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