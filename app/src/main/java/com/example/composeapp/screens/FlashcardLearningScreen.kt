package com.example.composeapp.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.rounded.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.viewmodels.FlashcardViewModel
import com.example.composeapp.components.FlashcardComponent
import kotlin.math.roundToInt

data class FlashcardData(
    val id: String = "",
    val word: String = "",
    val reading: String = "",
    val meaning: String = "",
    val example: String = "",
    val exampleMeaning: String = "",
    val isLearned: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardLearningScreen(
    categoryName: String,
    level: String,
    viewModel: FlashcardViewModel = viewModel(),
    onNavigateBack: () -> Unit
) {
    var currentIndex by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0f) }
    var showCompletionDialog by remember { mutableStateOf(false) }

    val flashcards by viewModel.flashcards.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    val wordsLearned by viewModel.wordsLearned.collectAsState()
    val totalWordsLearned by viewModel.totalWordsLearned.collectAsState()
    val todayStudyTime by viewModel.todayStudyTime.collectAsState()

    // Theo dõi từ vựng đã học trong phiên hiện tại
    var learnedWords by remember { mutableStateOf(setOf<String>()) }
    
    // State để theo dõi tiến độ theo chủ đề
    var topicWordsLearned by remember { mutableStateOf(0) }
    var totalWordsInTopic by remember { mutableStateOf(0) }

    // Load flashcards when the screen is first displayed
    LaunchedEffect(categoryName, level) {
        viewModel.loadFlashcards(categoryName, level)
        
        // Tải tiến độ theo chủ đề
        val progress = viewModel.getTopicProgress(categoryName, level)
        topicWordsLearned = progress.first
        totalWordsInTopic = progress.second
    }

    // Update progress when currentIndex changes
    LaunchedEffect(currentIndex, flashcards) {
        if (flashcards.isNotEmpty()) {
            progress = (currentIndex + 1).toFloat() / flashcards.size
            // Show completion dialog when reaching the end
            if (currentIndex == flashcards.size - 1) {
                showCompletionDialog = true
                // Đánh dấu hoàn thành bộ flashcard và cập nhật thời gian học lên Firebase
                viewModel.completeFlashcardSet(categoryName, level)
            }
        }
    }

    // Cập nhật thời gian học khi rời khỏi màn hình
    DisposableEffect(Unit) {
        onDispose {
            // Cập nhật thời gian học tập lên Firebase
            viewModel.updateStudyTime()
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Học từ vựng - $categoryName") },
                navigationIcon = {
                    IconButton(onClick = {
                        // Cập nhật thời gian học trước khi quay lại
                        viewModel.updateStudyTime()
                        onNavigateBack()
                    }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Quay lại")
                    }
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                if (flashcards.isNotEmpty()) {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Hiển thị thông tin học tập
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier.padding(16.dp)
                            ) {
                                // Hiển thị tiến độ
                                LinearProgressIndicator(
                                    progress = { (currentIndex + 1).toFloat() / flashcards.size },
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(bottom = 8.dp)
                                )
                                
                                // Hiển thị thông tin học tập
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.SpaceEvenly
                                ) {
                                    // Tổng số từ vựng đã học toàn thời gian
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Tổng đã học",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "$totalWordsLearned",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (totalWordsLearned > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "từ",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                    
                                    // Thời gian học hôm nay
                                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                        Text(
                                            text = "Hôm nay",
                                            style = MaterialTheme.typography.labelMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = if (todayStudyTime > 0) "$todayStudyTime" else "0",
                                            style = MaterialTheme.typography.bodyMedium,
                                            fontWeight = FontWeight.Bold,
                                            color = if (todayStudyTime > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                        Text(
                                            text = "phút",
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                        
                        // Lấy thông tin từ vựng hiện tại
                        val currentFlashcard = flashcards[currentIndex]
                        // Kiểm tra xem từ này đã được đánh dấu là đã học chưa
                        val isCurrentWordLearned = currentFlashcard.isLearned || learnedWords.contains(currentFlashcard.id)
                        
                        FlashcardComponent(
                            flashcard = currentFlashcard,
                            isLearned = isCurrentWordLearned,
                            canGoNext = currentIndex < flashcards.size - 1,
                            canGoPrevious = currentIndex > 0,
                            onNext = {
                                if (currentIndex < flashcards.size - 1) {
                                    currentIndex++
                                }
                            },
                            onPrevious = {
                                if (currentIndex > 0) {
                                    currentIndex--
                                }
                            },
                            onLearn = {
                                // Đánh dấu từ vựng hiện tại đã học
                                val currentWordId = flashcards[currentIndex].id
                                viewModel.markWordAsLearned(currentWordId)
                                // Cập nhật danh sách từ vựng đã học trong phiên hiện tại
                                learnedWords = learnedWords + currentWordId
                                // Cập nhật tiến độ theo chủ đề
                                topicWordsLearned++
                            }
                        )
                    }
                } else {
                    Text(
                        text = "Không có từ vựng nào",
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
            }
            
            // Hiển thị lỗi nếu có
            error?.let { error ->
                AlertDialog(
                    onDismissRequest = { viewModel.resetError() },
                    title = { Text("Lỗi") },
                    text = { Text(error) },
                    confirmButton = {
                        TextButton(onClick = { viewModel.resetError() }) {
                            Text("Đóng")
                        }
                    }
                )
            }
        }
    }

    // Completion dialog
    if (showCompletionDialog) {
        AlertDialog(
            onDismissRequest = { showCompletionDialog = false },
            icon = {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            },
            title = {
                Text(
                    text = "Chúc mừng!",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
            },
            text = {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Bạn đã hoàn thành bộ flashcard này!",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    // Hiển thị thông tin học tập
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Thời gian học hôm nay
                        Text(
                            text = "$todayStudyTime phút",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Thời gian học hôm nay",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                        )
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Tiếp tục luyện tập để ghi nhớ tốt hơn",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { 
                        showCompletionDialog = false
                        // Cập nhật thời gian học trước khi đóng dialog
                        viewModel.updateStudyTime()
                        // Reset counter sau khi đóng dialog
                        viewModel.resetWordsLearnedCounter()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text("Tiếp tục học")
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = {
                        // Cập nhật thời gian học trước khi quay lại
                        viewModel.updateStudyTime()
                        // Reset counter sau khi đóng dialog
                        viewModel.resetWordsLearnedCounter()
                        onNavigateBack()
                    }
                ) {
                    Text("Quay lại")
                }
            }
        )
    }
} 