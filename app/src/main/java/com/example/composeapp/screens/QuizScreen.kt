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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

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
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Chọn cấp độ - ${category.title}",
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

data class QuizCategory(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val questionCount: Int = 10
)

data class Level(
    val id: String,
    val name: String,
    val description: String,
    val color: Color
)

private val levels = listOf(
    Level(
        id = "N5",
        name = "N5 - Sơ cấp",
        description = "Dành cho người mới bắt đầu",
        color = Color(0xFF4CAF50)
    ),
    Level(
        id = "N4",
        name = "N4 - Trung cấp cơ bản",
        description = "Từ vựng và ngữ pháp cơ bản",
        color = Color(0xFF2196F3)
    )
)

private val categories = listOf(
    QuizCategory(
        title = "Động vật",
        icon = Icons.Default.Pets,
        color = Color(0xFF1E88E5)
    ),
    QuizCategory(
        title = "Màu sắc",
        icon = Icons.Default.Palette,
        color = Color(0xFFD81B60)
    ),
    QuizCategory(
        title = "Cuộc sống",
        icon = Icons.Default.Home,
        color = Color(0xFF43A047)
    ),
    QuizCategory(
        title = "Gia đình",
        icon = Icons.Default.People,
        color = Color(0xFF7B1FA2)
    ),
    QuizCategory(
        title = "Thức ăn",
        icon = Icons.Default.Restaurant,
        color = Color(0xFFC0CA33)
    ),
    QuizCategory(
        title = "Số đếm",
        icon = Icons.Default.Numbers,
        color = Color(0xFF00ACC1)
    ),
    QuizCategory(
        title = "Thời gian",
        icon = Icons.Default.Schedule,
        color = Color(0xFFFF7043)
    ),
    QuizCategory(
        title = "Giao thông",
        icon = Icons.Default.DirectionsCar,
        color = Color(0xFF8E24AA)
    ),
    QuizCategory(
        title = "Thời tiết",
        icon = Icons.Default.WbSunny,
        color = Color(0xFFFFB300)
    )
) 