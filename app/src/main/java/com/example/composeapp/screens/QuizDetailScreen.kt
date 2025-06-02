package com.example.composeapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizDetailScreen(
    category: String,
    level: String,
    onBackPress: () -> Unit
) {
    var currentQuestionIndex by remember { mutableStateOf(0) }
    var score by remember { mutableStateOf(0) }
    var showResult by remember { mutableStateOf(false) }
    val scrollState = rememberScrollState()

    val questions = remember {
        listOf(
            QuizQuestion(
                question = "「いぬ」はどういう意味ですか？",
                options = listOf(
                    "Con mèo",
                    "Con chó",
                    "Con gà",
                    "Con cá"
                ),
                correctAnswer = 1
            ),
            QuizQuestion(
                question = "「あか」の色は何ですか？",
                options = listOf(
                    "Màu xanh",
                    "Màu vàng",
                    "Màu đỏ",
                    "Màu trắng"
                ),
                correctAnswer = 2
            ),
            QuizQuestion(
                question = "「おはようございます」はいつ使いますか？",
                options = listOf(
                    "Buổi tối",
                    "Buổi trưa",
                    "Buổi chiều",
                    "Buổi sáng"
                ),
                correctAnswer = 3
            )
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$category - $level") },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (showResult) {
            // Hiển thị kết quả
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Kết quả",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Điểm số của bạn:",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Text(
                    text = "$score/${questions.size}",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Button(
                    onClick = {
                        currentQuestionIndex = 0
                        score = 0
                        showResult = false
                    }
                ) {
                    Text("Làm lại")
                }
                
                OutlinedButton(
                    onClick = onBackPress,
                    modifier = Modifier.padding(top = 8.dp)
                ) {
                    Text("Quay lại")
                }
            }
        } else {
            // Hiển thị câu hỏi
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Progress
                LinearProgressIndicator(
                    progress = (currentQuestionIndex + 1).toFloat() / questions.size,
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Câu ${currentQuestionIndex + 1}/${questions.size}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Question
                Text(
                    text = questions[currentQuestionIndex].question,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Options
                questions[currentQuestionIndex].options.forEachIndexed { index, option ->
                    OutlinedButton(
                        onClick = {
                            if (index == questions[currentQuestionIndex].correctAnswer) {
                                score++
                            }
                            if (currentQuestionIndex < questions.size - 1) {
                                currentQuestionIndex++
                            } else {
                                showResult = true
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        Text(
                            text = option,
                            modifier = Modifier.padding(8.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
        }
    }
}

data class QuizQuestion(
    val question: String,
    val options: List<String>,
    val correctAnswer: Int // Index of correct answer
) 