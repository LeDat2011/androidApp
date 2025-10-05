package com.example.composeapp.screens

import androidx.compose.foundation.clickable
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
import com.example.composeapp.models.*
import com.example.composeapp.viewmodels.GameViewModel
import com.example.composeapp.components.WordPuzzleGame
import com.example.composeapp.components.MemoryGameComponent
import com.example.composeapp.components.SpeedQuizComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameScreen(
    viewModel: GameViewModel = viewModel()
) {
    val questions by viewModel.questions.collectAsState()
    val currentSession by viewModel.currentSession.collectAsState()
    val gameResult by viewModel.gameResult.collectAsState()
    val playerStats by viewModel.playerStats.collectAsState()
    val userLevelInfo by viewModel.userLevelInfo.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val timeRemaining by viewModel.timeRemaining.collectAsState()
    val currentScore by viewModel.currentScore.collectAsState()
    val currentStreak by viewModel.currentStreak.collectAsState()
    
    var showGameResult by remember { mutableStateOf(false) }
    
    // Hiển thị kết quả game khi có
    LaunchedEffect(gameResult) {
        if (gameResult != null) {
            showGameResult = true
        }
    }
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { 
                    Text(
                        text = "🎮 Game Học Tiếng Nhật",
                        fontWeight = FontWeight.Bold
                    )
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
            when {
                currentSession != null && !currentSession!!.isCompleted -> {
                    // Hiển thị game đang chơi
                    PlayingGameView(
                        session = currentSession!!,
                        timeRemaining = timeRemaining,
                        score = currentScore,
                        streak = currentStreak,
                        onAnswer = { answer -> viewModel.submitAnswer(answer) },
                        onExit = { viewModel.exitGame() }
                    )
                }
                showGameResult && gameResult != null -> {
                    // Hiển thị kết quả game
                    GameResultView(
                        result = gameResult!!,
                        playerStats = playerStats,
                        onPlayAgain = { 
                            showGameResult = false
                            viewModel.exitGame()
                        },
                        onBackToMenu = { 
                            showGameResult = false
                            viewModel.exitGame()
                        }
                    )
                }
                else -> {
                    // Hiển thị menu game chính
                    GameMenuView(
                        playerStats = playerStats,
                        userLevelInfo = userLevelInfo,
                        onStartGame = { gameType, difficulty ->
                            viewModel.startNewGame(gameType, difficulty)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun GameMenuView(
    playerStats: PlayerStats,
    userLevelInfo: UserLevelInfo,
    onStartGame: (GameType, GameDifficulty) -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Thống kê người chơi
            PlayerStatsCard(playerStats = playerStats, userLevelInfo = userLevelInfo)
        }
        
        item {
            Text(
                text = "Chọn Game",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        item {
            // Danh sách các loại game
            GameTypeSelection(userLevelInfo = userLevelInfo, onStartGame = onStartGame)
        }
        
        item {
            Text(
                text = "Hướng dẫn",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
        
        item {
            GameInstructions()
        }
    }
}

@Composable
private fun PlayerStatsCard(playerStats: PlayerStats, userLevelInfo: UserLevelInfo) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "📊 Thống kê của bạn",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    )
                ) {
                    Text(
                        text = "Level ${userLevelInfo.currentLevel.displayName}",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                StatItem(
                    label = "Điểm tổng",
                    value = playerStats.totalScore.toString(),
                    icon = "⭐"
                )
                StatItem(
                    label = "Games",
                    value = playerStats.totalGamesPlayed.toString(),
                    icon = "🎮"
                )
                StatItem(
                    label = "Chuỗi tốt nhất",
                    value = playerStats.bestStreak.toString(),
                    icon = "🔥"
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Độ chính xác trung bình: ${(playerStats.averageAccuracy * 100).toInt()}%",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
    }
}

@Composable
private fun StatItem(
    label: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 24.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun GameTypeSelection(
    userLevelInfo: UserLevelInfo,
    onStartGame: (GameType, GameDifficulty) -> Unit
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Word Puzzle
        GameTypeCard(
            title = "🧩 Word Puzzle",
            description = "Sắp xếp các từ thành câu có nghĩa",
            icon = "🧩",
            color = Color(0xFF4CAF50),
            userLevelInfo = userLevelInfo,
            gameType = GameType.WORD_PUZZLE,
            onStart = { onStartGame(GameType.WORD_PUZZLE, GameDifficulty.EASY) }
        )
        
        // Memory Game
        GameTypeCard(
            title = "🧠 Memory Game",
            description = "Ghép từ tiếng Nhật với nghĩa",
            icon = "🧠",
            color = Color(0xFF2196F3),
            userLevelInfo = userLevelInfo,
            gameType = GameType.MEMORY_GAME,
            onStart = { onStartGame(GameType.MEMORY_GAME, GameDifficulty.EASY) }
        )
        
        // Speed Quiz
        GameTypeCard(
            title = "⚡ Speed Quiz",
            description = "Trả lời nhanh câu hỏi tiếng Nhật",
            icon = "⚡",
            color = Color(0xFFFF9800),
            userLevelInfo = userLevelInfo,
            gameType = GameType.SPEED_QUIZ,
            onStart = { onStartGame(GameType.SPEED_QUIZ, GameDifficulty.EASY) }
        )
    }
}

@Composable
private fun GameTypeCard(
    title: String,
    description: String,
    icon: String,
    color: Color,
    userLevelInfo: UserLevelInfo,
    gameType: GameType,
    onStart: () -> Unit
) {
    val isEasyUnlocked = GameDifficulty.EASY.isUnlockedForUser(userLevelInfo)
    val isMediumUnlocked = GameDifficulty.MEDIUM.isUnlockedForUser(userLevelInfo)
    val isHardUnlocked = GameDifficulty.HARD.isUnlockedForUser(userLevelInfo)
    
    val isAnyDifficultyUnlocked = isEasyUnlocked || isMediumUnlocked || isHardUnlocked
    
    val cardColor = if (isAnyDifficultyUnlocked) {
        color.copy(alpha = 0.1f)
    } else {
        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
    }
    
    val textColor = if (isAnyDifficultyUnlocked) {
        color
    } else {
        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
    }
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .then(
                if (isAnyDifficultyUnlocked) {
                    Modifier.clickable { onStart() }
                } else {
                    Modifier
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = cardColor
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = icon,
                    fontSize = 32.sp
                )
                
                Column {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = textColor
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    
                    // Hiển thị trạng thái unlock
                    if (!isAnyDifficultyUnlocked) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "🔒 Cần đạt level ${GameDifficulty.EASY.getRequiredLevel().displayName}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.error,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            if (isEasyUnlocked) {
                                Text(
                                    text = "🟢 N5",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFF4CAF50),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (isMediumUnlocked) {
                                Text(
                                    text = "🟡 N4-N3",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFFF9800),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                            if (isHardUnlocked) {
                                Text(
                                    text = "🔴 N2-N1",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = Color(0xFFF44336),
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
            
            if (isAnyDifficultyUnlocked) {
                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = "Bắt đầu",
                    tint = textColor
                )
            } else {
                Icon(
                    Icons.Default.Lock,
                    contentDescription = "Bị khóa",
                    tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                )
            }
        }
    }
}

@Composable
private fun GameInstructions() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "📖 Hướng dẫn chơi",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            InstructionItem(
                icon = "🧩",
                title = "Word Puzzle",
                description = "Sắp xếp các từ tiếng Nhật thành câu có nghĩa bằng cách kéo thả"
            )
            
            InstructionItem(
                icon = "🧠",
                title = "Memory Game",
                description = "Chọn nghĩa đúng của từ tiếng Nhật trong thời gian giới hạn"
            )
            
            InstructionItem(
                icon = "⚡",
                title = "Speed Quiz",
                description = "Trả lời nhanh các câu hỏi ngữ pháp và từ vựng"
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "💡 Mẹo: Trả lời đúng liên tiếp để tạo chuỗi và nhận thêm điểm!",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}

@Composable
private fun InstructionItem(
    icon: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = icon,
            fontSize = 16.sp
        )
        
        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
            )
        }
    }
}

@Composable
private fun PlayingGameView(
    session: GameSession,
    timeRemaining: Int,
    score: Int,
    streak: Int,
    onAnswer: (String) -> Unit,
    onExit: () -> Unit
) {
    val currentQuestion = session.questions[session.currentQuestionIndex]
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header với thông tin game
        GameHeader(
            questionNumber = session.currentQuestionIndex + 1,
            totalQuestions = session.questions.size,
            timeRemaining = timeRemaining,
            score = score,
            streak = streak,
            onExit = onExit
        )
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Hiển thị câu hỏi
        when (session.gameType) {
            GameType.WORD_PUZZLE -> {
                WordPuzzleGame(
                    question = currentQuestion,
                    onAnswer = onAnswer
                )
            }
            GameType.MEMORY_GAME -> {
                MemoryGameComponent(
                    question = currentQuestion,
                    onAnswer = onAnswer
                )
            }
            GameType.SPEED_QUIZ -> {
                SpeedQuizComponent(
                    question = currentQuestion,
                    onAnswer = onAnswer
                )
            }
            else -> {
                Text("Game type not implemented yet")
            }
        }
    }
}

@Composable
private fun GameHeader(
    questionNumber: Int,
    totalQuestions: Int,
    timeRemaining: Int,
    score: Int,
    streak: Int,
    onExit: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Câu $questionNumber/$totalQuestions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                
                IconButton(onClick = onExit) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Thoát game",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                GameStatItem(
                    label = "Thời gian",
                    value = "${timeRemaining}s",
                    icon = "⏱️"
                )
                GameStatItem(
                    label = "Điểm",
                    value = score.toString(),
                    icon = "⭐"
                )
                GameStatItem(
                    label = "Chuỗi",
                    value = streak.toString(),
                    icon = "🔥"
                )
            }
        }
    }
}

@Composable
private fun GameStatItem(
    label: String,
    value: String,
    icon: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = icon,
            fontSize = 20.sp
        )
        Text(
            text = value,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onPrimaryContainer
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
        )
    }
}

@Composable
private fun GameResultView(
    result: GameResult,
    playerStats: PlayerStats,
    onPlayAgain: () -> Unit,
    onBackToMenu: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Kết quả chính
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = if (result.accuracy >= 0.8f) 
                        Color(0xFF4CAF50).copy(alpha = 0.1f)
                    else if (result.accuracy >= 0.6f)
                        Color(0xFFFF9800).copy(alpha = 0.1f)
                    else
                        Color(0xFFF44336).copy(alpha = 0.1f)
                )
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (result.accuracy >= 0.8f) "🎉 Xuất sắc!" 
                               else if (result.accuracy >= 0.6f) "👍 Tốt lắm!" 
                               else "💪 Cố gắng hơn!",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Điểm: ${result.score}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    
                    Text(
                        text = "Độ chính xác: ${(result.accuracy * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium
                    )
                    
                    Text(
                        text = "Chuỗi tốt nhất: ${result.streak}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
        
        item {
            // Nút hành động
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Button(
                    onClick = onPlayAgain,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.primary
                    ),
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Chơi lại")
                }
                
                OutlinedButton(
                    onClick = onBackToMenu,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Về menu")
                }
            }
        }
    }
}

