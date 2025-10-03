package com.example.composeapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.components.FreeWritingComponent
import com.example.composeapp.components.StrokeWritingComponent
import com.example.composeapp.components.WritingSettingsComponent
import com.example.composeapp.components.BoldWritingCanvas
import com.example.composeapp.models.*
import com.example.composeapp.ui.design.DesignSystem
import com.example.composeapp.viewmodels.WritingViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WritingScreen(
    viewModel: WritingViewModel = viewModel()
) {
    val characters by viewModel.characters.collectAsState()
    val currentCharacter by viewModel.currentCharacter.collectAsState()
    val currentStrokeIndex by viewModel.currentStrokeIndex.collectAsState()
    val writingMode by viewModel.writingMode.collectAsState()
    val progress by viewModel.progress.collectAsState()
    val settings by viewModel.settings.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val writingResult by viewModel.writingResult.collectAsState()
    
    var showSettings by remember { mutableStateOf(false) }
    var showResult by remember { mutableStateOf(false) }
    var useBoldCanvas by remember { mutableStateOf(true) }
    
    // Hiển thị kết quả khi có
    LaunchedEffect(writingResult) {
        if (writingResult != null) {
            showResult = true
        }
    }
    
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "✍️ Luyện viết nét chữ",
                        fontWeight = FontWeight.Bold
                    )
                },
                actions = {
                    IconButton(onClick = { useBoldCanvas = !useBoldCanvas }) {
                        Icon(
                            if (useBoldCanvas) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                            contentDescription = if (useBoldCanvas) "Bảng vẽ đậm" else "Bảng vẽ thường",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    
                    IconButton(onClick = { showSettings = true }) {
                        Icon(
                            Icons.Default.Settings,
                            contentDescription = "Cài đặt",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val currentChar = currentCharacter
            if (currentChar == null) {
                // Hiển thị danh sách ký tự
                CharacterSelectionSection(
                    characters = characters,
                    isLoading = isLoading,
                    onCharacterSelected = { character ->
                        viewModel.selectCharacter(character)
                    }
                )
            } else {
                // Hiển thị giao diện luyện viết
                WritingInterface(
                    character = currentChar,
                    currentStrokeIndex = currentStrokeIndex,
                    writingMode = writingMode,
                    progress = progress,
                    settings = settings,
                    useBoldCanvas = useBoldCanvas,
                    onBack = { 
                        viewModel.backToCharacterSelection()
                    },
                    onModeChanged = { mode ->
                        viewModel.setWritingMode(mode)
                    },
                    onStrokeCompleted = { strokeIndex ->
                        viewModel.completeStroke(strokeIndex)
                    },
                    onNextStroke = {
                        viewModel.nextStroke()
                    },
                    onPreviousStroke = {
                        viewModel.previousStroke()
                    },
                    onWritingCompleted = { accuracy, timeSpent ->
                        viewModel.completeCharacterWriting(timeSpent, accuracy)
                    }
                )
            }
        }
    }
    
    // Dialog cài đặt
    if (showSettings) {
        AlertDialog(
            onDismissRequest = { showSettings = false },
            title = { 
                Text(
                    text = "Cài đặt hiển thị nét mẫu",
                    style = MaterialTheme.typography.headlineSmall
                ) 
            },
            text = {
                WritingSettingsComponent(
                    settings = settings,
                    onSettingsChanged = { newSettings ->
                        viewModel.updateSettings(newSettings)
                    }
                )
            },
            confirmButton = {
                TextButton(onClick = { showSettings = false }) {
                    Text("Xong")
                }
            }
        )
    }
    
    // Dialog kết quả
    if (showResult && writingResult != null) {
        WritingResultDialog(
            result = writingResult!!,
            onDismiss = { 
                showResult = false
                viewModel.resetProgress()
            },
            onTryAgain = {
                showResult = false
                viewModel.resetProgress()
            }
        )
    }
}

@Composable
private fun CharacterSelectionSection(
    characters: List<WritingCharacter>,
    isLoading: Boolean,
    onCharacterSelected: (WritingCharacter) -> Unit
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Text(
                text = "Chọn ký tự để luyện viết",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
        }
        
        // Lọc theo loại
        item {
            CharacterTypeFilter()
        }
        
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        } else {
            items(characters) { character ->
                CharacterCard(
                    character = character,
                    onClick = { onCharacterSelected(character) }
                )
            }
        }
    }
}

@Composable
private fun CharacterTypeFilter() {
    var selectedType by remember { mutableStateOf(WritingType.HIRAGANA) }
    
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(vertical = 8.dp)
    ) {
        items(WritingType.values()) { type ->
            FilterChip(
                onClick = { selectedType = type },
                label = { 
                    Text(
                        text = when (type) {
                            WritingType.HIRAGANA -> "Hiragana"
                            WritingType.KATAKANA -> "Katakana"
                            WritingType.KANJI -> "Kanji"
                        }
                    )
                },
                selected = selectedType == type,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }
    }
}

@Composable
private fun CharacterCard(
    character: WritingCharacter,
    onClick: () -> Unit
) {
    val backgroundColor = when (character.type) {
        WritingType.HIRAGANA -> Color(0xFF42A5F5) // Xanh dương sáng
        WritingType.KATAKANA -> Color(0xFFFF7043) // Cam đậm
        WritingType.KANJI -> Color(0xFF66BB6A) // Xanh lá đậm
    }
    
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        onClick = onClick,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor.copy(alpha = 0.1f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Ký tự
            Text(
                text = character.character,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                color = backgroundColor,
                modifier = Modifier.padding(end = 16.dp)
            )
            
            // Thông tin
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = character.reading ?: "N/A",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Text(
                    text = "${character.type.name} - ${character.level}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                if (character.meaning != null) {
                    Text(
                        text = character.meaning,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }
            }
            
            // Icon mũi tên
            Icon(
                imageVector = Icons.Default.ArrowForward,
                contentDescription = "Chọn",
                tint = backgroundColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
private fun WritingInterface(
    character: WritingCharacter,
    currentStrokeIndex: Int,
    writingMode: WritingMode,
    progress: WritingProgress?,
    settings: WritingSettings,
    useBoldCanvas: Boolean,
    onBack: () -> Unit,
    onModeChanged: (WritingMode) -> Unit,
    onStrokeCompleted: (Int) -> Unit,
    onNextStroke: () -> Unit,
    onPreviousStroke: () -> Unit,
    onWritingCompleted: (Float, Int) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Header với điều khiển
        WritingHeader(
            character = character,
            writingMode = writingMode,
            onBack = onBack,
            onModeChanged = onModeChanged
        )
        
        // Nội dung luyện viết
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            when (writingMode) {
                WritingMode.STROKE_BY_STROKE -> {
                    if (useBoldCanvas) {
                        BoldWritingCanvas(
                            character = character,
                            currentStrokeIndex = currentStrokeIndex,
                            completedStrokes = progress?.completedStrokes ?: emptyList(),
                            onStrokeCompleted = onStrokeCompleted,
                            onNextStroke = onNextStroke,
                            onPreviousStroke = onPreviousStroke
                        )
                    } else {
                        StrokeWritingComponent(
                            character = character,
                            currentStrokeIndex = currentStrokeIndex,
                            completedStrokes = progress?.completedStrokes ?: emptyList(),
                            onStrokeCompleted = onStrokeCompleted,
                            onNextStroke = onNextStroke,
                            onPreviousStroke = onPreviousStroke,
                            settings = settings
                        )
                    }
                }
                WritingMode.FREE_WRITING -> {
                    FreeWritingComponent(
                        character = character,
                        onWritingCompleted = onWritingCompleted,
                        settings = settings
                    )
                }
            }
        }
    }
}

@Composable
private fun WritingHeader(
    character: WritingCharacter,
    writingMode: WritingMode,
    onBack: () -> Unit,
    onModeChanged: (WritingMode) -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Nút quay lại
            IconButton(onClick = onBack) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // Thông tin ký tự
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = character.character,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = character.reading ?: "N/A",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            // Chọn chế độ
            Row {
                WritingMode.values().forEach { mode ->
                    FilterChip(
                        onClick = { onModeChanged(mode) },
                        label = { 
                            Text(
                                text = when (mode) {
                                    WritingMode.STROKE_BY_STROKE -> "Từng nét"
                                    WritingMode.FREE_WRITING -> "Tự do"
                                }
                            )
                        },
                        selected = writingMode == mode,
                        modifier = Modifier.padding(horizontal = 2.dp)
                    )
                }
            }
        }
    }
}


@Composable
private fun WritingResultDialog(
    result: WritingResult,
    onDismiss: () -> Unit,
    onTryAgain: () -> Unit
) {
    val accuracyPercent = (result.accuracy * 100).toInt()
    val grade = when {
        result.accuracy >= 0.9f -> "Xuất sắc! 🌟"
        result.accuracy >= 0.7f -> "Tốt! 👍"
        result.accuracy >= 0.5f -> "Khá! 👌"
        else -> "Cần cố gắng thêm! 💪"
    }
    
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Kết quả luyện viết") },
        text = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = grade,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text("Độ chính xác: $accuracyPercent%")
                Text("Thời gian: ${result.timeSpentSeconds}s")
                Text("Nét hoàn thành: ${result.strokesCompleted}/${result.totalStrokes}")
                Text("Chế độ: ${if (result.mode == WritingMode.STROKE_BY_STROKE) "Từng nét" else "Tự do"}")
            }
        },
        confirmButton = {
            TextButton(onClick = onTryAgain) {
                Text("Thử lại")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Đóng")
            }
        }
    )
}
