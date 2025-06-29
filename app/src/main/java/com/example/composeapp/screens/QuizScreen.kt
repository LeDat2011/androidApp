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
                    categoryTitle = selectedCategoryData.title,
                    onDismiss = { selectedCategory = null },
                    onLevelSelected = { level ->
                        showQuizDetail = true
                        selectedLevel = level
                    }
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
        "animals" -> Color(0xFF8BC34A)
        "colors" -> Color(0xFF2196F3)
        "family" -> Color(0xFFE91E63)
        "food" -> Color(0xFFFF9800)
        "numbers" -> Color(0xFF673AB7)
        "time" -> Color(0xFF009688)
        "transportation" -> Color(0xFF795548)
        "weather" -> Color(0xFF607D8B)
        else -> Color(0xFF9C27B0)
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
        else -> Icons.Default.Category
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = backgroundColor
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
                imageVector = icon,
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
                text = category.description,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.White.copy(alpha = 0.8f),
                textAlign = TextAlign.Center
            )
        }
    }
}

@Composable
fun LevelSelectionDialog(
    categoryTitle: String,
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
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Chọn cấp độ - $categoryTitle",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(24.dp))
                
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

data class Level(
    val id: String,
    val name: String,
    val description: String,
    val color: Color
)

private val levels = listOf(
    Level(
        id = "N1",
        name = "N1 - Cao cấp",
        description = "Trình độ cao nhất, tương đương bản ngữ",
        color = Color(0xFFE91E63)
    ),
    Level(
        id = "N2",
        name = "N2 - Trung cấp cao",
        description = "Có thể giao tiếp trong hầu hết tình huống",
        color = Color(0xFF2196F3)
    ),
    Level(
        id = "N3",
        name = "N3 - Trung cấp",
        description = "Hiểu và sử dụng tiếng Nhật trong cuộc sống hàng ngày",
        color = Color(0xFF4CAF50)
    ),
    Level(
        id = "N4",
        name = "N4 - Sơ trung cấp",
        description = "Hiểu được tiếng Nhật cơ bản",
        color = Color(0xFFFF9800)
    ),
    Level(
        id = "N5",
        name = "N5 - Sơ cấp",
        description = "Hiểu những cấu trúc tiếng Nhật đơn giản",
        color = Color(0xFF607D8B)
    )
) 