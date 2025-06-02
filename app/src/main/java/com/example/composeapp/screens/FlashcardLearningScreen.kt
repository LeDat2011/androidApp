package com.example.composeapp.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.viewmodels.FlashcardViewModel
import com.example.composeapp.components.FlashcardComponent

data class FlashcardData(
    val word: String,
    val reading: String,
    val meaning: String,
    val example: String,
    val exampleMeaning: String
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlashcardLearningScreen(
    categoryName: String,
    level: String,
    onBackPress: () -> Unit,
    viewModel: FlashcardViewModel = viewModel()
) {
    var currentIndex by remember { mutableStateOf(0) }
    var progress by remember { mutableStateOf(0f) }

    val flashcards by viewModel.flashcards.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    // Load flashcards when the screen is first displayed
    LaunchedEffect(categoryName, level) {
        viewModel.loadFlashcards(categoryName, level)
    }

    // Update progress when currentIndex changes
    LaunchedEffect(currentIndex, flashcards) {
        if (flashcards.isNotEmpty()) {
            progress = (currentIndex + 1).toFloat() / flashcards.size
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("$categoryName - $level") },
                navigationIcon = {
                    IconButton(onClick = onBackPress) {
                        Icon(Icons.Default.ArrowBack, "Back")
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
            when {
                isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Error,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = error ?: "Đã xảy ra lỗi",
                            color = MaterialTheme.colorScheme.error,
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                viewModel.loadFlashcards(categoryName, level)
                            }
                        ) {
                            Text("Thử lại")
                        }
                    }
                }
                flashcards.isEmpty() -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = null,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Không có flashcard nào cho chủ đề và level này",
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = androidx.compose.ui.text.style.TextAlign.Center
                        )
                    }
                }
                else -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Progress indicator
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                        )

                        Text(
                            text = "${currentIndex + 1}/${flashcards.size}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Spacer(modifier = Modifier.height(32.dp))

                        // Flashcard component
                        FlashcardComponent(
                            flashcard = flashcards[currentIndex],
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
                            canGoNext = currentIndex < flashcards.size - 1,
                            canGoPrevious = currentIndex > 0
                        )
                    }
                }
            }
        }
    }
} 