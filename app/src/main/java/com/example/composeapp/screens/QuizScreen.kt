package com.example.composeapp.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.composeapp.models.QuizCategory
import com.example.composeapp.viewmodels.QuizViewModel
import com.example.composeapp.ui.design.*
import com.example.composeapp.ui.theme.*
import androidx.compose.runtime.LaunchedEffect

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizScreen(
    viewModel: QuizViewModel = viewModel()
) {
    var selectedCategory by remember { mutableStateOf<String?>(null) }
    var showQuizDetail by remember { mutableStateOf(false) }
    var selectedLevel by remember { mutableStateOf<String?>(null) }
    
    val categories by viewModel.categories.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()
    
    // Lấy danh sách danh mục khi màn hình được tạo
    LaunchedEffect(key1 = Unit) {
        viewModel.loadCategories()
    }

    if (showQuizDetail && selectedCategory != null && selectedLevel != null) {
        QuizDetailScreen(
            category = selectedCategory!!,
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
            // Header với gradient đẹp
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = MaterialTheme.colorScheme.primary,
                tonalElevation = elevation().high
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = spacing().sm, vertical = spacing().sm)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "🎯 Kiểm Tra Kiến Thức",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimary,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(spacing().xs))
                        Text(
                            text = "Chọn chủ đề để bắt đầu học",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center,
                            maxLines = 1
                        )
                    }
                }
            }

            if (isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
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
            } else {
                // Categories Grid với spacing đẹp
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    contentPadding = PaddingValues(spacing().md),
                    horizontalArrangement = Arrangement.spacedBy(spacing().md),
                    verticalArrangement = Arrangement.spacedBy(spacing().md),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(categories) { category ->
                        QuizCategoryCard(
                            category = category,
                            onClick = { selectedCategory = category.id }
                        )
                    }
                }
            }
        }

        // Level Selection Dialog
        selectedCategory?.let { category ->
            val selectedCategoryData = categories.find { it.id == category }
            if (selectedCategoryData != null) {
                LevelSelectionDialog(
                    categoryId = selectedCategoryData.id,
                    categoryTitle = selectedCategoryData.title,
                    onDismiss = { selectedCategory = null },
                    onLevelSelected = { level ->
                        showQuizDetail = true
                        selectedLevel = level
                    },
                    viewModel = viewModel
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuizCategoryCard(
    category: QuizCategory,
    onClick: () -> Unit
) {
    val backgroundColor = when (category.id) {
        "animals" -> Color(0xFF42A5F5) // Xanh dương sáng (như Hiragana)
        "colors" -> Color(0xFFFF7043) // Cam đậm (như Katakana)
        "family" -> Color(0xFF66BB6A) // Xanh lá đậm (như Kanji)
        "food" -> Color(0xFFE53935) // Đỏ đậm
        "numbers" -> Color(0xFF9C27B0) // Tím đậm
        "time" -> Color(0xFF26C6DA) // Teal sáng
        "transportation" -> Color(0xFFAB47BC) // Tím nhạt
        "weather" -> Color(0xFFFFCA28) // Vàng cam
        "body" -> Color(0xFFEF5350) // Đỏ nhạt
        "clothing" -> Color(0xFF78909C) // Xanh xám
        "house" -> Color(0xFF8BC34A) // Xanh lá nhạt
        "nature" -> Color(0xFF009688) // Teal đậm
        "school" -> Color(0xFF607D8B) // Xanh xám đậm
        "work" -> Color(0xFF795548) // Nâu
        "hobby" -> Color(0xFFEC407A) // Hồng nhạt
        "sports" -> Color(0xFF4CAF50) // Xanh lá
        "music" -> Color(0xFF673AB7) // Tím đậm
        "travel" -> Color(0xFF00BCD4) // Cyan
        "shopping" -> Color(0xFFFF9800) // Cam
        "health" -> Color(0xFFE91E63) // Hồng đậm
        else -> Color(0xFF2196F3) // Xanh dương mặc định
    }
    
    val icon = when (category.id) {
        "animals" -> Icons.Default.Pets
        "colors" -> Icons.Default.Palette
        "family" -> Icons.Default.People
        "food" -> Icons.Default.Restaurant
        "numbers" -> Icons.Default.Calculate
        "time" -> Icons.Default.Schedule
        "transportation" -> Icons.Default.DirectionsCar
        "weather" -> Icons.Default.WbSunny
        "body" -> Icons.Default.Accessibility
        "clothing" -> Icons.Default.Checkroom
        "house" -> Icons.Default.Home
        "nature" -> Icons.Default.Park
        "school" -> Icons.Default.School
        "work" -> Icons.Default.Work
        "hobby" -> Icons.Default.Favorite
        "sports" -> Icons.Default.SportsSoccer
        "music" -> Icons.Default.MusicNote
        "travel" -> Icons.Default.Flight
        "shopping" -> Icons.Default.ShoppingCart
        "health" -> Icons.Default.HealthAndSafety
        else -> Icons.Default.Category
    }

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
        shape = shapes().large,
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = elevation().high
        ),
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(spacing().md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = category.title,
                modifier = Modifier.size(components().iconSize * 2.5f),
                tint = Color.White
            )
            
            Spacer(modifier = Modifier.height(spacing().sm))
            
            Text(
                text = category.title,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White,
                textAlign = TextAlign.Center
            )
            
            Spacer(modifier = Modifier.height(spacing().xs))
            
            Text(
                text = category.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.95f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
fun LevelSelectionDialog(
    categoryId: String,
    categoryTitle: String,
    onDismiss: () -> Unit,
    onLevelSelected: (String) -> Unit,
    viewModel: QuizViewModel = viewModel()
) {
    val levels by viewModel.levels.collectAsState()
    val isLoading by viewModel.quizRepository.isLoading.collectAsState()
    
    // Tải levels khi dialog mở
    LaunchedEffect(Unit) {
        viewModel.loadLevelsForCategory(categoryId)
    }
    
    Dialog(onDismissRequest = onDismiss) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(spacing().md),
            shape = shapes().large,
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            ),
            elevation = CardDefaults.cardElevation(
                defaultElevation = elevation().high
            )
        ) {
            Column(
                modifier = Modifier.padding(spacing().lg),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Title với icon
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "🎯 Chọn cấp độ",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center
                    )
                }
                
                Spacer(modifier = Modifier.height(spacing().sm))
                
                Text(
                    text = categoryTitle,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(spacing().lg))
                
                if (isLoading) {
                    Box(
                        modifier = Modifier.padding(spacing().xl),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                } else {
                    levels.forEach { level ->
                        val levelColor = when (level.id) {
                            "N1" -> Color(0xFFE53935) // Đỏ đậm (như Kanji)
                            "N2" -> Color(0xFFFF7043) // Cam đậm (như Katakana)
                            "N3" -> Color(0xFF42A5F5) // Xanh dương sáng (như Hiragana)
                            "N4" -> Color(0xFF66BB6A) // Xanh lá đậm
                            "N5" -> Color(0xFF9C27B0) // Tím đậm
                            else -> Color(0xFF607D8B) // Xanh xám
                        }
                        
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = spacing().xs),
                            shape = shapes().medium,
                            colors = CardDefaults.cardColors(
                                containerColor = levelColor.copy(alpha = 0.25f)
                            ),
                            elevation = CardDefaults.cardElevation(
                                defaultElevation = elevation().low
                            ),
                            onClick = { onLevelSelected(level.id) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(spacing().md),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Text(
                                        text = level.name,
                                        style = MaterialTheme.typography.titleLarge,
                                        fontWeight = FontWeight.Bold,
                                        color = levelColor
                                    )
                                    Text(
                                        text = level.description,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                                
                                Icon(
                                    imageVector = Icons.Default.ArrowForward,
                                    contentDescription = "Chọn",
                                    tint = levelColor,
                                    modifier = Modifier.size(components().iconSize * 1.2f)
                                )
                            }
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(spacing().md))
                
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.align(Alignment.End),
                    colors = ButtonDefaults.textButtonColors(
                        contentColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text(
                        text = "Đóng",
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}
