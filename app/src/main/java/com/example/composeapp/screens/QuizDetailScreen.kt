package com.example.composeapp.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.models.Question
import com.example.composeapp.models.QuestionType
import com.example.composeapp.viewmodels.AnswerResult
import com.example.composeapp.viewmodels.QuizViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizDetailScreen(
    category: String,
    level: String,
    onBackPress: () -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val currentQuiz by viewModel.currentQuiz.collectAsState()
    val currentQuestion by viewModel.currentQuestion.collectAsState()
    val questionIndex by viewModel.questionIndex.collectAsState()
    val score by viewModel.score.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val userAnswers by viewModel.userAnswers.collectAsState()
    val correctAnswersCount by viewModel.correctAnswersCount.collectAsState()
    val showResults by viewModel.showResults.collectAsState()
    val answerResults by viewModel.answerResults.collectAsState()
    
    val scrollState = rememberScrollState()
    
    // Lấy quiz từ Firebase khi màn hình được tạo
    LaunchedEffect(key1 = category, key2 = level) {
        viewModel.loadQuiz(level, category)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$category - $level") },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Lỗi: $error",
                        color = MaterialTheme.colorScheme.error
                    )
                    Button(
                        onClick = { viewModel.resetError() },
                        modifier = Modifier.padding(top = 16.dp)
                    ) {
                        Text("Thử lại")
                    }
                }
            }
        } else if (showResults) {
            // Hiển thị kết quả chi tiết
            ResultScreen(
                score = score,
                correctAnswers = correctAnswersCount,
                totalQuestions = currentQuiz?.questions?.size ?: 0,
                answerResults = answerResults,
                onRetry = { viewModel.resetQuiz() },
                onBack = onBackPress,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            )
        } else if (currentQuestion != null) {
            // Hiển thị câu hỏi hiện tại
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp)
                    .verticalScroll(scrollState)
            ) {
                // Progress
                LinearProgressIndicator(
                    progress = (questionIndex + 1).toFloat() / (currentQuiz?.questions?.size ?: 1),
                    modifier = Modifier.fillMaxWidth()
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = "Câu ${questionIndex + 1}/${currentQuiz?.questions?.size ?: 0}",
                    style = MaterialTheme.typography.titleMedium
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
                // Question
                Text(
                    text = currentQuestion!!.question,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Options based on question type
                when (currentQuestion!!.type) {
                    QuestionType.MULTIPLE_CHOICE -> {
                        MultipleChoiceQuestion(
                            question = currentQuestion!!,
                            onAnswerSelected = { answer ->
                                viewModel.submitAnswer(answer)
                            }
                        )
                    }
                    QuestionType.TRUE_FALSE -> {
                        TrueFalseQuestion(
                            question = currentQuestion!!,
                            onAnswerSelected = { answer ->
                                viewModel.submitAnswer(answer)
                            }
                        )
                    }
                    QuestionType.FILL_BLANK -> {
                        FillBlankQuestion(
                            question = currentQuestion!!,
                            onAnswerSubmitted = { answer ->
                                viewModel.submitAnswer(answer)
                            }
                        )
                    }
                    QuestionType.MATCHING -> {
                        MultipleChoiceQuestion(
                            question = currentQuestion!!,
                            onAnswerSelected = { answer ->
                                viewModel.submitAnswer(answer)
                            }
                        )
                    }
                    QuestionType.AUDIO_CHOICE -> {
                        // Audio question implementation will be added later
                        MultipleChoiceQuestion(
                            question = currentQuestion!!,
                            onAnswerSelected = { answer ->
                                viewModel.submitAnswer(answer)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ResultScreen(
    score: Int,
    correctAnswers: Int,
    totalQuestions: Int,
    answerResults: List<AnswerResult>,
    onRetry: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Kết quả",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Hiển thị điểm số
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer
            )
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Điểm số của bạn:",
                    style = MaterialTheme.typography.titleLarge
                )
                
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.displayLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = "Số câu đúng: $correctAnswers/$totalQuestions",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Danh sách câu trả lời
        Text(
            text = "Chi tiết câu trả lời",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Hiển thị từng câu trả lời
        answerResults.forEachIndexed { index, result ->
            AnswerResultItem(
                index = index + 1,
                result = result,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp)
            )
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        
        // Các nút hành động
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            Button(
                onClick = onRetry,
                modifier = Modifier.weight(1f)
            ) {
                Text("Làm lại")
            }
            
            Spacer(modifier = Modifier.width(16.dp))
            
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier.weight(1f)
            ) {
                Text("Quay lại")
            }
        }
    }
}

@Composable
fun AnswerResultItem(
    index: Int,
    result: AnswerResult,
    modifier: Modifier = Modifier
) {
    val backgroundColor = if (result.isCorrect) {
        Color(0xFFDCEDC8) // Màu xanh nhạt cho câu đúng
    } else {
        Color(0xFFFFCDD2) // Màu đỏ nhạt cho câu sai
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Tiêu đề câu hỏi
            Text(
                text = "Câu $index: ${result.question}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Câu trả lời của người dùng
            Text(
                text = "Câu trả lời của bạn: ${result.userAnswer}",
                style = MaterialTheme.typography.bodyLarge
            )
            
            // Câu trả lời đúng
            Text(
                text = "Đáp án đúng: ${result.correctAnswer}",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Giải thích
            if (result.explanation.isNotEmpty()) {
                Text(
                    text = "Giải thích: ${result.explanation}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
            }
            
            // Điểm số
            Text(
                text = "Điểm: ${result.points}",
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Bold,
                color = if (result.isCorrect) Color(0xFF4CAF50) else Color(0xFFE53935)
            )
        }
    }
}

@Composable
fun MultipleChoiceQuestion(
    question: Question,
    onAnswerSelected: (String) -> Unit
) {
    question.options?.forEach { (key, option) ->
        OutlinedButton(
            onClick = { onAnswerSelected(key) },
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

@Composable
fun TrueFalseQuestion(
    question: Question,
    onAnswerSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        Button(
            onClick = { onAnswerSelected("a") },
            modifier = Modifier.weight(1f)
        ) {
            Text("Đúng")
        }
        
        Spacer(modifier = Modifier.width(16.dp))
        
        Button(
            onClick = { onAnswerSelected("b") },
            modifier = Modifier.weight(1f)
        ) {
            Text("Sai")
        }
    }
}

@Composable
fun FillBlankQuestion(
    question: Question,
    onAnswerSubmitted: (String) -> Unit
) {
    var answer by remember { mutableStateOf("") }
    
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = answer,
            onValueChange = { answer = it },
            label = { Text("Nhập câu trả lời") },
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        Button(
            onClick = { onAnswerSubmitted(answer) },
            modifier = Modifier.align(Alignment.End)
        ) {
            Text("Gửi")
        }
    }
} 